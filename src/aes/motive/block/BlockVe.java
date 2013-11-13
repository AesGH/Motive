package aes.motive.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import aes.base.BlockBase;
import aes.motive.tileentity.TileEntityVe;

public class BlockVe extends BlockBase {
	public BlockVe(int id) {
		super(id, Material.iron);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		super.createNewTileEntity(world);
		return new TileEntityVe();
	}
}