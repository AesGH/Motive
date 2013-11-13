package aes.motive.render;

import net.minecraft.client.model.ModelRenderer;
import aes.motive.Motive;
import cpw.mods.fml.client.FMLClientHandler;

public abstract class ModelMotiveBase extends ModelBase {
	private final ModelRenderer center;
	private final ModelRenderer outline;
	private final ModelRenderer struts[] = new ModelRenderer[8];

	public ModelMotiveBase() {
		this.outline = new ModelRenderer(this, 0, 0);

		this.outline.addBox(-64, -64, -64, 128, 128, 128);
		this.outline.setRotationPoint(0F, 128F, 0F);
		this.outline.setTextureSize(512, 512);
		this.outline.mirror = false;

		for (int i = 0; i < 8; i++) {
			final ModelRenderer strut = this.struts[i] = new ModelRenderer(this, 0, 0);

			if (i < 4) {
				strut.addBox(0F, -4F, -4F, 104, 8, 8);
			} else {
				strut.addBox(-104F, -4F, -4F, 104, 8, 8);
			}
			strut.setRotationPoint(0F, 128F, 0F);
			strut.setTextureSize(512, 512);
			strut.mirror = true;
			setRotation(strut, 0F, 0.6154031F, (i * 2 + 1) * 0.7853982F);
		}

		this.center = new ModelRenderer(this, 0, 0);
		this.center.addBox(0, 0, 0, 64, 64, 64);
		this.center.setRotationPoint(-32F, 96F, -32F);
		this.center.setTextureSize(512, 512);
		this.center.mirror = true;
	}

	protected void renderFrontCase(float f5) {
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(Motive.resourceFrontCaseTexture);
		this.outline.render(f5);
	}

	protected void renderFrontStruts(float f5) {
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(Motive.resourceStrutTexture);
		for (int i = 0; i < 4; i++) {
			this.struts[i].render(f5);
		}
	}

	protected void renderGlassCase(float f5) {
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(Motive.resourceGlassCaseTexture);
		this.outline.render(f5);
	}

	protected void renderMote(float f5) {
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(Motive.resourceMoteTexture);
		this.center.render(f5);
	}

	protected void renderStruts(float f5) {
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(Motive.resourceStrutTexture);
		for (int i = 0; i < 8; i++) {
			this.struts[i].render(f5);
		}
	}

}