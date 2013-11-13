package aes.motive;

import net.minecraft.block.Block;

public class KnownBlock {
	Block block;
	int metadata;

	public KnownBlock(Block block, int metadata)
	{
		this.block = block;
		this.metadata = metadata;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof KnownBlock))
			return false;
		
		KnownBlock other = (KnownBlock)obj;
		return block == other.block && metadata == other.metadata;
	}
	
	@Override
	public int hashCode() {
		return block.blockID * 37 + metadata; 
	}
}
