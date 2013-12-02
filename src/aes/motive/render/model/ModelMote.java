package aes.motive.render.model;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ModelMote extends ModelMotiveBase {
	@Override
	protected String getRenderCacheKey(TileEntity tileEntity, ItemStack stack) {
		return "ModelMote";
	}

	@Override
	protected void renderModel(TileEntity tileEntity, ItemStack stack, float partialTickTime) {
		drawMote(false);
	}
}
