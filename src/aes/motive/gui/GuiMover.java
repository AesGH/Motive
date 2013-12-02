package aes.motive.gui;

import java.util.Arrays;

import net.minecraft.world.World;
import aes.gui.core.GuiDialog;
import aes.gui.widgets.ButtonVanilla;
import aes.gui.widgets.Label;
import aes.gui.widgets.MultiTooltip;
import aes.gui.widgets.SliderVanilla;
import aes.gui.widgets.Tooltip;
import aes.gui.widgets.base.Button.ButtonHandler;
import aes.gui.widgets.base.Container;
import aes.gui.widgets.base.Slider;
import aes.gui.widgets.base.Slider.ValueChangedHandler;
import aes.gui.widgets.base.Widget;
import aes.motive.FontUtils;
import aes.motive.Motive;
import aes.motive.Texture;
import aes.motive.tileentity.MoverMode;
import aes.motive.tileentity.TileEntityMover;
import aes.motive.tileentity.TileEntityMoverBase;
import aes.utils.Vector3i;

public class GuiMover extends GuiDialog {
	private final World world;
	private final String tileEntityUid;

	private Container container;

	private ButtonVanilla buttonActive;
	private ButtonVanilla buttonMode;;
	private ButtonVanilla buttonMoving;
	private ButtonVanilla buttonHighlight;
	private SliderVanilla sliderSpeed;
	private Label textStatus;
	private Label textStatusDetail;

	final int margin = 10;
	final int gap = 5;
	private Label textMoverPrompt;
	private Label textConnectedPrompt;
	private Label textStatusPrompt;
	private int statusAreaYStart;
	private int statusAreaYEnd;

	public GuiMover(TileEntityMover tileEntity) {
		super();
		this.world = tileEntity.worldObj;
		this.tileEntityUid = tileEntity.getUid();
	}

	@Override
	protected void createGui() {
		Motive.log(this.world, "Creating Mover gui");

		this.textMoverPrompt = new Label(Motive.BlockMover.getLocalizedName(), false);
		this.textConnectedPrompt = new Label("Moving", false);
		this.textStatusPrompt = new Label("Status", false);

		this.buttonActive = new ButtonVanilla("Activated", new ButtonHandler() {
			@Override
			public void buttonClicked(Widget widget, int button) {
				getTileEntity().clientChangedProperty("active", getTileEntity().getActive() ? "false" : "true");
			}
		});

		this.buttonMoving = new ButtonVanilla("Connected", new ButtonHandler() {
			@Override
			public void buttonClicked(Widget widget, int button) {
				getTileEntity().clientChangedProperty("locked", getTileEntity().getLocked() ? "false" : "true");
			}
		});

		this.buttonHighlight = new ButtonVanilla("Highlight", new ButtonHandler() {
			@Override
			public void buttonClicked(Widget widget, int button) {
				getTileEntity().clientChangedProperty("highlight", getTileEntity().getHighlight() ? "false" : "true");
			}
		});

		this.sliderSpeed = new SliderVanilla(this.xSize - 2 * this.margin, 20, 0, new Slider.SliderFormat() {
			@Override
			public String format(Slider slider) {
				final float value = slider.getValue();
				if (value < 0.2f)
					return "Crawling";
				if (value < 0.4f)
					return "Walking";
				if (value < 0.6f)
					return "Running";
				if (value < 0.8f)
					return "Flying";
				return "STUPID FAST";
			}
		}, new ValueChangedHandler() {
			@Override
			public void valueChanged(Slider slider) {
				getTileEntity().setSpeed(slider.getValue());
				getTileEntity().clientChangedProperty("speed", ((Object) slider.getValue()).toString());
			}
		});

		this.buttonMode = new ButtonVanilla("", new ButtonHandler() {
			@Override
			public void buttonClicked(Widget widget, int button) {
				getTileEntity().clientChangedProperty(
						"mode",
						((Object) ((getTileEntity().mode.ordinal() + (button == 0 ? -1 : 1) + MoverMode.values().length) % MoverMode.values().length))
								.toString());
			}
		});

		this.textStatus = new Label("", 0xfff0f0f0, 0xffffffff, false);
		this.textStatusDetail = new Label("", 0xfff0f0f0, 0xffffffff, false);
		this.textStatus.setShadowedText(true);
		this.textStatusDetail.setShadowedText(true);

		this.container = new Container();
		this.container.addWidgets(this.textMoverPrompt, this.textConnectedPrompt, this.textStatusPrompt, this.buttonActive, this.buttonMoving,
				this.buttonHighlight, this.sliderSpeed, this.buttonMode, this.textStatus, this.textStatusDetail);

		this.containers.add(this.container);
		this.selectedContainer = this.container;

	}

	@Override
	public void drawBackground() {
		super.drawBackground();
		Texture.guiInset.draw(this.container.left() + this.margin, this.statusAreaYStart, this.zLevel, this.xSize - 2 * this.margin, this.statusAreaYEnd
				- this.statusAreaYStart);
	}

	TileEntityMover getTileEntity() {
		final TileEntityMoverBase tileEntityMover = TileEntityMoverBase.getMover(this.world, this.tileEntityUid);
		return tileEntityMover instanceof TileEntityMover ? (TileEntityMover) tileEntityMover : null;
	}

	@Override
	public void initGui() {
		super.initGui(276, 146);
	}

	private int positionAtColumn(Widget widget, int y, int columIndex, int ofColumns) {
		return positionAtColumn(widget, y, columIndex, ofColumns, 1);
	};

	private int positionAtColumn(Widget widget, int y, int columIndex, int ofColumns, int colSpan) {
		final int totalWidth = this.xSize - 2 * this.margin;
		int widgetWidth = (int) ((totalWidth + (double) this.gap) / ofColumns - this.gap);
		final int leftMargin = this.container.left() + this.margin;

		final int x = (int) Math.ceil((columIndex - 1) * (widgetWidth + this.gap));
		final boolean last = columIndex + colSpan - 1 == ofColumns;

		if (last) {
			widgetWidth = totalWidth - x;
		} else {
			widgetWidth = (widgetWidth + this.gap) * colSpan - this.gap;
		}
		widget.setPosition(leftMargin + x, y, widgetWidth, widget.getHeight() == 0 ? 20 : widget.getHeight());

		if (last)
			return (int) widget.getHeight() + this.gap;
		return 0;
	}

	@Override
	protected void revalidateGui() {
		super.revalidateGui();

		Motive.log(this.world, "Resizing Mover gui");
		this.container.revalidate(this.xPos, this.yPos, this.xSize, this.ySize);

		int yLocation = this.container.top() + this.margin;

		yLocation += positionAtColumn(this.textMoverPrompt, yLocation, 1, 1) - this.gap / 2;
		yLocation += positionAtColumn(this.buttonActive, yLocation, 1, 8, 2);
		yLocation += positionAtColumn(this.sliderSpeed, yLocation, 3, 8, 5);
		yLocation += positionAtColumn(this.buttonMode, yLocation, 8, 8, 1);

		// yLocation += this.gap;
		yLocation += positionAtColumn(this.textConnectedPrompt, yLocation, 1, 1) - this.gap / 2;
		yLocation += positionAtColumn(this.buttonMoving, yLocation, 1, 3, 2);
		yLocation += positionAtColumn(this.buttonHighlight, yLocation, 3, 3);

		// yLocation += this.gap;
		yLocation += positionAtColumn(this.textStatusPrompt, yLocation, 1, 1) - this.gap / 2;

		this.statusAreaYStart = yLocation;
		yLocation += positionAtColumn(this.textStatus, yLocation, 1, 1);
		yLocation += positionAtColumn(this.textStatusDetail, yLocation, 1, 1);

		this.textStatus.setPosition(this.textStatus.getX() + 4, this.textStatus.getY() + 4);
		this.textStatusDetail.setPosition(this.textStatusDetail.getX() + 4, this.textStatusDetail.getY());

		this.statusAreaYEnd = yLocation;
	}

	@Override
	protected void update() {
		final TileEntityMover tileEntity = getTileEntity();
		if (tileEntity == null) {
			close();
			return;
		}
		this.buttonActive.setText(tileEntity.getActive() ? "Active" : "Inactive");
		this.buttonMoving.setText(tileEntity.getLocked() ? tileEntity.getLockedCount() - 1 + " connected blocks" : "Just moving self");
		this.buttonHighlight.setText(tileEntity.getHighlight() ? "Highlight" : "No highlight");
		this.sliderSpeed.setValue(getTileEntity().getSpeed());
		switch (tileEntity.mode) {
		case TowardsSignal:
			this.buttonMode.setTooltip(new Tooltip("Towards redstone signals"));
			this.buttonMode.setImage(Texture.modeToRedstone, false);
			break;
		case Remote:
			this.buttonMode.setTooltip(new Tooltip("Move with " + Motive.ItemMoverRemoteControl.getStatName()));
			this.buttonMode.setImage(Texture.modeRemote, false);
			break;
		case AwayFromSignal:
			this.buttonMode.setTooltip(new Tooltip("Away from redstone signals"));
			this.buttonMode.setImage(Texture.modeFromRedstone, false);
			break;
		case ComputerControlled:
			this.buttonMode.setTooltip(new MultiTooltip(Arrays.asList("ComputerCraft peripheral", FontUtils.textColorGrey + " " + TileEntityMover.usageMove,
					FontUtils.textColorGrey + " " + TileEntityMover.usageIsActive, FontUtils.textColorGrey + " " + TileEntityMover.usageIsMoving,
					FontUtils.textColorGrey + " " + TileEntityMover.usageLock, FontUtils.textColorGrey + " " + TileEntityMover.usageUnlock)));
			this.buttonMode.setImage(Texture.modeComputer, false);
			break;
		}
		if (tileEntity.getStatus().isEmpty()) {
			String status = "";
			if (!tileEntity.getActive()) {
				status += "Inactive, ";
			}
			final Vector3i signals = tileEntity.getSignals();
			if (signals.isEmpty()) {
				status += "No signal";
			} else {
				status += "Signal from " + signals.getDirection();
			}
			this.textStatus.setText(status);
			this.textStatusDetail.setText("");
		} else {
			this.textStatus.setText(tileEntity.getStatus());
			this.textStatusDetail.setText(tileEntity.getStatusDetail());
		}
	}
}
