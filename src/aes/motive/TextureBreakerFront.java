package aes.motive;

import aes.utils.Vector3d;

public class TextureBreakerFront extends Texture {

	protected TextureBreakerFront(int u, int v, int uSize, int vSize) {
		super(u, v, uSize, vSize);
	}

	@Override
	protected void drawTexture(float x, float y, float z, float width, float height) {

		final double inset = 0; // 8D / 128D;

		addQuadWithUV(new Vector3d(inset, inset, inset), 0, 0, new Vector3d(inset, 1 - inset, inset), 0, this.vSize, new Vector3d(1 - inset, 1 - inset, inset),
				this.uSize, this.vSize, new Vector3d(1 - inset, inset, inset), this.uSize, 0);

		addQuadWithUV(new Vector3d(inset, inset, inset), 0, 0, new Vector3d(1 - inset, inset, inset), this.uSize, 0, new Vector3d(1 - inset, 1 - inset, inset),
				this.uSize, this.vSize, new Vector3d(inset, 1 - inset, inset), 0, this.vSize);
	}
}
