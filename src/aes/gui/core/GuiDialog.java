package aes.gui.core;

import aes.motive.Texture;

public abstract class GuiDialog extends BasicScreen {
	protected int xSize;
	protected int ySize;
	protected int xPos;
	protected int yPos;

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawBackground() {
		super.drawBackground();
		Texture.dialogBorder.draw(this.xPos, this.yPos, this.zLevel, this.xSize, this.ySize);
		update();
	}

	protected void initGui(int width, int height) {
		super.initGui();
		this.xSize = width;
		this.ySize = height;
		revalidateGui();
	}

	@Override
	public void keyTyped(char par1, int par2) {
		if (par1 == 27 && par2 == 1) {
			close();
			return;
		}
		super.keyTyped(par1, par2);
	}

	@Override
	protected void reopenedGui() {

	}

	@Override
	protected void revalidateGui() {
		this.xPos = (this.width - this.xSize) / 2;
		this.yPos = (this.height - this.ySize) / 2;
	}

	protected void update() {
	}

}
