package aes.motive.render.model;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import aes.motive.item.ItemMoverRemoteControl;

public class ModelMover extends ModelMotiveBase {
	@Override
	protected String getRenderCacheKey(TileEntity tileEntity, ItemStack stack) {
		return "TileEntityMover" + (isPairedWithCurrentRemote(tileEntity) ? "_paired" : "");
	}

	protected boolean isPairedWithCurrentRemote(TileEntity tileEntity) {
		final InventoryPlayer inventoryPlayer = Minecraft.getMinecraft().thePlayer.inventory;
		final ItemStack selectedItem = inventoryPlayer.getCurrentItem();

		final boolean pairedWithCurrentRemote = tileEntity != null && selectedItem != null
				&& ItemMoverRemoteControl.getPairedMover(tileEntity.worldObj, selectedItem) == tileEntity;
		return pairedWithCurrentRemote;
	};

	@Override
	protected void renderModel(TileEntity tileEntity, ItemStack stack, float partialTickTime) {
		drawBoltedFrame();
		drawDiagonalStruts();

		drawMote(isPairedWithCurrentRemote(tileEntity));
	}
}
