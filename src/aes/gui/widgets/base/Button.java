package aes.gui.widgets.base;

/**
 * 
 * Abstract representation of a minecraft button. The buttons calls
 * handler.buttonClicked(this) when it is pressed.
 * 
 */
public abstract class Button extends Widget {

	public interface ButtonHandler {
		void buttonClicked(Widget widget, int button);
	}

	protected ButtonHandler handler;

	public Button(int width, int height, ButtonHandler handler) {
		super(width, height);

		this.handler = handler;
	}

	@Override
	public boolean click(int mx, int my) {
		return this.enabled && inBounds(mx, my);
	}

	public String getText() {
		return "";
	}

	@Override
	public void handleClick(int mx, int my, int button) {
		if (this.handler != null) {
			this.handler.buttonClicked(this, button);
		}
	}

	public void setEnabled(boolean flag) {
		this.enabled = flag;
	}

	public void setText(String str) {
	}
}
