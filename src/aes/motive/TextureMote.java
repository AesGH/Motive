package aes.motive;

import aes.utils.Vector3d;

public class TextureMote extends Texture {

	protected TextureMote(int u, int v, int uSize, int vSize) {
		super(u, v, uSize, vSize);
	}

	@Override
	protected void drawTexture(float x, float y, float z, float width, float height) {
		final double moteSize = 0.5f;
		final double moteInset = moteSize / 2;

		addQuadWithUV(new Vector3d(moteInset, moteInset, 1f - moteInset), 0, 0, new Vector3d(moteInset + moteSize, moteInset, 1f - moteInset), 1, 0,
				new Vector3d(moteInset + moteSize, moteInset + moteSize, 1f - moteInset), 1, 1, new Vector3d(moteInset, moteInset + moteSize, 1f - moteInset),
				0, 1);

		addQuadWithUV(new Vector3d(moteInset, moteInset, moteInset), 0, 0, new Vector3d(moteInset, moteInset + moteSize, moteInset), 0, 1, new Vector3d(
				moteInset + moteSize, moteInset + moteSize, moteInset), 1, 1, new Vector3d(moteInset + moteSize, moteInset, moteInset), 1, 0);

		addQuadWithUV(new Vector3d(moteInset, moteInset + moteSize, moteInset), 0, 0, new Vector3d(moteInset, moteInset + moteSize, moteInset + moteSize), 0,
				1, new Vector3d(moteInset + moteSize, moteInset + moteSize, moteInset + moteSize), 1, 1, new Vector3d(moteInset + moteSize, moteInset
						+ moteSize, moteInset), 1, 0);
		addQuadWithUV(new Vector3d(moteInset + moteSize, moteInset, moteInset), 1, 0, new Vector3d(moteInset + moteSize, moteInset, moteInset + moteSize), 1,
				1, new Vector3d(moteInset, moteInset, moteInset + moteSize), 0, 1, new Vector3d(moteInset, moteInset, moteInset), 0, 0);

		addQuadWithUV(new Vector3d(moteInset, moteInset, moteInset), 0, 0, new Vector3d(moteInset, moteInset, moteInset + moteSize), 0, 1, new Vector3d(
				moteInset, moteInset + moteSize, moteInset + moteSize), 1, 1, new Vector3d(moteInset, moteInset + moteSize, moteInset), 1, 0);
		addQuadWithUV(new Vector3d(moteInset + moteSize, moteInset + moteSize, moteInset), 1, 0, new Vector3d(moteInset + moteSize, moteInset + moteSize,
				moteInset + moteSize), 1, 1, new Vector3d(moteInset + moteSize, moteInset, moteInset + moteSize), 0, 1, new Vector3d(moteInset + moteSize,
				moteInset, moteInset), 0, 0);
	}
}
