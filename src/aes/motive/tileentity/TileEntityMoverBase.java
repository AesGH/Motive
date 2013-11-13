package aes.motive.tileentity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.BlockFluidBase;
import aes.base.TileEntityBase;
import aes.motive.BlockInfo;
import aes.motive.ConnectedBlocks;
import aes.motive.Motive;
import aes.motive.PacketHandler;
import aes.motive.core.asm.RenderHook;
import aes.utils.Vector2i;
import aes.utils.Vector3f;
import aes.utils.Vector3i;
import aes.utils.WorldUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMoverBase extends TileEntityBase {
	private static Map<String, TileEntityMoverBase> moversServer = new HashMap<String, TileEntityMoverBase>();

	private static Map<String, TileEntityMoverBase> moversClient = new HashMap<String, TileEntityMoverBase>();

	private static boolean DEBUG_HALT_MOVE_HALFWAY() {
		return false;
	}

	private static boolean DEBUG_LOOP_MOVE_HALFWAY() {
		return false;
	}

	public static TileEntityMoverBase getMover(World world, String uid) {
		return getMovers(world).get(uid);
	}

	public static Map<String, TileEntityMoverBase> getMovers(World world) {
		return world != null && world.isRemote ? moversClient : moversServer;
	}

	public static void removeMover(World world, TileEntityMoverBase tileEntityMover) {
		getMovers(world).remove(tileEntityMover.getUid());
	}

	public static void serverStopped() {
		moversServer = new HashMap<String, TileEntityMoverBase>();
		moversClient = new HashMap<String, TileEntityMoverBase>();
	}

	public static void setMover(World world, TileEntityMoverBase tileEntityMover) {
		getMovers(world).put(tileEntityMover.getUid(), tileEntityMover);
	}

	private String uid;

	public boolean moving = false;
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
			setStatus("Being moved already", moverMoving.blockType.getLocalizedName() + " at " + moverMoving.getLocation());
			return false;
		}

		if (!canMove())
			return false;

		setStatus("Moving", "");

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

	private void clearAllAffectedBlocks(Vector3i[] affectedLocations) {
		final WorldUtils we = new WorldUtils(this.worldObj);
		for (final Vector3i location : affectedLocations) {
			final Chunk chunkFrom = this.worldObj.getChunkFromBlockCoords(location.x, location.z);
			if (!chunkFrom.isChunkLoaded) {
				continue;
			}

			we.setBlockMetadataAndTileEntityWithoutUpdate(location.x, location.y, location.z, 0, 0, null, false);
			we.removeBlockScheduledUpdate(we.world, location);
			we.world.markBlockForRenderUpdate(location.x, location.y, location.z);
		}
	}

	public void clientChangedProperty(String name, String value) {
		Motive.packetHandler.sendThisMethodToServer(this.worldObj, this, name, value);
		if (!this.worldObj.isRemote) {
			Motive.log(this.worldObj, "property changed: " + name + " = " + value);
			propertyChanged(name, value);
		}
	}

	private float distanceFromSpeed() {
		return 0.01f + getSpeed() * 0.3f;
	}

	/*
	 * private void updatePlayersMovingWith(float movedX, float movedY, float
	 * movedZ) {
	 * 
	 * if (worldObj.isRemote && false) { // Movement movement = new
	 * Movement(lockedBlocks, worldObj, xCoord, // yCoord, zCoord, (int)movedX,
	 * (int)movedY, (int)movedZ);
	 * 
	 * for (final Object playerObj : worldObj.playerEntities) { final
	 * EntityPlayer playerMoving = (EntityPlayer) playerObj; if
	 * (playerMoving.onGround) { final Vec3 position =
	 * playerMoving.getPosition(1F); // playerMoving.get for (final Vector3i
	 * coord : movement.affectedBlocks.blocks) { if (position.xCoord >= coord.x
	 * && position.xCoord <= coord.x + 1) { if (position.zCoord >= coord.z &&
	 * position.zCoord <= coord.z + 1) { if (position.yCoord - 1.62 >= coord.y +
	 * 1 && position.yCoord - 1.62 <= coord.y + 2) { playerMoving
	 * .setPosition(position.xCoord + movedX - this.movedX, position.yCoord +
	 * movedY - this.movedY, position.zCoord + movedZ - this.movedZ); break; } }
	 * } } } } } }
	 */

	public boolean getActive() {
		return false;
	}

	public Vector3i[] getAffectedBlocks() {
		getConnectedBlocks();
		return this.affectedBlocks;
	}

	public ConnectedBlocks getConnectedBlocks() {
		if (this.connectedBlocks == null) {
			setConnectedBlocks(new ConnectedBlocks(getLocation()));
		}
		return this.connectedBlocks;
	}

	public boolean getLocked() {
		return getConnectedBlocks().blocks.size() > 1;
	}

	public int getLockedCount() {
		return getConnectedBlocks().blocks.size();
	}

	public Vector3i getPowered() {
		return new Vector3i();
	}

	public float getSpeed() {
		return 0;
	}

	public String getStatus() {
		return this.status;
	}

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
	};

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
			final Chunk chunk = this.worldObj.getChunkFromBlockCoords(to.x, to.z);
			if (!chunk.isChunkLoaded)
				return false;
			final int blockId = this.worldObj.getBlockId(to.x, to.y, to.z);
			if (blockId != 0 && !getConnectedBlocks().blocks.contains(to)) {
				if (!(Block.blocksList[blockId] instanceof BlockFluid || Block.blocksList[blockId] instanceof BlockFluidBase)) {
					setStatus("Obstructed", Block.blocksList[blockId].getLocalizedName() + " at " + to);
					/*
					 * Motive.log("obstruction at " + to + " of type " + blockId
					 * + " (" + Block.blocksList[blockId].getUnlocalizedName() +
					 * ")");
					 */return true;
				}
			}
		}
		return false;
	}

	public void moveEnd() {
		Motive.packetHandler.sendThisMethodToClient(this.worldObj, this);

		this.moving = false;
		this.moved = new Vector3f();

		Motive.log(this.worldObj, "move END from " + getLocation());

		// removeMover(world, tileEntityMover);
		/*
		 * if (this.worldObj.isRemote && removeTileEntityRenderer()) { //
		 * Motive.log(this.worldObj, "removed rendering engine moving"); }
		 */
		updateAffectedBlocks();

		final WorldUtils we = new WorldUtils(this.worldObj);
		if (canMove()) {
			final LinkedList<BlockInfo> blockInfos = readMovedBlocks(we);

			final Vector3i[] affected = this.affectedBlocks;

			setConnectedBlocks(getConnectedBlocks().add(getPowered()));

			clearAllAffectedBlocks(affected);
			writeMovedBlocks(getLocation().add(getPowered()), blockInfos, affected);
		}
	}

	public void moveStart(Vector3i powered) {
		Motive.packetHandler.sendThisMethodToClient(this.worldObj, this, powered);
		// Motive.log(this.worldObj, "move START from " + getLocation());

		this.moving = true;
		this.moved = new Vector3f();

		if (this.worldObj.isRemote) {
			setPowered(powered);
			// final Vector3i vector3i = getLocation();
			// Motive.log(this.worldObj, "added rendering engine moving");
			// RenderHook.INSTANCE.movers.put(vector3i, this);
		}

		// updateBlock();
	}

	/*
	 * void Movement(LinkedList<Vector3i> fixedBlockOffsets, World world, int x,
	 * int y, int z) { }
	 */
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

	// Computer craft interface

	private LinkedList<Vector3i> readBlockListFromNBT(NBTTagCompound nbtTagCompound, String name) {
		final LinkedList<Vector3i> result = new LinkedList<Vector3i>();
		final int[] coords = nbtTagCompound.getIntArray(name);
		for (int i = 0; i < coords.length / 3; i++) {
			result.add(new Vector3i(coords[i * 3], coords[i * 3 + 1], coords[i * 3 + 2]));
		}
		// ModMotive.log(worldObj, "read tileEntity " + name +
		// " from NBT, count = " + result.size());
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
		this.moved = new Vector3f(nbtTagCompound.getFloat("movedX"), nbtTagCompound.getFloat("movedY"), nbtTagCompound.getFloat("movedZ"));
		setPowered(new Vector3i(nbtTagCompound.getInteger("poweredX"), nbtTagCompound.getInteger("poweredY"), nbtTagCompound.getInteger("poweredZ")));
		setConnectedBlocks(new ConnectedBlocks(getLocation(), readBlockListFromNBT(nbtTagCompound, "connectedBlocks")));

		Motive.log(this.worldObj, "reading tileEntityMover NBT " + toString());
	}

	protected LinkedList<BlockInfo> readMovedBlocks(final WorldUtils we) {
		final LinkedList<BlockInfo> blockInfos = new LinkedList<BlockInfo>();
		for (final Vector3i location : getConnectedBlocks().blocks) {
			final BlockInfo movingBlock = new BlockInfo();

			movingBlock.blockid = we.world.getBlockId(location.x, location.y, location.z);
			movingBlock.coord = location;
			movingBlock.metadata = we.world.getBlockMetadata(location.x, location.y, location.z);
			movingBlock.entity = we.world.getBlockTileEntity(location.x, location.y, location.z);
			movingBlock.nextUpdate = we.getBlockNextUpdate(we.world, location);

			blockInfos.add(movingBlock);
		}
		return blockInfos;
	}

	private void removeEmptyLockedBlocks() {
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
		removeMover(this.worldObj, this);
	}

	@SideOnly(Side.SERVER)
	public void sendIfInRange(String toPlayerName) {
		PacketHandler.sendToPlayerIfAllBlocksLoaded((WorldServer) this.worldObj, toPlayerName, getAffectedBlocks());
	}

	public void setActive(boolean value) {
	}

	private void setConnectedBlocks(ConnectedBlocks connectedBlocks) {
		this.connectedBlocks = connectedBlocks;
		updateAffectedBlocks();
	}

	public boolean setLocked(boolean value) {
		if (this.moving)
			return false;

		if (value) {
			setConnectedBlocks(new ConnectedBlocks(this.worldObj, getLocation()));
		} else {
			final LinkedList<Vector3i> lockedBlocks = new LinkedList<Vector3i>();
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
			
			removeMover(this.worldObj, this);
//			throw new Exception("uid already has a value");
		}
		if (value == null || value.equals("")) {
			value = UUID.randomUUID().toString();
		}
		this.uid = value;
		setMover(this.worldObj, this);
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

	@Override
	public void updateEntity() {
		super.updateEntity();

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
			// Motive.log(this.worldObj, "UPDATING move " +
			// this.moved.toString());
			for (final Vector3i location : getConnectedBlocks().blocks) {
				WorldUtils.markBlockForRender(location.x, location.y, location.z);
			}

			this.worldObj.spawnParticle("reddust", this.xCoord + this.moved.x + 0.5F, this.yCoord + this.moved.y + 0.5F, this.zCoord + this.moved.z + 0.5F,
					-0.4, 0, 1);
			return;
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

	private void writeMovedBlocks(Vector3i to, LinkedList<BlockInfo> blockInfos, Vector3i[] affectedLocations) {
		final WorldUtils we = new WorldUtils(this.worldObj);
		boolean anyBlocksLoaded = false;
		for (final BlockInfo block : blockInfos) {
			final Vector3i c = block.coord.add(getPowered());

			final Chunk chunkFrom = this.worldObj.getChunkFromBlockCoords(c.x, c.z);
			if (!chunkFrom.isChunkLoaded) {
				continue;
			}

			anyBlocksLoaded = true;
			we.setBlockMetadataAndTileEntityWithoutUpdate(c.x, c.y, c.z, block.blockid, block.metadata, block.entity, false);
			we.setNextBlockUpdate(c, block.blockid, block.nextUpdate);
		}

		// ensure client updated TE location even if it's moving out of loaded
		// chunks.
		this.xCoord = to.x;
		this.yCoord = to.y;
		this.zCoord = to.z;

		// remove from client if moved out of range
		if (!anyBlocksLoaded) {
			Motive.log(this.worldObj, "Moved out of range, unregistering mover");
			removeMover(this.worldObj, this);
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
				final Chunk chunkFrom = this.worldObj.getChunkFromBlockCoords(c.x, c.z);
				if (!chunkFrom.isChunkLoaded) {
					continue;
				}

				/*
				 * if (this.worldObj.isRemote) {
				 * we.world.markBlockForUpdate(c.x, c.y, c.z); } else {
				 */we.world.notifyBlockOfNeighborChange(c.x, c.y, c.z, we.world.getBlockId(c.x, c.y, c.z));
				// }
			}
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);

		Motive.log(this.worldObj, "writing tileEntityMover NBT " + toString());

		nbtTagCompound.setString("uid", getUid());
		nbtTagCompound.setBoolean("moving", this.moving);

		nbtTagCompound.setString("status", this.status);
		nbtTagCompound.setString("statusDetail", this.statusDetail);

		nbtTagCompound.setFloat("movedX", this.moved.x);
		nbtTagCompound.setFloat("movedY", this.moved.y);
		nbtTagCompound.setFloat("movedZ", this.moved.z);

		nbtTagCompound.setInteger("poweredX", getPowered().x);
		nbtTagCompound.setInteger("poweredY", getPowered().y);
		nbtTagCompound.setInteger("poweredZ", getPowered().z);

		writeBlockListToNBT(nbtTagCompound, "connectedBlocks", getConnectedBlocks().blocks, false);
	}
}
