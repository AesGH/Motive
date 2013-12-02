package aes.motive.render.model;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import aes.motive.Texture;
import aes.motive.tileentity.TileEntityBreaker;

public class ModelBreaker extends ModelMotiveBase {
	public ModelBreaker() {
	}

	@Override
	protected void renderModel(TileEntity tileEntity, ItemStack stack, float partialTickTime) {
		Texture.breakerFront.draw();
		Texture.breakerBody.draw();
		Texture.breakerEndcap.draw();

		if (!(tileEntity instanceof TileEntityBreaker))
			return;

		final TileEntityBreaker tileEntityBreaker = (TileEntityBreaker) tileEntity;

		if (tileEntityBreaker.getConnectionMask().isConnectedLeft()) {
			GL11.glPushMatrix();

			GL11.glTranslated(0.5f, 0.5f, 0.5f);
			GL11.glRotated(90, 0, 1, 0);
			GL11.glTranslated(-0.5f, -0.5f, -0.5f);

			Texture.breakerConnection.draw();
			GL11.glPopMatrix();
		}

		if (tileEntityBreaker.getConnectionMask().isConnectedRight()) {
			GL11.glPushMatrix();

			GL11.glTranslated(0.5f, 0.5f, 0.5f);
			GL11.glRotated(270, 0, 1, 0);
			GL11.glTranslated(-0.5f, -0.5f, -0.5f);

			Texture.breakerConnection.draw();
			GL11.glPopMatrix();
		}

		if (tileEntityBreaker.getConnectionMask().isConnectedBack()) {
			GL11.glPushMatrix();

			GL11.glTranslated(0.5f, 0.5f, 0.5f);
			GL11.glRotated(180, 0, 1, 0);
			GL11.glTranslated(-0.5f, -0.5f, -0.5f);

			Texture.breakerConnection.draw();
			GL11.glPopMatrix();
		}

		if (tileEntityBreaker.getConnectionMask().isConnectedUp()) {
			GL11.glPushMatrix();

			GL11.glTranslated(0.5f, 0.5f, 0.5f);
			GL11.glRotated(90, 1, 0, 0);
			GL11.glTranslated(-0.5f, -0.5f, -0.5f);

			Texture.breakerConnection.draw();
			GL11.glPopMatrix();
		}

		if (tileEntityBreaker.getConnectionMask().isConnectedDown()) {
			GL11.glPushMatrix();

			GL11.glTranslated(0.5f, 0.5f, 0.5f);
			GL11.glRotated(270, 1, 0, 0);
			GL11.glTranslated(-0.5f, -0.5f, -0.5f);

			Texture.breakerConnection.draw();
			GL11.glPopMatrix();
		}
	}
}
