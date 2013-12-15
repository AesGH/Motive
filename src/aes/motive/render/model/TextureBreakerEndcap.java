package aes.motive.render.model;

import aes.motive.Texture;

public class TextureBreakerEndcap extends Texture {

	public TextureBreakerEndcap(int u, int v, int uSize, int vSize) {
		super(u, v, uSize, vSize);
	}

	@Override
	protected void drawTexture(float x, float y, float z, float width, float height) {
		this.u = 36 + 47 * TextureBreakerConnection.getWhich();

		this.vectorScale = 1;
		final double strutHeight = 0.15f;

		final double middleStart = 0.5f - strutHeight / 2;
		final double middleEnd = 0.5f + strutHeight / 2;
		
		addRectangleXY(middleEnd, middleEnd, middleEnd, 0, 0, middleStart, middleStart, this.uSize, this.vSize, true);
	}
}
