package aes.motive;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import aes.utils.Vector3i;
import aes.utils.WorldUtils;

import com.google.common.collect.Queues;

public class ConnectedBlocks {
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
		final Set<Vector3i> checked = new HashSet<Vector3i>();
		addConnectedBlocks(world, checked, this.location, null);
	}

	private void addConnectedBlocks(World world, Set<Vector3i> checked, Vector3i location, Set<Vector3i> candidates) {
		final Queue<Vector3i> queue = Queues.newLinkedBlockingQueue();
		queue.add(location);
		while (!queue.isEmpty()) {
			location = queue.remove();
			if ((candidates == null || candidates.contains(location)) && !checked.contains(location)) {
				checked.add(location);
				if (canMoveBlock(world, location)) {
					this.blocks.add(location);
					if (this.blocks.size() > MAX_BLOCKS_CAN_MOVE)
						return;

					for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
						queue.add(location.increment(direction));
					}
				}
			}
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
		addConnectedBlocks(world, checked, this.location, candidates);
		return true;
	}
}
