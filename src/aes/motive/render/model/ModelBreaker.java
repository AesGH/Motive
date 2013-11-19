package aes.motive.render.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import aes.motive.Motive;
import aes.utils.Vector3d;

public class ModelBreaker extends ModelMotiveBase {
	private final ModelRenderer outline;

	public ModelBreaker() {
		this.outline = new ModelRenderer(this, 0, 0);
		this.outline.addBox(-64, -64, -64, 128, 128, 128);
		this.outline.setRotationPoint(0F, 128F, 0F);
		this.outline.setTextureSize(512, 512);
		this.outline.mirror = false;
	}

	@Override
	protected void renderModel(TileEntity tileEntity, ItemStack stack, float partialTickTime) {
		drawBoltedFrame();

		startDrawing();
		texture(Motive.resourceBreakerTexture);

		setUVOffset(0, 0);

		final double inset = 8D;

		addQuadWithUV(new Vector3d(inset, inset, inset), 0, 0, new Vector3d(inset, 128 - inset, inset), 0, 128, new Vector3d(128 - inset, 128 - inset, inset),
				128, 128, new Vector3d(128 - inset, inset, inset), 128, 0);

		addQuadWithUV(new Vector3d(inset, inset, inset), 0, 0, new Vector3d(128 - inset, inset, inset), 128, 0, new Vector3d(128 - inset, 128 - inset, inset),
				128, 128, new Vector3d(inset, 128 - inset, inset), 0, 128);

		draw();
	}
}
