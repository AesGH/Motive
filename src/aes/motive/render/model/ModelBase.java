package aes.motive.render.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import aes.utils.Vector3d;

public class ModelBase extends net.minecraft.client.model.ModelBase {
	final String textColorBlack = (char) 167 + "0";

	final String textColorBlue = (char) 167 + "1";
	final String textColorGreen = (char) 167 + "2";
	final String textColorCyan = (char) 167 + "3";
	final String textColorRed = (char) 167 + "4";
	final String textColorPurple = (char) 167 + "5";
	final String textColorYellow = (char) 167 + "6";
	final String textColorGrey = (char) 167 + "7";
	final String textColorDarkGrey = (char) 167 + "8";

	final String textColorLightBlue = (char) 167 + "9";
	final String textColorLightGreen = (char) 167 + "a";
	final String textColorLightCyan = (char) 167 + "b";
	final String textColorLightRed = (char) 167 + "c";
	final String textColorLightPurple = (char) 167 + "d";
	final String textColorLightYellow = (char) 167 + "e";
	final String textColorWhite = (char) 167 + "f";
	final String textRandomCycling = (char) 167 + "k";

	final String textStyleBold = (char) 167 + "l";
	final String textStyleStrikethru = (char) 167 + "m";
	final String textStyleUnderline = (char) 167 + "n";
	final String textStyleItalic = (char) 167 + "o";
	final String textStyleReset = (char) 167 + "r";
	protected Tessellator tessellator;
	private double xOffset;
	private double yOffset;
	private double zOffset;
	private double uOffset;
	private double vOffset;

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
	}

	private void addVertexWithUV(double x, double y, double z, double u, double v) {
		this.tessellator.addVertexWithUV(pxTexture(x + this.xOffset), pxTexture(y + this.yOffset), pxTexture(z + this.zOffset), pxVertex(u + this.uOffset),
				pxVertex(v + this.vOffset));
	};

	protected void draw() {
		this.tessellator.draw();
	}

	FontRenderer getFontRenderer() {
		return Minecraft.getMinecraft().fontRenderer;
	}

	private double pxTexture(double i) {
		return i / 128D;
	}

	private double pxVertex(double i) {
		return i / 128D;
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

		renderModel(tileEntity, stack, partialTickTime);

		if (GuiScreen.isShiftKeyDown()) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			// z- face

			startDrawing();
			this.tessellator.setColorRGBA(0xff, 0, 0, 0x20);

			addQuadWithUV(new Vector3d(0, 0, 0), 0, 0, new Vector3d(0, 1, 0), 1, 0, new Vector3d(1, 1, 0), 1, 1, new Vector3d(1, 0, 0), 0, 1);

			addQuadWithUV(new Vector3d(0, 0, 0), 0, 0, new Vector3d(1, 0, 0), 1, 0, new Vector3d(1, 1, 0), 1, 1, new Vector3d(0, 1, 0), 0, 1);
			draw();
			startDrawing();
			this.tessellator.setColorRGBA(0xff, 0xff, 0, 0x20);
			addQuadWithUV(new Vector3d(0, 0, 1), 0, 0, new Vector3d(1, 0, 1), 1, 0, new Vector3d(1, 1, 1), 1, 1, new Vector3d(0, 1, 1), 0, 1);

			addQuadWithUV(new Vector3d(0, 0, 1), 0, 0, new Vector3d(0, 1, 1), 1, 0, new Vector3d(1, 1, 1), 1, 1, new Vector3d(1, 0, 1), 0, 1);
			draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
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
		this.uOffset = uOffset;
		this.vOffset = vOffset;
	}

	protected void setVertexOffset(double xOffset, double yOffset, double zOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
	}

	protected void startDrawing() {
		this.tessellator.startDrawingQuads();
		// this.tessellator.setBrightness(0xff);
	}

	protected void texture(ResourceLocation resource) {
		Minecraft.getMinecraft().renderEngine.bindTexture(resource);
	}
}
