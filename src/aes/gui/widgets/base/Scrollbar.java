package aes.gui.widgets.base;

import org.lwjgl.input.Mouse;

/**
 * 
 * Abstract representation of a scrollbar.
 * 
 */
public abstract class Scrollbar extends Widget {

	public interface Shiftable {
		void shiftY(int dy);
	}

	protected int yClick;
	protected Container container;

	private int topY, bottomY;
	private int offset;

	public Scrollbar(int width) {
		super(width, 0);

		this.yClick = -1;
	}

	@Override
	public boolean click(int mx, int my) {
		return false;
	}

	@Override
	public void draw(int mx, int my) {
		final int length = getLength();

		if (Mouse.isButtonDown(0)) {
			if (this.yClick == -1) {
				if (inBounds(mx, my)) {
					this.yClick = my;
				}
			} else {
				float scrollMultiplier = 1.0F;
				int diff = getHeightDifference();

				if (diff < 1) {
					diff = 1;
				}

				scrollMultiplier /= (this.bottomY - this.topY - length) / (float) diff;
				shift((int) ((this.yClick - my) * scrollMultiplier));
				this.yClick = my;
			}
		} else {
			this.yClick = -1;
		}

		drawBoundary(this.x, this.topY, this.width, this.height);

		int y = -this.offset * (this.bottomY - this.topY - length) / getHeightDifference() + this.topY;
		if (y < this.topY) {
			y = this.topY;
		}

		drawScrollbar(this.x, y, this.width, length);
	}

	protected abstract void drawBoundary(int x, int y, int width, int height);

	protected abstract void drawScrollbar(int x, int y, int width, int height);

	protected int getHeightDifference() {
		return this.container.getContentHeight() - (this.bottomY - this.topY);
	}

	protected int getLength() {
		if (this.container.getContentHeight() == 0)
			return 0;
		int length = (this.bottomY - this.topY) * (this.bottomY - this.topY) / this.container.getContentHeight();
		if (length < 32) {
			length = 32;
		}
		if (length > this.bottomY - this.topY - 8) {
			length = this.bottomY - this.topY - 8;
		}
		return length;
	}

	public void onChildRemoved() {
		final int heightDiff = getHeightDifference();
		if (this.offset != 0) {
			if (heightDiff <= 0) {
				shiftChildren(-this.offset);
				this.offset = 0;
			} else if (this.offset < -heightDiff) {
				shiftChildren(-heightDiff - this.offset);
				this.offset = -heightDiff;
			}
		}
	}

	public void revalidate(int topY, int bottomY) {
		this.topY = topY;
		this.bottomY = bottomY;
		this.height = bottomY - topY;
		final int heightDiff = getHeightDifference();
		if (this.offset != 0 && heightDiff <= 0) {
			this.offset = 0;
		}
		if (heightDiff > 0 && this.offset < -heightDiff) {
			this.offset = -heightDiff;
		}
		if (this.offset != 0) {
			shiftChildren(this.offset);
		}
	}

	public void setContainer(Container c) {
		this.container = c;
	}

	/**
	 * Shifts the scrollbar by i pixels.
	 * 
	 * @param i
	 *            How many pixels to shift the scrollbar.
	 */
	public void shift(int i) {
		final int heightDiff = getHeightDifference();
		if (heightDiff > 0) {
			int dif = this.offset + i;
			if (dif > 0) {
				dif = 0;
			}
			if (dif < -heightDiff) {
				dif = -heightDiff;
			}
			final int result = dif - this.offset;
			if (result != 0) {
				shiftChildren(result);
			}
			this.offset = dif;
		}
	}

	protected abstract void shiftChildren(int dy);

	/**
	 * Shifts this scrollbar relative to its size + contentHeight.
	 * 
	 * @param i
	 *            Base pixels to shift.
	 */
	public void shiftRelative(int i) {
		final int heightDiff = getHeightDifference();
		if (heightDiff > 0) {
			i *= 1 + heightDiff / (float) (this.bottomY - this.topY);
			// shift(i) inlined
			int dif = this.offset + i;
			if (dif > 0) {
				dif = 0;
			}
			if (dif < -heightDiff) {
				dif = -heightDiff;
			}
			final int result = dif - this.offset;
			if (result != 0) {
				shiftChildren(result);
			}
			this.offset = dif;
		}
	}

	@Override
	public boolean shouldRender(int topY, int bottomY) {
		return getHeightDifference() > 0;
	}

}
