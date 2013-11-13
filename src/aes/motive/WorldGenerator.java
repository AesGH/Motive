package aes.motive;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		switch (world.provider.dimensionId) {
		case -1:
			generateNether(world, random, chunkX * 16, chunkZ * 16);
			break;
		case 0:
			generateSurface(world, random, chunkX * 16, chunkZ * 16);
			break;
		case 1:
			generateEnd(world, random, chunkX * 16, chunkZ * 16);
			break;
		}
	}

	private void generateEnd(World world, Random random, int i, int j) {
	}

	private void generateNether(World world, Random random, int chunkX, int chunkZ) {
		final int firstBlockXCoord = chunkX + random.nextInt(16);
		final int firstBlockYCoord = random.nextInt(64);
		final int firstBlockZCoord = chunkZ + random.nextInt(16);

		final int blockId = world.getBlockId(firstBlockXCoord, firstBlockYCoord, firstBlockZCoord);
		if (blockId == 0) {
			if (random.nextInt(100) > 90) {
				world.setBlock(firstBlockXCoord, firstBlockYCoord, firstBlockZCoord, Motive.BlockMote.blockID);
			}
		} else if (blockId == Block.netherBrick.blockID) {
			world.setBlock(firstBlockXCoord, firstBlockYCoord, firstBlockZCoord, Motive.BlockVe.blockID);
		}
	}

	private void generateSurface(World world, Random random, int chunkX, int chunkZ) {
	}
}
