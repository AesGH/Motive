package aes.motive;

import net.minecraft.client.renderer.Tessellator;

public class NinePatch extends Texture {
	Tessellator tessellator;

	float stretchUFrom, stretchUTo, stretchVFrom, stretchVTo;

	public NinePatch(int u, int v, int uSize, int vSize, float stretchUFrom, float stretchUTo, float stretchVFrom, float stretchVTo) {
		super(u, v, uSize, vSize);
		this.stretchUFrom = stretchUFrom;
		this.stretchUTo = stretchUTo;
		this.stretchVFrom = stretchVFrom;
		this.stretchVTo = stretchVTo;
	}

	private void drawBottom(float x, float y, float z, float width, float height) {
		addVertex(x + this.stretchUFrom, y + height - (this.vSize - this.stretchVTo), z, this.stretchUFrom, this.stretchVTo);
		addVertex(x + this.stretchUFrom, y + height, z, this.stretchUFrom, this.vSize);
		addVertex(x + width - (this.uSize - this.stretchUTo), y + height, z, this.stretchUTo, this.vSize);
		addVertex(x + width - (this.uSize - this.stretchUTo), y + height - (this.vSize - this.stretchVTo), z, this.stretchUTo, this.stretchVTo);
	}

	private void drawBottomLeft(float x, float y, float z, float height) {
		addVertex(x, y + height - (this.vSize - this.stretchVTo), z, 0, this.stretchVTo);
		addVertex(x, y + height, z, 0, this.vSize);
		addVertex(x + this.stretchUFrom, y + height, z, this.stretchUFrom, this.vSize);
		addVertex(x + this.stretchUFrom, y + height - (this.vSize - this.stretchVTo), z, this.stretchUFrom, this.stretchVTo);
	}

	private void drawBottomRight(float x, float y, float z, float width, float height) {
		addVertex(x + width - (this.uSize - this.stretchUTo), y + height - (this.vSize - this.stretchVTo), z, this.stretchUTo, this.stretchVTo);
		addVertex(x + width - (this.uSize - this.stretchUTo), y + height, z, this.stretchUTo, this.vSize);
		addVertex(x + width, y + height, z, this.uSize, this.vSize);
		addVertex(x + width, y + height - (this.vSize - this.stretchVTo), z, this.uSize, this.stretchVTo);
	}

	private void drawLeft(float x, float y, float z, float height) {
		addVertex(x, y + this.stretchVFrom, z, 0, this.stretchVFrom);
		addVertex(x, y + height - (this.vSize - this.stretchVTo), z, 0, this.stretchVTo);
		addVertex(x + this.stretchUFrom, y + height - (this.vSize - this.stretchVTo), z, this.stretchUFrom, this.stretchVTo);
		addVertex(x + this.stretchUFrom, y + this.stretchVFrom, z, this.stretchUFrom, this.stretchVFrom);
	}

	private void drawMiddle(float x, float y, float z, float width, float height) {
		addVertex(x + this.stretchUFrom, y + this.stretchVFrom, z, this.stretchUFrom, this.stretchVFrom);
		addVertex(x + this.stretchUFrom, y + height - (this.vSize - this.stretchVTo), z, this.stretchUFrom, this.stretchVTo);
		addVertex(x + width - (this.uSize - this.stretchUTo), y + height - (this.vSize - this.stretchVTo), z, this.stretchUTo, this.stretchVTo);
		addVertex(x + width - (this.uSize - this.stretchUTo), y + this.stretchVFrom, z, this.stretchUTo, this.stretchVFrom);
	}

	private void drawRight(float x, float y, float z, float width, float height) {
		addVertex(x + width - (this.uSize - this.stretchUTo), y + this.stretchVFrom, z, this.stretchUTo, this.stretchVFrom);
		addVertex(x + width - (this.uSize - this.stretchUTo), y + height - (this.vSize - this.stretchVTo), z, this.stretchUTo, this.stretchVTo);
		addVertex(x + width, y + height - (this.vSize - this.stretchVTo), z, this.uSize, this.stretchVTo);
		addVertex(x + width, y + this.stretchVFrom, z, this.uSize, this.stretchVFrom);
	}

	@Override
	protected void drawTexture(float x, float y, float z, float width, float height) {
		drawTopLeft(x, y, z);
		drawTopMiddle(x, y, z, width);
		drawTopRight(x, y, z, width);
		drawRight(x, y, z, width, height);
		drawBottomRight(x, y, z, width, height);
		drawBottom(x, y, z, width, height);
		drawBottomLeft(x, y, z, height);
		drawLeft(x, y, z, height);
		drawMiddle(x, y, z, width, height);
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
		addVertex(x + width - (this.uSize - this.stretchUTo), y + this.stretchVFrom, z, this.stretchUTo, this.stretchVFrom);
		addVertex(x + width - (this.uSize - this.stretchUTo), y, z, this.stretchUTo, 0);
	}

	private void drawTopRight(float x, float y, float z, float width) {
		addVertex(x + width - (this.uSize - this.stretchUTo), y, z, this.stretchUTo, this.v);
		addVertex(x + width - (this.uSize - this.stretchUTo), y + this.stretchVFrom, z, this.stretchUTo, this.stretchVFrom);
		addVertex(x + width, y + this.stretchVFrom, z, this.uSize, this.stretchVFrom);
		addVertex(x + width, y, z, this.uSize, 0);
	}
}
