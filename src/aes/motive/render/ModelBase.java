package aes.motive.render;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

public class ModelBase extends net.minecraft.client.model.ModelBase {

	public ModelBase() {
		this.textureWidth = 512;
		this.textureHeight = 512;
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		GL11.glTranslatef(0.00005F, 0.00005F, 0.00005F);
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
