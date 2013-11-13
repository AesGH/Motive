package aes.motive.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import aes.base.BlockBase;
import aes.motive.Motive;
import aes.motive.tileentity.TileEntityMover;

public class BlockMover extends BlockBase {
	boolean updating = false;

	public BlockMover(int id) {
		super(id, Material.rock);
		setStepSound(soundStoneFootstep);
		setHardness(30F);
		setResistance(100F);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		Motive.log(world, "createNewTileEntity");
		return new TileEntityMover();
	};

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are) {
		final TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking())
			return false;
		player.openGui(Motive.instance, 0, world, x, y, z);
		return true;
	}

	@Override
	public void registerIcons(IconRegister iconRegister) {
		this.blockIcon = iconRegister.registerIcon("motive:moverIcon");
		super.registerIcons(iconRegister);
	}
}