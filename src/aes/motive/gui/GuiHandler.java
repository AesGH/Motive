package aes.motive.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import aes.motive.tileentity.TileEntityBreaker;
import aes.motive.tileentity.TileEntityMover;

public class GuiHandler implements cpw.mods.fml.common.network.IGuiHandler {

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		final TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityMover)
			return new GuiMover((TileEntityMover) tileEntity);
		if (tileEntity instanceof TileEntityBreaker)
			return new GuiBreaker();
		return null;

	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

}
