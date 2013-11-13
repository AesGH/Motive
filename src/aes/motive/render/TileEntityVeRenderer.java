package aes.motive.render;

import net.minecraft.client.model.ModelBase;
import aes.base.TileEntitySpecialRendererBase;

public class TileEntityVeRenderer extends TileEntitySpecialRendererBase {
	@Override
	protected ModelBase getModel() {
		return new ModelVe();
	}
}
