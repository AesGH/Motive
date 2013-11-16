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
	public MoverMode mode = MoverMode.AwayFromSignal;

	static String[] commands = new String[] { "isActive", "isMoving", "move", "lock" };

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

		if (command.equals("isActive"))
			return new Object[] { getActive() };

		if (command.equals("isMoving"))
			return new Object[] { this.moving };

		if (command.equals("move")) {
			if (this.moving) {
				Motive.log(this.worldObj, "IPeripheral.move returning false");
				return new Object[] { false };
			}

			Motive.log(this.worldObj, "IPeripheral.move");
			if (arguments.length == 3) {
				setPowered(new Vector3i(getIntArg(arguments, 0), getIntArg(arguments, 1), getIntArg(arguments, 2)));
				return new Object[] { checkBeginMoving() };
			}
			if (arguments.length == 1) {
				final String direction = getStringArg(arguments, 0);
				final ForgeDirection side = ForgeDirection.valueOf(direction.toUpperCase());
				if (side != null) {
					setPowered(new Vector3i(side.offsetX, side.offsetY, side.offsetZ));
					return new Object[] { checkBeginMoving() };
				}
			}
			throw new Exception("Usage: move(x, y, z) or move(\"direction\") north, south, east, west, up or down");
		}

		if (command.equals("lock"))
			return new Object[] { setLocked(true) };

		if (command.equals("unlock"))
			return new Object[] { setLocked(false) };

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

	@Override
	public boolean getActive() {
		return this.active;
	}

	@Override
	public Block getBlockType() {
		return Motive.BlockMover;
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
			updateBlock();
		}
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

	@Override
	public void setSpeed(float speed) {
		final float newSpeed = Math.max(0f, Math.min(1f, speed));
		if (this.speed != newSpeed) {
			this.speed = newSpeed;
			updateBlock();
		}
	}

	@Override
	boolean updatePowered() {
		if (this.mode != MoverMode.ComputerControlled) {
			int poweredX = 0, poweredY = 0, poweredZ = 0;
			final Vector3i location = getLocation();
			for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
				final Vector3i from = location.increment(direction);

				if (this.worldObj.isBlockProvidingPowerTo(from.x, from.y, from.z, direction.getOpposite().ordinal()) != 0
						|| this.worldObj.getIndirectPowerOutput(from.x, from.y, from.z, direction.ordinal())) {
					poweredX += direction.offsetX;
					poweredY += direction.offsetY;
					poweredZ += direction.offsetZ;
				}
			}
			setPowered((this.mode == MoverMode.TowardsSignal ? new Vector3i(poweredX, poweredY, poweredZ) : new Vector3i(-poweredX, -poweredY, -poweredZ))
					.normalise());
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
	}
}
