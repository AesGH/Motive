package aes.gui.widgets.base;

/**
 * A Focusable Widget. This widget can gain and lose focus.
 * 
 */
public abstract class FocusableWidget extends Widget {

	public FocusableWidget(int width, int height) {
		super(width, height);

	}

	public FocusableWidget(int x, int y, int width, int height) {
		super(x, y, width, height);

	}

	public abstract void focusGained();

	public abstract void focusLost();

}
