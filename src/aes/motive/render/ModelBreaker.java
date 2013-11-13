package aes.motive.render;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import aes.motive.Motive;
import cpw.mods.fml.client.FMLClientHandler;

public class ModelBreaker extends ModelBase {
	private final ModelRenderer outline;

	public ModelBreaker() {
		this.outline = new ModelRenderer(this, 0, 0);
		this.outline.addBox(-64, -64, -64, 128, 128, 128);
		this.outline.setRotationPoint(0F, 128F, 0F);
		this.outline.setTextureSize(512, 512);
		this.outline.mirror = false;
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);

		FMLClientHandler.instance().getClient().renderEngine.bindTexture(Motive.resourceBreakerTexture);

		this.outline.render(f5);
	}

}
