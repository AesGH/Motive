package aes.gui.widgets.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import aes.gui.widgets.Tooltip;

/**
 * 
 * Widgets are the core of this library. All controls should be a subclass of
 * Widget.
 * 
 */
public abstract class Widget extends Gui {
	// protected Minecraft mc = Minecraft.getMinecraft();
	protected float x, y, width, height;
	protected boolean enabled;

	protected Tooltip tooltip;
	private boolean hover;
	private long hoverStart;

	public static FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

	/**
	 * 
	 * @param width
	 *            Widget of this widget
	 * @param height
	 *            Height of this widget
	 */
	public Widget(int width, int height) {
		this.width = width;
		this.height = height;
		this.enabled = true;
	}

	/**
	 * 
	 * @param x
	 *            Leftmost x of this widget
	 * @param y
	 *            Topmost y of this widget
	 * @param width
	 *            Widget of this widget
	 * @param height
	 *            Height of this widget
	 */
	public Widget(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.enabled = true;
	}

	/**
	 * Called when the mouse is clicked.
	 * 
	 * @param mx
	 *            Mouse-X
	 * @param my
	 *            Mouse-Y
	 * @return Whether the control should handleClick
	 */
	public abstract boolean click(int mx, int my);

	/**
	 * Draws this widget
	 * 
	 * @param mx
	 *            Mouse-X
	 * @param my
	 *            Mouse-Y
	 */
	public abstract void draw(int mx, int my);

	protected void drawGradientRect(float f, float g, float h, float i, int gradient1, int gradient2) {
		super.drawGradientRect((int) f, (int) g, (int) h, (int) i, gradient1, gradient2);
	}

	protected void drawRect(float f, float g, float h, float i, int outlineColor) {
		super.drawRect((int) f, (int) g, (int) h, (int) i, outlineColor);
	}

	protected void drawTexturedModalRect(float x, float y, int par3, int par4, float width, float height) {
		super.drawTexturedModalRect((int) x, (int) y, par3, par4, (int) width, (int) height);
	}

	public float getHeight() {
		return this.height;
	}

	/**
	 * Called when rendering to get tooltips.
	 * 
	 * @param my
	 * @param mx
	 * 
	 * @return Tooltips for this widget
	 */
	public Widget getTooltip(int mx, int my) {
		if (this.tooltip == null)
			return null;

		final boolean wasHover = this.hover;
		this.hover = inBounds(mx, my);
		if (this.hover && !wasHover) {
			this.hoverStart = System.currentTimeMillis();
		}

		if (!this.hover || System.currentTimeMillis() - this.hoverStart < 500)
			return null;

		this.tooltip.setPosition(mx + 3, this.y + this.height);
		return this.tooltip;
	}

	public float getWidth() {
		return this.width;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	/**
	 * Called when a call to click(mx, my) returns true. Handle the click event
	 * in this method.
	 * 
	 * @param mx
	 *            Mouse-X
	 * @param my
	 *            Mouse-Y
	 * @param button
	 */
	public void handleClick(int mx, int my, int button) {
	}

	/**
	 * Called to see if the specified coordinate is in bounds.
	 * 
	 * @param mx
	 *            Mouse-X
	 * @param my
	 *            Mouse-Y
	 * @return Whether the mouse is in the bounds of this widget
	 */
	public boolean inBounds(int mx, int my) {
		return mx >= this.x && my >= this.y && mx < this.x + this.width && my < this.y + this.height;
	}

	/**
	 * Called when a key is typed.
	 * 
	 * @param c
	 *            Character typed (if any)
	 * @param code
	 *            Keyboard.KEY_ code for this key
	 * @return Whether this widget has captured this keyboard event
	 */
	public boolean keyTyped(char c, int code) {
		return false;
	}

	/**
	 * Called when the mouse is released.
	 * 
	 * @param mx
	 *            Mouse-X
	 * @param my
	 *            Mouse-Y
	 * @param button
	 */
	public void mouseReleased(int mx, int my, int button) {
	}

	/**
	 * Called when the mouse wheel has moved.
	 * 
	 * @param delta
	 *            Clamped difference, currently either +5 or -5
	 * @return Whether this widget has captured this mouse wheel event
	 */
	public boolean mouseWheel(int delta) {
		return false;
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Set the position of this widget.
	 * 
	 * @param x
	 *            Left-most X
	 * @param y
	 *            Top-most Y
	 */
	public void setPosition(float x, float y, float width, float height) {
		this.width = width;
		this.height = height;
		setPosition(x, y);
	}

	public Widget setTooltip(Tooltip tooltip) {
		this.tooltip = tooltip;
		return this;
	}

	/**
	 * Called to see if this widget should render.
	 * 
	 * @param topY
	 *            Top-Y of the screen
	 * @param bottomY
	 *            Bottom-Y of the screen
	 * @return Whether or not this widget should be rendered
	 */
	public boolean shouldRender(int topY, int bottomY) {
		return this.y + this.height >= topY && this.y <= bottomY;
	}

	/**
	 * Update this control (if necessary).
	 */
	public void update() {
	}

}
