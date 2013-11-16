package aes.motive.render.model;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ModelVe extends ModelMotiveBase {
	@Override
	public void render(TileEntity tileEntity, ItemStack stack, float scale) {
		super.render(tileEntity, stack, scale);

		renderFrontCase(scale);
		renderFrontStruts(scale);
	}
}
