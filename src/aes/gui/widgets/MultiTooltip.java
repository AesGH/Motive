package aes.gui.widgets;

import java.util.List;

public class MultiTooltip extends Tooltip {

	public static int getMaxStringWidth(List<String> strings) {
		int max = 0;
		for (final String s : strings) {
			final int width = fontRenderer.getStringWidth(s);
			if (width > max) {
				max = width;
			}
		}
		return max;
	}

	protected int color, txtColor;

	private final List<String> text;

	public MultiTooltip(List<String> strings) {
		super(getMaxStringWidth(strings) + 2 * marginX, strings.size() * (fontRenderer.FONT_HEIGHT + 1) + 2 * marginY);

		this.text = strings;
		this.zLevel = 1.0f;
		this.color = 0xff000000;
		this.txtColor = 0xff000000;
	}

	@Override
	public boolean click(int mx, int my) {
		return false;
	}

	@Override
	public void draw(int mx, int my) {
		super.draw(mx, my);
		// drawRect(this.x, this.y, this.x + this.width, this.y + this.height,
		// this.color);

		int textY = (int) this.y + marginY;
		for (final String line : this.text) {
			fontRenderer.drawString(line, (int) this.x + marginX, textY, this.txtColor);
			textY += fontRenderer.FONT_HEIGHT + 1;
		}
	}

	@Override
	public void setBackgroundColor(int color) {
		this.color = color;
	}

	@Override
	public void setTextColor(int color) {
		this.txtColor = color;
	}

}
