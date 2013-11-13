package aes.gui.widgets.base;

import net.minecraft.util.ResourceLocation;

/**
 * 
 * Abstract representation of a minecraft button. The buttons calls
 * handler.buttonClicked(this) when it is pressed.
 * 
 */
public abstract class ImageButton extends Button {

	protected ResourceLocation imageTexture;
	protected int imageU, imageV, imageWidth, imageHeight;

	public ImageButton(int width, int height, ResourceLocation texture, int u, int v, int imageWidth, int imageHeight, ButtonHandler handler) {
		super(width, height, handler);
		this.imageTexture = texture;
		this.imageU = u;
		this.imageV = v;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	@Override
	public void handleClick(int mx, int my) {
		super.handleClick(mx, my);
	}
}
