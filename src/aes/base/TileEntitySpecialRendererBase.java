package aes.base;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

public abstract class TileEntitySpecialRendererBase extends TileEntitySpecialRenderer implements IItemRenderer {
	public static float getRotationFromDirection(ForgeDirection direction) {
		switch (direction) {
		case NORTH:
			return 0F;
		case SOUTH:
			return 180F;
		case WEST:
			return 90F;
		case EAST:
			return -90F;
		default:
			return 0F;
		}
	}

	protected ModelBase model;

	static int angle = 0;

	public static void glRotateForFaceDir(ForgeDirection direction) {
		// if (direction == ForgeDirection.UP) {
		GL11.glTranslatef(0F, 1F, 0F);

		/*
		 * if (!Keyboard.isKeyDown(61)) { GL11.glRotatef(angle, 1.0F, 0F, 0F);
		 * angle++; if (angle >= 360) angle -= 360; // GL11.glRotatef(90F, 1.0F,
		 * 0F, 0F); }
		 */

		switch (direction) {
		case UP:
			GL11.glRotatef(90F, 1.0F, 0F, 0F);
			break;
		case DOWN:
			GL11.glRotatef(-90F, 1.0F, 0F, 0F);
			break;
		default:
			GL11.glRotatef(getRotationFromDirection(direction), 0F, 1.0F, 0F);
		}

		GL11.glTranslatef(0F, -1F, 0F);
		// }
	}

	protected TileEntitySpecialRendererBase() {
		this.model = getModel();
	}

	protected abstract ModelBase getModel();

	private ModelBase getModelForRendering() {
		return this.model;
		// return getModel();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	protected void light(TileEntity tileEntity) {
		try {
			final float blockBrightness = tileEntity.blockType.getBlockBrightness(tileEntity.worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
			final int lightBrightness = tileEntity.worldObj.getLightBrightnessForSkyBlocks(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 0);
			final int lightBrightnessLo = lightBrightness % 65536;
			final int lightBrightnessHi = lightBrightness / 65536;

			Tessellator.instance.setColorOpaque_F(blockBrightness, blockBrightness, blockBrightness);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightBrightnessLo, lightBrightnessHi);
		} catch (NullPointerException e) {
		}
	}

	private void renderAsItem(float x, float y, float z, float scale) {
		GL11.glPushMatrix();

		GL11.glTranslatef(x, y, z);
		GL11.glScalef(scale, scale, scale);
		GL11.glRotatef(180f, 0f, 1f, 0f);

		getModelForRendering().render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GL11.glPopMatrix();
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		switch (type) {
		case ENTITY:
			renderAsItem(0f, 0f, 0f, 1f);
			break;
		case EQUIPPED:
			renderAsItem(0f, 1f, 1f, 1f);
			break;
		case INVENTORY:
			renderAsItem(0f, -1f, 0f, 1f);
			break;
		case EQUIPPED_FIRST_PERSON:
		case FIRST_PERSON_MAP:
			break;
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tickDelta) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y - 0.5F, (float) z + 0.5F);
		light(tileEntity);

		GL11.glPushMatrix();
		glRotateForFaceDir(BlockBase.getFacing(tileEntity.worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
		getModelForRendering().render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}
}
