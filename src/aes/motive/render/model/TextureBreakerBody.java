package aes.motive.render.model;

import aes.motive.Texture;

public class TextureBreakerBody extends Texture {

	public TextureBreakerBody(int u, int v, int uSize, int vSize) {
		super(u, v, uSize, vSize);
	}

	@Override
	protected void drawTexture(float x, float y, float z, float width, float height) {
		this.u = 47 * TextureBreakerConnection.getWhich();

		this.vectorScale = 1;
		final double strutHeight = 0.15f;

		final double middleStart = 0.5f - strutHeight / 2;
		final double middleEnd = 0.5f + strutHeight / 2;
		final double end = 0f;

		// left side
		addRectangleYZ(middleEnd, middleEnd, middleEnd, 0, this.vSize, middleStart, end, this.uSize / 2, 0, true);

		// right side
		addRectangleYZ(middleStart, middleEnd, middleEnd, 0, 0, middleStart, end, this.uSize / 2, this.vSize, false);

		// bottom side
		addRectangleXZ(middleEnd, middleStart, middleEnd, 0, 0, middleStart, end, this.uSize / 2, this.vSize, true);

		// top side
		addRectangleXZ(middleEnd, middleEnd, middleEnd, 0, 0, middleStart, end, this.uSize / 2, this.vSize, false);
	}
}