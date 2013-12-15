package aes.motive.render.model;

import aes.motive.Texture;
import aes.utils.Vector3d;

public class TextureBoltPanel extends Texture {
	private final double nutHeight;
	private double frontZ;
	private double backZ;

	public TextureBoltPanel(int u, int v, int uSize, int vSize) {
		super(u, v, uSize, vSize);
		this.nutHeight = ModelMotiveBase.getNutHeight();
		this.vectorOffsetX = this.nutHeight;
		this.vectorOffsetY = this.nutHeight;
		this.vectorScale = 1D / 128D;
	}

	protected void drawBoltPanelBack() {
		addQuadWithUV(new Vector3d(9, 1, this.backZ), 9, 1, new Vector3d(1, 1, this.backZ), 1, 1, new Vector3d(1, 20, this.backZ), 1, 20, new Vector3d(9, 20,
				this.backZ), 9, 20);
		addQuadWithUV(new Vector3d(20, 1, this.backZ), 20, 1, new Vector3d(9, 1, this.backZ), 9, 1, new Vector3d(9, 18, this.backZ), 9, 18, new Vector3d(20, 7,
				this.backZ), 20, 7);
		addQuadWithUV(new Vector3d(9, 18, this.backZ), 19, 18, new Vector3d(9, 20, this.backZ), 20, 19, new Vector3d(20, 9, this.backZ), 20, 9, new Vector3d(
				20, 7, this.backZ), 19, 7);
	}

	protected void drawBoltPanelBottom() {
		addQuadWithUV(new Vector3d(0, 0, this.frontZ), 0, 0, new Vector3d(1, 1, this.backZ), 0, 0, new Vector3d(20, 1, this.backZ), 20, 0, new Vector3d(20, 0,
				this.frontZ), 20, 0);
	}

	protected void drawBoltPanelFront() {
		addQuadWithUV(new Vector3d(9, 20, this.frontZ), 9, 20, new Vector3d(0, 20, this.frontZ), 0, 20, new Vector3d(0, 0, this.frontZ), 0, 0, new Vector3d(9,
				0, this.frontZ), 9, 0);
		addQuadWithUV(new Vector3d(20, 7, this.frontZ), 20, 7, new Vector3d(9, 18, this.frontZ), 9, 18, new Vector3d(9, 0, this.frontZ), 9, 0, new Vector3d(20,
				0, this.frontZ), 20, 0);
		addQuadWithUV(new Vector3d(20, 7, this.frontZ), 19, 7, new Vector3d(20, 9, this.frontZ), 20, 9, new Vector3d(9, 20, this.frontZ), 20, 19, new Vector3d(
				9, 18, this.frontZ), 19, 18);
	}

	protected void drawBoltPanelLeft() {
		addQuadWithUV(new Vector3d(0, 0, this.frontZ), 0, 0, new Vector3d(0, 20, this.frontZ), 0, 20, new Vector3d(1, 20, this.backZ), 0, 20, new Vector3d(1,
				1, this.backZ), 0, 0);
	}

	protected void drawBoltPanelRight() {
		addQuadWithUV(new Vector3d(20, 0, this.frontZ), 19, 0, new Vector3d(20, 1, this.backZ), 19, 0, new Vector3d(20, 9, this.backZ), 19, 9, new Vector3d(20,
				9, this.frontZ), 19, 9);
	}

	protected void drawBoltPanelTop() {
		addQuadWithUV(new Vector3d(0, 20, this.frontZ), 0, 19, new Vector3d(9, 20, this.frontZ), 9, 19, new Vector3d(9, 20, this.backZ), 9, 19, new Vector3d(1,
				20, this.backZ), 0, 19);
	}

	protected void drawBoltPanelTopRight() {
		addQuadWithUV(new Vector3d(20, 9, this.frontZ), 19, 9, new Vector3d(20, 9, this.backZ), 19, 9, new Vector3d(9, 20, this.backZ), 19, 19, new Vector3d(9,
				20, this.frontZ), 19, 19);
	}

	private void drawNut() {
		addQuadWithUV(new Vector3d(4, 4, 128 - this.nutHeight), 4, 4, new Vector3d(12, 4, 128 - this.nutHeight), 12, 4, new Vector3d(12, 4, 128), 12, 4,
				new Vector3d(4, 4, 128), 4, 4);

		addQuadWithUV(new Vector3d(4, 4, 128 - this.nutHeight), 4, 4, new Vector3d(4, 4, 128), 4, 4, new Vector3d(4, 12, 128), 4, 12, new Vector3d(4, 12,
				128 - this.nutHeight), 4, 12);

		addQuadWithUV(new Vector3d(12, 4, 128), 11, 4, new Vector3d(12, 4, 128 - this.nutHeight), 11, 4, new Vector3d(12, 12, 128 - this.nutHeight), 11, 12,
				new Vector3d(12, 12, 128), 11, 12);

		addQuadWithUV(new Vector3d(4, 12, 128), 4, 11, new Vector3d(12, 12, 128), 12, 11, new Vector3d(12, 12, 128 - this.nutHeight), 12, 11, new Vector3d(4,
				12, 128 - this.nutHeight), 4, 11);

		addQuadWithUV(new Vector3d(4, 4, 128), 4, 4, new Vector3d(12, 4, 128), 12, 4, new Vector3d(12, 12, 128), 12, 12, new Vector3d(4, 12, 128), 4, 12);
	}

	@Override
	protected void drawTexture(float x, float y, float z, float width, float height) {
		this.frontZ = 128 - this.nutHeight;
		this.backZ = this.frontZ - this.nutHeight;

		drawBoltPanelFront();

		drawNut();

		drawBoltPanelBottom();
		drawBoltPanelRight();
		drawBoltPanelLeft();
		drawBoltPanelTop();
		drawBoltPanelTopRight();
		drawBoltPanelBack();
	}
}
