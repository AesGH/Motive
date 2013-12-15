package aes.motive.render.model;

import aes.motive.Texture;
import aes.utils.Vector3d;

public class TextureDiagonalStrut extends Texture {

	public TextureDiagonalStrut(int u, int v, int uSize, int vSize) {
		super(u, v, uSize, vSize);
		this.vectorScale = 1D / 128D;
	}

	@Override
	protected void drawTexture(float x, float y, float z, float width, float height) {
		final double strutSize = 3D;

		final double strutDistance = Math.sqrt(strutSize * strutSize + strutSize * strutSize);
		final double strutHeight = strutDistance * 2;

		final double inset = ModelMotiveBase.getNutHeight() + ModelMotiveBase.getFaceHeight();

		addQuadWithUV(new Vector3d(inset + strutDistance, inset, 128 - inset), 0, 0,
				new Vector3d(128 - inset, 128 - inset - strutHeight, inset + strutDistance), this.uSize, 0, new Vector3d(128 - inset, 128 - inset, inset
						+ strutDistance), this.uSize, this.vSize, new Vector3d(inset + strutDistance, inset + strutHeight, 128 - inset), 0, this.vSize);

		addQuadWithUV(new Vector3d(inset, inset + strutHeight, 128 - inset - strutDistance), 0, 0,
				new Vector3d(128 - inset - strutDistance, 128 - inset, inset), this.uSize, 0, new Vector3d(128 - inset - strutDistance, 128 - inset
						- strutHeight, inset), this.uSize, this.vSize, new Vector3d(inset, inset, 128 - inset - strutDistance), 0, this.vSize);

		addQuadWithUV(new Vector3d(128 - inset, 128 - inset - strutHeight, inset + strutDistance), 0, 0,
				new Vector3d(inset + strutDistance, inset, 128 - inset), this.uSize, 0, new Vector3d(inset, inset, 128 - inset - strutDistance), this.uSize,
				this.vSize, new Vector3d(128 - inset - strutDistance, 128 - inset - strutHeight, inset), 0, this.vSize);

		addQuadWithUV(new Vector3d(inset + strutDistance, inset + strutHeight, 128 - inset), 0, 0,
				new Vector3d(128 - inset, 128 - inset, inset + strutDistance), this.uSize, 0, new Vector3d(128 - inset - strutDistance, 128 - inset, inset),
				this.uSize, this.vSize, new Vector3d(inset, inset + strutHeight, 128 - inset - strutDistance), 0, this.vSize);

	}
}
