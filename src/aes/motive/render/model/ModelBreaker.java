package aes.motive.render.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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
	public void render(TileEntity tileEntity, ItemStack stack, float scale) {
		super.render(tileEntity, stack, scale);

		FMLClientHandler.instance().getClient().renderEngine.bindTexture(Motive.resourceBreakerTexture);
		this.outline.render(scale);
	}

}
