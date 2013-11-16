package aes.motive.render;

import aes.base.TileEntitySpecialRendererBase;
import aes.motive.render.model.ModelBase;
import aes.motive.render.model.ModelVe;

public class TileEntityVeRenderer extends TileEntitySpecialRendererBase {
	@Override
	protected ModelBase getModel() {
		return new ModelVe();
	}
}
