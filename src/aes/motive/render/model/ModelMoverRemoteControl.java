package aes.motive.render.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;

import org.lwjgl.opengl.GL11;

import aes.motive.Motive;
import aes.motive.item.ItemMoverRemoteControl;
import aes.motive.tileentity.TileEntityMover;
import aes.utils.Vector3i;
import cpw.mods.fml.client.FMLClientHandler;

public class ModelMoverRemoteControl extends ModelBase {
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
		final float width = fontRenderer.getStringWidth(string);

		if (width == 0)
			return;

		final float textWidth = 108f;

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
	public void render(TileEntity tileEntity, ItemStack stack, float scale) {
		super.render(tileEntity, stack, scale);

		FMLClientHandler.instance().getClient().renderEngine.bindTexture(Motive.resourceRemoteControlTexture);
		this.outline.render(scale);
		// this.arrow.render(scale);

		final TileEntityMover tileEntityMoverPairedTo = ItemMoverRemoteControl.getPairedMover(Minecraft.getMinecraft().theWorld, stack);

		final float fontScale = 0.06f;

		GL11.glPushMatrix();
		GL11.glScalef(fontScale, fontScale, fontScale);
		GL11.glTranslatef(-31f, 77, -10f);
		GL11.glDisable(GL11.GL_LIGHTING);

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

		final String titleStyle = textColorWhite + textStyleItalic;
		final String shortcutStyle = textColorWhite;
		final String actionStyle = textColorGrey;

		final String pairedStyle = textColorLightGreen + textStyleBold;
		final String unpairedStyle = textColorLightRed + textStyleBold;

		final String pairedIndicator = pairedStyle + "PAIRED";
		final String unpairedIndicator = unpairedStyle + "NOT PAIRED";

		final String shortcutClick = shortcutStyle + "Click: ";
		final String shortcutRightClick = shortcutStyle + "Right click: ";

		final String actionPair = actionStyle + textColorLightGreen + "Pair";
		final String actionUnpair = actionStyle + textColorLightRed + "Unpair";
		final String actionChangePair = actionStyle + textColorGreen + "Change pair";

		final String actionDisconnect = actionStyle + textColorLightRed + "Disconnect";
		final String actionConnect = actionStyle + textColorLightGreen + "Connect";
		final String actionOpen = actionStyle + textColorLightCyan + "Open";

		final String unpairedTip = textColorLightGreen + "Use on " + Motive.BlockMover.getLocalizedName() + " to pair";

		fontRenderer.drawString(titleStyle + Motive.BlockMover.getLocalizedName() + " Remote", 0, 0, 0, false);

		GL11.glTranslatef(-20f, 14f, 0);

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
		GL11.glEnable(GL11.GL_LIGHTING);
	}
}
