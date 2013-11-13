package aes.gui.core;

import aes.gui.widgets.base.Container;
import aes.gui.widgets.base.FocusableWidget;
import aes.gui.widgets.base.Scrollbar;
import aes.gui.widgets.base.Widget;

/**
 * 
 * A "Focused" version of a Container. This container will always have a focused
 * widget as long as there is a focusable widget contained.
 * 
 */
public class FocusedContainer extends Container {

	public FocusedContainer() {
		super();
	}

	public FocusedContainer(Scrollbar scrollbar, int shiftAmount, int extraScrollHeight) {
		super(scrollbar, shiftAmount, extraScrollHeight);
	}

	@Override
	public void addWidgets(Widget... arr) {
		super.addWidgets(arr);

		if (this.focusIndex == -1 && this.focusList.size() > 0) {
			this.focusIndex = 0;
			this.focusList.get(this.focusIndex).focusGained();
		}
	}

	@Override
	public void setFocused(FocusableWidget f) {
		if (f != null) {
			super.setFocused(f);
		}
	}
}
