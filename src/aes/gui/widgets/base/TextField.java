package aes.gui.widgets.base;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

/**
 * 
 * Abstract representation of a minecraft textfield. This is pretty much a copy
 * of the vanilla textfield, so it support highlighting/copying/pasting text.
 * 
 */
public abstract class TextField extends FocusableWidget {

	public interface CharacterFilter {
		public String filter(String s);

		public boolean isAllowedCharacter(char c);
	}

	protected String text;
	protected int maxLength = 32;
	protected boolean focused;
	protected int cursorCounter, cursorPosition, charOffset, selectionEnd;
	protected int color;

	protected CharacterFilter filter;

	public TextField(int width, int height, CharacterFilter filter) {
		super(width, height);

		this.text = "";
		this.filter = filter;
		this.color = 0xffffff;
	}

	@Override
	public boolean click(int mx, int my) {
		return inBounds(mx, my);
	}

	public void deleteFromCursor(int amt) {
		if (this.text.length() > 0) {
			if (this.selectionEnd != this.cursorPosition) {
				writeText("");
			} else {
				final boolean flag = amt < 0;
				final int j = flag ? this.cursorPosition + amt : this.cursorPosition;
				final int k = flag ? this.cursorPosition : this.cursorPosition + amt;
				String s = "";
				if (j >= 0) {
					s = this.text.substring(0, j);
				}
				if (k < this.text.length()) {
					s = s + this.text.substring(k);
				}
				this.text = s;
				if (flag) {
					moveCursorBy(amt);
				}
			}
		}
	}

	@Override
	public void draw(int mx, int my) {
		drawBackground();

		final int j = this.cursorPosition - this.charOffset;
		int k = this.selectionEnd - this.charOffset;
		final String s = fontRenderer.trimStringToWidth(this.text.substring(this.charOffset), getInternalWidth());
		final boolean flag = j >= 0 && j <= s.length();
		final boolean cursor = this.focused && this.cursorCounter / 6 % 2 == 0 && flag;
		final int l = getDrawX();
		final int i1 = getDrawY();
		int j1 = l;

		if (k > s.length()) {
			k = s.length();
		}

		if (s.length() > 0) {
			final String s1 = flag ? s.substring(0, j) : s;
			j1 = fontRenderer.drawStringWithShadow(s1, l, i1, this.color);
		}

		final boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.maxLength;
		int k1 = j1;

		if (!flag) {
			k1 = j > 0 ? l + (int) this.width : l;
		} else if (flag2) {
			k1 = j1 - 1;
			--j1;
		}
		if (s.length() > 0 && flag && j < s.length()) {
			fontRenderer.drawStringWithShadow(s.substring(j), j1, i1, this.color);
		}
		if (cursor) {
			if (flag2) {
				Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + fontRenderer.FONT_HEIGHT, -3092272);
			} else {
				fontRenderer.drawStringWithShadow("_", k1, i1, this.color);
			}
		}
		if (k != j) {
			final int l1 = l + fontRenderer.getStringWidth(s.substring(0, k));
			drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + fontRenderer.FONT_HEIGHT);
		}

	}

	protected abstract void drawBackground();

	protected void drawCursorVertical(int x1, int y1, int x2, int y2) {
		int temp;
		if (x1 < x2) {
			temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if (y1 < y2) {
			temp = y1;
			y1 = y2;
			y2 = temp;
		}

		final Tessellator tessellator = Tessellator.instance;
		GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glLogicOp(GL11.GL_OR_REVERSE);
		tessellator.startDrawingQuads();
		tessellator.addVertex(x1, y2, 0.0D);
		tessellator.addVertex(x2, y2, 0.0D);
		tessellator.addVertex(x2, y1, 0.0D);
		tessellator.addVertex(x1, y1, 0.0D);
		tessellator.draw();
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	@Override
	public void focusGained() {
		this.cursorCounter = 0;
		this.focused = true;
	}

	@Override
	public void focusLost() {
		this.focused = false;
	}

	protected abstract int getDrawX();

	protected abstract int getDrawY();

	public abstract int getInternalWidth();

	public String getSelectedtext() {
		final int start = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		final int end = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		return this.text.substring(start, end);
	}

	public String getText() {
		return this.text;
	}

	@Override
	public void handleClick(int mx, int my, int button) {
		int pos = mx - (int) this.x;
		pos -= Math.abs(getInternalWidth() - this.width) / 2;

		final String s = fontRenderer.trimStringToWidth(this.text.substring(this.charOffset), (int) getWidth());
		setCursorPosition(fontRenderer.trimStringToWidth(s, pos).length() + this.charOffset);
	}

	@Override
	public boolean keyTyped(char par1, int par2) {
		if (this.focused) {
			switch (par1) {
			case 1:
				setCursorPosition(this.text.length());
				setSelectionPos(0);
				return true;
			case 3:
				GuiScreen.setClipboardString(getSelectedtext());
				return true;
			case 22:
				writeText(GuiScreen.getClipboardString());
				return true;
			case 24:
				GuiScreen.setClipboardString(getSelectedtext());
				writeText("");
				return true;
			default:
				switch (par2) {
				case 14:
					deleteFromCursor(-1);
					return true;
				case 199:
					setSelectionPos(0);
					setCursorPosition(0);
					return true;
				case 203:
					if (GuiScreen.isShiftKeyDown()) {
						setSelectionPos(this.selectionEnd - 1);
					} else {
						moveCursorBy(-1);
					}
					return true;
				case 205:
					if (GuiScreen.isShiftKeyDown()) {
						setSelectionPos(this.selectionEnd + 1);
					} else {
						moveCursorBy(1);
					}
					return true;
				case 207:
					if (GuiScreen.isShiftKeyDown()) {
						setSelectionPos(this.text.length());
					} else {
						setCursorPosition(this.text.length());
					}
					return true;
				case 211:
					deleteFromCursor(1);
					return true;
				default:
					if (this.filter.isAllowedCharacter(par1)) {
						writeText(Character.toString(par1));
						return true;
					}
					return false;
				}
			}
		}
		return false;
	}

	public void moveCursorBy(int offs) {
		setCursorPosition(this.selectionEnd + offs);
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setCursorPosition(int index) {
		this.cursorPosition = MathHelper.clamp_int(index, 0, this.text.length());
		setSelectionPos(this.cursorPosition);
	}

	public void setMaxLength(int length) {
		this.maxLength = length;

		if (this.text.length() > length) {
			this.text = this.text.substring(0, length);
		}
	}

	public void setSelectionPos(int index) {
		index = MathHelper.clamp_int(index, 0, this.text.length());
		this.selectionEnd = index;

		if (this.charOffset > index) {
			this.charOffset = index;
		}

		final int width = getInternalWidth();
		final String s = fontRenderer.trimStringToWidth(this.text.substring(this.charOffset), width);
		final int pos = s.length() + this.charOffset;

		if (index == this.charOffset) {
			this.charOffset -= fontRenderer.trimStringToWidth(this.text, width, true).length();
		}
		if (index > pos) {
			this.charOffset += index - 1;
		} else if (index <= this.charOffset) {
			this.charOffset = index;
		}

		this.charOffset = MathHelper.clamp_int(this.charOffset, 0, this.text.length());
	}

	public void setText(String str) {
		this.text = str.length() > this.maxLength ? str.substring(0, this.maxLength) : str;
		setCursorPosition(this.text.length());
	}

	@Override
	public void update() {
		++this.cursorCounter;
	}

	public void writeText(String str) {
		String s1 = "";
		str = this.filter.filter(str);
		final int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		final int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		final int k = this.maxLength - this.text.length() - (i - this.selectionEnd);
		if (this.text.length() > 0) {
			s1 = s1 + this.text.substring(0, i);
		}

		int l;

		if (k < str.length()) {
			s1 = s1 + str.substring(0, k);
			l = k;
		} else {
			s1 = s1 + str;
			l = str.length();
		}

		if (this.text.length() > 0 && j < this.text.length()) {
			s1 = s1 + this.text.substring(j);
		}

		this.text = s1;
		moveCursorBy(i - this.selectionEnd + l);
	}

}
