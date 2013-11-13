package aes.gui.widgets;

import net.minecraft.client.Minecraft;
import aes.gui.widgets.base.Widget;

public class Tooltip extends Widget {

	protected int color, txtColor;
	private final String str;

	public Tooltip(String text) {
		super(Minecraft.getMinecraft().fontRenderer.getStringWidth(text) + 4, 12);

		this.zLevel = 1.0f;
		this.str = text;
		this.color = 0xff000000;
		this.txtColor = 0xffffff;
	}

	public Tooltip(String text, int color, int txtColor) {
		super(Minecraft.getMinecraft().fontRenderer.getStringWidth(text) + 4, 12);

		this.zLevel = 1.0f;
		this.str = text;
		this.color = color;
		this.txtColor = txtColor;
	}

	@Override
	public boolean click(int mx, int my) {
		return false;
	}

	@Override
	public void draw(int mx, int my) {
		drawRect(this.x, this.y, this.x + this.width, this.y + this.height, this.color);
		drawCenteredString(this.mc.fontRenderer, this.str, this.x + this.width / 2, this.y + (this.height - 8) / 2, this.txtColor);
	}

	public void setBackgroundColor(int color) {
		this.color = color;
	}

	public void setTextColor(int color) {
		this.txtColor = color;
	}

}
