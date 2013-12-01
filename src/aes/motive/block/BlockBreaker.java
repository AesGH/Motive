package aes.motive.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import aes.base.BlockBase;
import aes.motive.Motive;
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

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are) {
		final TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking())
			return false;
		player.openGui(Motive.instance, 0, world, x, y, z);
		return true;
	}
}