package aes.motive.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import aes.base.TileEntityBase;
import aes.motive.tileentity.TileEntityMover;

public class GuiHandlerMover implements cpw.mods.fml.common.network.IGuiHandler {

	// returns an instance of the Gui you made earlier
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		final TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityBase)
			return new GuiMover((TileEntityMover) tileEntity);
		return null;

	}

	// returns an instance of the Container you made earlier
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

}
