package aes.base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import aes.motive.Motive;
import aes.utils.Vector3i;

public class TileEntityBase extends TileEntity {

	@Override
	public Packet getDescriptionPacket() {
		final NBTTagCompound nbttagcompound = new NBTTagCompound();
		writeToNBT(nbttagcompound);
		return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 2, nbttagcompound);
	}

	public Vector3i getLocation() {
		return new Vector3i(this.xCoord, this.yCoord, this.zCoord);
	}

	public void onBlockNeighborChange() {
	}

	@Override
	public void onDataPacket(net.minecraft.network.INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
	}

	public void removeTileEntity() {
//		Motive.log(this.worldObj, "TileEntity removed");
		this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
		invalidate();
	}

	@Override
	public boolean shouldRefresh(int oldID, int newID, int oldMeta, int newMeta, World world, int x, int y, int z) {
		if (newID != oldID) {
			if (this.worldObj.isRemote) {
				removeTileEntity();
			}
		}
		return true;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0;
	};

	protected void updateBlock() {
		if (this.worldObj == null)
			return;
		Motive.log(this.worldObj, "updateBlock " + getLocation() + " at tick " + this.worldObj.getWorldTime());
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}
}
