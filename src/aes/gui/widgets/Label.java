package aes.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import aes.gui.widgets.base.Widget;

public class Label extends Widget {

	private static int getStringWidth(String text) {
		return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
	}

	private String str;

	private int color, hoverColor;
	private final List<Widget> tooltips;
	private boolean hover;
	private final boolean center;
	private boolean shadow;

	private long hoverStart;

	public Label(String text, boolean center, Widget... tooltips) {
		this(text, 0xffffff, 0xffffff, center, tooltips);
	}

	public Label(String text, int color, int hoverColor, boolean center, Widget... tooltips) {
		super(getStringWidth(text), 11);

		this.center = center;
		this.str = text;
		this.color = color;
		this.hoverColor = hoverColor;
		this.shadow = true;
		this.tooltips = new ArrayList<Widget>();
		for (final Widget w : tooltips) {
			this.tooltips.add(w);
		}
	}

	public Label(String text, int color, int hoverColor, Widget... tooltips) {
		this(text, color, hoverColor, true, tooltips);
	}

	public Label(String text, Widget... tooltips) {
		this(text, 0xffffff, 0xffffff, true, tooltips);
	}

	@Override
	public boolean click(int mx, int my) {
		return false;
	}

	@Override
	public void draw(int mx, int my) {
		final boolean newHover = inBounds(mx, my);

		if (newHover && !this.hover) {
			this.hoverStart = System.currentTimeMillis();
			// Label is designed for a single tooltip
			for (final Widget w : this.tooltips) {
				w.setPosition(mx + 3, this.y + this.height);
			}
		}
		this.hover = newHover;

		if (this.shadow) {
			this.mc.fontRenderer.drawStringWithShadow(this.str, this.x, this.y + 2, this.hover ? this.hoverColor : this.color);
		} else {
			this.mc.fontRenderer.drawString(this.str, this.x, this.y + 2, this.hover ? this.hoverColor : this.color);
		}
	}

	public String getText() {
		return this.str;
	}

	@Override
	public List<Widget> getTooltips() {
		return this.hover && System.currentTimeMillis() - this.hoverStart >= 500 ? this.tooltips : super.getTooltips();
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setHoverColor(int hoverColor) {
		this.hoverColor = hoverColor;
	}

	@Override
	public void setPosition(int x, int y) {
		this.x = this.center ? x - this.width / 2 : x;
		this.y = y;
	}

	public void setShadowedText(boolean useShadow) {
		this.shadow = useShadow;
	}

	public void setText(String text) {
		// Find the center
		if (this.center) {
			this.x += this.width / 2;
		}
		this.str = text;
		this.width = getStringWidth(text);
		if (this.center) {
			this.x -= this.width / 2;
		}
	}

}
