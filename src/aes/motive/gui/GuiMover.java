package aes.motive.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import aes.gui.core.BasicScreen;
import aes.gui.widgets.ButtonVanilla;
import aes.gui.widgets.Label;
import aes.gui.widgets.SliderVanilla;
import aes.gui.widgets.base.Button.ButtonHandler;
import aes.gui.widgets.base.Container;
import aes.gui.widgets.base.Slider;
import aes.gui.widgets.base.Slider.ValueChangedHandler;
import aes.gui.widgets.base.Widget;
import aes.motive.tileentity.MoverMode;
import aes.motive.tileentity.TileEntityMover;
import aes.motive.tileentity.TileEntityMoverBase;

public class GuiMover extends BasicScreen {
	TileEntityMover tileEntity;
	private Container container;
	private int xSize;
	private int ySize;
	private int xPos;
	private int yPos;
	private ButtonVanilla buttonActive;
	private SliderVanilla sliderSpeed;

	final int margin = 10;

	final int gap = 5;;

	private ButtonVanilla buttonMode;;

	private ButtonVanilla buttonMoving;

	private Label textStatus;
	private final String uid;
	private final World world;
	private Label textStatusDetail;;

	public GuiMover(TileEntityMover tileEntity) {
		super(null);
		this.tileEntity = tileEntity;
		this.uid = tileEntity.getUid();
		this.world = tileEntity.worldObj;
	}

	@Override
	protected void createGui() {
		this.container = new Container();

		this.buttonActive = new ButtonVanilla(this.xSize - 2 * this.margin, 20, "Activated", new ButtonHandler() {
			@Override
			public void buttonClicked(Widget widget, int button) {
				GuiMover.this.tileEntity.clientChangedProperty("active", GuiMover.this.tileEntity.getActive() ? "false" : "true");
			}
		});

		final float speedValue = this.tileEntity.getSpeed();

		this.buttonMoving = new ButtonVanilla(this.xSize - 2 * this.margin, 20, "Connected", new ButtonHandler() {
			@Override
			public void buttonClicked(Widget widget, int button) {
				GuiMover.this.tileEntity.clientChangedProperty("locked", GuiMover.this.tileEntity.getLocked() ? "false" : "true");
			}
		});

		this.sliderSpeed = new SliderVanilla(this.xSize - 2 * this.margin, 20, speedValue, new Slider.SliderFormat() {
			@Override
			public String format(Slider slider) {
				final float value = slider.getValue();
				if (value < 0.25f)
					return "Speed: SLOW";
				if (value < 0.50f)
					return "Speed: WALKING";
				if (value < 0.75f)
					return "Speed: SPRINTING";
				return "Speed: STUPID FAST";
			}
		}, new ValueChangedHandler() {
			@Override
			public void valueChanged(Slider slider) {
				GuiMover.this.tileEntity.clientChangedProperty("speed", ((Object) slider.getValue()).toString());
			}
		});

		this.buttonMode = new ButtonVanilla(this.xSize - 2 * this.margin, 20, "Mode", new ButtonHandler() {
			@Override
			public void buttonClicked(Widget widget, int button) {
				GuiMover.this.tileEntity.clientChangedProperty("mode",
						((Object) ((GuiMover.this.tileEntity.mode.ordinal() + (button == 0 ? -1 : 1) + MoverMode.values().length) % MoverMode.values().length)).toString());
			}
		});

		this.textStatus = new Label("Status: " + this.tileEntity.getStatus());
		this.textStatusDetail = new Label("Status: " + this.tileEntity.getStatusDetail());

		this.container.addWidgets(this.buttonActive, this.buttonMoving, this.sliderSpeed, this.buttonMode, this.textStatus, this.textStatusDetail);

		this.containers.add(this.container);
		this.selectedContainer = this.container;

	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawBackground() {
		super.drawBackground();
		drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);

		this.mc.renderEngine.bindTexture(new ResourceLocation("motive", "textures/gui/mover.png"));
		drawTexturedModalRect(this.xPos, this.yPos, 0, 0, this.xSize, this.ySize);
		update();
	}

	@Override
	public void initGui() {
		this.xSize = 176;
		this.ySize = 166;
		this.xPos = (this.width - this.xSize) / 2;
		this.yPos = (this.height - this.ySize) / 2;

		super.initGui();

	}

	@Override
	public void keyTyped(char par1, int par2) {
		if (par1 == 27 && par2 == 1) {
			close();
			return;
		}
		super.keyTyped(par1, par2);
	}

	@Override
	protected void reopenedGui() {

	}

	@Override
	protected void revalidateGui() {
		this.container.revalidate(this.xPos, this.yPos, this.xSize, this.ySize);
		final int leftMargin = this.container.left() + this.margin;
		int yLocation = this.container.top() + this.margin;
		this.buttonActive.setPosition(leftMargin, yLocation);
		yLocation += this.buttonActive.getHeight() + this.gap;
		this.buttonMoving.setPosition(leftMargin, yLocation);
		yLocation += this.buttonMoving.getHeight() + this.gap;
		this.sliderSpeed.setPosition(this.buttonActive.getX(), yLocation);
		yLocation += this.sliderSpeed.getHeight() + this.gap;

		this.buttonMode.setPosition(leftMargin, yLocation);
		yLocation += this.buttonMode.getHeight() + this.gap;

		this.textStatus.setPosition(this.xPos + this.xSize / 2, yLocation);

		yLocation += this.textStatus.getHeight() + this.gap;

		this.textStatusDetail.setPosition(this.xPos + this.xSize / 2, yLocation);
	}

	private void update() {
		final TileEntityMoverBase tileEntityMover = TileEntityMoverBase.getMover(this.world, this.uid);
		if (tileEntityMover instanceof TileEntityMover) {
			this.tileEntity = (TileEntityMover) tileEntityMover;
		}
		this.buttonActive.setText(this.tileEntity.getActive() ? "Activated: \247aON" : "Activated: \247cOFF");
		this.buttonMoving
				.setText(this.tileEntity.getLocked() ? "Moving: " + (this.tileEntity.getLockedCount() - 1) + " connected blocks" : "Moving: Just self");
		switch (this.tileEntity.mode) {
		case TowardsSignal:
			this.buttonMode.setText("Mode: Towards signals");
			break;
		case AwayFromSignal:
			this.buttonMode.setText("Mode: Away from signals");
			break;
		case ComputerControlled:
			this.buttonMode.setText("Mode: ComputerCraft peripheral");
			break;
		}
		this.textStatus.setText(this.tileEntity.getStatus());
		this.textStatusDetail.setText(this.tileEntity.getStatusDetail());
	}
}
