package aes.motive.render.model;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import aes.motive.Motive;
import aes.utils.Vector3d;

public class ModelMotiveBase extends ModelBase {
	private double nutHeight = 1.25D;

	private double faceHeight = 3.5f;

	private void drawBoltCorner() {
		GL11.glPushMatrix();
		drawBoltPanel();

		rotate(90, 0, 0, 1);
		rotate(-90, 1, 0, 0);

		drawBoltPanel();

		rotate(-90, 1, 0, 0);
		rotate(-90, 0, 1, 0);

		drawBoltPanel();
		GL11.glPopMatrix();
	}

	protected void drawBoltedFace(boolean withStruts) {
		texture(Motive.resourceBoltedFrameTexture);
		GL11.glPushMatrix();

		for (int x = 0; x < 4; x++) {
			drawBoltCorner();
			drawEdgeStrut();
			rotate(90, 0, 0, 1);
		}

		GL11.glPopMatrix();
	}

	protected void drawBoltedFrame() {
		GL11.glPushMatrix();
		drawBoltedFace(true);
		rotate(180, 1, 0, 0);
		drawBoltedFace(true);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		rotate(90, 0, 1, 0);
		for (int x = 0; x < 4; x++) {
			drawEdgeStrut();
			rotate(90, 1, 0, 0);
		}
		GL11.glPopMatrix();
	}

	private void drawBoltPanel() {
		texture(Motive.resourceBoltedFrameTexture);
		setUVOffset(0, 0);
		setVertexOffset(getNutHeight(), getNutHeight(), 0);

		final double frontZ = 128 - getNutHeight();
		final double backZ = frontZ - getFaceHeight();

		startDrawing();

		// front
		addQuadWithUV(new Vector3d(9, 20, frontZ), 9, 20, new Vector3d(0, 20, frontZ), 0, 20, new Vector3d(0, 0, frontZ), 0, 0, new Vector3d(9, 0, frontZ), 9,
				0);
		addQuadWithUV(new Vector3d(20, 7, frontZ), 20, 7, new Vector3d(9, 18, frontZ), 9, 18, new Vector3d(9, 0, frontZ), 9, 0, new Vector3d(20, 0, frontZ),
				20, 0);
		addQuadWithUV(new Vector3d(20, 7, frontZ), 19, 7, new Vector3d(20, 9, frontZ), 20, 9, new Vector3d(9, 20, frontZ), 20, 19, new Vector3d(9, 18, frontZ),
				19, 18);

		drawNut();

		// bottom
		addQuadWithUV(new Vector3d(0, 0, frontZ), 0, 0, new Vector3d(1, 1, backZ), 0, 0, new Vector3d(20, 1, backZ), 20, 0, new Vector3d(20, 0, frontZ), 20, 0);
		// right
		addQuadWithUV(new Vector3d(20, 0, frontZ), 19, 0, new Vector3d(20, 1, backZ), 19, 0, new Vector3d(20, 9, backZ), 19, 9, new Vector3d(20, 9, frontZ),
				19, 9);
		// left
		addQuadWithUV(new Vector3d(0, 0, frontZ), 0, 0, new Vector3d(0, 20, frontZ), 0, 20, new Vector3d(1, 20, backZ), 0, 20, new Vector3d(1, 1, backZ), 0, 0);
		// top
		addQuadWithUV(new Vector3d(0, 20, frontZ), 0, 19, new Vector3d(9, 20, frontZ), 9, 19, new Vector3d(9, 20, backZ), 9, 19, new Vector3d(1, 20, backZ), 0,
				19);
		// topright
		addQuadWithUV(new Vector3d(20, 9, frontZ), 19, 9, new Vector3d(20, 9, backZ), 19, 9, new Vector3d(9, 20, backZ), 19, 19, new Vector3d(9, 20, frontZ),
				19, 19);

		// back

		addQuadWithUV(new Vector3d(9, 1, backZ), 9, 1, new Vector3d(1, 1, backZ), 1, 1, new Vector3d(1, 20, backZ), 1, 20, new Vector3d(9, 20, backZ), 9, 20);
		addQuadWithUV(new Vector3d(20, 1, backZ), 20, 1, new Vector3d(9, 1, backZ), 9, 1, new Vector3d(9, 18, backZ), 9, 18, new Vector3d(20, 7, backZ), 20, 7);
		addQuadWithUV(new Vector3d(9, 18, backZ), 19, 18, new Vector3d(9, 20, backZ), 20, 19, new Vector3d(20, 9, backZ), 20, 9, new Vector3d(20, 7, backZ),
				19, 7);
		draw();
	}

	protected void drawDiagonalStrut() {
		startDrawing();

		texture(Motive.resourceStrutTexture);

		setUVOffset(0, 0);
		setVertexOffset(0, 0, 0);

		final double strutSize = 3D;

		final double strutDistance = Math.sqrt(strutSize * strutSize + strutSize * strutSize);
		final double strutHeight = strutDistance * 2;

		final double inset = getNutHeight() + getFaceHeight();

		addQuadWithUV(new Vector3d(inset + strutDistance, inset, 128 - inset), 0, 0,
				new Vector3d(128 - inset, 128 - inset - strutHeight, inset + strutDistance), 88, 0, new Vector3d(128 - inset, 128 - inset, inset
						+ strutDistance), 88, 6, new Vector3d(inset + strutDistance, inset + strutHeight, 128 - inset), 0, 6);

		addQuadWithUV(new Vector3d(inset, inset + strutHeight, 128 - inset - strutDistance), 0, 6,
				new Vector3d(128 - inset - strutDistance, 128 - inset, inset), 88, 6, new Vector3d(128 - inset - strutDistance, 128 - inset - strutHeight,
						inset), 88, 0, new Vector3d(inset, inset, 128 - inset - strutDistance), 0, 0);

		addQuadWithUV(new Vector3d(128 - inset, 128 - inset - strutHeight, inset + strutDistance), 88, 0, new Vector3d(inset + strutDistance, inset,
				128 - inset), 0, 0, new Vector3d(inset, inset, 128 - inset - strutDistance), 0, 0, new Vector3d(128 - inset - strutDistance, 128 - inset
				- strutHeight, inset), 88, 0);

		addQuadWithUV(new Vector3d(inset + strutDistance, inset + strutHeight, 128 - inset), 0, 6,
				new Vector3d(128 - inset, 128 - inset, inset + strutDistance), 88, 6, new Vector3d(128 - inset - strutDistance, 128 - inset, inset), 88, 6,
				new Vector3d(inset, inset + strutHeight, 128 - inset - strutDistance), 0, 6);

		/*
		 * addQuadWithUV( new Vector3d(inset + strutDistance, inset +
		 * strutHeight, 128 - inset), 0, 6, new Vector3d(128 - inset -
		 * strutDistance, 128-inset, inset + strutDistance), 88, 6, new
		 * Vector3d(128 - inset - strutDistance, 128-inset - strutDistance,
		 * inset + strutDistance), 88, 0, new Vector3d(inset + strutDistance,
		 * inset, 128 - inset), 0, 0 );
		 */
		/*
		 * addQuadWithUV(new Vector3d(128 - inset, inset + strutHeight, 128 -
		 * inset), 0, 0, new Vector3d(128 - inset, inset + strutHeight, 128 -
		 * inset - strutHeight), 0, 6, new Vector3d(inset, inset + strutHeight,
		 * 128 - inset - strutHeight), 88, 6, new Vector3d(inset, inset +
		 * strutHeight, 128 - inset), 88, 0); addQuadWithUV(new Vector3d(inset,
		 * inset + strutHeight, 128 - inset - strutHeight), 0, 6, new
		 * Vector3d(128 - inset, inset + strutHeight, 128 - inset -
		 * strutHeight), 88, 6, new Vector3d(128 - inset, inset, 128 - inset -
		 * strutHeight), 88, 0, new Vector3d(inset, inset, 128 - inset -
		 * strutHeight), 0, 0); addQuadWithUV(new Vector3d(inset, inset, 128 -
		 * inset), 88, 0, new Vector3d(inset, inset, 128 - inset - strutHeight),
		 * 88, 6, new Vector3d(128 - inset, inset, 128 - inset - strutHeight),
		 * 0, 6, new Vector3d(128 - inset, inset, 128 - inset), 0, 0);
		 */
		draw();
	}

	protected void drawDiagonalStruts() {

		GL11.glPushMatrix();
		for (int i = 0; i < 4; i++) {
			drawDiagonalStrut();
			rotate(90, 1, 0, 0);
		}
		// drawDiagonalStrut();

		GL11.glPopMatrix();

	}

	private void drawEdgeStrut() {
		startDrawing();

		setUVOffset(20, 0);
		setVertexOffset(0, 0, 0);

		final double strutHeight = 13D;
		final double inset = getNutHeight() + getFaceHeight();

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

		draw();
	}

	protected void drawMote(boolean pairedWithCurrentRemote) {

		startDrawing();

		setUVOffset(0, 0);
		setVertexOffset(0, 0, 0);

		texture(pairedWithCurrentRemote ? Motive.resourcePairedMoteTexture : Motive.resourceMoteTexture);

		final double moteSize = 64D;
		final double moteInset = 64D - moteSize / 2;

		addQuadWithUV(new Vector3d(moteInset, moteInset, 128 - moteInset), 0, 0, new Vector3d(moteInset + moteSize, moteInset, 128 - moteInset), 1, 0,
				new Vector3d(moteInset + moteSize, moteInset + moteSize, 128 - moteInset), 1, 1,
				new Vector3d(moteInset, moteInset + moteSize, 128 - moteInset), 0, 1);
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

		draw();
	}

	private void drawNut() {
		addQuadWithUV(new Vector3d(4, 4, 128 - getNutHeight()), 4, 4, new Vector3d(12, 4, 128 - getNutHeight()), 12, 4, new Vector3d(12, 4, 128), 12, 4,
				new Vector3d(4, 4, 128), 4, 4);

		addQuadWithUV(new Vector3d(4, 4, 128 - getNutHeight()), 4, 4, new Vector3d(4, 4, 128), 4, 4, new Vector3d(4, 12, 128), 4, 12, new Vector3d(4, 12,
				128 - getNutHeight()), 4, 12);

		addQuadWithUV(new Vector3d(12, 4, 128), 11, 4, new Vector3d(12, 4, 128 - getNutHeight()), 11, 4, new Vector3d(12, 12, 128 - getNutHeight()), 11, 12,
				new Vector3d(12, 12, 128), 11, 12);

		addQuadWithUV(new Vector3d(4, 12, 128), 4, 11, new Vector3d(12, 12, 128), 12, 11, new Vector3d(12, 12, 128 - getNutHeight()), 12, 11, new Vector3d(4,
				12, 128 - getNutHeight()), 4, 11);

		addQuadWithUV(new Vector3d(4, 4, 128), 4, 4, new Vector3d(12, 4, 128), 12, 4, new Vector3d(12, 12, 128), 12, 12, new Vector3d(4, 12, 128), 4, 12);
	}

	public double getFaceHeight() {
		return this.faceHeight;
	}

	public double getNutHeight() {
		return this.nutHeight;
	}

	@Override
	protected void renderModel(TileEntity tileEntity, ItemStack stack, float partialTickTime) {
		drawBoltedFrame();
		drawDiagonalStruts();
		drawMote(false);
	}

	public void setFaceHeight(double faceHeight) {
		this.faceHeight = faceHeight;
	}

	public void setNutHeight(double nutHeight) {
		this.nutHeight = nutHeight;
	}
}
