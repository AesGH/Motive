package aes.motive.render.model;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import aes.base.TileEntityBase;
import aes.utils.Vector3d;

public class ModelBase extends net.minecraft.client.model.ModelBase {
	protected Tessellator tessellator;
	private double xOffset;
	private double yOffset;
	private double zOffset;
	private double uOffset;
	private double vOffset;
	private double uvScale;

	static Map<String, Integer> cachedDisplayLists = new HashMap<String, Integer>();

	public ModelBase() {
		this.textureWidth = 512;
		this.textureHeight = 512;
	}

	protected void addQuadWithUV(Vector3d a, double uA, double vA, Vector3d b, double uB, double vB, Vector3d c, double uC, double vC, Vector3d d, double uD,
			double vD) {

		final Vector3d normal = b.subtract(c).crossProduct(b.subtract(a)).normalise();
		this.tessellator.setNormal((float) normal.x, (float) normal.y, (float) normal.z);

		addVertexWithUV(a.x, a.y, a.z, uA, vA);
		addVertexWithUV(b.x, b.y, b.z, uB, vB);
		addVertexWithUV(c.x, c.y, c.z, uC, vC);
		addVertexWithUV(d.x, d.y, d.z, uD, vD);
	};

	private void addVertexWithUV(double x, double y, double z, double u, double v) {
		this.tessellator.addVertexWithUV(pxTexture(x + this.xOffset), pxTexture(y + this.yOffset), pxTexture(z + this.zOffset), pxVertex(u + this.uOffset),
				pxVertex(v + this.vOffset));
	}

	protected void draw() {
		this.tessellator.draw();
	}

	FontRenderer getFontRenderer() {
		return Minecraft.getMinecraft().fontRenderer;
	}

	protected String getRenderCacheKey(TileEntity tileEntity, ItemStack stack) {
		return ((TileEntityBase) tileEntity).getRenderCacheKey();
	}

	private double pxTexture(double i) {
		return i / this.uvScale;
	}

	private double pxVertex(double i) {
		return i / this.uvScale;
	}

	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
		try {
			throw new Exception("don't use this function");
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void render(TileEntity tileEntity, ItemStack stack, float scale, float partialTickTime) {
		this.tessellator = Tessellator.instance;
		GL11.glScalef(scale, scale, scale);

		boolean cached = false;

		if (tileEntity instanceof TileEntityBase) {
			final String key = getRenderCacheKey(tileEntity, stack);

			if (key != null) {
				int displayList;
				if (cachedDisplayLists.containsKey(key)) {
					displayList = cachedDisplayLists.get(key);
				} else {
					displayList = GLAllocation.generateDisplayLists(1);
					GL11.glNewList(displayList, GL11.GL_COMPILE);
					renderModel(tileEntity, stack, partialTickTime);
					GL11.glEndList();
					cachedDisplayLists.put(key, displayList);
				}
				/*
				 * if ( //++count > 100 && (this instanceof ModelMote || this
				 * instanceof ModelVe || this instanceof ModelMoverRemoteControl
				 * || this instanceof ModelMover || this instanceof
				 * ModelBreaker)) { Motive.log("rendering " + }
				 * this.getClass().getName());
				 */
				GL11.glCallList(displayList);
				cached = true;
			}
		}

		if (!cached) {
			renderModel(tileEntity, stack, partialTickTime);
		}
	}

	protected void renderModel(TileEntity tileEntity, ItemStack stack, float partialTickTime) {
	}

	protected void rotate(float angle, float x, float y, float z) {
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		GL11.glRotatef(angle, x, y, z);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
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

	protected void setUVOffset(double uOffset, double vOffset) {
		setUVOffset(uOffset, vOffset, 128);
	}

	protected void setUVOffset(double uOffset, double vOffset, double uvScale) {
		this.uOffset = uOffset;
		this.vOffset = vOffset;
		this.uvScale = uvScale;
	}

	protected void setVertexOffset(double xOffset, double yOffset, double zOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
	}
}
