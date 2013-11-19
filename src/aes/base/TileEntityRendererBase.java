package aes.base;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import aes.base.render.RenderUtils;
import aes.motive.render.model.ModelBase;
import aes.motive.render.model.ModelMotiveBase;

public abstract class TileEntityRendererBase extends TileEntitySpecialRenderer implements IItemRenderer {
	public static void glRotateForFaceDir(ForgeDirection direction) {
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		switch (direction) {
		case UP:
			GL11.glRotatef(90F, 1.0F, 0F, 0F);
			break;
		case DOWN:
			GL11.glRotatef(-90F, 1.0F, 0F, 0F);
			break;
		case NORTH:
			GL11.glRotatef(0, 0F, 1.0F, 0F);
			break;
		case SOUTH:
			GL11.glRotatef(180, 0F, 1.0F, 0F);
			break;
		case WEST:
			GL11.glRotatef(90, 0F, 1.0F, 0F);
			break;
		case EAST:
			GL11.glRotatef(-90, 0F, 1.0F, 0F);
			break;
		default:
			break;
		}
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
	}

	protected ModelMotiveBase model;

	protected TileEntityRendererBase() {
		this.model = getModel();
	}

	protected abstract ModelMotiveBase getModel();

	private ModelBase getModelForRendering() {
		return this.model;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	protected void light(TileEntity tileEntity) {
		try {
			RenderUtils.setBrightnessDirect(tileEntity.worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
			/*
			 * final float blockBrightness =
			 * tileEntity.blockType.getBlockBrightness(tileEntity.worldObj,
			 * tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord); final
			 * int lightBrightness =
			 * tileEntity.worldObj.getLightBrightnessForSkyBlocks
			 * (tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 0);
			 * final int lightBrightnessLo = lightBrightness % 65536; final int
			 * lightBrightnessHi = lightBrightness / 65536;
			 * 
			 * Tessellator.instance.setColorOpaque_F(blockBrightness,
			 * blockBrightness, blockBrightness);
			 * OpenGlHelper.setLightmapTextureCoords
			 * (OpenGlHelper.lightmapTexUnit, lightBrightnessLo,
			 * lightBrightnessHi);
			 */} catch (final NullPointerException e) {
		}
	}

	protected void renderAsItem(ItemStack stack, float x, float y, float z, float scale) {
		renderAsItem(stack, x, y, z, scale, 0, 0, 0);
	}

	protected void renderAsItem(ItemStack stack, float x, float y, float z, float scale, float xRot, float yRot, float zRot) {
		GL11.glPushMatrix();

		GL11.glTranslatef(x, y, z);

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		GL11.glRotatef(xRot, 1f, 0f, 0f);
		GL11.glRotatef(yRot, 0f, 1f, 0f);
		GL11.glRotatef(zRot, 0f, 0f, 1f);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		getModelForRendering().render(null, stack, scale, 0);

		GL11.glPopMatrix();
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		switch (type) {
		case ENTITY:
			renderAsItem(item, 0f, 0f, 0f, 1f);
			break;
		case EQUIPPED:
			renderAsItem(item, 0, 0, 0, 1f, 90, 0, 0);
			break;
		case INVENTORY:
			renderAsItem(item, 0f, -0.1f, 0f, 1f, 0, 180, 180);
			break;
		case EQUIPPED_FIRST_PERSON:
			renderAsItem(item, 0, 0, 0f, 1f, 0, 90, 180);
			break;
		case FIRST_PERSON_MAP:
			renderAsItem(item, 0f, 0f, 0f, 2f, 180, 90, 0);
			break;
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		// light(tileEntity);
		glRotateForFaceDir(BlockBase.getFacing(tileEntity.worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));

		getModelForRendering().render(tileEntity, null, 1, partialTickTime);

		GL11.glPopMatrix();
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}
}
