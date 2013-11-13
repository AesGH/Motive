package aes.gui.widgets;

import java.util.List;

import net.minecraft.client.Minecraft;
import aes.gui.widgets.base.Widget;

public class MultiTooltip extends Widget {

	public static int getMaxStringWidth(List<String> strings) {
		final Minecraft mc = Minecraft.getMinecraft();
		int max = 0;
		for (final String s : strings) {
			final int width = mc.fontRenderer.getStringWidth(s);
			if (width > max) {
				max = width;
			}
		}
		return max;
	}

	protected int color, txtColor;

	private final List<String> text;

	public MultiTooltip(List<String> strings) {
		super(getMaxStringWidth(strings) + 4, strings.size() * 12);

		this.text = strings;
		this.zLevel = 1.0f;
		this.color = 0xff000000;
		this.txtColor = 0xffffff;
	}

	public MultiTooltip(List<String> strings, int color, int txtColor) {
		super(getMaxStringWidth(strings) + 4, strings.size() * 12);

		this.text = strings;
		this.zLevel = 1.0f;
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

		int textY = this.y + 2;
		for (final String line : this.text) {
			this.mc.fontRenderer.drawStringWithShadow(line, this.x + 2, textY, this.txtColor);
			textY += 11;
		}
	}

	public void setBackgroundColor(int color) {
		this.color = color;
	}

	public void setTextColor(int color) {
		this.txtColor = color;
	}

}
