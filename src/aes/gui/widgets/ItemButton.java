package aes.gui.widgets;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import aes.gui.widgets.base.Button;
import aes.gui.widgets.base.Scrollbar.Shiftable;
import aes.gui.widgets.base.Widget;

/**
 * 
 * This class is a Widget copy of a vanilla item button. This button supports
 * "Air" - an item with ID 0.
 * 
 * Note that items use zLevel for rendering - change zLevel as needed.
 * 
 */
public class ItemButton extends Button implements Shiftable {

	public static final int WIDTH = 18;
	public static final int HEIGHT = 18;
	public static final RenderItem itemRenderer = new RenderItem();

	protected ItemStack item;
	protected List<Widget> tooltip;

	private final GuiScreen parent;
	protected boolean hover;

	public ItemButton(ItemStack item, ButtonHandler handler) {
		super(WIDTH, HEIGHT, handler);

		this.parent = this.mc.currentScreen;
		this.zLevel = 100;
		setItem(item);
	}

	/**
	 * Draws the item or string "Air" if itemID = 0.
	 */
	@Override
	public void draw(int mx, int my) {
		this.hover = inBounds(mx, my);
		if (this.hover) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x55909090);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			this.tooltip.get(0).setPosition(mx, my);
		}
		if (this.item.itemID != 0) {
			RenderHelper.enableGUIStandardItemLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			itemRenderer.zLevel = this.zLevel;
			itemRenderer.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), this.item, this.x + 1, this.y + 1);
			itemRenderer.zLevel = 0;
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			RenderHelper.disableStandardItemLighting();
		} else {
			drawString(this.mc.fontRenderer, "Air", this.x + 3, this.y + 5, -1);
		}
	}

	@Override
	public List<Widget> getTooltips() {
		return this.hover ? this.tooltip : super.getTooltips();
	}

	protected void setItem(ItemStack item) {
		if (item.getItem() == null && item.itemID != 0)
			throw new IllegalArgumentException("Item to display does not exist");
		this.item = item;
		this.tooltip = Arrays.asList((Widget) new ItemTooltip(item, this.parent));
	}

	@Override
	public void shiftY(int dy) {
		this.y += dy;
	}
}
