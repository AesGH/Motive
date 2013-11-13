package aes.gui.core;

public abstract class OverlayScreen extends BasicScreen {

	protected BasicScreen bg;

	public OverlayScreen(BasicScreen bg) {
		super(bg);

		this.bg = bg;
	}

	@Override
	public void drawBackground() {
		this.bg.drawScreen(-1, -1, 0);
	}

	@Override
	protected void reopenedGui() {
	}

	@Override
	protected void revalidateGui() {
		this.bg.width = this.width;
		this.bg.height = this.height;
		this.bg.revalidateGui();
	}

}
