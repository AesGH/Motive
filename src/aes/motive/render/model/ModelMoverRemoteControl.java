package aes.motive.render.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;

import org.lwjgl.opengl.GL11;

import aes.motive.Motive;
import aes.motive.item.ItemMoverRemoteControl;
import aes.motive.tileentity.TileEntityMover;
import aes.utils.Vector3i;

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

		GL11.glScalef(scale, scale, scale);

		// GL11.glEnable(GL11.GL_LIGHTING);

		// this.outline.render(1f);

		// this.arrow.render(scale);

		final TileEntityMover tileEntityMoverPairedTo = ItemMoverRemoteControl.getPairedMover(Minecraft.getMinecraft().theWorld, stack);

		GL11.glPushMatrix();
		GL11.glTranslatef(68f, 37, 3f);

		final String titleStyle = this.textColorWhite + this.textStyleItalic;
		final String shortcutStyle = this.textColorWhite;
		final String actionStyle = this.textColorGrey;

		final String pairedStyle = this.textColorLightGreen + this.textStyleBold;
		final String unpairedStyle = this.textColorLightRed + this.textStyleBold;

		final String pairedIndicator = pairedStyle + "PAIRED";
		final String unpairedIndicator = unpairedStyle + "NOT PAIRED";

		final String shortcutClick = shortcutStyle + "Click: ";
		final String shortcutRightClick = shortcutStyle + "Right click: ";

		final String actionPair = actionStyle + this.textColorLightGreen + "Pair";
		final String actionUnpair = actionStyle + this.textColorLightRed + "Unpair";
		final String actionChangePair = actionStyle + this.textColorGreen + "Change pair";

		final String actionDisconnect = actionStyle + this.textColorLightRed + "Disconnect";
		final String actionConnect = actionStyle + this.textColorLightGreen + "Connect";
		final String actionOpen = actionStyle + this.textColorLightCyan + "Open";

		final String unpairedTip = this.textColorLightGreen + "Use on " + Motive.BlockMover.getLocalizedName() + " to pair";

		GL11.glTranslatef(-20f, -5f, 0);

		drawCenteredString(titleStyle + Motive.BlockMover.getLocalizedName() + " Remote");

		final MovingObjectPosition objectMouseOver = Minecraft.getMinecraft().objectMouseOver;
		final TileEntity tileEntityMouseOver = objectMouseOver == null ? null : Minecraft.getMinecraft().theWorld.getBlockTileEntity(objectMouseOver.blockX,
				objectMouseOver.blockY, objectMouseOver.blockZ);

		drawCenteredString(tileEntityMoverPairedTo != null ? pairedIndicator : unpairedIndicator, 0);
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
			}
		}

		GL11.glPopMatrix();
	};
}
