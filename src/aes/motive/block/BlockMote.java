package aes.motive.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
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

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are) {
		final TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking())
			return false;
		return true;
	}
}