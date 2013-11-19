package aes.motive.render;

import net.minecraft.item.ItemStack;
import aes.base.TileEntityRendererBase;
import aes.motive.render.model.ModelMotiveBase;
import aes.motive.render.model.ModelVe;

public class TileEntityVeRenderer extends TileEntityRendererBase {
	@Override
	protected ModelMotiveBase getModel() {
		return new ModelVe();
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if (type == ItemRenderType.INVENTORY) {
			renderAsItem(item, 0.4f, 0.2f, 0f, 1f, 0, 180, 180);
			return;
		}
		super.renderItem(type, item, data);
	}
}
