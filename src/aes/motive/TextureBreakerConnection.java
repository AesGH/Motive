package aes.motive;

public class TextureBreakerConnection extends Texture {

	public static final double strutHeight = 0.15f;

	public static int getWhich() {
		return 2;
	}

	protected TextureBreakerConnection(int u, int v, int uSize, int vSize) {
		super(u, v, uSize, vSize);
	}

	@Override
	protected void drawTexture(float x, float y, float z, float width, float height) {

		this.u = 47 * getWhich();

		this.vectorScale = 1;

		final double middleStart = 0.5f - strutHeight / 2;
		final double middleEnd = 0.5f + strutHeight / 2;
		final double end = 0f;

		// left side
		addRectangleYZ(middleEnd, middleEnd, middleStart, 0, 0, middleStart, end, this.uSize, this.vSize, true);

		// right side
		addRectangleYZ(middleStart, middleEnd, middleStart, 0, 0, middleStart, end, this.uSize, this.vSize, false);

		// bottom side
		addRectangleXZ(middleEnd, middleStart, middleStart, 0, 0, middleStart, end, this.uSize, this.vSize, true);

		// top side
		addRectangleXZ(middleEnd, middleEnd, middleStart, 0, 0, middleStart, end, this.uSize, this.vSize, false);
	}

}
