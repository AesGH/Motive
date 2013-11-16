package aes.utils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class WorldUtils {
	private static TreeSet<?> pendingTickListEntries;
	private static Set<?> pendingTickListEntriesSet;

	static Field storageArraysField = PrivateFieldAccess.getField(Chunk.class, Obfuscation.getSrgName("storageArrays"));

	public static boolean containsChunk(Vector3i[] locations, Vector2i chunkLocation) {
		// return false;
		for (final Vector3i location : locations) {
			if (location.x >> 4 == chunkLocation.x && location.z >> 4 == chunkLocation.y)
				return true;
		}
		return false;
	}

	public static void dropItem(World world, int i, int j, int k, ItemStack ist) {
		final double d = 0.7D;
		final double x = world.rand.nextFloat() * d + (1.0D - d) * 0.5D;
		final double y = world.rand.nextFloat() * d + (1.0D - d) * 0.5D;
		final double z = world.rand.nextFloat() * d + (1.0D - d) * 0.5D;
		final EntityItem item = new EntityItem(world, i + x, j + y, k + z, ist);
		item.delayBeforeCanPickup = 10;
		world.spawnEntityInWorld(item);
	}

	public static Set<Vector2i> getChunksForLocations(Vector3i[] locations) {
		final Set<Vector2i> result = new HashSet<Vector2i>();
		for (final Vector3i location : locations) {
			result.add(new Vector2i(location.x >> 4, location.z >> 4));
		}
		return result;
	}

	public static boolean isNonEmptyBlock(World worldObj, Vector3i location) {
		if (worldObj.isAirBlock(location.x, location.y, location.z))
			return false;

		final int bid = worldObj.getBlockId(location.x, location.y, location.z);
		if (bid == Block.bedrock.blockID)
			return false;

		final Block block = Block.blocksList[bid];
		if (block == null || block.blockID != bid)
			return false;

		if (block.isBlockReplaceable(worldObj, location.x, location.y, location.z))
			return false;

		return true;
	}

	public static void markBlockForRender(int xCoord, int yCoord, int zCoord) {
		Minecraft.getMinecraft().renderGlobal.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	public World world;

	public WorldUtils(World world) {
		this.world = world;
		if (!world.isRemote) {
			if (pendingTickListEntries == null) {
				final WorldServer server = (WorldServer) world;
				pendingTickListEntries = (TreeSet<?>) PrivateFieldAccess.getValue(server, Obfuscation.getSrgName("pendingTickListEntriesTreeSet"));
				// Obfuscation.getFieldName("net.minecraft.world.Server",
				// "pendingTickListEntriesTreeSet", "Ljava/util/Set;"));
				pendingTickListEntriesSet = (Set<?>) PrivateFieldAccess.getValue(server, Obfuscation.getSrgName("pendingTickListEntriesHashSet"));
				// Obfuscation.getFieldName("net.minecraft.world.Server",
				// "pendingTickListEntriesHashSet", "Ljava/util/HashSet;"));
			}
		}
	}

	public long getBlockNextUpdate(World world, Vector3i block) {
		if (!world.isRemote && pendingTickListEntries != null) {
			final Iterator<?> iterator = pendingTickListEntries.iterator();
			while (iterator.hasNext()) {
				final NextTickListEntry entry = (NextTickListEntry) iterator.next();
				if (entry.xCoord == block.x && entry.yCoord == block.y && entry.zCoord == block.z)
					return entry.scheduledTime;
			}
		}
		return -1;
	}

	public void removeBlockScheduledUpdate(World world, Vector3i block) {
		if (!world.isRemote && pendingTickListEntries != null) {
			final Iterator<?> iterator = pendingTickListEntries.iterator();
			while (iterator.hasNext()) {
				final NextTickListEntry entry = (NextTickListEntry) iterator.next();
				if (entry.xCoord == block.x && entry.yCoord == block.y && entry.zCoord == block.z) {
					pendingTickListEntries.remove(entry);
					pendingTickListEntriesSet.remove(entry);
					return;
				}
			}
		}
	}

	public boolean setBlockMetadataAndTileEntityWithoutUpdate(int x, int y, int z, int id, int md, TileEntity par4TileEntity, boolean updateLighting) {
		if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000 || y < 0 || y >= 256)
			return false;

		// world.setBlockMetadataWithNotify(par1, par2, par3, par4, par5);
		// world.setBlockTileEntity(par1, par2, par3, par4TileEntity);

		final Chunk chunk = this.world.getChunkFromChunkCoords(x >> 4, z >> 4);
		final boolean result = setChunkBlockIDWithMetadata(chunk, x & 15, y, z & 15, id, md);

		if (updateLighting) {
			this.world.theProfiler.startSection("checkLight");
			this.world.updateAllLightTypes(x, y, z);
			this.world.theProfiler.endSection();
		}
		// world.markBlockForUpdate(par1, par2, par3);

		if (par4TileEntity == null)
			return result;

		// if (par4TileEntity.canUpdate())
		// {
		// List dest = scanningTileEntities ? addedTileEntityList :
		// loadedTileEntityList;
		// dest.add(par4TileEntity);
		// }

		/*
		 * par4TileEntity.xCoord = x; par4TileEntity.yCoord = y;
		 * par4TileEntity.zCoord = z; par4TileEntity.validate();
		 */
		chunk.setChunkBlockTileEntity(x & 15, y, z & 15, par4TileEntity);

		return result;
	}

	public boolean setChunkBlockIDWithMetadata(Chunk chunk, int x, int y, int z, int newBid, int newMd) {
		final int var6 = z << 4 | x;

		if (y >= chunk.precipitationHeightMap[var6] - 1) {
			chunk.precipitationHeightMap[var6] = -999;
		}

		final int var7 = chunk.heightMap[var6];
		final int bid = chunk.getBlockID(x, y, z);
		final int md = chunk.getBlockMetadata(x, y, z);

		if (bid == newBid && md == newMd)
			return false;

		ExtendedBlockStorage[] storageArrays;
		try {
			storageArrays = (ExtendedBlockStorage[]) storageArraysField.get(chunk);
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}

		if (y >> 4 >= storageArrays.length || y >> 4 < 0)
			return false;

		ExtendedBlockStorage ebs = storageArrays[y >> 4];
		boolean var11 = false;

		if (ebs == null) {
			if (newBid == 0)
				return false;

			ebs = storageArrays[y >> 4] = new ExtendedBlockStorage(y >> 4 << 4, !this.world.provider.hasNoSky);
			var11 = y >= var7;
		}

		final int worldRelativeX = chunk.xPosition * 16 + x;
		final int worldRelativeZ = chunk.zPosition * 16 + z;

		// if (bid != 0 && !world.isRemote)
		// {
		// Block.blocksList[bid].onSetBlockIDWithMetaData(this.worldObj,
		// worldRelativeX, y, worldRelativeZ, md);
		// }

		ebs.setExtBlockID(x, y & 15, z, newBid);

		if (bid != 0) {
			// if (!world.isRemote)
			// {
			// Block.blocksList[bid].breakBlock(world, worldRelativeX, y,
			// worldRelativeZ, bid, md);
			// }
			// else
			if (Block.blocksList[bid] != null && Block.blocksList[bid].hasTileEntity(md)) {
				final TileEntity te = this.world.getBlockTileEntity(worldRelativeX, y, worldRelativeZ);
				if (te != null) // && te.shouldRefresh(bid, newBid, md, newMd,
								// world, worldRelativeX, y, worldRelativeZ))
				{
					this.world.removeBlockTileEntity(worldRelativeX, y, worldRelativeZ);
				}
			}
		}

		if (ebs.getExtBlockID(x, y & 15, z) != newBid)
			return false;

		ebs.setExtBlockMetadata(x, y & 15, z, newMd);

		if (var11) {
			chunk.generateSkylightMap();
		}
		/*
		 * else { if (chunk.getBlockLightOpacity(x, y, z) > 0) { if (y >= var7)
		 * { chunk.relightBlock(x, y + 1, z); } } else if (y == var7 - 1) {
		 * chunk.relightBlock(x, y, z); }
		 * 
		 * chunk.propagateSkylightOcclusion(x, z); }
		 */

		// TileEntity var14;
		// if (newBid != 0)
		// {
		// if (!world.isRemote)
		// {
		// Block.blocksList[newBid].onBlockAdded(world, worldRelativeX, y,
		// worldRelativeZ);
		// }
		//
		// if (Block.blocksList[newBid] != null &&
		// Block.blocksList[newBid].hasTileEntity(newMd))
		// {
		// var14 = this.getChunkBlockTileEntity(x, y, _z);
		//
		// if (var14 == null)
		// {
		// var14 = Block.blocksList[newBid].createTileEntity(this.worldObj,
		// newMd);
		// this.worldObj.setBlockTileEntity(worldRelativeX, y, worldRelativeZ,
		// var14);
		// }
		//
		// if (var14 != null)
		// {
		// var14.updateContainingBlockInfo();
		// var14.blockMetadata = newMd;
		// }
		// }
		// }

		chunk.isModified = true;
		return true;
	}

	public void setNextBlockUpdate(Vector3i position, int blockId, long blockNextUpdate) {
		final long time = this.world.getTotalWorldTime();
		if (blockNextUpdate != -1) {
			this.world.scheduleBlockUpdate(position.x, position.y, position.z, blockId, (int) (blockNextUpdate - time));
		}
	}
}
