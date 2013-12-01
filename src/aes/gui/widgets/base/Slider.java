package aes.gui.widgets.base;

import net.minecraft.util.MathHelper;

/**
 * 
 * Abstract representation of a minecraft slider.
 * 
 */
public abstract class Slider extends Widget {

	public interface SliderFormat {
		String format(Slider slider);
	}

	public interface ValueChangedHandler {
		void valueChanged(Slider slider);
	}

	protected SliderFormat format;
	protected ValueChangedHandler valueChangedHandler;
	protected float value;
	protected boolean dragging;

	public Slider(int width, int height, float value, SliderFormat format, ValueChangedHandler valueChangedHandler) {
		super(width, height);

		this.value = MathHelper.clamp_float(value, 0, 1);
		this.format = format;
		this.valueChangedHandler = valueChangedHandler;
	}

	@Override
	public boolean click(int mx, int my) {
		if (inBounds(mx, my)) {
			this.value = (mx - (this.x + 4)) / (this.width - 8);
			this.value = MathHelper.clamp_float(this.value, 0, 1);
			this.valueChangedHandler.valueChanged(this);
			this.dragging = true;
			return true;
		}
		return false;
	}

	public float getValue() {
		return this.value;
	}

	@Override
	public void handleClick(int mx, int my, int button) {
		if (button == 0) {
			this.value = (mx - (this.x + 4)) / (this.width - 8);
			this.value = MathHelper.clamp_float(this.value, 0, 1);
			this.valueChangedHandler.valueChanged(this);
			this.dragging = true;
		}
	}

	@Override
	public void mouseReleased(int mx, int my, int button) {
		if (button == 0) {
			this.dragging = false;
			this.valueChangedHandler.valueChanged(this);
		}
	}

	public void setValue(float value) {
		this.value = value;
	}

}
