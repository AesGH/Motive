package aes.motive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import aes.motive.render.model.TextureBoltPanel;
import aes.motive.render.model.TextureBreakerBody;
import aes.motive.render.model.TextureBreakerConnection;
import aes.motive.render.model.TextureBreakerEndcap;
import aes.motive.render.model.TextureBreakerFront;
import aes.motive.render.model.TextureDiagonalStrut;
import aes.motive.render.model.TextureEdgeStrut;
import aes.motive.render.model.TextureMote;
import aes.utils.Vector3d;

public class Texture {
	private final static ResourceLocation texture = new ResourceLocation("motive", "textures/texture.png");
	protected static float uvScale = 1F / 256F;
	protected double vectorScale = 1;
	protected double vectorOffsetX;
	protected double vectorOffsetY;
	protected double vectorOffsetZ;

	public static Texture dialogBorder = new NinePatch(0, 0, 12, 12, 4, 5, 4, 5);
	public static Texture guiInset = new NinePatch(13, 0, 11, 11, 4, 5, 4, 5);
	public static Texture tooltipBackground = new NinePatch(25, 0, 12, 12, 4, 5, 4, 5);

	public static Texture modeToRedstone = new Texture(0, 33, 10, 7);
	public static Texture modeFromRedstone = new Texture(0, 40, 10, 7);
	public static Texture modeComputer = new Texture(11, 33, 10, 10);
	public static Texture modeRemote = new Texture(21, 33, 10, 10);

	public static Texture breakerFront = new TextureBreakerFront(108, 0, 64, 62);
	public static Texture mote = new TextureMote(31, 22, 32, 32);
	public static Texture motePaired = new TextureMote(63, 22, 32, 32);
	public static Texture diagonalStrut = new TextureDiagonalStrut(0, 47, 31, 32);
	public static Texture boltPanel = new TextureBoltPanel(0, 13, 20, 20);
	public static Texture edgeStrut = new TextureEdgeStrut(20, 13, 88, 9);
	public static Texture pairedArrow = new Texture(4, 34, 6, 5);

	public static Texture breakerBody = new TextureBreakerBody(0, 97, 46, 9);
	public static Texture breakerConnection = new TextureBreakerConnection(0, 88, 36, 9);
	public static Texture breakerEndcap = new TextureBreakerEndcap(36, 88, 9, 9);

	public int u, v, uSize, vSize;
	protected Tessellator tessellator;

	protected Texture(int u, int v, int uSize, int vSize) {
		this.u = u;
		this.v = v;
		this.uSize = uSize;
		this.vSize = vSize;
	}

	protected void addQuadWithUV(Vector3d a, float uA, float vA, Vector3d b, float uB, float vB, Vector3d c, float uC, float vC, Vector3d d, float uD, float vD) {

		final Vector3d normal = b.subtract(c).crossProduct(b.subtract(a)).normalise();
		this.tessellator.setNormal((float) normal.x, (float) normal.y, (float) normal.z);

		addVertex(a.x, a.y, a.z, uA, vA);
		addVertex(b.x, b.y, b.z, uB, vB);
		addVertex(c.x, c.y, c.z, uC, vC);
		addVertex(d.x, d.y, d.z, uD, vD);
	}

	protected void addRectangleXY(double x1, double y1, double z, int u1, int v1, double x2, double y2, int u2, int v2, boolean flip) {

		if (flip) {
			addQuadWithUV(new Vector3d(x1, y1, z), u1, v1, new Vector3d(x2, y1, z), u2, v1, new Vector3d(x2, y2, z), u2, v2, new Vector3d(x1, y2, z), u1, v2);
		} else {
			addQuadWithUV(new Vector3d(x1, y1, z), u1, v1, new Vector3d(x1, y2, z), u1, v2, new Vector3d(x2, y2, z), u2, v2, new Vector3d(x2, y1, z), u2, v1);
		}
	}

	protected void addRectangleXZ(double x1, double y, double z1, int u1, int v1, double x2, double z2, int u2, int v2, boolean flip) {

		if (flip) {
			addQuadWithUV(new Vector3d(x1, y, z1), u1, v1, new Vector3d(x2, y, z1), u1, v2, new Vector3d(x2, y, z2), u2, v2, new Vector3d(x1, y, z2), u2, v1);
		} else {
			addQuadWithUV(new Vector3d(x1, y, z1), u1, v1, new Vector3d(x1, y, z2), u2, v1, new Vector3d(x2, y, z2), u2, v2, new Vector3d(x2, y, z1), u1, v2);
		}
	}

	protected void addRectangleYZ(double x, double y1, double z1, int u1, int v1, double y2, double z2, int u2, int v2, boolean flip) {

		if (flip) {
			addQuadWithUV(new Vector3d(x, y1, z1), u1, v1, new Vector3d(x, y2, z1), u1, v2, new Vector3d(x, y2, z2), u2, v2, new Vector3d(x, y1, z2), u2, v1);
		} else {
			addQuadWithUV(new Vector3d(x, y1, z1), u1, v1, new Vector3d(x, y1, z2), u2, v1, new Vector3d(x, y2, z2), u2, v2, new Vector3d(x, y2, z1), u1, v2);
		}
	}

	protected void addVertex(double x, double y, double z, float u, float v) {
		addVertex((float) x, (float) y, (float) z, u, v);
	}

	protected void addVertex(float x, float y, float z, float u, float v) {
		this.tessellator.addVertexWithUV((x + this.vectorOffsetX) * this.vectorScale, (y + this.vectorOffsetY) * this.vectorScale, (z + this.vectorOffsetZ)
				* this.vectorScale, (this.u + u) * uvScale, (this.v + v) * uvScale);
	}

	public void bind() {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
	}

	public final void draw() {
		draw(0, 0, 0);
	}

	public final void draw(float x, float y, float z) {
		draw(x, y, z, this.uSize, this.vSize);
	}

	public final void draw(float x, float y, float z, float width, float height) {
		bind();
		this.tessellator = Tessellator.instance;
		this.tessellator.startDrawingQuads();

		GL11.glPushMatrix();
		drawTexture(x, y, z, width, height);
		this.tessellator.draw();
		GL11.glPopMatrix();
	}

	protected void drawTexture(float x, float y, float z, float width, float height) {
		addVertex(x, y, z, 0, 0);
		addVertex(x, y + height, z, 0, this.vSize);
		addVertex(x + width, y + height, z, this.uSize, this.vSize);
		addVertex(x + width, y, z, this.uSize, 0);
	}

}
