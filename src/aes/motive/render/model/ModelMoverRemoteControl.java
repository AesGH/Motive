package aes.motive.render.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import aes.motive.FontUtils;
import aes.motive.Motive;
import aes.motive.item.ItemMoverRemoteControl;
import aes.motive.tileentity.MoverMode;
import aes.motive.tileentity.TileEntityMover;
import aes.utils.Vector3i;
import aes.utils.WorldUtils;

public class ModelMoverRemoteControl extends ModelMotiveBase {
	private final ModelRenderer outline;
	private final ModelRenderer arrow;

	float textWidth = 115f;

	public ModelMoverRemoteControl() {
		super();
		this.outline = new ModelRenderer(this, 0, 0);

		this.outline.addBox(-64, -64, -8, 128, 128, 16);
		this.outline.setRotationPoint(0F, 128F, 0F);
		this.outline.setTextureSize(512, 512);
		this.outline.mirror = false;

		this.arrow = new ModelRenderer(this, 0, 0);
		this.arrow.addBox(-32, -32, 16, 64, 64, 16);
		this.arrow.setRotationPoint(0F, 128F, 0F);
		this.arrow.setTextureSize(512, 512);
		this.arrow.mirror = false;

	}

	protected double angleToEntity(final TileEntityMover tileEntityMoverPairedTo, EntityClientPlayerMP thePlayer) {
		final Vec3 lookVec = thePlayer.getLookVec();
		final Vec3 pairedToVec = Vec3.createVectorHelper(tileEntityMoverPairedTo.xCoord + 0.5f - thePlayer.posX,
				tileEntityMoverPairedTo.yCoord + 0.5f - (thePlayer.posY + thePlayer.eyeHeight), tileEntityMoverPairedTo.zCoord + 0.5f - thePlayer.posZ)
				.normalize();

		final Vec3 right = lookVec.crossProduct(Vec3.createVectorHelper(0, 1, 0)).normalize();
		final Vec3 up = right.crossProduct(lookVec);

		final Vec3 obj = Vec3.createVectorHelper(pairedToVec.dotProduct(right), pairedToVec.dotProduct(up), pairedToVec.dotProduct(lookVec));

		final double azimuth = Math.atan2(obj.xCoord, obj.zCoord);

		final Vec3 proj = Vec3.createVectorHelper(obj.xCoord, 0, obj.zCoord);
		double alt = Math.acos(obj.normalize().dotProduct(proj.normalize()));
		if (obj.yCoord < 0) {
			alt = -alt;
		}

		final double angle = Math.atan2(alt, azimuth) * 180f / Math.PI;
		return angle;
	}

	protected void drawArrowTo(final TileEntityMover tileEntityMoverPairedTo, EntityClientPlayerMP thePlayer) {
		GL11.glPushMatrix();

		texture(new ResourceLocation("motive", "textures/gui/modes.png"));

		final Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();

		tessellator.addVertexWithUV(0, 0, 0, 15f / 256f, 2f / 256f);
		tessellator.addVertexWithUV(0, 5, 0, 15f / 256f, 7f / 256f);
		tessellator.addVertexWithUV(6, 5, 0, 21f / 256f, 7f / 256f);
		tessellator.addVertexWithUV(6, 0, 0, 21f / 256f, 2f / 256f);

		GL11.glTranslatef(100, -14, -0.1f);

		final double angle = angleToEntity(tileEntityMoverPairedTo, thePlayer);

		GL11.glTranslatef(3, 2.5f, 0);
		GL11.glRotatef(-(float) angle, 0, 0, 1);
		GL11.glScalef(2, 2, 1);
		GL11.glTranslatef(-3, -2.5f, 0);

		tessellator.draw();

		GL11.glPopMatrix();
	}

	private void drawCenteredString(String string) {
		drawCenteredString(string, 0xffffff);
	}

	private void drawCenteredString(String string, int colorRGB) {
		final FontRenderer fontRenderer = getFontRenderer();
		final float width = fontRenderer.getStringWidth(string);

		if (width == 0)
			return;

		final float textWidth = 115f;

		final float scale = Math.min(2f, textWidth / width);

		final float scaledWidth = width * scale;
		final float scaledHeight = scale * fontRenderer.FONT_HEIGHT;

		final float marginLeft = (textWidth - scaledWidth) / 2f;

		GL11.glPushMatrix();

		GL11.glTranslatef(marginLeft, 0, 0);
		GL11.glScalef(scale, scale, 1);

		fontRenderer.drawString(string, 0, 0, colorRGB);
		GL11.glPopMatrix();

		GL11.glTranslatef(0, scaledHeight + 1, 0);
	}

	@Override
	protected void renderModel(TileEntity tileEntity, ItemStack stack, float partialTickTime) {
		texture(Motive.resourceRemoteControlTexture);

		// GL11.glDisable(GL11.GL_LIGHTING);

		rotate(180, 1, 0, 0);
		drawBoltedFace(true);

		rotate(180, 1, 0, 0);

		final float scale = 1f / 192f;

		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glScalef(scale, scale, scale);

		// GL11.glEnable(GL11.GL_LIGHTING);

		// this.outline.render(1f);

		// this.arrow.render(scale);

		final TileEntityMover tileEntityMoverPairedTo = ItemMoverRemoteControl.getPairedMover(Minecraft.getMinecraft().theWorld, stack);

		GL11.glPushMatrix();
		GL11.glTranslatef(68f, 37, 3f);

		final String titleStyle = FontUtils.textColorWhite + FontUtils.textStyleItalic;
		final String shortcutStyle = FontUtils.textColorWhite;
		final String actionStyle = FontUtils.textColorLightYellow;

		final String pairedStyle = FontUtils.textColorLightGreen + FontUtils.textStyleBold;
		final String unpairedStyle = FontUtils.textColorLightRed + FontUtils.textStyleBold;

		final String pairedIndicator = pairedStyle + "PAIRED";
		final String unpairedIndicator = unpairedStyle + "NOT PAIRED";

		final String shortcutClick = shortcutStyle + "Click: ";
		final String shortcutRightClick = shortcutStyle + "Right click: ";

		final String actionPair = actionStyle + FontUtils.textColorLightGreen + "Pair";
		final String actionUnpair = actionStyle + FontUtils.textColorLightRed + "Unpair";
		final String actionChangePair = actionStyle + FontUtils.textColorGreen + "Change pair";

		final String actionDisconnect = actionStyle + FontUtils.textColorLightRed + "Disconnect";
		final String actionConnect = actionStyle + "Connect";
		final String actionOpen = actionStyle + "Open";
		final String actionMove = actionStyle + "Move";
		final String actionStop = actionStyle + FontUtils.textColorLightRed + "Stop";

		final String unpairedTip = FontUtils.textColorLightGreen + "Use on " + Motive.BlockMover.getLocalizedName() + " to pair";

		GL11.glTranslatef(-20f, -5f, 0);

		drawCenteredString(titleStyle + Motive.BlockMover.getLocalizedName() + " Remote");
		GL11.glTranslatef(0, 4f, 0);

		final MovingObjectPosition objectMouseOver = Minecraft.getMinecraft().objectMouseOver;
		final TileEntity tileEntityMouseOver = objectMouseOver == null ? null : Minecraft.getMinecraft().theWorld.getBlockTileEntity(objectMouseOver.blockX,
				objectMouseOver.blockY, objectMouseOver.blockZ);

		drawCenteredString(tileEntityMoverPairedTo != null ? pairedIndicator : unpairedIndicator, 0);
		final EntityClientPlayerMP thePlayer = Minecraft.getMinecraft().thePlayer;

		if (tileEntityMoverPairedTo != null) {
			drawArrowTo(tileEntityMoverPairedTo, thePlayer);
		}

		GL11.glTranslatef(0, 2f, 0);

		if (tileEntityMouseOver instanceof TileEntityMover || tileEntityMoverPairedTo != null) {
			drawCenteredString(shortcutRightClick + actionOpen);
		}

		if (tileEntityMoverPairedTo == null) {
			drawCenteredString(tileEntityMouseOver instanceof TileEntityMover ? shortcutClick + actionPair : unpairedTip);
		} else {
			if (objectMouseOver != null) {
				if (tileEntityMouseOver instanceof TileEntityMover) {
					drawCenteredString(shortcutClick + (tileEntityMoverPairedTo == tileEntityMouseOver ? actionUnpair : actionChangePair));
				} else {
					final Vector3i cursorLocation = new Vector3i(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
					if (tileEntityMoverPairedTo.isConnectedTo(cursorLocation)) {
						drawCenteredString(shortcutClick + actionDisconnect);
					} else if (tileEntityMoverPairedTo.canConnectTo(cursorLocation)) {
						drawCenteredString(shortcutClick + actionConnect);
					}
				}
			} else if (tileEntityMoverPairedTo.getActive() && tileEntityMoverPairedTo.mode == MoverMode.Remote) {
				final Vector3i directionLooking = WorldUtils.getDirectionLooking(thePlayer);
				if (directionLooking.equals(tileEntityMoverPairedTo.getRequestedDirection())) {
					drawCenteredString(shortcutClick + actionStop);
				} else {
					drawCenteredString(shortcutClick + actionMove + " " + directionLooking.getDirection());
				}
			}
		}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	};
}
