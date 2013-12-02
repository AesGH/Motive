package aes.motive;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
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
		this.blocks = new LinkedHashSet<Vector3i>();
	}

	public ConnectedBlocks(Vector3i location) {
		this.location = location;
		this.blocks = new LinkedHashSet<Vector3i>();
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
		final Set<Vector3i> checked = new LinkedHashSet<Vector3i>();
		addConnectedBlocks(world, checked, this.location, null);
	}

	private void addConnectedBlocks(World world, Set<Vector3i> checked, Vector3i location, Set<Vector3i> candidates) {
		final Queue<Vector3i> queue = Queues.newLinkedBlockingQueue();
		queue.add(location);
		while (!queue.isEmpty()) {
			location = queue.remove();
			if ((candidates == null || candidates.contains(location)) && !checked.contains(location)) {
				checked.add(location);
				if (canMoveBlock(candidates, world, location)) {
					this.blocks.add(location);
					if (this.blocks.size() > MAX_BLOCKS_CAN_MOVE)
						return;

					for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
						final Vector3i neighbour = location.increment(direction);
						if ((candidates == null || candidates.contains(neighbour)) && !checked.contains(neighbour)) {
							queue.add(neighbour);
						}
					}
				}
			}
		}
	}

	private boolean canMoveBlock(Set<Vector3i> candidates, World world, Vector3i location) {
		return candidates != null && candidates.contains(location) || WorldUtils.isNonEmptyBlock(world, location);
	}

	private List<Vector3i> getAttachedBlocks(World world, Vector3i location) {
		final int blockId = world.getBlockId(location.x, location.y, location.z);
		Motive.log("block: " + blockId);
		if (blockId == Block.doorIron.blockID || blockId == Block.doorWood.blockID) {
			if ((world.getBlockMetadata(location.x, location.y, location.z) & 8) == 0)
				return Arrays.asList(location.increment(ForgeDirection.UP));
			return Arrays.asList(location.increment(ForgeDirection.DOWN));
		}

		if (isPistonBase(blockId))
			return getPistonAttachedBlocks(world, location);

		for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			Vector3i neighbourLocation = location.increment(direction);
			int neighbourBlockId = world.getBlockId(neighbourLocation.x, neighbourLocation.y, neighbourLocation.z);
			if (isPistonBase(neighbourBlockId)
					&& ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[pistonDirection(world, neighbourLocation).ordinal()]] == direction)
				return getPistonAttachedBlocks(world, neighbourLocation);

			neighbourLocation = neighbourLocation.increment(direction);
			neighbourBlockId = world.getBlockId(neighbourLocation.x, neighbourLocation.y, neighbourLocation.z);
			if (isPistonBase(neighbourBlockId) && isStickyPiston(world, neighbourLocation)
					&& ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[pistonDirection(world, neighbourLocation).ordinal()]] == direction)
				return getPistonAttachedBlocks(world, neighbourLocation);
		}

		/*
		 * if(blockId == Block.pistonStickyBase.blockID) { ForgeDirection
		 * direction = pistonDirection(world, location); return
		 * Arrays.asList(location.increment(direction),
		 * location.increment(direction, 2)); }
		 */
		return Arrays.asList();
	}

	protected List<Vector3i> getPistonAttachedBlocks(World world, Vector3i location) {
		final ForgeDirection direction = pistonDirection(world, location);
		if (isStickyPiston(world, location))
			return Arrays.asList(location, location.increment(direction), location.increment(direction, 2));
		return Arrays.asList(location, location.increment(direction));
	}

	protected boolean isPistonBase(int blockId) {
		return blockId == Block.pistonBase.blockID || blockId == Block.pistonMoving.blockID || blockId == Block.pistonStickyBase.blockID;
	}

	protected boolean isStickyPiston(World world, Vector3i location) {
		boolean sticky = false;
		final int blockId = world.getBlockId(location.x, location.y, location.z);

		final TileEntity tileEntity = world.getBlockTileEntity(location.x, location.y, location.z);
		if (tileEntity instanceof TileEntityPiston) {
			sticky = ((TileEntityPiston) tileEntity).getStoredBlockID() == Block.pistonStickyBase.blockID;
		} else {
			sticky = blockId == Block.pistonStickyBase.blockID;
		}
		return sticky;
	}

	public ConnectedBlocks offset(Vector3i value) {
		final ConnectedBlocks result = new ConnectedBlocks();
		result.location = this.location.add(value);
		for (final Vector3i block : this.blocks) {
			result.blocks.add(block.add(value));
		}
		return result;
	}

	protected ForgeDirection pistonDirection(World world, Vector3i location) {
		return ForgeDirection.VALID_DIRECTIONS[BlockPistonBase.getOrientation(world.getBlockMetadata(location.x, location.y, location.z))];
	}

	public boolean toggle(World world, Vector3i locationToToggle) {
		if (!this.blocks.contains(locationToToggle)) {
			this.blocks.add(locationToToggle);
			this.blocks.addAll(getAttachedBlocks(world, locationToToggle));

			return true;
		}

		final Set<Vector3i> candidates = new LinkedHashSet<Vector3i>();
		candidates.addAll(this.blocks);
		candidates.remove(locationToToggle);
		candidates.removeAll(getAttachedBlocks(world, locationToToggle));

		this.blocks = new LinkedHashSet<Vector3i>();
		final Set<Vector3i> checked = new LinkedHashSet<Vector3i>();
		addConnectedBlocks(world, checked, this.location, candidates);
		return true;
	}
}
