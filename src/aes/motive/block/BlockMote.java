package aes.motive.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import aes.base.BlockBase;
import aes.motive.tileentity.TileEntityMote;

public class BlockMote extends BlockBase {
	public BlockMote(int id) {
		super(id, Material.rock);
		setStepSound(soundClothFootstep);
		setBlockUnbreakable();
		setResistance(6000000.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		super.createNewTileEntity(world);
		return new TileEntityMote();
	};

	private TileEntityMote getTileEntity(World world, int x, int y, int z) {
		final TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityMote)
			return (TileEntityMote) tileEntity;
		return null;
	}

	@Override
	public void onPostBlockPlaced(World par1World, int par2, int par3, int par4, int par5) {
		getTileEntity(par1World, par2, par3, par4).onBlockNeighborChange();
	};
}