package aes.gui.widgets;

import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import aes.gui.widgets.base.ImageButton;

/**
 * 
 * Vanilla GuiButton in Widget form.
 * 
 */
public class ImageButtonVanilla extends ImageButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/widgets.png");

	protected String str;

	public ImageButtonVanilla(int width, int height, String text, ResourceLocation texture, int u, int v, int imageWidth, int imageHeight, ButtonHandler handler) {
		super(width, height, texture, u, v, imageWidth, imageHeight, handler);

		this.str = text;
	}

	@Override
	public void draw(int mx, int my) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		final boolean hover = inBounds(mx, my);

		final int u = 0;
		int v;
		this.mc.renderEngine.bindTexture(TEXTURE);
		v = 46 + getStateOffset(hover);

		if (this.width == 200 && this.height == 20) {
			drawTexturedModalRect(this.x, this.y, u, v, this.width, this.height);
		} else {
			drawTexturedModalRect(this.x, this.y, u, v, this.width / 2, this.height / 2);
			drawTexturedModalRect(this.x + this.width / 2, this.y, u + 200 - this.width / 2, v, this.width / 2, this.height / 2);
			drawTexturedModalRect(this.x, this.y + this.height / 2, u, v + 20 - this.height / 2, this.width / 2, this.height / 2);
			drawTexturedModalRect(this.x + this.width / 2, this.y + this.height / 2, u + 200 - this.width / 2, v + 20 - this.height / 2, this.width / 2,
					this.height / 2);
		}

		this.mc.renderEngine.bindTexture(this.imageTexture);

		drawTexturedModalRect(this.x + (this.width - this.imageWidth) / 2, this.y + (this.height - this.imageHeight) / 2, this.imageU, this.imageV,
				this.imageWidth, this.imageHeight);

		// drawCenteredString(mc.fontRenderer, str, x + width / 2, y + (height -
		// 8) / 2, getTextColor(hover));
	}

	private int getStateOffset(boolean hover) {
		return this.enabled ? hover ? 40 : 20 : 0;
	}

	@Override
	public String getText() {
		return this.str;
	}

	@Override
	public void handleClick(int mx, int my) {
		this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
		super.handleClick(mx, my);
	}

	@Override
	public void setText(String str) {
		this.str = str;
	}

}
