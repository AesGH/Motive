package aes.gui.widgets;

import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import aes.gui.widgets.base.Slider;

/**
 * 
 * Vanilla GuiSlider in Widget form.
 * 
 */
public class SliderVanilla extends Slider {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/widgets.png");

	public SliderVanilla(int width, int height, float value, SliderFormat format, ValueChangedHandler valueChangedHandler) {
		super(width, height, value, format, valueChangedHandler);
	}

	@Override
	public void draw(int mx, int my) {
		if (this.dragging) {
			this.value = (float) (mx - (this.x + 4)) / (float) (this.width - 8);
			this.value = MathHelper.clamp_float(this.value, 0, 1);
		}

		this.mc.renderEngine.bindTexture(TEXTURE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(this.x, this.y, 0, 46, this.width / 2, this.height);
		drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46, this.width / 2, this.height);
		drawTexturedModalRect(this.x + (int) (this.value * (this.width - 8)), this.y, 0, 66, 4, 20);
		drawTexturedModalRect(this.x + (int) (this.value * (this.width - 8)) + 4, this.y, 196, 66, 4, 20);
		drawCenteredString(this.mc.fontRenderer, this.format.format(this), this.x + this.width / 2, this.y + (this.height - 8) / 2, inBounds(mx, my) ? 16777120
				: 0xffffff);
	}

	@Override
	public void handleClick(int mx, int my) {
		super.handleClick(mx, my);
		this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
	}

}
