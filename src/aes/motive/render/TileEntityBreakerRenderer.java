package aes.motive.render;

import aes.base.TileEntitySpecialRendererBase;
import aes.motive.render.model.ModelBase;
import aes.motive.render.model.ModelBreaker;

public class TileEntityBreakerRenderer extends TileEntitySpecialRendererBase {
	@Override
	protected ModelBase getModel() {
		return new ModelBreaker();
	}
}
