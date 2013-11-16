package aes.motive;

import java.util.HashSet;
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

	public ConnectedBlocks(Vector3i location, Set<Vector3i> fixedBlockOffsets) {
		this(location);
		this.blocks.addAll(fixedBlockOffsets);
	}

	public ConnectedBlocks(World world, ConnectedBlocks connectedBlocks, Vector3i locationToToggle) {
		this(connectedBlocks.location, connectedBlocks.blocks);
		toggle(world, locationToToggle);
	}

	public ConnectedBlocks(World world, Vector3i location) {
		this(location);

		try {
			final Set<Vector3i> checked = new HashSet<Vector3i>();
			addConnectedBlocks(world, checked, this.location, null);
			return;
		} catch (final TooManyBlocksException e) {
		} catch (final CannotMoveAnotherMoverException e) {
		}
		this.blocks = new HashSet<Vector3i>();
		this.blocks.add(this.location);
	}

	private void addConnectedBlocks(World world, Set<Vector3i> checked, Vector3i location, Set<Vector3i> candidates) throws TooManyBlocksException,
			CannotMoveAnotherMoverException {
		if (candidates != null && !candidates.contains(location))
			return;
		if (checked.contains(location))
			return;
		checked.add(location);

		if (!canMoveBlock(world, location))
			return;

		this.blocks.add(location);
		if (this.blocks.size() > MAX_BLOCKS_CAN_MOVE)
			throw new TooManyBlocksException();

		for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			addConnectedBlocks(world, checked, location.increment(direction), candidates);
		}
	}

	private boolean canMoveBlock(World world, Vector3i location) {
		return WorldUtils.isNonEmptyBlock(world, location);
	}

	public ConnectedBlocks offset(Vector3i value) {
		final ConnectedBlocks result = new ConnectedBlocks();

		result.location = this.location.add(value);

		for (final Vector3i block : this.blocks) {
			result.blocks.add(block.add(value));
		}
		return result;
	}

	public boolean toggle(World world, Vector3i locationToToggle) {
		if (!this.blocks.contains(locationToToggle)) {
			this.blocks.add(locationToToggle);
			return true;
		}

		final Set<Vector3i> candidates = new HashSet<Vector3i>();
		candidates.addAll(this.blocks);
		candidates.remove(locationToToggle);

		this.blocks = new HashSet<Vector3i>();
		final Set<Vector3i> checked = new HashSet<Vector3i>();
		try {
			addConnectedBlocks(world, checked, this.location, candidates);
		} catch (final TooManyBlocksException e) {
		} catch (final CannotMoveAnotherMoverException e) {
		}
		return true;
	}
}
