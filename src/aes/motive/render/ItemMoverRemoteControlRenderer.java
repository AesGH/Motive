package aes.motive.render;

import aes.base.TileEntitySpecialRendererBase;
import aes.motive.render.model.ModelBase;
import aes.motive.render.model.ModelMoverRemoteControl;

public class ItemMoverRemoteControlRenderer extends TileEntitySpecialRendererBase {
	@Override
	protected ModelBase getModel() {
		return new ModelMoverRemoteControl();
	}
}