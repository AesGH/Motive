package aes.gui.widgets.base;

import aes.gui.widgets.base.Button.ButtonHandler;

/**
 * 
 * Abstract representation of a checkbox.
 * 
 */
public abstract class Checkbox extends Widget {

	protected String str;
	protected boolean check;
	private final ButtonHandler handler;

	public Checkbox(int width, int height, String text, boolean checked, ButtonHandler handler) {
		this(width, height, text, handler);

		this.check = checked;
	}

	public Checkbox(int width, int height, String text, ButtonHandler handler) {
		super(width, height);
		this.handler = handler;

		this.str = text;
	}

	@Override
	public boolean click(int mx, int my) {
		return inBounds(mx, my);
	}

	@Override
	public void handleClick(int mx, int my, int button) {
		this.check = !this.check;
		if (this.handler != null) {
			this.handler.buttonClicked(this, button);
		}
	}

	public boolean isChecked() {
		return this.check;
	}

	public void setChecked(boolean checked) {
		this.check = checked;
	}

}
