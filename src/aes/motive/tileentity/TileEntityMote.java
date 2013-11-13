package aes.motive.tileentity;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import aes.motive.Motive;
import aes.utils.Vector3i;

public class TileEntityMote extends TileEntityMoverBase {
	private ForgeDirection currentDirection = ForgeDirection.NORTH;

	@Override
	public boolean getActive() {
		return true;
	}

	public ForgeDirection getCurrentDirection() {
		return this.currentDirection;
	};

	@Override
	public Vector3i getPowered() {
		return new Vector3i(getCurrentDirection().offsetX, getCurrentDirection().offsetY, getCurrentDirection().offsetZ);
	}

	@Override
	public float getSpeed() {
		return 0.5f;
	};

	@Override
    public Block getBlockType()
    {
		return Motive.BlockMote;
    }
	
	@Override
	public void onBlockNeighborChange() {
		super.onBlockNeighborChange();
		final Vector3i movingTo = getLocation().add(getPowered());
		if (!this.worldObj.isAirBlock(movingTo.x, movingTo.y, movingTo.z)) {
			Motive.log(this.worldObj, "Changing direction");
			setCurrentDirection(getCurrentDirection().getRotation(ForgeDirection.UP));
		}
		setActive(true);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		setCurrentDirection(ForgeDirection.getOrientation(nbtTagCompound.getInteger("currentDirection")));
	}

	public void setCurrentDirection(ForgeDirection currentDirection) {
		if (this.currentDirection != currentDirection) {
			this.currentDirection = currentDirection;
			updateBlock();
		}
	}

	@Override
	boolean updatePowered() {
		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setInteger("currentDirection", getCurrentDirection().ordinal());
	}
}
