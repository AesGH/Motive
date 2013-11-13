package aes.motive;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import aes.utils.Vector3i;
import aes.utils.WorldUtils;

public class ConnectedBlocks {
	public class TooManyBlocksException extends Exception {
		private static final long serialVersionUID = 412087143138113297L;
	}

	Vector3i location;
	public Set<Vector3i> blocks;
	public static int MAX_BLOCKS_CAN_MOVE = 16384;

	private ConnectedBlocks() {
		this.blocks = new HashSet<Vector3i>();
	}

	public ConnectedBlocks(Vector3i location) {
		this.location = location;
		this.blocks = new HashSet<Vector3i>();
		this.blocks.add(location);
	}

	public ConnectedBlocks(Vector3i location, LinkedList<Vector3i> fixedBlockOffsets) {
		this(location);
		this.blocks.addAll(fixedBlockOffsets);
	}

	public ConnectedBlocks(World world, Vector3i location) {
		this(location);

		try {
			final LinkedList<Vector3i> checked = new LinkedList<Vector3i>();
			addConnectedBlocks(world, checked, this.location);
			return;
		} catch (final TooManyBlocksException e) {
		} catch (final CannotMoveAnotherMoverException e) {
		}
		this.blocks = new HashSet<Vector3i>();
		this.blocks.add(this.location);
	}

	public ConnectedBlocks add(Vector3i value) {
		final ConnectedBlocks result = new ConnectedBlocks();

		result.location = this.location.add(value);

		for (final Vector3i block : this.blocks) {
			result.blocks.add(block.add(value));
		}
		return result;
	}

	private void addConnectedBlocks(World world, LinkedList<Vector3i> checked, Vector3i coord) throws TooManyBlocksException, CannotMoveAnotherMoverException {
		if (checked.contains(coord))
			return;
		checked.add(coord);

		if (!canMoveBlock(world, coord))
			return;

		/*
		 * final int blockId = world.getBlockId(coord.x, coord.y, coord.z); if
		 * (blockId == Motive.BlockMover.blockID && checked.size() > 1) throw
		 * new CannotMoveAnotherMoverException();
		 */this.blocks.add(coord);
		for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			addConnectedBlocks(world, checked, coord.increment(direction));
			if (this.blocks.size() > MAX_BLOCKS_CAN_MOVE)
				throw new TooManyBlocksException();
		}
	}

	private boolean canMoveBlock(World world, Vector3i location) {
		return WorldUtils.isNonEmptyBlock(world, location);
	}
}
