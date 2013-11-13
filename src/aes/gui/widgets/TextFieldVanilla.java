package aes.gui.widgets;

import net.minecraft.util.ChatAllowedCharacters;
import aes.gui.widgets.base.TextField;

/**
 * 
 * Vanilla GuiTextField in Widget form.
 * 
 */
public class TextFieldVanilla extends TextField {

	public static class NumberFilter implements CharacterFilter {
		@Override
		public String filter(String s) {
			final StringBuilder sb = new StringBuilder();
			for (final char c : s.toCharArray())
				if (isAllowedCharacter(c)) {
					sb.append(c);
				}
			return sb.toString();
		}

		@Override
		public boolean isAllowedCharacter(char c) {
			return Character.isDigit(c);
		}

	}

	public static class VanillaFilter implements CharacterFilter {
		@Override
		public String filter(String s) {
			return ChatAllowedCharacters.filerAllowedCharacters(s);
		}

		@Override
		public boolean isAllowedCharacter(char c) {
			return ChatAllowedCharacters.isAllowedCharacter(c);
		}

	}

	private int outerColor, innerColor;

	public TextFieldVanilla(CharacterFilter filter) {
		this(200, 20, filter);
	}

	public TextFieldVanilla(int width, int height, CharacterFilter filter) {
		super(width, height, filter);

		this.outerColor = -6250336;
		this.innerColor = -16777216;
	}

	public TextFieldVanilla(int width, int height, int innerColor, int outerColor, CharacterFilter filter) {
		super(width, height, filter);

		this.outerColor = outerColor;
		this.innerColor = innerColor;
	}

	@Override
	protected void drawBackground() {
		drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, this.outerColor);
		drawRect(this.x, this.y, this.x + this.width, this.y + this.height, this.innerColor);
	}

	@Override
	protected int getDrawX() {
		return this.x + 4;
	}

	@Override
	protected int getDrawY() {
		return this.y + (this.height - 8) / 2;
	}

	@Override
	public int getInternalWidth() {
		return this.width - 8;
	}

	public void setInnerColor(int c) {
		this.innerColor = c;
	}

	public void setOuterColor(int c) {
		this.outerColor = c;
	}

}
