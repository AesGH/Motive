package aes.gui.widgets;

import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import aes.gui.widgets.base.Button;

/**
 * 
 * Vanilla GuiButton in Widget form.
 * 
 */
public class ButtonVanilla extends Button {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/widgets.png");

	protected String str;

	public ButtonVanilla(int width, int height, String text, ButtonHandler handler) {
		super(width, height, handler);

		this.str = text;
	}

	public ButtonVanilla(String text, ButtonHandler handler) {
		this(200, 20, text, handler);
	}

	@Override
	public void draw(int mx, int my) {
		this.mc.renderEngine.bindTexture(TEXTURE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		final boolean hover = inBounds(mx, my);
		final int u = 0, v = 46 + getStateOffset(hover);

		if (this.width == 200 && this.height == 20) {
			drawTexturedModalRect(this.x, this.y, u, v, this.width, this.height);
		} else {
			drawTexturedModalRect(this.x, this.y, u, v, this.width / 2, this.height / 2);
			drawTexturedModalRect(this.x + this.width / 2, this.y, u + 200 - this.width / 2, v, this.width / 2, this.height / 2);
			drawTexturedModalRect(this.x, this.y + this.height / 2, u, v + 20 - this.height / 2, this.width / 2, this.height / 2);
			drawTexturedModalRect(this.x + this.width / 2, this.y + this.height / 2, u + 200 - this.width / 2, v + 20 - this.height / 2, this.width / 2,
					this.height / 2);
		}
		drawCenteredString(this.mc.fontRenderer, this.str, this.x + this.width / 2, this.y + (this.height - 8) / 2, getTextColor(hover));
	}

	private int getStateOffset(boolean hover) {
		return this.enabled ? hover ? 40 : 20 : 0;
	}

	@Override
	public String getText() {
		return this.str;
	}

	private int getTextColor(boolean hover) {
		return this.enabled ? hover ? 16777120 : 14737632 : 6250336;
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
