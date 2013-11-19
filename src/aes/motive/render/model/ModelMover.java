package aes.motive.render.model;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import aes.motive.item.ItemMoverRemoteControl;

public class ModelMover extends ModelMotiveBase {
	@Override
	protected void renderModel(TileEntity tileEntity, ItemStack stack, float partialTickTime) {
		drawBoltedFrame();
		drawDiagonalStruts();

		final InventoryPlayer inventoryPlayer = Minecraft.getMinecraft().thePlayer.inventory;
		final ItemStack selectedItem = inventoryPlayer.getCurrentItem();

		final boolean pairedWithCurrentRemote = tileEntity != null && selectedItem != null
				&& ItemMoverRemoteControl.getPairedMover(tileEntity.worldObj, selectedItem) == tileEntity;
		/*
		 * { float fontScale = 0.06f;
		 * 
		 * GL11.glPushMatrix(); GL11.glScalef(fontScale, fontScale, fontScale);
		 * GL11.glTranslatef(-35f, 80, -10f); GL11.glDisable(GL11.GL_LIGHTING);
		 * 
		 * fontRenderer.drawString("PAIRED", 0, 0, 0x00ff00);
		 * 
		 * GL11.glPopMatrix(); GL11.glEnable(GL11.GL_LIGHTING); }
		 */
		drawMote(pairedWithCurrentRemote);

	};
}
