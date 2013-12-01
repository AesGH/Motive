package aes.gui.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class NinePatch {
	Tessellator tessellator;

	ResourceLocation resourceLocation;
	float u, v, uWidth, vHeight;
	float fX, fY;
	float stretchUFrom, stretchUTo, stretchVFrom, stretchVTo;

	public NinePatch(ResourceLocation resourceLocation, float resourceWidth, float resourceHeight, float u, float v, float uWidth, float vHeight,
			float stretchUFrom, float stretchUTo, float stretchVFrom, float stretchVTo) {
		this.resourceLocation = resourceLocation;
		this.fX = 1F / resourceWidth;
		this.fY = 1F / resourceHeight;
		this.u = u;
		this.v = v;
		this.uWidth = uWidth;
		this.vHeight = vHeight;
		this.stretchUFrom = stretchUFrom;
		this.stretchUTo = stretchUTo;
		this.stretchVFrom = stretchVFrom;
		this.stretchVTo = stretchVTo;
	}

	protected void addVertex(float x, float y, float z, float u, float v) {
		this.tessellator.addVertexWithUV(x, y, z, (this.u + u) * this.fX, (this.v + v) * this.fY);
	}

	public void draw(float x, float y, float z, float width, float height) {
		Minecraft.getMinecraft().renderEngine.bindTexture(this.resourceLocation);

		this.tessellator = Tessellator.instance;

		this.tessellator.startDrawingQuads();

		drawTopLeft(x, y, z);
		drawTopMiddle(x, y, z, width);
		drawTopRight(x, y, z, width);
		drawRight(x, y, z, width, height);
		drawBottomRight(x, y, z, width, height);
		drawBottom(x, y, z, width, height);
		drawBottomLeft(x, y, z, height);
		drawLeft(x, y, z, height);
		drawMiddle(x, y, z, width, height);

		this.tessellator.draw();
	}

	private void drawBottom(float x, float y, float z, float width, float height) {
		addVertex(x + this.stretchUFrom, y + height - (this.vHeight - this.stretchVTo), z, this.stretchUFrom, this.stretchVTo);
		addVertex(x + this.stretchUFrom, y + height, z, this.stretchUFrom, this.vHeight);
		addVertex(x + width - (this.uWidth - this.stretchUTo), y + height, z, this.stretchUTo, this.vHeight);
		addVertex(x + width - (this.uWidth - this.stretchUTo), y + height - (this.vHeight - this.stretchVTo), z, this.stretchUTo, this.stretchVTo);
	}

	private void drawBottomLeft(float x, float y, float z, float height) {
		addVertex(x, y + height - (this.vHeight - this.stretchVTo), z, 0, this.stretchVTo);
		addVertex(x, y + height, z, 0, this.vHeight);
		addVertex(x + this.stretchUFrom, y + height, z, this.stretchUFrom, this.vHeight);
		addVertex(x + this.stretchUFrom, y + height - (this.vHeight - this.stretchVTo), z, this.stretchUFrom, this.stretchVTo);
	}

	private void drawBottomRight(float x, float y, float z, float width, float height) {
		addVertex(x + width - (this.uWidth - this.stretchUTo), y + height - (this.vHeight - this.stretchVTo), z, this.stretchUTo, this.stretchVTo);
		addVertex(x + width - (this.uWidth - this.stretchUTo), y + height, z, this.stretchUTo, this.vHeight);
		addVertex(x + width, y + height, z, this.uWidth, this.vHeight);
		addVertex(x + width, y + height - (this.vHeight - this.stretchVTo), z, this.uWidth, this.stretchVTo);
	}

	private void drawLeft(float x, float y, float z, float height) {
		addVertex(x, y + this.stretchVFrom, z, 0, this.stretchVFrom);
		addVertex(x, y + height - (this.vHeight - this.stretchVTo), z, 0, this.stretchVTo);
		addVertex(x + this.stretchUFrom, y + height - (this.vHeight - this.stretchVTo), z, this.stretchUFrom, this.stretchVTo);
		addVertex(x + this.stretchUFrom, y + this.stretchVFrom, z, this.stretchUFrom, this.stretchVFrom);
	}

	private void drawMiddle(float x, float y, float z, float width, float height) {
		addVertex(x + this.stretchUFrom, y + this.stretchVFrom, z, this.stretchUFrom, this.stretchVFrom);
		addVertex(x + this.stretchUFrom, y + height - (this.vHeight - this.stretchVTo), z, this.stretchUFrom, this.stretchVTo);
		addVertex(x + width - (this.uWidth - this.stretchUTo), y + height - (this.vHeight - this.stretchVTo), z, this.stretchUTo, this.stretchVTo);
		addVertex(x + width - (this.uWidth - this.stretchUTo), y + this.stretchVFrom, z, this.stretchUTo, this.stretchVFrom);
	}

	private void drawRight(float x, float y, float z, float width, float height) {
		addVertex(x + width - (this.uWidth - this.stretchUTo), y + this.stretchVFrom, z, this.stretchUTo, this.stretchVFrom);
		addVertex(x + width - (this.uWidth - this.stretchUTo), y + height - (this.vHeight - this.stretchVTo), z, this.stretchUTo, this.stretchVTo);
		addVertex(x + width, y + height - (this.vHeight - this.stretchVTo), z, this.uWidth, this.stretchVTo);
		addVertex(x + width, y + this.stretchVFrom, z, this.uWidth, this.stretchVFrom);
	}

	protected void drawTopLeft(float x, float y, float z) {
		addVertex(x, y, z, 0, 0);
		addVertex(x, y + this.stretchVFrom, z, 0, this.stretchVFrom);
		addVertex(x + this.stretchUFrom, y + this.stretchVFrom, z, this.stretchUFrom, this.stretchVFrom);
		addVertex(x + this.stretchUFrom, y, z, this.stretchUFrom, 0);
	}

	private void drawTopMiddle(float x, float y, float z, float width) {
		addVertex(x + this.stretchUFrom, y, z, this.stretchUFrom, 0);
		addVertex(x + this.stretchUFrom, y + this.stretchVFrom, z, this.stretchUFrom, this.stretchVFrom);
		addVertex(x + width - (this.uWidth - this.stretchUTo), y + this.stretchVFrom, z, this.stretchUTo, this.stretchVFrom);
		addVertex(x + width - (this.uWidth - this.stretchUTo), y, z, this.stretchUTo, 0);
	}

	private void drawTopRight(float x, float y, float z, float width) {
		addVertex(x + width - (this.uWidth - this.stretchUTo), y, z, this.stretchUTo, this.v);
		addVertex(x + width - (this.uWidth - this.stretchUTo), y + this.stretchVFrom, z, this.stretchUTo, this.stretchVFrom);
		addVertex(x + width, y + this.stretchVFrom, z, this.uWidth, this.stretchVFrom);
		addVertex(x + width, y, z, this.uWidth, 0);
	}
}
