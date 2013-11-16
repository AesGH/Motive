package aes.motive.render.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

public class ModelBase extends net.minecraft.client.model.ModelBase {

	protected static FontRenderer fontRenderer;

	public ModelBase() {
		if (fontRenderer == null) {
			fontRenderer = Minecraft.getMinecraft().fontRenderer;
		}

		this.textureWidth = 512;
		this.textureHeight = 512;
	}

	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
		try {
			throw new Exception("don't use this function");
		} catch (final Exception e) {
			e.printStackTrace();
		}
	};

	public void render(TileEntity tileEntity, ItemStack stack, float scale) {
		// super.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, scale);
		// setRotationAngles(0, 0, 0, -0.1F, 0, 0, null);
		// GL11.glTranslatef(0.00005F, 0.00005F, 0.00005F);
		GL11.glScalef(0.1249F, 0.1249F, 0.1249F);
	}

	protected void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

}
