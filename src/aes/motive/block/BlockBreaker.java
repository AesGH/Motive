package aes.motive.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import aes.base.BlockBase;
import aes.motive.tileentity.TileEntityBreaker;

public class BlockBreaker extends BlockBase {
	public BlockBreaker(int id) {
		super(id, Material.rock);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		super.createNewTileEntity(world);
		return new TileEntityBreaker();
	}
}