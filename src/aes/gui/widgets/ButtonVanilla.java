package aes.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import aes.gui.widgets.base.Button;
import aes.motive.Texture;

/**
 * 
 * Vanilla GuiButton in Widget form.
 * 
 */
public class ButtonVanilla extends Button {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/widgets.png");

	protected String str;

	private Texture texture;
	boolean imageOnRight;

	public ButtonVanilla(String text, ButtonHandler handler) {
		super(100, 20, handler);
		this.str = text;
	}

	@Override
	public void draw(int mx, int my) {
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		final boolean hover = inBounds(mx, my);
		final int u = 0, v = 46 + getStateOffset(hover);

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
		final int stringWidth = fontRenderer.getStringWidth(this.str);
		final int imageWidth = this.texture != null ? this.texture.uSize : 0;
		final int imageGap = 2;
		final int totalWidth = stringWidth + imageWidth + (stringWidth > 0 && imageWidth > 0 ? imageGap : 0);

		int left = (int) this.x + (int) ((this.width - totalWidth) / 2);
		if (this.texture != null && !this.imageOnRight) {
			drawImage(left);
			left += imageWidth + imageGap;
		}

		drawString(fontRenderer, this.str, left, (int) this.y + ((int) this.height - 8) / 2, getTextColor(hover));
		if (this.texture != null && this.imageOnRight) {
			left += stringWidth + imageGap;
			drawImage(left + stringWidth + imageGap);
		}
	}

	private void drawImage(int left) {
		this.texture.draw(left, this.y + (this.height - this.texture.uSize) / 2, this.zLevel);
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
	public void handleClick(int mx, int my, int button) {
		Minecraft.getMinecraft().sndManager.playSoundFX("random.click", 1.0F, 1.0F);
		super.handleClick(mx, my, button);
	}

	public void setImage(Texture texture, boolean imageOnRight) {
		this.texture = texture;
		this.imageOnRight = imageOnRight;
	}

	@Override
	public void setText(String str) {
		this.str = str;
	}

}
