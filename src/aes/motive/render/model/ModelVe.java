package aes.motive.render.model;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ModelVe extends ModelMotiveBase {
	@Override
	protected String getRenderCacheKey(TileEntity tileEntity, ItemStack stack) {
		return "ModelVe";
	};

	@Override
	protected void renderModel(TileEntity tileEntity, ItemStack stack, float partialTickTime) {
		rotate(180, 1, 0, 0);
		drawBoltedFace(true);
		// drawDiagonalStruts();
	}
}
