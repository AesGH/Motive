package aes.gui.widgets.base;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import aes.gui.widgets.base.Scrollbar.Shiftable;

/**
 * 
 * A simple focusable label.
 * 
 */
public class FocusableLabel extends FocusableWidget implements Shiftable {

	private static int getStringWidth(String text) {
		return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
	}

	private String str;
	private int color, hoverColor, focusColor;
	private final List<Widget> tooltips;
	private boolean hover;
	private final boolean center;
	private boolean focused;

	private Object userData;

	public FocusableLabel(int x, int y, String text, Widget... tooltips) {
		this(text, 0xffffff, 16777120, 0x22aaff, true, tooltips);

		setPosition(x, y);
	}

	public FocusableLabel(String text, boolean center, Widget... tooltips) {
		this(text, 0xffffff, 16777120, 0x22aaff, center, tooltips);
	}

	public FocusableLabel(String text, int color, int hoverColor, int focusColor, boolean center, Widget... tooltips) {
		super(getStringWidth(text), 11);

		this.center = center;
		this.str = text;
		this.color = color;
		this.hoverColor = hoverColor;
		this.focusColor = focusColor;
		this.tooltips = new ArrayList<Widget>();
		for (final Widget w : tooltips) {
			this.tooltips.add(w);
		}
	}

	public FocusableLabel(String text, int color, int hoverColor, int focusColor, Widget... tooltips) {
		this(text, color, hoverColor, focusColor, true, tooltips);
	}

	public FocusableLabel(String text, Widget... tooltips) {
		this(text, 0xffffff, 16777120, 0x22aaff, true, tooltips);
	}

	@Override
	public boolean click(int mx, int my) {
		return inBounds(mx, my);
	}

	@Override
	public void draw(int mx, int my) {
		final boolean newHover = inBounds(mx, my);
		if (newHover && !this.hover) {
			for (final Widget w : this.tooltips) {
				w.setPosition(mx + 3, this.y + this.height);
			}
		}
		this.hover = newHover;
		if (this.focused) {
			drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x99999999);
		}
		fontRenderer.drawStringWithShadow(this.str, (int) this.x, (int) this.y + 2, this.focused ? this.focusColor : this.hover ? this.hoverColor : this.color);
	}

	@Override
	public void focusGained() {
		this.focused = true;
	}

	@Override
	public void focusLost() {
		this.focused = false;
	}

	public String getText() {
		return this.str;
	}

	public Object getUserData() {
		return this.userData;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setColors(int color, int hoverColor, int focusColor) {
		this.color = color;
		this.hoverColor = hoverColor;
		this.focusColor = focusColor;
	}

	public void setFocusColor(int focusColor) {
		this.focusColor = focusColor;
	}

	public void setHoverColor(int hoverColor) {
		this.hoverColor = hoverColor;
	}

	@Override
	public void setPosition(float x, float y) {
		this.x = this.center ? x - this.width / 2 : x;
		this.y = y;
	}

	public void setText(String text) {
		if (this.center) {
			this.x += this.width / 2;
		}
		this.str = text;
		this.width = getStringWidth(text);
		if (this.center) {
			this.x -= this.width / 2;
		}
	}

	public void setUserData(Object data) {
		this.userData = data;
	}

	@Override
	public void shiftY(int dy) {
		this.y += dy;
	}

}
