package aes.gui.widgets;

import net.minecraft.util.MathHelper;
import aes.gui.widgets.base.Slider;

/**
 * 
 * A Vanilla-style Integer slider that supports the mouse wheel.
 * 
 */
public class IntSlider extends SliderVanilla {

	protected class IntSliderFormat implements SliderFormat {
		@Override
		public String format(Slider slider) {
			return String.format(IntSlider.this.nameFormat, getIntValue());
		}

	}

	public static float getFloatValue(int val, int min, int max) {
		val = MathHelper.clamp_int(val, min, max);
		return (float) (val - min) / (max - min);
	}

	protected final int minVal, maxVal;

	protected final String nameFormat;

	protected boolean hover;

	/**
	 * @param nameFormat
	 *            Format string, used as a parameter to String.format
	 */
	public IntSlider(int width, int height, String nameFormat, int val, int minVal, int maxVal) {
		super(width, height, getFloatValue(val, minVal, maxVal), null, null);

		this.format = new IntSliderFormat();
		this.nameFormat = nameFormat;
		this.minVal = minVal;
		this.maxVal = maxVal;
	}

	public IntSlider(String name, int val, int minVal, int maxVal) {
		this(150, 20, name, val, minVal, maxVal);
	}

	@Override
	public void draw(int mx, int my) {
		this.hover = inBounds(mx, my);
		super.draw(mx, my);
	}

	public int getIntValue() {
		return Math.round(this.value * (this.maxVal - this.minVal) + this.minVal);
	}

	@Override
	public boolean mouseWheel(int delta) {
		if (this.hover && !this.dragging) {
			this.value = getFloatValue(getIntValue() + (int) Math.signum(delta), this.minVal, this.maxVal);
			return true;
		}
		return false;
	}

	public void setIntValue(int val) {
		this.value = MathHelper.clamp_float(getFloatValue(val, this.minVal, this.maxVal), 0, 1);
	}

}
