package aes.motive.render;

import aes.base.TileEntitySpecialRendererBase;
import aes.motive.render.model.ModelBase;
import aes.motive.render.model.ModelMover;

public class TileEntityMoverRenderer extends TileEntitySpecialRendererBase {
	@Override
	protected ModelBase getModel() {
		return new ModelMover();
	}

	/*
	 * private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
	 * int meta = world.getBlockMetadata(x, y, z); GL11.glPushMatrix();
	 * GL11.glRotatef(meta * (-90), 0.0F, 0.0F, 1.0F); GL11.glPopMatrix(); }
	 */
	// This method is called when minecraft renders a tile entity

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
