package aes.motive.render.model;

import aes.motive.Texture;

public class TextureBreakerConnection extends Texture {

	public static final double strutHeight = 0.15f;

	public static int getWhich() {
		return 2;
	}

	public TextureBreakerConnection(int u, int v, int uSize, int vSize) {
		super(u, v, uSize, vSize);
	}

	@Override
	protected void drawTexture(float x, float y, float z, float width, float height) {

		this.u = 47 * getWhich();

		this.vectorScale = 1;

		final double middleStart = 0.5f - strutHeight / 2;
		final double middleEnd = 0.5f + strutHeight / 2;
		final double end = 0f;

		addRectangleYZ(middleEnd, middleEnd, middleStart, 0, 0, middleStart, end, this.uSize, this.vSize, true);
		addRectangleYZ(middleStart, middleEnd, middleStart, 0, 0, middleStart, end, this.uSize, this.vSize, false);
		addRectangleXZ(middleEnd, middleStart, middleStart, 0, 0, middleStart, end, this.uSize, this.vSize, true);
		addRectangleXZ(middleEnd, middleEnd, middleStart, 0, 0, middleStart, end, this.uSize, this.vSize, false);
	}

}
