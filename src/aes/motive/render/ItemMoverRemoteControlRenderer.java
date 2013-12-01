package aes.motive.render;

import net.minecraft.item.ItemStack;
import aes.base.TileEntityRendererBase;
import aes.motive.render.model.ModelMotiveBase;
import aes.motive.render.model.ModelMoverRemoteControl;

public class ItemMoverRemoteControlRenderer extends TileEntityRendererBase {
	@Override
	protected ModelMotiveBase getModel() {
		return new ModelMoverRemoteControl();
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if (type == ItemRenderType.INVENTORY) {
			renderAsItem(item, 0.4f, 0.2f, 0f, 1f, 0, 180, 180);
			return;
		}
		if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			renderAsItem(item, 0, 1, -0.3f, 1.35f, 10, 90, 180);
			return;
		}
		if (type == ItemRenderType.EQUIPPED) {
			renderAsItem(item, 0, 0, 0, 1f, 120, 0, 0);
			return;
		}

		super.renderItem(type, item, data);
	}
}