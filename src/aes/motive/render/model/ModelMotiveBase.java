package aes.motive.render.model;

import org.lwjgl.opengl.GL11;

import aes.motive.Texture;

public class ModelMotiveBase extends ModelBase {
	private static double nutHeight = 1.25D;
	private static double faceHeight = 3.5f;

	public static double getFaceHeight() {
		return faceHeight;
	}

	public static double getNutHeight() {
		return nutHeight;
	}

	private void drawBoltCorner() {
		GL11.glPushMatrix();
		Texture.boltPanel.draw();

		rotate(90, 0, 0, 1);
		rotate(-90, 1, 0, 0);

		Texture.boltPanel.draw();

		rotate(-90, 1, 0, 0);
		rotate(-90, 0, 1, 0);

		Texture.boltPanel.draw();
		GL11.glPopMatrix();
	}

	protected void drawBoltedFace(boolean withStruts) {
		GL11.glPushMatrix();

		for (int x = 0; x < 4; x++) {
			drawBoltCorner();
			Texture.edgeStrut.draw();
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
			Texture.edgeStrut.draw();
			rotate(90, 1, 0, 0);
		}
		GL11.glPopMatrix();
	}

	protected void drawDiagonalStruts() {

		GL11.glPushMatrix();
		for (int i = 0; i < 4; i++) {
			Texture.diagonalStrut.draw();
			rotate(90, 1, 0, 0);
		}
		GL11.glPopMatrix();

	}

	protected void drawMote(boolean pairedWithCurrentRemote) {
		(pairedWithCurrentRemote ? Texture.motePaired : Texture.mote).draw(0, 0, 0);
	}
}
