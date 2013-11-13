package aes.gui.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import aes.gui.widgets.base.Button.ButtonHandler;
import aes.gui.widgets.base.Container;
import aes.gui.widgets.base.Widget;

/**
 * 
 * The core GuiScreen - use this class for your GUIs.
 * 
 */
public abstract class BasicScreen extends GuiScreen {

	public class CloseHandler implements ButtonHandler {
		@Override
		public void buttonClicked(Widget button) {
			close();
		}
	}

	private final GuiScreen parent;
	private boolean hasInit, closed;
	protected List<Container> containers;

	protected Container selectedContainer;

	public BasicScreen(GuiScreen parent) {
		this.parent = parent;
		this.containers = new ArrayList<Container>();
	}

	public void close() {
		this.mc.displayGuiScreen(this.parent);
	}

	/**
	 * Called ONCE to create this GUI. Create your containers and widgets here.
	 */
	protected abstract void createGui();

	/**
	 * Called to draw this screen's background
	 */
	protected void drawBackground() {
		drawDefaultBackground();
	}

	public void drawCenteredStringNoShadow(FontRenderer ft, String str, int cx, int y, int color) {
		ft.drawString(str, cx - ft.getStringWidth(str) / 2, y, color);
	}

	@Override
	public void drawScreen(int mx, int my, float f) {
		drawBackground();
		final List<Widget> overlays = new ArrayList<Widget>();
		final int scale = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight).getScaleFactor();
		for (final Container c : this.containers) {
			overlays.addAll(c.draw(mx, my, scale));
		}
		for (final Widget w : overlays) {
			w.draw(mx, my);
		}
	}

	public List<Container> getContainers() {
		return this.containers;
	}

	public GuiScreen getParent() {
		return this.parent;
	}

	/**
	 * See {@link GuiScreen#handleMouseInput} for more information about mx and
	 * my.
	 */
	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int delta = Mouse.getEventDWheel();
		if (delta != 0) {
			final int mx = Mouse.getEventX() * this.width / this.mc.displayWidth;
			final int my = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
			boolean handled = false;
			delta = MathHelper.clamp_int(delta, -5, 5);

			for (final Container c : this.containers) {
				if (c.inBounds(mx, my)) {
					c.mouseWheel(delta);
					handled = true;
					break;
				}
			}
			if (!handled && this.selectedContainer != null) {
				this.selectedContainer.mouseWheel(delta);
			}
		}
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);

		if (!this.hasInit) {
			createGui();
			this.hasInit = true;
		}
		revalidateGui();
		if (this.closed) {
			reopenedGui();
			this.closed = false;
		}
	}

	@Override
	public void keyTyped(char c, int code) {
		final boolean handled = this.selectedContainer != null ? this.selectedContainer.keyTyped(c, code) : false;
		if (!handled) {
			unhandledKeyTyped(c, code);
		}
	}

	@Override
	protected void mouseClicked(int mx, int my, int code) {
		if (code == 0) {
			for (final Container c : this.containers) {
				if (c.mouseClicked(mx, my)) {
					this.selectedContainer = c;
					break;
				}
			}
			for (final Container c : this.containers)
				if (c != this.selectedContainer) {
					c.setFocused(null);
				}
		}
	}

	@Override
	protected void mouseMovedOrUp(int mx, int my, int code) {
		if (code == 0) {
			for (final Container c : this.containers) {
				c.mouseReleased(mx, my);
			}
		}
	}

	@Override
	public void onGuiClosed() {
		this.closed = true;
		Keyboard.enableRepeatEvents(false);
	}

	/**
	 * Called when this GUI is reopened after being closed.
	 */
	protected abstract void reopenedGui();

	/**
	 * Revalidate this GUI. Reset your widget locations/dimensions here.
	 */
	protected abstract void revalidateGui();

	/**
	 * Called when the selectedContainer did not capture this keyboard event.
	 * 
	 * @param c
	 *            Character typed (if any)
	 * @param code
	 *            Keyboard.KEY_ code for this key
	 */
	protected void unhandledKeyTyped(char c, int code) {
	}

	@Override
	public void updateScreen() {
		for (final Container c : this.containers) {
			c.update();
		}
	}

}
