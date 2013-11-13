package aes.motive.render;

import net.minecraft.client.model.ModelBase;

import org.lwjgl.opengl.GL11;

import aes.base.TileEntitySpecialRendererBase;
import aes.motive.tileentity.TileEntityMoverBase;

public class TileEntityMoteRenderer extends TileEntitySpecialRendererBase {

	@Override
	protected ModelBase getModel() {
		return new ModelMote();
	}

	/**
	 * sets the scale for the slime based on getSlimeSize in EntitySlime
	 */
	protected void scale(TileEntityMoverBase tileEntityMoverBase, float par2) {
		final float f1 = 1;
		final float f2 = (tileEntityMoverBase.prevSquishFactor + (tileEntityMoverBase.squishFactor - tileEntityMoverBase.prevSquishFactor) * par2)
				/ (f1 * 0.5F + 1.0F);
		final float f3 = 1.0F / (f2 + 1.0F);

		// GL11.glPushMatrix();

		// GL11.glTranslatef(0.5F*f3, 0.5f*f3, 0.5F*f3);
		GL11.glScalef(f3 * f1, f3 * f1, f3 * f1);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		// GL11.glPopMatrix();
		// GL11.glTranslatef(-4f, -8f, 4f);
	}

	/*
	 * @Override public void renderTileEntityAt(TileEntity te, double x, double
	 * y, double z, float scale) { GL11.glPushMatrix();
	 * 
	 * GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
	 * 
	 * func_110628_a(new
	 * ResourceLocation("inmotion:textures/blocks/TextureMover.png"));
	 * 
	 * // GL11.glPushMatrix(); // GL11.glScalef(scale, scale, scale);
	 * GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
	 * 
	 * // adjustLightFixture(te.getWorldObj(), (int)x, (int)y, (int)z,
	 * InMotion.BlockMover);
	 * 
	 * this.model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
	 * // GL11.glPopMatrix();
	 * 
	 * GL11.glPopMatrix(); }
	 * 
	 * // Set the lighting stuff, so it changes it's brightness properly.
	 * private void adjustLightFixture(World world, int i, int j, int k, Block
	 * block) { Tessellator tess = Tessellator.instance; float brightness =
	 * block.getBlockBrightness(world, i, j, k); int skyLight =
	 * world.getLightBrightnessForSkyBlocks(i, j, k, 0); int modulousModifier =
	 * skyLight % 65536; int divModifier = skyLight / 65536;
	 * tess.setColorOpaque_F(brightness, brightness, brightness);
	 * OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
	 * (float) modulousModifier, divModifier); }
	 */
}
