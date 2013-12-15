package aes.motive.core.asm;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerInstance;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import aes.motive.Motive;
import aes.motive.tileentity.TileEntityMover;
import aes.motive.tileentity.TileEntityMoverBase;
import aes.utils.Obfuscation;
import aes.utils.PrivateFieldAccess;
import aes.utils.Vector2i;
import aes.utils.Vector3f;
import aes.utils.Vector3i;

public class RenderHook {
	public static RenderHook INSTANCE = new RenderHook();

	Block modifyingBlock = null;

	TileEntityMoverBase modifyingMover = null;

	public List<Tessellator> movedTessellators = null;

	private int chunkFromX;

	private int chunkFromY;

	private int chunkFromZ;

	Queue<TileEntityMoverBase> moversToRemove = new ConcurrentLinkedQueue<TileEntityMoverBase>();

	private Vector3f viewEntityOffset;

	Vector3f entityOffset = new Vector3f();

	Entity offsettingEntity;

	private double offsetTessellatorX;

	// Timer timer;

	private double offsetTessellatorY;
	private double offsetTessellatorZ;
	private int offsetted;

	private int offsettedTE;

	private RenderHook() {
		// this.timer = (Timer)
		// PrivateFieldAccess.getValue(Minecraft.getMinecraft(),
		// Obfuscation.getFieldName("", "timer", ""));
	}

	public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3 par5Vec3, Vec3 par6Vec3, boolean par3a, boolean par4a,
			int k1, int l1) {
		int blockId = par1World.getBlockId(par2, par3, par4);
		if (blockId != 0) {
			final Block block = Block.blocksList[blockId];

			if (block != null && (!par4a || block == null || block.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4) != null) && k1 > 0
					&& block.canCollideCheck(l1, par3a)) {

				final MovingObjectPosition result = block.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3);
				if (result != null)
					return result;
			}
		}
		final int x = par2;
		final int y = par3;
		final int z = par4;

		for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			par2 = x + direction.offsetX;
			par3 = y + direction.offsetY;
			par4 = z + direction.offsetZ;

			blockId = par1World.getBlockId(par2, par3, par4);
			if (blockId != 0) {
				final Block block = Block.blocksList[blockId];
				if (block != null) {
					if (block.canCollideCheck(par1World.getBlockMetadata(par2, par3, par4), par3a)) {
						final MovingObjectPosition result = block.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3);
						if (result != null)
							return result;
					}
				}
			}
		}
		return null;
	}

	private void drawHighlightCube(TileEntityMover tileEntityMover, int x, int y, int z, float density) {
		final Vector3i location = new Vector3i(x, y, z);

		final boolean[] isMovingNeighbour = new boolean[ForgeDirection.VALID_DIRECTIONS.length];

		for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			isMovingNeighbour[direction.ordinal()] = tileEntityMover.isMovingBlock(location.increment(direction));
		}

		for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			renderHighlightFace((double) x + tileEntityMover.moved.x, (double) y + tileEntityMover.moved.y, (double) z + tileEntityMover.moved.z, direction,
					isMovingNeighbour, density);
		}
	}

	TileEntityMoverBase getMoverForEntity(Entity entity) {
		if (entity == null)
			return null;

		TileEntityMoverBase tileEntityMoverBase = null;
		for (int i = -1; tileEntityMoverBase == null && i <= 3; i++) {
			tileEntityMoverBase = RenderHook.INSTANCE.getMoverMoving(entity.worldObj, (int) Math.floor(entity.posX),
					(int) Math.floor(entity.posY - entity.yOffset - i), (int) Math.floor(entity.posZ));
		}
		return tileEntityMoverBase;
	}

	public TileEntityMoverBase getMoverMoving(World world, int x, int y, int z) {
		// Minecraft.getMinecraft().mcProfiler.startSection("getMoverMoving");

		try {
			// if(false)
			{
				final Vector3i coord = new Vector3i(x, y, z);
				for (final TileEntityMoverBase mover : TileEntityMoverBase.getMovers(world).values()) {
					if (world.getBlockTileEntity(mover.xCoord, mover.yCoord, mover.zCoord) != mover) {
						if(!mover.areAnyConnectedBlocksLoaded())
						{
							this.moversToRemove.add(mover);
							continue;
						}
					}
					if (mover.getConnectedBlocks().blocks.contains(coord))
						return mover;
				}
			}
			return null;
		} catch (final ConcurrentModificationException e) {
			return getMoverMoving(world, x, y, z);
		} finally {

			for (final TileEntityMoverBase tileEntityMoverBase : this.moversToRemove) {
				Motive.log(world, "removing because tileEntity no longer at location");
				TileEntityMoverBase.removeMover(tileEntityMoverBase);
			}
			this.moversToRemove.clear();
			// Minecraft.getMinecraft().mcProfiler.endSection();
		}
	}

	private boolean highlightIfConnected(boolean hasStartedDrawing, int glRenderList, int x, int y, int z) {
		final World world = Minecraft.getMinecraft().theWorld;
		final TileEntityMoverBase tileEntityMoverBase = getMoverMoving(world, x, y, z);
		if (tileEntityMoverBase instanceof TileEntityMover) {
			final TileEntityMover tileEntityMover = (TileEntityMover) tileEntityMoverBase;
			if (tileEntityMover.getHighlight() || tileEntityMover.getFlashPct() != 0) {

				if (
				// !hasStartedDrawing &&
				!Tessellator.instance.isDrawing) {
					GL11.glNewList(glRenderList + 1, GL11.GL_COMPILE);
					GL11.glPushMatrix();

					final int posXClip = this.chunkFromX & 1023;
					final int posYClip = this.chunkFromY;
					final int posZClip = this.chunkFromZ & 1023;

					GL11.glTranslatef(posXClip, posYClip, posZClip);

					final float f = 1.000001F;
					GL11.glTranslatef(-8.0F, -8.0F, -8.0F);
					GL11.glScalef(f, f, f);
					GL11.glTranslatef(8.0F, 8.0F, 8.0F);

					Tessellator.instance.startDrawingQuads();
					Tessellator.instance.setTranslation(-this.chunkFromX, -this.chunkFromY, -this.chunkFromZ);
				}

				drawHighlightCube(tileEntityMover, x, y, z, tileEntityMover.getHighlight() ? 1f : tileEntityMover.getFlashPct() / 50f);
				return true;
			}
		}
		return false;
	}

	public boolean highlightIfConnected(boolean hasStartedDrawing, int glRenderList, int pass, int chunkFromX, int chunkFromY, int chunkFromZ) {
		if (pass == 0)
			return true;

		this.chunkFromX = chunkFromX;
		this.chunkFromY = chunkFromY;
		this.chunkFromZ = chunkFromZ;
		final int chunkToX = chunkFromX + 16;
		final int chunkToY = chunkFromY + 16;
		final int chunkToZ = chunkFromZ + 16;

		boolean result = false;
		for (int y = chunkFromY; y < chunkToY; ++y) {
			for (int z = chunkFromZ; z < chunkToZ; ++z) {
				for (int x = chunkFromX; x < chunkToX; ++x) {
					result |= highlightIfConnected(hasStartedDrawing, glRenderList, x, y, z);
					if (result) {
						hasStartedDrawing = true;
					}
				}
			}
		}
		return result;
	}

	public boolean isBlockMoving(int par1, int par2, int par3) {
		return getMoverMoving(Minecraft.getMinecraft().theWorld, par1, par2, par3) != null;
	}

	public void modifyMovingBlockBounds(World world, int x, int y, int z) {
		final int blockId = world.getBlockId(x, y, z);
		final Block block = Block.blocksList[blockId];
		final TileEntityMoverBase mover = getMoverMoving(Minecraft.getMinecraft().theWorld, x, y, z);
		if (mover != null) {
			this.modifyingBlock = block;
			this.modifyingMover = mover;

			block.setBlockBounds((float) block.getBlockBoundsMinX() + mover.moved.x, (float) block.getBlockBoundsMinY() + mover.moved.y,
					(float) block.getBlockBoundsMinZ() + mover.moved.z, (float) block.getBlockBoundsMaxX() + mover.moved.x, (float) block.getBlockBoundsMaxY()
							+ mover.moved.y, (float) block.getBlockBoundsMaxZ() + mover.moved.z);
		}
	}

	public void offsetEntityPosition(Entity entity) {
		this.offsettingEntity = entity;

		final TileEntityMoverBase tileEntityMoverBase = getMoverForEntity(entity);
		if (tileEntityMoverBase != null) {
			this.entityOffset = new Vector3f(tileEntityMoverBase.moved);
			entity.posX += this.entityOffset.x;
			entity.posY += this.entityOffset.y;
			entity.posZ += this.entityOffset.z;

			entity.lastTickPosX += this.entityOffset.x;
			entity.lastTickPosY += this.entityOffset.y;
			entity.lastTickPosZ += this.entityOffset.z;
		} else {
			this.entityOffset = new Vector3f();
		}
	}

	public void offsetMovingTesselator(int x, int y, int z) {
		if (++this.offsetted > 1 || this.offsettedTE > 0)
			return;

		final TileEntityMoverBase mover = getMoverMoving(Minecraft.getMinecraft().theWorld, x, y, z);
		if (mover == null)
			return;

		/*
		 * Vector3f lastMoved = mover.getMovedPreviousTick(); final Vector3f
		 * moved = mover.moved; if (lastMoved == null) { lastMoved =
		 * mover.moved; }
		 */
		this.offsetTessellatorX = mover.moved.x;
		this.offsetTessellatorY = mover.moved.y;
		this.offsetTessellatorZ = mover.moved.z;

		Tessellator.instance.addTranslation((float) this.offsetTessellatorX, (float) this.offsetTessellatorY, (float) this.offsetTessellatorZ);
	}

	public void offsetOtherEntityPosition(Entity entity) {
		if (Minecraft.getMinecraft().thePlayer != entity) {
			offsetEntityPosition(entity);
		}
	}

	public void offsetViewEntityPosition() {
		offsetEntityPosition(Minecraft.getMinecraft().renderViewEntity);
		this.viewEntityOffset = this.entityOffset;
	}

	public MovingObjectPosition rayTraceBlocks_do_do(World world, Vec3 from, Vec3 to, boolean includeLiquids, boolean includeEmpty) {
		if (!Double.isNaN(from.xCoord) && !Double.isNaN(from.yCoord) && !Double.isNaN(from.zCoord)) {
			if (!Double.isNaN(to.xCoord) && !Double.isNaN(to.yCoord) && !Double.isNaN(to.zCoord)) {
				final int xFrom = MathHelper.floor_double(to.xCoord);
				final int yFrom = MathHelper.floor_double(to.yCoord);
				final int zFrom = MathHelper.floor_double(to.zCoord);
				int xTo = MathHelper.floor_double(from.xCoord);
				int yTo = MathHelper.floor_double(from.yCoord);
				int zTo = MathHelper.floor_double(from.zCoord);
				int k1 = world.getBlockId(xTo, yTo, zTo);
				final int l1 = world.getBlockMetadata(xTo, yTo, zTo);
				MovingObjectPosition movingobjectposition = collisionRayTrace(world, xTo, yTo, zTo, from, to, includeLiquids, includeEmpty, k1, l1);

				if (movingobjectposition != null)
					return movingobjectposition;

				k1 = 200;

				while (k1-- >= 0) {
					if (Double.isNaN(from.xCoord) || Double.isNaN(from.yCoord) || Double.isNaN(from.zCoord))
						return null;

					if (xTo == xFrom && yTo == yFrom && zTo == zFrom)
						return null;

					boolean xVarying = true;
					boolean yVarying = true;
					boolean zVarying = true;
					double nextX = 999.0D;
					double nextY = 999.0D;
					double nextZ = 999.0D;

					if (xFrom > xTo) {
						nextX = xTo + 1.0D;
					} else if (xFrom < xTo) {
						nextX = xTo + 0.0D;
					} else {
						xVarying = false;
					}

					if (yFrom > yTo) {
						nextY = yTo + 1.0D;
					} else if (yFrom < yTo) {
						nextY = yTo + 0.0D;
					} else {
						yVarying = false;
					}

					if (zFrom > zTo) {
						nextZ = zTo + 1.0D;
					} else if (zFrom < zTo) {
						nextZ = zTo + 0.0D;
					} else {
						zVarying = false;
					}

					double d3 = 999.0D;
					double d4 = 999.0D;
					double d5 = 999.0D;
					final double d6 = to.xCoord - from.xCoord;
					final double d7 = to.yCoord - from.yCoord;
					final double d8 = to.zCoord - from.zCoord;

					if (xVarying) {
						d3 = (nextX - from.xCoord) / d6;
					}

					if (yVarying) {
						d4 = (nextY - from.yCoord) / d7;
					}

					if (zVarying) {
						d5 = (nextZ - from.zCoord) / d8;
					}

					byte b0;

					if (d3 < d4 && d3 < d5) {
						if (xFrom > xTo) {
							b0 = 4;
						} else {
							b0 = 5;
						}

						from.xCoord = nextX;
						from.yCoord += d7 * d3;
						from.zCoord += d8 * d3;
					} else if (d4 < d5) {
						if (yFrom > yTo) {
							b0 = 0;
						} else {
							b0 = 1;
						}

						from.xCoord += d6 * d4;
						from.yCoord = nextY;
						from.zCoord += d8 * d4;
					} else {
						if (zFrom > zTo) {
							b0 = 2;
						} else {
							b0 = 3;
						}

						from.xCoord += d6 * d5;
						from.yCoord += d7 * d5;
						from.zCoord = nextZ;
					}

					final Vec3 vec32 = world.getWorldVec3Pool().getVecFromPool(from.xCoord, from.yCoord, from.zCoord);
					xTo = (int) (vec32.xCoord = MathHelper.floor_double(from.xCoord));

					if (b0 == 5) {
						--xTo;
						++vec32.xCoord;
					}

					yTo = (int) (vec32.yCoord = MathHelper.floor_double(from.yCoord));

					if (b0 == 1) {
						--yTo;
						++vec32.yCoord;
					}

					zTo = (int) (vec32.zCoord = MathHelper.floor_double(from.zCoord));

					if (b0 == 3) {
						--zTo;
						++vec32.zCoord;
					}

					movingobjectposition = collisionRayTrace(world, xTo, yTo, zTo, from, to, includeLiquids, includeEmpty, k1, l1);

					if (movingobjectposition != null)
						return movingobjectposition;
				}

				return null;
			} else
				return null;
		} else
			return null;
	}

	public void renderHighlightFace(double x, double y, double z, ForgeDirection direction, boolean[] isMovingNeighbour, float density) {
		if (isMovingNeighbour[direction.ordinal()])
			return;

		final double gap = 0.1D;

		final double renderMinX = isMovingNeighbour[ForgeDirection.WEST.ordinal()] ? 0 : -gap;
		final double renderMaxX = 1 + (isMovingNeighbour[ForgeDirection.EAST.ordinal()] ? 0 : gap);
		final double renderMinY = isMovingNeighbour[ForgeDirection.DOWN.ordinal()] ? 0 : -gap;
		final double renderMaxY = 1 + (isMovingNeighbour[ForgeDirection.UP.ordinal()] ? 0 : gap);
		final double renderMinZ = isMovingNeighbour[ForgeDirection.NORTH.ordinal()] ? 0 : -gap;
		final double renderMaxZ = 1 + (isMovingNeighbour[ForgeDirection.SOUTH.ordinal()] ? 0 : gap);

		final double x1 = direction.offsetX > 0 ? x + renderMaxX : x + renderMinX;
		final double x2 = direction.offsetX == 0 ? x + renderMaxX : x1;
		final double y1 = direction.offsetY > 0 ? y + renderMaxY : y + renderMinY;
		final double y2 = direction.offsetY == 0 ? y + renderMaxY : y1;
		final double z1 = direction.offsetZ > 0 ? z + renderMaxZ : z + renderMinZ;
		final double z2 = direction.offsetZ == 0 ? z + renderMaxZ : z1;

		final Tessellator tessellator = Tessellator.instance;

		tessellator.draw();
		tessellator.startDrawingQuads();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		Tessellator.instance.setBrightness(0x00F000F0);

		// GL11.glColorMask(0xff, 0x00, 00, 0x40);
		tessellator.setColorRGBA(0xFF, 0xff, 0xff, (int) (density * 0x40));

		tessellator.setNormal(-direction.offsetX, -direction.offsetY, -direction.offsetZ);

		if (direction.offsetX != 0) {
			tessellator.addVertex(x1, y1, z1);
			tessellator.addVertex(x1, y1, z2);
			tessellator.addVertex(x2, y2, z2);
			tessellator.addVertex(x2, y2, z1);
		} else if (direction.offsetZ != 0) {
			tessellator.addVertex(x1, y1, z1);
			tessellator.addVertex(x2, y1, z2);
			tessellator.addVertex(x2, y2, z2);
			tessellator.addVertex(x1, y2, z1);
		} else {
			tessellator.addVertex(x1, y1, z1);
			tessellator.addVertex(x1, y2, z2);
			tessellator.addVertex(x2, y1, z2);
			tessellator.addVertex(x2, y2, z1);
		}

		tessellator.draw();
		tessellator.startDrawingQuads();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void renderTileEntity(TileEntitySpecialRenderer renderer, TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
		final int xPos = (int) (x + TileEntityRenderer.staticPlayerX);
		final int yPos = (int) (y + TileEntityRenderer.staticPlayerY);
		final int zPos = (int) (z + TileEntityRenderer.staticPlayerZ);
		final TileEntityMoverBase mover = getMoverMoving(Minecraft.getMinecraft().theWorld, xPos, yPos, zPos);
		double renderX = x;
		double renderY = y;
		double renderZ = z;

		if (++this.offsettedTE > 1) {
			renderer.renderTileEntityAt(tileEntity, renderX, renderY, renderZ, partialTickTime);
			return;
		}

		try {
			if (mover != null) {
				renderX += mover.moved.x;
				renderY += mover.moved.y;
				renderZ += mover.moved.z;
			}
			renderer.renderTileEntityAt(tileEntity, renderX, renderY, renderZ, partialTickTime);
		} finally {
			--this.offsettedTE;
			if (this.offsettedTE < 0) {
				this.offsettedTE = 0;
			}
		}
	}

	public void resetEntityPosition(Entity entity) {
		if (entity == null)
			return;
		entity.posX -= this.entityOffset.x;
		entity.posY -= this.entityOffset.y;
		entity.posZ -= this.entityOffset.z;

		entity.lastTickPosX -= this.entityOffset.x;
		entity.lastTickPosY -= this.entityOffset.y;
		entity.lastTickPosZ -= this.entityOffset.z;
	}

	public void resetMovingBlockBounds(World world, int x, int y, int z) {
		final int blockId = world.getBlockId(x, y, z);
		final Block block = Block.blocksList[blockId];
		if (this.modifyingBlock == block) {
			final TileEntityMoverBase mover = this.modifyingMover;
			block.setBlockBounds((float) block.getBlockBoundsMinX() - mover.moved.x, (float) block.getBlockBoundsMinY() - mover.moved.y,
					(float) block.getBlockBoundsMinZ() - mover.moved.z, (float) block.getBlockBoundsMaxX() - mover.moved.x, (float) block.getBlockBoundsMaxY()
							- mover.moved.y, (float) block.getBlockBoundsMaxZ() - mover.moved.z);

			this.modifyingBlock = null;
			this.modifyingMover = null;
		}
	}

	public void resetMovingTesselator() {
		if (--this.offsetted > 0)
			return;
		if (this.offsetted < 0) {
			this.offsetted = 0;
		}

		// if (this.movedTessellators != null) {
		// for (final Tessellator movedTessellator : this.movedTessellators) {
		Tessellator.instance.addTranslation((float) -this.offsetTessellatorX, (float) -this.offsetTessellatorY, (float) -this.offsetTessellatorZ);
		// }
		// }
		this.offsetTessellatorX = this.offsetTessellatorY = this.offsetTessellatorZ = 0;

		// this.movedTessellators = null;
	}

	public void resetOtherEntityPosition(Entity entity) {
		if (Minecraft.getMinecraft().thePlayer != entity) {
			resetEntityPosition(entity);
		}
	}

	public void resetViewEntityPosition() {
		this.offsettingEntity = Minecraft.getMinecraft().renderViewEntity;
		this.entityOffset = this.viewEntityOffset;
		resetEntityPosition(Minecraft.getMinecraft().renderViewEntity);
	}

	@SuppressWarnings("unchecked")
	public void updateWorldRendererTileEntities() {
		final RenderGlobal renderGlobal = Minecraft.getMinecraft().renderGlobal;

		renderGlobal.tileEntities.clear();
		for (final WorldRenderer renderer : (WorldRenderer[]) PrivateFieldAccess.getValue(renderGlobal, Obfuscation.getSrgName("worldRenderers"))) {
			if (renderer != null && renderer.tileEntityRenderers != null) {
				renderGlobal.tileEntities.addAll(renderer.tileEntityRenderers);
			}
		}
	}

	public boolean worldRendererContainsMovingBlocks(WorldRenderer worldrenderer) {
//		return false;
		return TileEntityMover.worldRenderersToUpdate.remove(worldrenderer);
		
/*		// worldrenderer.worldObj.theProfiler.startSection("checkContainsMoving");
		for (final TileEntityMoverBase tileEntityMoverBase : TileEntityMoverBase.getMovers(worldrenderer.worldObj).values()) {
			
			if (tileEntityMoverBase.moving 
					//&& tileEntityMoverBase.getForcingSimultaneousRenderering()
					&& tileEntityMoverBase.getAffectedWorldRenderers().contains(worldrenderer))
				// worldrenderer.worldObj.theProfiler.endSection();
				return true;
		}
		// worldrenderer.worldObj.theProfiler.endSection();
		return false;
*/	}

	public void updateEntity(Entity par1Entity) {
        if(par1Entity instanceof EntityPlayerMP)
        {
        	EntityPlayerMP player = (EntityPlayerMP)par1Entity;
            
            
        	/*            for(EntityPlayerMP par1EntityPlayerMP : (List<EntityPlayerMP>)this.players)
            {
    */            
            // monkey


            
            PlayerManager playerManager = player.getServerForPlayer().getPlayerManager();
//            playerManager.filterChunkLoadQueue(this);
            for(TileEntityMoverBase mover : TileEntityMoverBase.getMovers(player.worldObj).values())
            {
            	boolean containsAnyMoving = false;
            	for(Vector2i chunkLocation : mover.affectedChunks)
            	{
            		if(playerManager.isPlayerWatchingChunk(player, chunkLocation.x, chunkLocation.y) || player.loadedChunks.contains(new ChunkCoordIntPair(chunkLocation.x,  chunkLocation.y)))
            		{
            			if(chunkLocation.distanceTo(new Vector2i((int)player.posX >> 4, (int)player.posZ >> 4)) < 18)
            			{
            			containsAnyMoving = true;
            			break;
            			}
            		}
            	}
            	
            	if(containsAnyMoving)
            	{
            		Motive.log(player.worldObj, "contains a mover with " + mover.affectedChunks.size() + " attatched chunks");
            		
                	for(Vector2i chunkLocation : mover.affectedChunks)
                	{
                		if(!(playerManager.isPlayerWatchingChunk(player, chunkLocation.x, chunkLocation.y) || player.loadedChunks.contains(new ChunkCoordIntPair(chunkLocation.x,  chunkLocation.y))))
                		{
                			playerManager.getOrCreateChunkWatcher(chunkLocation.x, chunkLocation.y, true).addPlayer(player);
//                			playerManager.addPlayer(this);
//                			this.loadedChunks.add(new ChunkCoordIntPair(chunkLocation.x,  chunkLocation.y));
                    		Motive.log(player.worldObj, "adding player as watching chunk: " + chunkLocation);
                		}
                	}
            	}
            	else
            	{
            		Motive.log(player.worldObj, "doesn't contain a mover with " + mover.affectedChunks.size() + " attatched chunks");
            		
                	for(Vector2i chunkLocation : mover.affectedChunks)
                	{
                		if((playerManager.isPlayerWatchingChunk(player, chunkLocation.x, chunkLocation.y) || player.loadedChunks.contains(new ChunkCoordIntPair(chunkLocation.x,  chunkLocation.y))))
                		{
                			PlayerInstance chunkWatcher = playerManager.getOrCreateChunkWatcher(chunkLocation.x, chunkLocation.y, false);
                			if(chunkWatcher != null)
                			{
							chunkWatcher.removePlayer(player);
//                			playerManager.addPlayer(this);
//                			this.loadedChunks.add(new ChunkCoordIntPair(chunkLocation.x,  chunkLocation.y));
                    		Motive.log(player.worldObj, "removed player as watching chunk: " + chunkLocation);
                			}
                		}
                	}
            	}
            	
            }
        }
		
	}
}
