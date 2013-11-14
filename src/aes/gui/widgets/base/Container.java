package aes.gui.widgets.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import aes.gui.widgets.base.Scrollbar.Shiftable;

public class Container {

	protected final Minecraft mc = Minecraft.getMinecraft();
	protected List<FocusableWidget> focusList;
	protected List<Widget> widgets;

	protected int left, right, top, bottom, shiftAmount, extraScrollHeight, scrollbarWidth;
	protected int cHeight, focusIndex;
	protected Scrollbar scrollbar;
	protected Widget lastSelected;
	protected boolean clip;

	/**
	 * Create this Container, which by default will clip the elements contained.
	 */
	public Container() {
		this(null, 0, 0);
	}

	/**
	 * Create this Container, which by default will clip the elements contained.
	 * 
	 * @param scrollbar
	 *            The scrollbar for this container
	 * @param shiftAmount
	 *            The amount to shift the scrollbar when focus is shifted; this
	 *            should normally be the height of the FocusableWidget,
	 *            depending on spacing.
	 * @param extraScrollHeight
	 *            The sum of the supposed gap between the top+bottom of this
	 *            container and the FocusableWidgets that are inside. Say that
	 *            the items in the list should go from this container's top+2 to
	 *            this container's bottom-2, then extraScrollHeight should be 4.
	 */
	public Container(Scrollbar scrollbar, int shiftAmount, int extraScrollHeight) {
		this.scrollbar = scrollbar;
		this.shiftAmount = shiftAmount;
		this.extraScrollHeight = extraScrollHeight;
		this.widgets = new ArrayList<Widget>();
		this.focusList = new ArrayList<FocusableWidget>();
		this.focusIndex = -1;
		this.clip = true;

		if (scrollbar != null) {
			scrollbar.setContainer(this);
			this.scrollbarWidth = scrollbar.width;
		}
	}

	public void addWidgets(Widget... arr) {
		for (final Widget w : arr) {
			this.widgets.add(w);
			if (w instanceof FocusableWidget) {
				this.focusList.add((FocusableWidget) w);
			}
		}
		calculateContentHeight();
	}

	public int bottom() {
		return this.bottom;
	}

	private void calculateContentHeight() {
		int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

		for (final Widget w : this.widgets) {
			if (w instanceof Shiftable) {
				if (w.y < minY) {
					minY = w.y;
				}
				if (w.y + w.height > maxY) {
					maxY = w.y + w.height;
				}
			}
		}
		this.cHeight = minY > maxY ? 0 : maxY - minY + this.extraScrollHeight;
	}

	public FocusableWidget deleteFocused() {
		if (hasFocusedWidget()) {
			final FocusableWidget w = getFocusedWidget();
			if (this.lastSelected == w) {
				this.lastSelected = null;
			}
			this.focusList.remove(this.focusIndex);
			if (this.focusList.size() == 0) {
				this.focusIndex = -1;
			} else {
				this.focusIndex = MathHelper.clamp_int(this.focusIndex, 0, this.focusList.size() - 1);
				this.focusList.get(this.focusIndex).focusGained();
			}

			final int index = this.widgets.indexOf(w);
			int offset = Integer.MAX_VALUE;
			for (int i = index + 1; i < this.widgets.size(); ++i) {
				final Widget cur = this.widgets.get(i);
				if (cur instanceof Shiftable) {
					if (offset == Integer.MAX_VALUE) {
						offset = w.getY() - cur.getY();
					}
					((Shiftable) cur).shiftY(offset);
				}
			}
			this.widgets.remove(w);
			calculateContentHeight();
			if (this.scrollbar != null) {
				this.scrollbar.onChildRemoved();
			}

			return w;
		}
		return null;
	}

	public List<Widget> draw(int mx, int my, int scale) {
		if (this.clip) {
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			GL11.glScissor(this.left * scale, this.mc.displayHeight - this.bottom * scale, (this.right - this.left - this.scrollbarWidth) * scale,
					(this.bottom - this.top) * scale);
		}

		final List<Widget> overlays = new ArrayList<Widget>();

		/*
		 * Fix to prevent clipped widgets from thinking that they extend outside
		 * of the container when checking for hover.
		 */
		final boolean mouseInBounds = inBounds(mx, my);
		final int widgetX = mouseInBounds || !this.clip ? mx : -1;
		final int widgetY = mouseInBounds || !this.clip ? my : -1;

		for (final Widget w : this.widgets) {
			if (w.shouldRender(this.top, this.bottom)) {
				w.draw(widgetX, widgetY);
				overlays.addAll(w.getTooltips());
			}
		}

		// Don't clip the scrollbar!
		if (this.clip) {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}

		if (this.scrollbar != null && this.scrollbar.shouldRender(this.top, this.bottom)) {
			this.scrollbar.draw(mx, my);
		}

		return overlays;
	}

	public int getContentHeight() {
		return this.cHeight;
	}

	public List<FocusableWidget> getFocusableWidgets() {
		return this.focusList;
	}

	public FocusableWidget getFocusedWidget() {
		return this.focusList.get(this.focusIndex);
	}

	public List<Widget> getWidgets() {
		return this.widgets;
	}

	public boolean hasFocusedWidget() {
		return this.focusIndex != -1;
	}

	public boolean inBounds(int mx, int my) {
		return mx >= this.left && my >= this.top && mx < this.right && my < this.bottom;
	}

	public boolean isClipping() {
		return this.clip;
	}

	public boolean keyTyped(char c, int code) {
		boolean handled = this.focusIndex != -1 ? this.focusList.get(this.focusIndex).keyTyped(c, code) : false;
		if (!handled) {
			switch (code) {
			case Keyboard.KEY_UP:
				shift(-1);
				handled = true;
				break;
			case Keyboard.KEY_DOWN:
				shift(1);
				handled = true;
				break;
			case Keyboard.KEY_TAB:
				shiftFocusToNext();
				handled = true;
				break;
			}
		}
		return handled;
	}

	public int left() {
		return this.left;
	}

	public boolean mouseClicked(int mx, int my, int button) {
		if (inBounds(mx, my)) {
			boolean resetFocus = true;

			if (this.scrollbar != null && this.scrollbar.shouldRender(this.top, this.bottom) && this.scrollbar.inBounds(mx, my))
				return true;

			for (final Widget w : this.widgets) {
				if (w.shouldRender(this.top, this.bottom) && w.click(mx, my)) {
					this.lastSelected = w;
					if (w instanceof FocusableWidget) {
						setFocused((FocusableWidget) w);
						resetFocus = false;
					}
					w.handleClick(mx, my, button);
					break;
				}
			}
			if (resetFocus) {
				setFocused(null);
			}
			return true;
		}
		return false;
	}

	public void mouseReleased(int mx, int my, int button) {
		if (this.lastSelected != null) {
			this.lastSelected.mouseReleased(mx, my, button);
			this.lastSelected = null;
		}
	}

	public void mouseWheel(int delta) {
		if (this.scrollbar != null && this.scrollbar.shouldRender(this.top, this.bottom)) {
			this.scrollbar.shiftRelative(delta);
		} else {
			boolean done = false;
			if (this.focusIndex != -1) {
				done = this.focusList.get(this.focusIndex).mouseWheel(delta);
			} else {
				for (final Iterator<Widget> it = this.widgets.iterator(); it.hasNext() && !done;) {
					done = it.next().mouseWheel(delta);
				}
			}
		}
	}

	public void removeFocusableWidgets() {
		this.focusIndex = -1;
		if (this.lastSelected instanceof FocusableWidget) {
			this.lastSelected = null;
		}

		this.widgets.removeAll(this.focusList);
		this.focusList.clear();

		calculateContentHeight();
		if (this.scrollbar != null) {
			this.scrollbar.onChildRemoved();
		}
	}

	public void revalidate(int x, int y, int width, int height) {
		this.left = x;
		this.right = x + width;
		this.top = y;
		this.bottom = y + height;
		calculateContentHeight();
		if (this.scrollbar != null) {
			this.scrollbar.revalidate(this.top, this.bottom);
			this.scrollbarWidth = this.scrollbar.width;
		}
	}

	public int right() {
		return this.right;
	}

	public void setClipping(boolean clip) {
		this.clip = clip;
	}

	public void setFocused(FocusableWidget f) {
		final int newIndex = f == null ? -1 : this.focusList.indexOf(f);
		if (this.focusIndex != newIndex) {
			if (this.focusIndex != -1) {
				this.focusList.get(this.focusIndex).focusLost();
			}
			if (newIndex != -1) {
				this.focusList.get(newIndex).focusGained();
			}

			this.focusIndex = newIndex;
		}
	}

	protected void shift(int delta) {
		if (this.focusIndex != -1) {
			shiftFocus(MathHelper.clamp_int(this.focusIndex + delta, 0, this.focusList.size() - 1));
		} else if (this.scrollbar != null && this.scrollbar.shouldRender(this.top, this.bottom)) {
			this.scrollbar.shiftRelative(delta * -4);
		}
	}

	protected void shiftFocus(int newIndex) {
		if (this.focusIndex != newIndex) {
			this.focusList.get(this.focusIndex).focusLost();
			this.focusList.get(newIndex).focusGained();
			if (this.scrollbar != null && this.scrollbar.shouldRender(this.top, this.bottom)) {
				this.scrollbar.shift((this.focusIndex - newIndex) * this.shiftAmount);
			}
			this.focusIndex = newIndex;
		}
	}

	protected void shiftFocusToNext() {
		if (this.focusIndex != -1 && this.focusList.size() > 1) {
			final int newIndex = (this.focusIndex + 1) % this.focusList.size();
			if (newIndex != this.focusIndex) {
				this.focusList.get(this.focusIndex).focusLost();
				this.focusList.get(newIndex).focusGained();
				if (this.scrollbar != null && this.scrollbar.shouldRender(this.top, this.bottom)) {
					this.scrollbar.shift((this.focusIndex - newIndex) * this.shiftAmount);
				}
				this.focusIndex = newIndex;
			}
		}
	}

	public int top() {
		return this.top;
	}

	public void update() {
		for (final Widget w : this.widgets) {
			w.update();
		}
	}

}
