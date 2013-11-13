package aes.motive.render;

import net.minecraft.client.model.ModelBase;
import aes.base.TileEntitySpecialRendererBase;

public class TileEntityBreakerRenderer extends TileEntitySpecialRendererBase {
	@Override
	protected ModelBase getModel() {
		return new ModelBreaker();
	}
}
