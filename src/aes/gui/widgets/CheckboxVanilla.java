package aes.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import aes.gui.widgets.base.Button.ButtonHandler;
import aes.gui.widgets.base.Checkbox;

/**
 * 
 * Default style checkbox.
 * 
 */
public class CheckboxVanilla extends Checkbox {

	private static final ResourceLocation TEXTURE = new ResourceLocation("guilib", "textures/gui/checkbox.png");

	public static final int SIZE = 10;

	public CheckboxVanilla(String text, boolean checked, ButtonHandler handler) {
		this(text, handler);

		this.check = checked;
	}

	public CheckboxVanilla(String text, ButtonHandler handler) {
		super(SIZE + 2 + Minecraft.getMinecraft().fontRenderer.getStringWidth(text), SIZE, text, handler);
	}

	@Override
	public void draw(int mx, int my) {
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect((int) this.x, (int) this.y, 0, this.check ? SIZE : 0, SIZE, SIZE);
		fontRenderer.drawStringWithShadow(this.str, (int) this.x + SIZE + 1, (int) this.y + 1, inBounds(mx, my) ? 16777120 : 0xffffff);
	}

	@Override
	public void handleClick(int mx, int my, int button) {
		Minecraft.getMinecraft().sndManager.playSoundFX("random.click", 1.0F, 1.0F);
		super.handleClick(mx, my, button);
	}

}
