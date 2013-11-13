package aes.motive;

import java.util.LinkedList;

import aes.utils.Vector3i;
import aes.utils.WorldUtils;

public class AffectedBlocks {
	public LinkedList<Vector3i> blocks;

	public AffectedBlocks(WorldUtils we, ConnectedBlocks connectedBlocks, int moveX, int moveY, int moveZ) {
		this.blocks = new LinkedList<Vector3i>();
		for (final Vector3i block : connectedBlocks.blocks) {
			if (!this.blocks.contains(block)) {
				this.blocks.add(block);
			}
			final Vector3i wouldMoveTo = new Vector3i(block.x + moveX, block.y + moveY, block.z + moveZ);

			if (!this.blocks.contains(wouldMoveTo)) {
				this.blocks.add(wouldMoveTo);
			}
		}

	}
}
