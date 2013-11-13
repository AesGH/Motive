package aes.motive;

import net.minecraft.tileentity.TileEntity;
import aes.utils.Vector3i;

public class BlockInfo {
	public int blockid;
	public Vector3i coord;
	public TileEntity entity;
	public int metadata;
	public long nextUpdate;
}