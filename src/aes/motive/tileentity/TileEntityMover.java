package aes.motive.tileentity;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import aes.motive.Motive;
import aes.utils.Vector3i;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;

public class TileEntityMover extends TileEntityMoverBase implements dan200.computer.api.IPeripheral {
	private boolean active = false;
	private Vector3i powered;
	private float speed;
	private boolean highlight;
	private float flashPct;
	public MoverMode mode = MoverMode.AwayFromSignal;

	static String[] commands = new String[] { "isActive", "isMoving", "move", "lockConnectedBlocks", "unlockConnectedBlocks" };

	public static final String usageMove = "move(x, y, z) or move(\"direction\")";

	public static final String usageIsMoving = "isMoving()";

	public static final String usageIsActive = "isActive()";
	public static final String usageLock = "lockConnectedBlocks()";
	public static final String usageUnlock = "unlockConnectedBlocks()";
	private Vector3i requestedDirection = new Vector3i();

	public TileEntityMover() {
		this.blockType = Motive.BlockMover;
	}

	@Override
	public void attach(IComputerAccess computer) {
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
		if (this.mode != MoverMode.ComputerControlled)
			throw new Exception("Block is not set to allow computer control");
		final String command = commands[method];

		if (command.equals("isActive")) {
			if (arguments.length != 0)
				throw new Exception("Usage: " + usageIsActive);
			return new Object[] { getActive() };
		}
		if (command.equals("isMoving")) {
			if (arguments.length != 0)
				throw new Exception("Usage: " + usageIsMoving);
			return new Object[] { this.moving };
		}

		if (command.equals("move")) {
			Motive.log(this.worldObj, "IPeripheral.move");
			if (arguments.length == 3) {
				if (this.moving)
					return new Object[] { false };
				setRequestedDirection(new Vector3i(getIntArg(arguments, 0), getIntArg(arguments, 1), getIntArg(arguments, 2)));
				return new Object[] { checkBeginMoving() };
			}
			if (arguments.length == 1) {
				if (this.moving)
					return new Object[] { false };
				final String direction = getStringArg(arguments, 0);
				final ForgeDirection side = ForgeDirection.valueOf(direction.toUpperCase());
				if (side != null) {
					setRequestedDirection(new Vector3i(side.offsetX, side.offsetY, side.offsetZ));
					return new Object[] { checkBeginMoving() };
				}
			}
			throw new Exception("Usage: " + usageMove);
		}

		if (command.equals("lockConnectedBlocks")) {
			if (arguments.length != 0)
				throw new Exception("Usage: " + usageLock);
			return new Object[] { setLocked(true) };
		}

		if (command.equals("resetConnectedBlocks")) {
			if (arguments.length != 0)
				throw new Exception("Usage: " + usageUnlock);
			return new Object[] { setLocked(false) };
		}

		return null;
	}

	@Override
	public boolean canAttachToSide(int side) {
		return this.mode == MoverMode.ComputerControlled;
	}

	public boolean canConnectTo(Vector3i location) {
		for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			if (isConnectedTo(location.increment(direction)))
				return true;
		}
		return false;
	}

	@Override
	public void detach(IComputerAccess computer) {
	}

	public void flash() {
		Motive.packetHandler.sendThisMethodToClient(this.worldObj, this);
		if (this.worldObj.isRemote) {
			setFlashPct(100f);
		}
	}

	@Override
	public boolean getActive() {
		return this.active;
	}

	@Override
	public Block getBlockType() {
		return Motive.BlockMover;
	}

	public float getFlashPct() {
		return this.flashPct;
	}

	public boolean getHighlight() {
		return this.highlight;
	}

	private int getIntArg(Object[] arguments, int i) throws Exception {
		final Class<?> argClass = arguments[i].getClass();
		if (argClass.getName().equals("java.lang.Double")) {
			final double arg = (Double) arguments[i];
			return (int) (arg / Math.abs(arg));
		}
		throw new Exception("invalid argument: " + arguments[i] + ", was expecting number, got " + argClass.getName());
	}

	@Override
	public String[] getMethodNames() {
		return commands;
	}

	@Override
	public Vector3i getPowered() {
		if (this.powered == null) {
			this.powered = new Vector3i();
		}
		return this.powered;
	}

	public Vector3i getRequestedDirection() {
		return this.requestedDirection;
	}

	public Vector3i getSignals() {
		int x = 0, y = 0, z = 0;
		final Vector3i location = getLocation();
		for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			final Vector3i from = location.increment(direction);
			if (this.worldObj.isBlockProvidingPowerTo(from.x, from.y, from.z, direction.getOpposite().ordinal()) != 0
					|| this.worldObj.getIndirectPowerOutput(from.x, from.y, from.z, direction.ordinal())) {
				x += direction.offsetX;
				y += direction.offsetY;
				z += direction.offsetZ;
			}
		}
		return new Vector3i(x, y, z).normalise();
	}

	@Override
	public float getSpeed() {
		return this.speed;
	}

	private String getStringArg(Object[] arguments, int i) throws Exception {
		final Class<?> argClass = arguments[i].getClass();
		if (argClass.getName().equals("java.lang.String"))
			return (String) arguments[i];
		throw new Exception("invalid argument: " + arguments[i] + ", was expecting string, got " + argClass.getName());
	}

	@Override
	public String getType() {
		return "engine";
	}

	public boolean isConnectedTo(Vector3i location) {
		return getConnectedBlocks().blocks.contains(location);
	}

	public boolean isMovingBlock(Vector3i location) {
		return getConnectedBlocks().blocks.contains(location);
	}

	@Override
	public void propertyChanged(String name, String value) {
		super.propertyChanged(name, value);
		if ("mode".equals(name)) {
			setMode(MoverMode.values()[java.lang.Integer.parseInt(value)]);
		}
		if ("highlight".equals(name)) {
			setHighlight("true".equals(value));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		setActive(nbtTagCompound.getBoolean("active"));
		setHighlight(nbtTagCompound.getBoolean("highlight"));
		this.mode = MoverMode.values()[nbtTagCompound.getInteger("mode")];
		setSpeed(nbtTagCompound.getFloat("speed"));
		setRequestedDirection(readVector3i(nbtTagCompound, "requested"));
	}

	@Override
	public void removeTileEntity() {
		if (getHighlight()) {
			this.highlight = false;
			markConnectedBlocksForRender();
		}
		super.removeTileEntity();
	}

	@Override
	public void setActive(boolean value) {
		if (this.active != value) {
			this.active = value;
			Motive.log(this.worldObj, "active set to " + this.active);
			if (!this.active) {
				this.requestedDirection = new Vector3i();
			}
			updateBlock();
		}
	}

	public void setFlashPct(float flashPct) {
		this.flashPct = flashPct;
		markConnectedBlocksForRender();
	}

	public void setHighlight(boolean highlight) {
		if (this.highlight != highlight) {
			this.highlight = highlight;
			Motive.log(this.worldObj, "highlight set to " + this.highlight);
			updateBlock();

			markConnectedBlocksForRender();
		}
	}

	public void setMode(MoverMode moverMode) {
		if (this.mode != moverMode) {
			this.mode = moverMode;
			Motive.log(this.worldObj, "mode set to " + this.mode);
			updateBlock();
		}
	}

	@Override
	public void setPowered(Vector3i powered) {
		this.powered = powered;
	}

	public void setRequestedDirection(Vector3i requestedDirection) {
		if (!this.requestedDirection.equals(requestedDirection)) {
			this.requestedDirection = requestedDirection;
			updateBlock();
		}
	}

	@Override
	public void setSpeed(float speed) {
		final float newSpeed = Math.max(0f, Math.min(1f, speed));
		if (this.speed != newSpeed) {
			this.speed = newSpeed;
			updateBlock();
		}
	}

	@Override
	public boolean toggleConnectedBlock(Vector3i location) {
		if (!getHighlight()) {
			flash();
		}
		return super.toggleConnectedBlock(location);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (this.flashPct != 0) {
			setFlashPct(this.flashPct - 8);
			if (Math.abs(this.flashPct) < 10) {
				this.flashPct = 0;
			}
		}
	}

	@Override
	public boolean updatePowered() {
		if (this.mode == MoverMode.ComputerControlled || this.mode == MoverMode.Remote) {
			setPowered(getRequestedDirection());
		} else {
			setPowered(this.mode == MoverMode.TowardsSignal ? getSignals() : getSignals().negate());
		}
		return !getPowered().isEmpty();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setBoolean("active", getActive());
		nbtTagCompound.setBoolean("highlight", getHighlight());
		nbtTagCompound.setInteger("mode", this.mode.ordinal());
		nbtTagCompound.setFloat("speed", getSpeed());
		writeVector(nbtTagCompound, "requested", getRequestedDirection());
	}
}
