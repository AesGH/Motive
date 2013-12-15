package aes.motive.render.model;

import aes.motive.Texture;
import aes.utils.Vector3d;

public class TextureEdgeStrut extends Texture {

	public TextureEdgeStrut(int u, int v, int uSize, int vSize) {
		super(u, v, uSize, vSize);
		this.vectorScale = 1D / 128D;
	}

	@Override
	protected void drawTexture(float x, float y, float z, float width, float height) {
		final double strutHeight = 13D;
		final double inset = ModelMotiveBase.getNutHeight() + ModelMotiveBase.getFaceHeight();

		addQuadWithUV(new Vector3d(inset, inset, 128 - inset), 0, 0, new Vector3d(128 - inset, inset, 128 - inset), 88, 0, new Vector3d(128 - inset, inset
				+ strutHeight, 128 - inset), 88, 6, new Vector3d(inset, inset + strutHeight, 128 - inset), 0, 6);
		addQuadWithUV(new Vector3d(128 - inset, inset + strutHeight, 128 - inset), 0, 0, new Vector3d(128 - inset, inset + strutHeight, 128 - inset
				- strutHeight), 0, 6, new Vector3d(inset, inset + strutHeight, 128 - inset - strutHeight), 88, 6, new Vector3d(inset, inset + strutHeight,
				128 - inset), 88, 0);
		addQuadWithUV(new Vector3d(inset, inset + strutHeight, 128 - inset - strutHeight), 0, 6, new Vector3d(128 - inset, inset + strutHeight, 128 - inset
				- strutHeight), 88, 6, new Vector3d(128 - inset, inset, 128 - inset - strutHeight), 88, 0,
				new Vector3d(inset, inset, 128 - inset - strutHeight), 0, 0);
		addQuadWithUV(new Vector3d(inset, inset, 128 - inset), 88, 0, new Vector3d(inset, inset, 128 - inset - strutHeight), 88, 6, new Vector3d(128 - inset,
				inset, 128 - inset - strutHeight), 0, 6, new Vector3d(128 - inset, inset, 128 - inset), 0, 0);
	}
}
