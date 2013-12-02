package aes.gui.widgets;

import net.minecraft.client.Minecraft;
import aes.gui.core.BasicScreen;
import aes.gui.widgets.base.Widget;
import aes.motive.Texture;

public class Tooltip extends Widget {

	protected int color, txtColor;
	private String str;

	protected final static int marginX = 4;
	protected final static int marginY = 5;

	public Tooltip(int width, int height) {
		super(width, height);

		this.zLevel = 1.0f;
		this.str = "";
		this.color = 0xff000000;
		this.txtColor = 0xff000000;
	}

	public Tooltip(String text) {
		this(fontRenderer.getStringWidth(text) + 2 * marginX, fontRenderer.FONT_HEIGHT + 2 * marginY - 1);
		this.str = text;
	}

	@Override
	public boolean click(int mx, int my) {
		return false;
	}

	/*
	 * @Override public Widget getTooltip(int mx, int my) { return null; };
	 */
	@Override
	public void draw(int mx, int my) {
		Texture.tooltipBackground.draw(this.x, this.y, this.zLevel - 1, this.width, this.height);
		fontRenderer.drawString(this.str, (int) (this.x + marginX), (int) (this.y + marginY), this.txtColor);
	}

	public void setBackgroundColor(int color) {
		this.color = color;
	}

	@Override
	public void setPosition(float x, float y) {
		int overflow = (int) (x + this.width - Minecraft.getMinecraft().displayWidth / BasicScreen.getScale());
		if (overflow > 0) {
			x -= overflow;
		}
		if (x < 0) {
			x = 0;
		}

		overflow = (int) (y + this.height - Minecraft.getMinecraft().displayHeight / BasicScreen.getScale());
		if (overflow > 0) {
			y -= overflow;
		}
		if (y < 0) {
			y = 0;
		}
		super.setPosition(x, y);
	}

	public void setTextColor(int color) {
		this.txtColor = color;
	}

}
