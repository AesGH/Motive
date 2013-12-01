package aes.gui.widgets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import aes.gui.widgets.base.Widget;

/**
 * 
 * This class represents a Widget copy of an Item's tooltip.
 * 
 */
public class ItemTooltip extends Widget {

	public static final char SECTION = '\u00a7';

	public static final Map<Class<?>, String> NAME_MAP = new HashMap<Class<?>, String>();

	static {
		NAME_MAP.put(BlockPistonExtension.class, "Piston Extension");
		NAME_MAP.put(BlockPistonMoving.class, "Piston Moving");
		NAME_MAP.put(BlockEndPortal.class, "End Portal");
	}

	private static String getUnknownName(ItemStack stack) {
		final Item item = stack.getItem();
		if (item instanceof ItemBlock) {
			final int id = ((ItemBlock) item).getBlockID();
			final Class<? extends Block> c = Block.blocksList[id].getClass();
			return NAME_MAP.containsKey(c) ? NAME_MAP.get(c) : "Unknown";
		}
		return "Unknown";
	}

	private final List<String> tooltips;
	private final FontRenderer font;
	private final GuiScreen parent;

	/**
	 * See {@link net.minecraft.client.gui.inventory.GuiContainer#drawScreen}
	 * for more information.
	 */
	@SuppressWarnings("unchecked")
	public ItemTooltip(ItemStack stack, GuiScreen parent) {
		super(0, 0);

		if (stack.itemID != 0) {
			this.tooltips = stack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
			if (!this.tooltips.isEmpty()) {
				String name = this.tooltips.get(0);
				if (name.startsWith("tile.null.name")) {
					name = name.replace("tile.null.name", getUnknownName(stack));
				}
				this.tooltips.set(0, SECTION + Integer.toHexString(stack.getRarity().rarityColor) + name);
				for (int i = 1; i < this.tooltips.size(); ++i) {
					this.tooltips.set(i, EnumChatFormatting.GRAY + this.tooltips.get(i));
				}
			}
			final FontRenderer itemRenderer = stack.getItem().getFontRenderer(stack);
			this.font = itemRenderer == null ? fontRenderer : itemRenderer;
		} else {
			this.tooltips = Arrays.asList("Air");
			this.font = fontRenderer;
		}
		this.parent = parent;
		this.width = getMaxStringWidth();
		this.height = this.tooltips.size() > 1 ? this.tooltips.size() * 10 : 8;
	}

	@Override
	public boolean click(int mx, int my) {
		return false;
	}

	/**
	 * See
	 * {@link net.minecraft.client.gui.inventory.GuiContainer#drawHoveringText}
	 */
	@Override
	public void draw(int mx, int my) {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		if (!this.tooltips.isEmpty()) {
			final int outlineColor = 0xf0100010;
			drawRect(this.x - 3, this.y - 4, this.x + this.width + 3, this.y - 3, outlineColor);
			drawRect(this.x - 3, this.y + this.height + 3, this.x + this.width + 3, this.y + this.height + 4, outlineColor);
			drawRect(this.x - 3, this.y - 3, this.x + this.width + 3, this.y + this.height + 3, outlineColor);
			drawRect(this.x - 4, this.y - 3, this.x - 3, this.y + this.height + 3, outlineColor);
			drawRect(this.x + this.width + 3, this.y - 3, this.x + this.width + 4, this.y + this.height + 3, outlineColor);
			final int gradient1 = 1347420415;
			final int gradient2 = (gradient1 & 16711422) >> 1 | gradient1 & -16777216;
			drawGradientRect(this.x - 3, this.y - 3 + 1, this.x - 3 + 1, this.y + this.height + 3 - 1, gradient1, gradient2);
			drawGradientRect(this.x + this.width + 2, this.y - 3 + 1, this.x + this.width + 3, this.y + this.height + 3 - 1, gradient1, gradient2);
			drawGradientRect(this.x - 3, this.y - 3, this.x + this.width + 3, this.y - 3 + 1, gradient1, gradient1);
			drawGradientRect(this.x - 3, this.y + this.height + 2, this.x + this.width + 3, this.y + this.height + 3, gradient2, gradient2);
			for (int index = 0; index < this.tooltips.size(); ++index) {
				this.font.drawStringWithShadow(this.tooltips.get(index), (int) this.x, (int) this.y, -1);
				if (index == 0) {
					this.y += 2;
				}
				this.y += 10;
			}
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	private int getMaxStringWidth() {
		int max = 0;
		for (final String s : this.tooltips) {
			final int width = this.font.getStringWidth(s);
			if (width > max) {
				max = width;
			}
		}
		return max;
	}

	@Override
	public void setPosition(float newX, float newY) {
		this.x = newX + 12;
		this.y = newY - 12;
		if (this.x + this.width + 6 > this.parent.width) {
			this.x -= 28 + this.width;
		}
		if (this.y + this.height + 6 > this.parent.height) {
			this.y = this.parent.height - this.height - 6;
		}
	}

}