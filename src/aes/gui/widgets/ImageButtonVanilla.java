package aes.gui.widgets;

import net.minecraft.client.Minecraft;
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
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		v = 46 + getStateOffset(hover);

		if (this.width == 200 && this.height == 20) {
			drawTexturedModalRect((int) this.x, (int) this.y, u, v, (int) this.width, (int) this.height);
		} else {
			drawTexturedModalRect((int) this.x, (int) this.y, u, v, (int) this.width / 2, (int) this.height / 2);
			drawTexturedModalRect((int) this.x + (int) this.width / 2, (int) this.y, u + 200 - (int) this.width / 2, v, (int) this.width / 2,
					(int) this.height / 2);
			drawTexturedModalRect((int) this.x, (int) this.y + (int) this.height / 2, u, v + 20 - (int) this.height / 2, (int) this.width / 2,
					(int) this.height / 2);
			drawTexturedModalRect((int) this.x + (int) this.width / 2, (int) this.y + (int) this.height / 2, u + 200 - (int) this.width / 2, v + 20
					- (int) this.height / 2, (int) this.width / 2, (int) this.height / 2);
		}

		Minecraft.getMinecraft().renderEngine.bindTexture(this.imageTexture);

		drawTexturedModalRect((int) this.x + ((int) this.width - this.imageWidth) / 2, (int) this.y + ((int) this.height - this.imageHeight) / 2, this.imageU,
				this.imageV, this.imageWidth, this.imageHeight);

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
	public void handleClick(int mx, int my, int button) {
		Minecraft.getMinecraft().sndManager.playSoundFX("random.click", 1.0F, 1.0F);
		super.handleClick(mx, my, button);
	}

	@Override
	public void setText(String str) {
		this.str = str;
	}

}
