package aes.motive;

import net.minecraft.block.Block;

public class KnownBlock {
	Block block;
	int metadata;

	public KnownBlock(Block block, int metadata) {
		this.block = block;
		this.metadata = metadata;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof KnownBlock))
			return false;

		final KnownBlock other = (KnownBlock) obj;
		return this.block == other.block && this.metadata == other.metadata;
	}

	@Override
	public int hashCode() {
		return this.block.blockID * 37 + this.metadata;
	}
}
