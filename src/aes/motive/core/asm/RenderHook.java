package aes.motive.core.asm;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import aes.motive.tileentity.TileEntityMoverBase;
import aes.utils.Obfuscation;
import aes.utils.PrivateFieldAccess;
import aes.utils.Vector2i;
import aes.utils.Vector3i;

public class RenderHook {
	public static RenderHook INSTANCE = new RenderHook();

	public static boolean worldRendererContainsMovingBlocks(WorldRenderer worldrenderer) {
		worldrenderer.worldObj.theProfiler.startSection("checkContainsMoving");

		for (final TileEntityMoverBase tileEntityMoverBase : TileEntityMoverBase.getMovers(Minecraft.getMinecraft().theWorld).values()) {
			if (tileEntityMoverBase.moving && tileEntityMoverBase.affectedChunks.contains(new Vector2i(worldrenderer.posX >> 4, worldrenderer.posZ >> 4)))
			// if(WorldUtils.containsChunk(tileEntityMoverBase.getAffectedBlocks(),
			// chunkLocation))
			{
				worldrenderer.worldObj.theProfiler.endSection();
				return false;
			}
		}
		worldrenderer.worldObj.theProfiler.endSection();
		return false;
	}

	// public Map<Vector3i, TileEntityMoverBase> movers = new HashMap<Vector3i,
	// TileEntityMoverBase>();

	Block modifyingBlock = null;

	TileEntityMoverBase modifyingMover = null;

	public List<Tessellator> movedTessellators = null;

	private RenderHook() {
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

				final MovingObjectPosition result = block.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3);
				if (result != null)
					return result;
			}
		}
		return null;
	}

	public TileEntityMoverBase getMoverMoving(World world, int x, int y, int z) {
		Minecraft.getMinecraft().mcProfiler.startSection("getMoverMoving");

		try {
			// if(false)
			{
				final Vector3i coord = new Vector3i(x, y, z);
				for (final TileEntityMoverBase mover : TileEntityMoverBase.getMovers(world).values()) {
					if (mover.moving && mover.getConnectedBlocks().blocks.contains(coord))
						// return null;
						return mover;
				}
			}
			return null;
		} catch (final ConcurrentModificationException e) {
			return getMoverMoving(world, x, y, z);
		} finally {
			Minecraft.getMinecraft().mcProfiler.endSection();
		}
	}

	public boolean isBlockMoving(int par1, int par2, int par3) {
		// return false;
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

	public void modifyMovingTesselator(int x, int y, int z) {
		final TileEntityMoverBase mover = getMoverMoving(Minecraft.getMinecraft().theWorld, x, y, z);
		if (mover != null) {
			this.modifyingMover = mover;
			this.movedTessellators = new LinkedList<Tessellator>();

			if (!this.movedTessellators.contains(Tessellator.instance)) {
				this.movedTessellators.add(Tessellator.instance);
				Tessellator.instance.addTranslation(mover.moved.x, mover.moved.y, mover.moved.z);
			}

			return;
		}
		this.movedTessellators = null;
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

	public void renderTileEntity(TileEntitySpecialRenderer renderer, TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
		final TileEntityMoverBase blocks = getMoverMoving(Minecraft.getMinecraft().theWorld, (int) (x + TileEntityRenderer.staticPlayerX),
				(int) (y + TileEntityRenderer.staticPlayerY), (int) (z + TileEntityRenderer.staticPlayerZ));
		if (blocks != null) {
			x += blocks.moved.x;
			y += blocks.moved.y;
			z += blocks.moved.z;
		}
		renderer.renderTileEntityAt(tileEntity, x, y, z, partialTickTime);
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
		if (this.movedTessellators != null) {
			for (final Tessellator movedTessellator : this.movedTessellators) {
				movedTessellator.addTranslation(-this.modifyingMover.moved.x, -this.modifyingMover.moved.y, -this.modifyingMover.moved.z);
			}
		}
		this.movedTessellators = null;
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

}
