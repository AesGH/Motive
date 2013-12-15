package aes.motive.tileentity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeDirection;
import aes.base.TileEntityBase;
import aes.motive.BlockInfo;
import aes.motive.ConnectedBlocks;
import aes.motive.Motive;
import aes.motive.PacketHandler;
import aes.motive.core.asm.RenderHook;
import aes.utils.Obfuscation;
import aes.utils.PrivateFieldAccess;
import aes.utils.Vector2i;
import aes.utils.Vector3f;
import aes.utils.Vector3i;
import aes.utils.WorldUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMoverBase extends TileEntityBase {
	private static final boolean REMOVE_EMPTY_CONNECTED_BLOCKS = false;

	private static Map<String, Map<String, TileEntityMoverBase>> movers = new HashMap<String, Map<String, TileEntityMoverBase>>();

	public static void clientDimensionUnloaded(World world) {
		final String key = getKeyForWorld(world);
		Motive.log("MOVERS: Removing movers for " + key);
		movers.remove(key);
	}

	private static boolean DEBUG_DUMP_MOVER_REGISTRY_ON_CHANGE() {
		return true;
	}

	private static boolean DEBUG_HALT_MOVE_HALFWAY() {
		return false;
	}

	private static boolean DEBUG_LOOP_MOVE_HALFWAY() {
		return false;
	}

	private static void dumpMovers() {
		if (DEBUG_DUMP_MOVER_REGISTRY_ON_CHANGE()) {
			Motive.log("current movers:");
			for (final String key : movers.keySet()) {
				final Map<String, TileEntityMoverBase> entities = movers.get(key);
				Motive.log(key + " : " + entities.size() + " movers");
			}
			Thread.dumpStack();			
		}
	}

	protected static String getKeyForWorld(World world) {
		if (world == null)
			return "(null)";

		final String key = (world.isRemote ? "client" : "server") + "." + world.getWorldInfo().getWorldName() + "." + world.provider.dimensionId + "."
				+ world.provider.getDimensionName();
		return key;
	}

	public static TileEntityMoverBase getMover(World world, String uid) {
		return getMovers(world).get(uid);
	}

	public static Map<String, TileEntityMoverBase> getMovers(World world) {
		if (world == null) {
			try {
				throw new Exception("world is NULL getting movers");
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		final String key = getKeyForWorld(world);

		if (!movers.containsKey(key)) {
			movers.put(key, new HashMap<String, TileEntityMoverBase>());
		}

		return movers.get(key);
	}

	public static void removeMover(TileEntityMoverBase tileEntityMover) {
		final World world = tileEntityMover.worldObj;
		if (getMovers(world).remove(tileEntityMover.getUid()) != null) {
			// Motive.log(world, "MOVERS: removing mover from " +
			// getKeyForWorld(world));
			dumpMovers();
		}
	}

	public static void serverStopped() {
		Motive.log("MOVERS: Removing server movers");
		final List<String> toRemove = new LinkedList<String>();
		for (final String key : movers.keySet()) {
			if (key.startsWith("server.")) {
				toRemove.add(key);
			}
		}

		for (final String key : toRemove) {
			movers.remove(key);
		}
		dumpMovers();
	}

	public static void setMover(TileEntityMoverBase tileEntityMover) {
		final World world = tileEntityMover.worldObj;
		// Motive.log(world, "MOVERS: adding mover to " +
		// getKeyForWorld(world));

		getMovers(world).put(tileEntityMover.getUid(), tileEntityMover);

		dumpMovers();
	}

	private String uid;
	public boolean moving = false;
	private boolean forcingSimultaneousRenderering = false;
	public Vector3f moved = new Vector3f();
	private ConnectedBlocks connectedBlocks;
	private String status = "";

	private String statusDetail = "";

	private Vector3i[] affectedBlocks;

	final float MIN_SQUISH = -0.6f;

	final float MAX_SQUISH = 0.6f;

	public float squishFactor, prevSquishFactor;
	public float squishAmount = 0.6f;

	public Set<Vector2i> affectedChunks;

	private boolean registerMover;

	private Vector3f movedPreviousTick;

	private Set<WorldRenderer> affectedWorldRenderers = new LinkedHashSet<WorldRenderer>();

	public boolean canMove() {
		return !isObstructed() && (this.worldObj.isRemote || !isMovingIntoUnloadedChunk());
	}

	protected boolean checkBeginMoving() {
		if (!getActive()) {
			setPowered(new Vector3i());
			setStatus("", "");
			return false;
		}

		if (!updatePowered()) {
			setStatus("No signal", "");
			return false;
		}

		updateAffectedBlocks();

		final TileEntityMoverBase moverMoving = RenderHook.INSTANCE.getMoverMoving(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		if (moverMoving != null && moverMoving != this) {
			try {
				setStatus("Being moved already", moverMoving.blockType.getLocalizedName() + " at " + moverMoving.getLocation());
			} catch (final NullPointerException e) {
				Motive.log("moverMoving found with problems - moverMoving of type " + moverMoving.getClass().getName() + " with blocktype of "
						+ (moverMoving.blockType == null ? "null" : moverMoving.blockType.getClass().getName()));
				throw e;
			}
			return false;
		}

		if (!canMove()) {
			setPowered(new Vector3i());
			return false;
		}

		setStatus("Moving", getPowered().getDirection());

		moveStart(getPowered());
		return true;
	}

	private float clamp(float value) {
		if (DEBUG_HALT_MOVE_HALFWAY()) {
			if (value > 0.5f)
				return 0.5f;
			if (value < -0.5f)
				return -0.5f;
		}
		if (DEBUG_LOOP_MOVE_HALFWAY()) {
			if (value > 0.5f)
				return 0;
			if (value < -0.5f)
				return 0;
		}
		if (value + 0.001f >= 1f) {
			value = 1f;
		}
		if (value - 0.001f <= -1f) {
			value = -1f;
		}
		return value;
	}

	public void clientChangedProperty(String name, String value) {
		Motive.packetHandler.sendThisMethodToServer(this.worldObj, this, name, value);
		if (!this.worldObj.isRemote) {
			Motive.log(this.worldObj, "property changed: " + name + " = " + value);
			propertyChanged(name, value);
		}
	}

	private float distanceFromSpeed() {
		return 0.01f + getSpeed() * 0.8f;
	}

	public boolean getActive() {
		return false;
	}

	public Vector3i[] getAffectedBlocks() {
		getConnectedBlocks();
		return this.affectedBlocks;
	}

	public Set<WorldRenderer> getAffectedWorldRenderers() {
		return this.affectedWorldRenderers;
	}

	public ConnectedBlocks getConnectedBlocks() {
		if (this.connectedBlocks == null) {
			setConnectedBlocks(new ConnectedBlocks(getLocation()));
		}
		return this.connectedBlocks;
	}

	public boolean getForcingSimultaneousRenderering() {
		return this.forcingSimultaneousRenderering;
	}

	public boolean getLocked() {
		return getConnectedBlocks().blocks.size() > 1;
	}

	public int getLockedCount() {
		return getConnectedBlocks().blocks.size();
	}

	public Vector3f getMovedPreviousTick() {
		return this.movedPreviousTick;
	}

	public Vector3i getPowered() {
		return new Vector3i();
	}

	public float getSpeed() {
		return 0;
	}

	public String getStatus() {
		return this.status;
	};

	public String getStatusDetail() {
		return this.statusDetail;
	}

	public String getUid() {
		if (this.uid == null) {
			try {
				setUid(null);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return this.uid;
	}

	private boolean isMoveComplete() {
		return Math.abs(this.moved.x) >= 1f || Math.abs(this.moved.y) >= 1f || Math.abs(this.moved.z) >= 1f;
	}

	protected boolean isMovingIntoUnloadedChunk() {
		for (final Vector3i location : getConnectedBlocks().blocks) {
			final Chunk chunkFrom = this.worldObj.getChunkFromBlockCoords(location.x, location.z);
			if (!chunkFrom.isChunkLoaded)
				return true;
			final Vector3i to = location.add(getPowered());
			final Chunk chunk = this.worldObj.getChunkFromBlockCoords(to.x, to.z);
			if (!chunk.isChunkLoaded)
				return true;
		}
		return false;
	}

	public boolean isObstructed() {
		for (final Vector3i location : getConnectedBlocks().blocks) {
			final Chunk chunkFrom = this.worldObj.getChunkFromBlockCoords(location.x, location.z);
			if (!chunkFrom.isChunkLoaded)
				return false;

			final Vector3i to = location.add(getPowered());

			if (to.x < -30000000 || to.z < -30000000 || to.x >= 30000000 || to.z >= 30000000 || to.y < 0 || to.y >= 256) {
				setStatus("Obstructed", "end of world at " + to);
				return true;
			}

			final Chunk chunk = this.worldObj.getChunkFromBlockCoords(to.x, to.z);
			if (!chunk.isChunkLoaded)
				return false;

			final int blockId = this.worldObj.getBlockId(to.x, to.y, to.z);
			if (blockId != 0 && !getConnectedBlocks().blocks.contains(to)) {

				final Block block = Block.blocksList[blockId];

				if (WorldUtils.isBlockFluid(block)) {
					continue;
				}

				if (block.isBlockReplaceable(this.worldObj, to.x, to.y, to.z)) {
					continue;
				}

				setStatus("Obstructed", block.getLocalizedName() + " at " + to);
				/*
				 * Motive.log("obstruction at " + to + " of type " + blockId +
				 * " (" + Block.blocksList[blockId].getUnlocalizedName() + ")");
				 */return true;
			}
		}
		return false;
	}

	public final static Set<WorldRenderer> worldRenderersToUpdate = new HashSet<WorldRenderer>();
	
	@SuppressWarnings("unchecked")
	protected void markConnectedBlocksForRender() {
		if (this.worldObj == null || !this.worldObj.isRemote)
			return;
		if (this.connectedBlocks == null)
			return;

		final RenderGlobal renderGlobal = Minecraft.getMinecraft().renderGlobal;

		final List<WorldRenderer> worldRenderersToUpdate = (List<WorldRenderer>) PrivateFieldAccess.getValue(renderGlobal,
				Obfuscation.getSrgName("worldRenderersToUpdate"));
		for (final WorldRenderer worldrenderer : getAffectedWorldRenderers()) {
			if (!worldRenderersToUpdate.contains(worldrenderer)) {
				TileEntityMover.worldRenderersToUpdate.add(worldrenderer);
				
				worldRenderersToUpdate.add(worldrenderer);
				worldrenderer.markDirty();
			}
		}

		
		  for (final Vector3i location : this.connectedBlocks.blocks) {
		  renderGlobal.markBlockForRenderUpdate(location.x, location.y,
		  location.z); }
		 }

	protected void moveConnectedEntities() {
		for (final Object entity : this.worldObj.loadedEntityList) {
			final Entity entity1 = (Entity) entity;
			if (entity1 != null) {
				Vector3i location = new Vector3i((int) Math.floor(entity1.posX), (int) Math.floor(entity1.posY - entity1.yOffset + 2),
						(int) Math.floor(entity1.posZ));
				for (int i = 0; i <= 4; i++) {
					if (this.connectedBlocks.blocks.contains(location)) {
						entity1.posX += getPowered().x;
						entity1.posY += getPowered().y;
						entity1.posZ += getPowered().z;
						entity1.lastTickPosX += getPowered().x;
						entity1.lastTickPosY += getPowered().y;
						entity1.lastTickPosZ += getPowered().z;
						entity1.prevPosX += getPowered().x;
						entity1.prevPosY += getPowered().y;
						entity1.prevPosZ += getPowered().z;
						entity1.serverPosX += getPowered().x;
						entity1.serverPosY += getPowered().y;
						entity1.serverPosZ += getPowered().z;
						entity1.setPosition(entity1.posX, entity1.posY, entity1.posZ);
						// Motive.log(this.worldObj, "moved " +
						// entity.getClass().getName() +
						// entity.getEntityName());
						break;
					}
					location = location.increment(ForgeDirection.DOWN);
				}
			}
		}
	}

	public void moveEnd() {
		Motive.packetHandler.sendThisMethodToClient(this.worldObj, this);

		this.moving = false;
		this.moved = new Vector3f();
		this.movedPreviousTick = this.moved;

		//Motive.log(this.worldObj, "moving " + getLockedCount() + " blocks " + getPowered().getDirection() + " from " + getLocation());

		updateAffectedBlocks();

		if (canMove()) {
			moveConnectedEntities();

			final LinkedList<BlockInfo> blockInfos = readMovedBlocks();

			final Vector3i[] affected = this.affectedBlocks;

			setConnectedBlocks(getConnectedBlocks().offset(getPowered()));

			moveWriteEmptyBlocks(affected);
			moveWriteMovedBlocks(getLocation().add(getPowered()), blockInfos, affected);
		}
	}

	public void moveStart(Vector3i powered) {
		Motive.packetHandler.sendThisMethodToClient(this.worldObj, this, powered);

		this.moving = true;
		this.moved = new Vector3f();

		if (this.worldObj.isRemote) {
			setPowered(powered);
		}
	}

	private void moveWriteEmptyBlocks(Vector3i[] affectedLocations) {
		final WorldUtils we = new WorldUtils(this.worldObj);
		for (final Vector3i location : affectedLocations) {
			final Chunk chunkFrom = this.worldObj.getChunkFromBlockCoords(location.x, location.z);
			if (!chunkFrom.isChunkLoaded) {
				continue;
			}

			WorldUtils.setBlockMetadataAndTileEntityWithoutUpdate(this.worldObj, location.x, location.y, location.z, 0, 0, null, false);
			we.removeBlockScheduledUpdate(we.world, location);
			we.world.markBlockForRenderUpdate(location.x, location.y, location.z);
		}
	}

	private void moveWriteMovedBlocks(Vector3i to, LinkedList<BlockInfo> blockInfos, Vector3i[] affectedLocations) {
		final WorldUtils we = new WorldUtils(this.worldObj);
		boolean anyBlocksLoaded = false;
		for (final BlockInfo block : blockInfos) {
			final Vector3i c = block.coord.add(getPowered());

			final Chunk chunkFrom = this.worldObj.getChunkFromBlockCoords(c.x, c.z);
			if (!chunkFrom.isChunkLoaded) {
				continue;
			}

			anyBlocksLoaded = true;
			WorldUtils.setBlockMetadataAndTileEntityWithoutUpdate(this.worldObj, c.x, c.y, c.z, block.blockid, block.metadata, block.entity, true);
			we.setNextBlockUpdate(c, block.blockid, block.nextUpdate);
		}

		// ensure client updated TE location even if it's moving out of loaded
		// chunks.
		this.xCoord = to.x;
		this.yCoord = to.y;
		this.zCoord = to.z;

		// remove from client if moved out of range
		if (!anyBlocksLoaded) {
			Motive.log(this.worldObj, "Moved out of range (all " + blockInfos + " blocks). deregistering mover");
			
			// remove cleared tileEntities
			for (final BlockInfo block : blockInfos) {
				if(block.entity != null)
				{
					//this.worldObj.removeBlockTileEntity(block.entity.xCoord, block.entity.yCoord, block.entity.zCoord);
					block.entity.invalidate();
				}
			}			
			removeMover(this);
			return;
		}

		/*
		 * if (false) { try { final Class<?> tileMultipartClass =
		 * Class.forName("codechicken.multipart.TileMultipart$"); final Object
		 * tileMultipartInstance =
		 * tileMultipartClass.getField("MODULE$").get(null); final Method
		 * getClientFlushMap = tileMultipartClass.getMethod(
		 * "codechicken$multipart$TileMultipart$$clientFlushMap"); final
		 * HashMap<?, ?> clientFlushMap = (HashMap<?, ?>)
		 * getClientFlushMap.invoke(tileMultipartInstance);
		 * 
		 * final Object ooo = clientFlushMap;
		 * 
		 * final int tableSize =
		 * HashMap.class.getField("tableSize").getInt(clientFlushMap); if
		 * (tableSize > 0) {
		 * 
		 * @SuppressWarnings("unused") final Object ccc = ooo; }
		 * 
		 * // 0: getstatic #25 // Field //
		 * codechicken/multipart/TileMultipart$.MODULE$
		 * :Lcodechicken/multipart/TileMultipart$; // 3: invokevirtual #29 //
		 * Method // codechicken/multipart/TileMultipart$.
		 * codechicken$multipart$TileMultipart$$clientFlushMap
		 * :()Lscala/collection/mutable/Map; // 6: aload_0
		 * 
		 * // scala.collection.mutable.Map<codechicken.lib.vec.BlockCoord, //
		 * codechicke
		 * 
		 * 
		 * for (final BlockInfo block : blockInfos) { final Vector3i c = new
		 * Vector3i(block.coord.x + moveX, block.coord.y + moveY, block.coord.z
		 * + moveZ); }
		 * 
		 * } catch (final ClassNotFoundException e) { } catch (final
		 * IllegalArgumentException e) { } catch (final IllegalAccessException
		 * e) { } catch (final NoSuchFieldException e) { } catch (final
		 * SecurityException e) { } catch (final NoSuchMethodException e) { }
		 * catch (final InvocationTargetException e) { } }
		 */
		/*
		 * for (final BlockInfo block : blockInfos) { final Vector3i c =
		 * block.coord.add(this.powered); we.world.markBlockForUpdate(c.x, c.y,
		 * c.z); we.world.notifyBlockChange(c.x, c.y, c.z, block.blockid); }
		 */

		if (!this.worldObj.isRemote) {
			for (final Vector3i c : affectedLocations) {
				if (this.worldObj.getChunkFromBlockCoords(c.x, c.z).isChunkLoaded) {
					we.world.notifyBlockOfNeighborChange(c.x, c.y, c.z, we.world.getBlockId(c.x, c.y, c.z));
				}
			}
		}
	}

	@Override
	public void onBlockNeighborChange() {
		removeEmptyLockedBlocks();
	}

	public void propertyChanged(String name, String value) {
		if ("active".equals(name)) {
			setActive("true".equals(value));
		}
		if ("locked".equals(name)) {
			setLocked("true".equals(value));
		}
		if ("speed".equals(name)) {
			final float valueFloat = java.lang.Float.parseFloat(value);
			setSpeed(valueFloat);
		}
	}

	private Set<Vector3i> readBlockListFromNBT(NBTTagCompound nbtTagCompound, String name) {
		final Set<Vector3i> result = new HashSet<Vector3i>();
		final int[] coords = nbtTagCompound.getIntArray(name);
		for (int i = 0; i < coords.length / 3; i++) {
			result.add(new Vector3i(coords[i * 3], coords[i * 3 + 1], coords[i * 3 + 2]));
		}
		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		try {
			setUid(nbtTagCompound.getString("uid"));
		} catch (final Exception e) {
			e.printStackTrace();
		}

		this.moving = nbtTagCompound.getBoolean("moving");
		this.status = nbtTagCompound.getString("status");
		this.statusDetail = nbtTagCompound.getString("statusDetail");

		this.moved = readVector3f(nbtTagCompound, "moved");// new
															// Vector3f(nbtTagCompound.getFloat("movedX"),
															// nbtTagCompound.getFloat("movedY"),
															// nbtTagCompound.getFloat("movedZ"));
		setPowered(readVector3i(nbtTagCompound, "powered"));
		this.forcingSimultaneousRenderering = nbtTagCompound.getBoolean("forceRender");
		// new Vector3i(nbtTagCompound.getInteger("poweredX"),
		// nbtTagCompound.getInteger("poweredY"),
		// nbtTagCompound.getInteger("poweredZ")));

		setConnectedBlocks(new ConnectedBlocks(getLocation(), readBlockListFromNBT(nbtTagCompound, "connectedBlocks")));

		Motive.log(this.worldObj, "reading tileEntityMover NBT " + toString());
	}

	protected LinkedList<BlockInfo> readMovedBlocks() {
		final LinkedList<BlockInfo> blockInfos = new LinkedList<BlockInfo>();
		for (final Vector3i location : getConnectedBlocks().blocks) {
			final BlockInfo movingBlock = new BlockInfo();

			movingBlock.blockid = this.worldObj.getBlockId(location.x, location.y, location.z);
			movingBlock.coord = location;
			movingBlock.metadata = this.worldObj.getBlockMetadata(location.x, location.y, location.z);
			movingBlock.entity = this.worldObj.getBlockTileEntity(location.x, location.y, location.z);
			movingBlock.nextUpdate = WorldUtils.getBlockNextUpdate(this.worldObj, location);

			blockInfos.add(movingBlock);
		}
		return blockInfos;
	}

	protected Vector3f readVector3f(NBTTagCompound nbtTagCompound, String name) {
		return new Vector3f(nbtTagCompound.getFloat(name + "X"), nbtTagCompound.getFloat(name + "Y"), nbtTagCompound.getFloat(name + "Z"));
	}

	protected Vector3i readVector3i(NBTTagCompound nbtTagCompound, String name) {
		return new Vector3i(nbtTagCompound.getInteger(name + "X"), nbtTagCompound.getInteger(name + "Y"), nbtTagCompound.getInteger(name + "Z"));
	}

	private void removeEmptyLockedBlocks() {
		if (!REMOVE_EMPTY_CONNECTED_BLOCKS)
			return;
		final LinkedList<Vector3i> toRemove = new LinkedList<Vector3i>();
		for (final Vector3i location : getConnectedBlocks().blocks) {
			if (!WorldUtils.isNonEmptyBlock(this.worldObj, location)) {
				toRemove.add(location);
			}
		}
		for (final Vector3i location : toRemove) {
			getConnectedBlocks().blocks.remove(location);
		}
		if (!toRemove.isEmpty()) {
			updateBlock();
		}
	}

	@Override
	public void removeTileEntity() {
		super.removeTileEntity();
		removeMover(this);
	}

	@SideOnly(Side.SERVER)
	public void sendIfInRange(String toPlayerName) {
		PacketHandler.sendToPlayerIfAllBlocksLoaded((WorldServer) this.worldObj, toPlayerName, getAffectedBlocks());
	}

	public void setActive(boolean value) {
	}

	public void setAffectedWorldRenderers(Set<WorldRenderer> affectedWorldRenderers) {
		this.affectedWorldRenderers = affectedWorldRenderers;
	}

	private void setConnectedBlocks(ConnectedBlocks connectedBlocks) {
		if (connectedBlocks != null) {
			markConnectedBlocksForRender();
		}
		this.connectedBlocks = connectedBlocks;
		updateAffectedBlocks();
		updateAffectedWorldRenderers();
		markConnectedBlocksForRender();
	}

	public void setForcingSimultaneousRenderering(boolean forcingSimultaneousRenderering) {
		this.forcingSimultaneousRenderering = forcingSimultaneousRenderering;
	}

	public boolean setLocked(boolean value) {
		if (this.moving)
			return false;

		if (value) {
			setConnectedBlocks(new ConnectedBlocks(this.worldObj, getLocation()));
		} else {
			final Set<Vector3i> lockedBlocks = new HashSet<Vector3i>();
			lockedBlocks.add(new Vector3i(this.xCoord, this.yCoord, this.zCoord));
			setConnectedBlocks(new ConnectedBlocks(getLocation(), lockedBlocks));
		}

		Motive.log(this.worldObj, "locked set to " + "true".equals(value));
		updateBlock();
		return true;
	}

	public void setPowered(Vector3i powered) {
	}

	public void setSpeed(float speed) {
	}

	private void setStatus(String value) {
		if (!this.status.equals(value)) {
			this.status = value;
			updateBlock();
		}
	}

	private void setStatus(String value, String detail) {
		setStatus(value);
		setStatusDetail(detail);
	}

	public void setStatusDetail(String value) {
		if (!this.statusDetail.equals(value)) {
			this.statusDetail = value;
			updateBlock();
		}
	}

	private void setUid(String value) throws Exception {
		if (this.uid != null) {
			if (this.uid.equals(value))
				return;
			removeMover(this);
		}
		if (value == null || value.equals("")) {
			value = UUID.randomUUID().toString();
		}
		this.uid = value;
		if (this.worldObj == null) {
			this.registerMover = true;
		} else {
			setMover(this);
		}

	}

	public boolean toggleConnectedBlock(Vector3i location) {
		setConnectedBlocks(new ConnectedBlocks(this.worldObj, getConnectedBlocks(), location));
		updateBlock();
		return true;
	}

	@Override
	public String toString() {
		return "{ uid: " + getUid() + ", location: { " + getLocation() + " }, active: " + getActive() + ", moving: " + this.moving + ", speed: " + getSpeed()
				+ ", moved: " + this.moved.toString() + ", powered: " + getPowered().toString() + ", lockedBlocksCount: " + getLockedCount() + ", status: "
				+ this.status + " }";
	}

	protected void updateAffectedBlocks() {
		final HashSet<Vector3i> result = new HashSet<Vector3i>();
		for (final Vector3i block : getConnectedBlocks().blocks) {
			result.add(block);
			result.add(block.add(getPowered()));
		}
		this.affectedBlocks = result.toArray(new Vector3i[0]);
		this.affectedChunks = WorldUtils.getChunksForLocations(this.affectedBlocks);
	}

	private void updateAffectedWorldRenderers() {
		if (this.worldObj == null || !this.worldObj.isRemote)
			return;

		getAffectedWorldRenderers().clear();

		final RenderGlobal renderGlobal = Minecraft.getMinecraft().renderGlobal;

		if (renderGlobal == null)
			return;

		final int renderChunksWide = (Integer) PrivateFieldAccess.getValue(renderGlobal, Obfuscation.getSrgName("renderChunksWide"));
		final int renderChunksTall = (Integer) PrivateFieldAccess.getValue(renderGlobal, Obfuscation.getSrgName("renderChunksTall"));
		final int renderChunksDeep = (Integer) PrivateFieldAccess.getValue(renderGlobal, Obfuscation.getSrgName("renderChunksDeep"));

		final WorldRenderer[] worldRenderers = (WorldRenderer[]) PrivateFieldAccess.getValue(renderGlobal, Obfuscation.getSrgName("worldRenderers"));

		for (final Vector3i location : this.connectedBlocks.blocks) {
			getAffectedWorldRenderers().add(WorldUtils.getWorldRenderer(worldRenderers, renderChunksWide, renderChunksTall, renderChunksDeep, location));
		}

//		Motive.log("found " + getAffectedWorldRenderers().size() + " affected world renderers");
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (this.registerMover) {
			this.registerMover = false;
			setMover(this);
		}

		TileEntityMoverBase moverMoving;
		moverMoving = getMover(worldObj, getUid());
		if (moverMoving != null && moverMoving != this) {
			Motive.log(worldObj, "found a different mover registered as this uid");
			
			Motive.log(worldObj, "location of other: " + moverMoving.getLocation() + ", location of me: " + getLocation());
			Motive.log(worldObj, "uid of other: " + moverMoving.getUid() + ", location of me: " + getUid());
			Motive.log(worldObj, "duplicate, invalidating self");
			invalidate();
			return;
		}

		
		if(moverMoving == null)
		{
			Motive.log(worldObj, "I'm not registered. Invalidating.");
			invalidate();
			return;
		}
		
		
		moverMoving = RenderHook.INSTANCE.getMoverMoving(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		if (moverMoving != null && moverMoving != this) {
			Motive.log(worldObj, "found a different mover moving this block");
			
			Motive.log(worldObj, "location of other: " + moverMoving.getLocation() + ", location of me: " + getLocation());
			Motive.log(worldObj, "uid of other: " + moverMoving.getUid() + ", location of me: " + getUid());
			if(moverMoving.getUid().equals(this.getUid()))
			{
				Motive.log(worldObj, "duplicate, invalidating self");
				invalidate();
				return;
			}
		}
		
		
		if (this.moving) {
			if (this.worldObj.isRemote) {
				this.prevSquishFactor = this.squishFactor;
				this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;

				if (Math.abs(this.squishFactor - this.MIN_SQUISH) < 0.01f || this.squishAmount == 0) {
					this.squishAmount = 0.6f;
				}
				if (Math.abs(this.squishFactor - this.MAX_SQUISH) < 0.01f) {
					this.squishAmount = -0.6f;
				}
				// this.squishAmount *= 0.99f;
			}

			final float distance = distanceFromSpeed();

			this.movedPreviousTick = new Vector3f(this.moved);
			this.moved = new Vector3f(clamp(this.moved.x + getPowered().x * distance), clamp(this.moved.y + getPowered().y * distance), clamp(this.moved.z
					+ getPowered().z * distance));
		} else {
			if (this.worldObj.isRemote) {
				this.prevSquishFactor = this.squishFactor;
				this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;

				if (Math.abs(this.squishFactor - this.MIN_SQUISH) < 0.01f || this.squishAmount == 0) {
					this.squishAmount = 0.6f;
				}
				if (Math.abs(this.squishFactor - this.MAX_SQUISH) < 0.01f) {
					this.squishAmount = -0.6f;
				}
				// this.squishAmount *= 0.99f;
			}

		}

		if (!this.worldObj.isRemote) {
			if (this.moving && isMoveComplete()) {
				moveEnd();
			}
			if (!this.moving) {
				checkBeginMoving();
			}
			return;
		}

		if (this.moving) {
			if(getMover(worldObj, this.getUid()) != null)
			{
				markConnectedBlocksForRender();
				this.worldObj.spawnParticle("reddust", this.xCoord + this.moved.x + 0.5F, this.yCoord + this.moved.y + 0.5F, this.zCoord + this.moved.z + 0.5F,
					-0.4, 0, 1);
			}
			else
			{
				removeTileEntity();
			}
		}
	}

	boolean updatePowered() {
		return false;
	}

	private void writeBlockListToNBT(NBTTagCompound nbtTagCompound, String name, Set<Vector3i> blocks, boolean adjusToBeRelative) {
		if (blocks == null)
			return;

		final int[] coords = new int[blocks.size() * 3];

		int i = 0;
		if (adjusToBeRelative) {
			for (final Vector3i vector3i : blocks) {
				coords[i++] = vector3i.x - this.xCoord;
				coords[i++] = vector3i.y - this.yCoord;
				coords[i++] = vector3i.z - this.zCoord;
			}
		} else {
			for (final Vector3i vector3i : blocks) {
				coords[i++] = vector3i.x;
				coords[i++] = vector3i.y;
				coords[i++] = vector3i.z;
			}
		}
		nbtTagCompound.setIntArray(name, coords);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);

		Motive.log(this.worldObj, "writing tileEntityMover NBT " + toString());

		nbtTagCompound.setString("uid", getUid());
		nbtTagCompound.setBoolean("moving", this.moving);

		nbtTagCompound.setString("status", this.status);
		nbtTagCompound.setString("statusDetail", this.statusDetail);

		writeVector(nbtTagCompound, "moved", this.moved);

		writeVector(nbtTagCompound, "powered", getPowered());
		nbtTagCompound.setBoolean("forceRender", this.forcingSimultaneousRenderering);

		writeBlockListToNBT(nbtTagCompound, "connectedBlocks", getConnectedBlocks().blocks, false);
	}

	protected void writeVector(NBTTagCompound nbtTagCompound, String name, Vector3f vector) {
		nbtTagCompound.setFloat(name + "X", vector.x);
		nbtTagCompound.setFloat(name + "Y", vector.y);
		nbtTagCompound.setFloat(name + "Z", vector.z);
	}

	protected void writeVector(NBTTagCompound nbtTagCompound, String name, Vector3i vector) {
		nbtTagCompound.setInteger(name + "X", vector.x);
		nbtTagCompound.setInteger(name + "Y", vector.y);
		nbtTagCompound.setInteger(name + "Z", vector.z);
	}

	public boolean areAnyConnectedBlocksLoaded() {
		for (final Vector3i c : affectedBlocks) {
			final Chunk chunkFrom = this.worldObj.getChunkFromBlockCoords(c.x, c.z);
			if (!chunkFrom.isChunkLoaded) {
				continue;
			}
			return true;
		}
		return false;
	}
}
