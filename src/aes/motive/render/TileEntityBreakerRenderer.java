package aes.motive.render;

import aes.base.TileEntityRendererBase;
import aes.motive.render.model.ModelBreaker;
import aes.motive.render.model.ModelMotiveBase;

public class TileEntityBreakerRenderer extends TileEntityRendererBase {
	@Override
	protected ModelMotiveBase getModel() {
		return new ModelBreaker();
	}
}
