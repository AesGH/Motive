package aes.motive.tileentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraftforge.common.ForgeDirection;
import aes.base.BlockBase;
import aes.base.TileEntityBase;
import aes.motive.Motive;
import aes.motive.render.model.TextureBreakerConnection;
import aes.utils.Vector3i;
import aes.utils.WorldUtils;
import cpw.mods.fml.common.FMLCommonHandler;

public class TileEntityBreaker extends TileEntityBase {
	public static boolean DROP_IF_NO_ATTACHED_INVENTORY = false;
	public static boolean BREAK_IF_NO_ATTACHED_INVENTORY = false;

	List<IInventory> foundConnectedInventories = new LinkedList<IInventory>();

	long connectedInventoriesFoundOnTick;
	private ConnectionMask connectionMask = new ConnectionMask();

	private void addConnectedLocationsWithTileEntities(HashSet<Vector3i> locations, Vector3i location, HashSet<Vector3i> checkedLocations) {
		if (checkedLocations.contains(location) || !WorldUtils.isNonEmptyBlock(this.worldObj, location))
			return;

		final TileEntity tileEntity = this.worldObj.getBlockTileEntity(location.x, location.y, location.z);
		if (tileEntity == null)
			return;

		locations.add(location);

		if (!(tileEntity instanceof TileEntityBreaker))
			return;

		final ForgeDirection facing = ForgeDirection.getOrientation(this.worldObj.getBlockMetadata(location.x, location.y, location.z));

		checkedLocations.add(location);
		for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			if (direction != facing) {
				addConnectedLocationsWithTileEntities(locations, location.increment(direction), checkedLocations);
			}
		}
	}

	private boolean checkIsConnected(ForgeDirection direction) {
		final Vector3i location = getLocation().increment(direction);
		final TileEntity tileEntity = this.worldObj.getBlockTileEntity(location.x, location.y, location.z);
		if (tileEntity instanceof TileEntityBreaker)
			return true;
		if (tileEntity instanceof IInventory)
			return true;
		else if (Minecraft.getMinecraft().isSingleplayer() && tileEntity instanceof TileEntityEnderChest)
			return true;

		return false;
	}

	private IInventory findConnectedInventoryFor(ItemStack stack) {
		IInventory inventoryToAddTo = null;
		int mostSpareSlots = -1;

		for (final IInventory inventory : getConnectedInventories()) {
			int spareSlots = 0;
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				final ItemStack stackInSlot = inventory.getStackInSlot(i);
				if (stackInSlot == null) {
					spareSlots++;
					continue;
				}
				if (stack.isStackable() && stackInSlot.itemID == stack.itemID && stackInSlot.getItemDamage() == stack.getItemDamage()
						&& stackInSlot.stackSize + stack.stackSize <= inventory.getInventoryStackLimit())
					return inventory;
			}

			if (spareSlots > mostSpareSlots) {
				mostSpareSlots = spareSlots;
				inventoryToAddTo = inventory;
			}
		}

		return inventoryToAddTo;
	}

	public List<IInventory> getConnectedInventories() {
		final long tick = this.worldObj.getWorldTime();
		if (tick == this.connectedInventoriesFoundOnTick)
			return this.foundConnectedInventories;

		final HashSet<Vector3i> locationsWithTileEntities = getConnectedLocationsWithTileEntities();

		final List<IInventory> connectedInventories = new LinkedList<IInventory>();

		for (final Vector3i location : locationsWithTileEntities) {
			final TileEntity tileEntity = this.worldObj.getBlockTileEntity(location.x, location.y, location.z);
			if (tileEntity instanceof IInventory) {
				connectedInventories.add((IInventory) tileEntity);
			} else if (Minecraft.getMinecraft().isSingleplayer() && tileEntity instanceof TileEntityEnderChest) {
				final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				if (!server.getConfigurationManager().playerEntityList.isEmpty()) {
					connectedInventories.add(((EntityPlayerMP) server.getConfigurationManager().playerEntityList.get(0)).getInventoryEnderChest());
				}
			}
		}

		for (final Vector3i location : locationsWithTileEntities) {
			final TileEntity tileEntity = this.worldObj.getBlockTileEntity(location.x, location.y, location.z);
			if (tileEntity instanceof TileEntityBreaker) {
				final TileEntityBreaker tileEntityBreaker = (TileEntityBreaker) tileEntity;
				tileEntityBreaker.foundConnectedInventories = connectedInventories;
				tileEntityBreaker.connectedInventoriesFoundOnTick = tick;
			}
		}

		return connectedInventories;
	}

	public HashSet<Vector3i> getConnectedLocationsWithTileEntities() {
		final HashSet<Vector3i> inventoryLocations = new HashSet<Vector3i>();
		addConnectedLocationsWithTileEntities(inventoryLocations, new Vector3i(this.xCoord, this.yCoord, this.zCoord), new HashSet<Vector3i>());
		return inventoryLocations;
	}

	public ConnectionMask getConnectionMask() {
		return this.connectionMask;
	}

	@Override
	public String getRenderCacheKey() {
		return "TileEntityBreaker_" + this.connectionMask.getValue();
	}

	private boolean isBreakableBlock(Vector3i location) {
		if (!WorldUtils.isNonEmptyBlock(this.worldObj, location))
			return false;

		final Block bl = Block.blocksList[this.worldObj.getBlockId(location.x, location.y, location.z)];
		if (bl.getBlockHardness(this.worldObj, location.x, location.y, location.z) < 0.0F && bl.blockID != Motive.BlockMote.blockID)
			return false;

		return true;
	}

	@Override
	public void onBlockNeighborChange() {
		updateConnections();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		setConnectionMask(new ConnectionMask(nbtTagCompound.getInteger("connectionMask")));
	}

	public void setBlockBounds() {
		final float strutHeight = (float) TextureBreakerConnection.strutHeight / 2;

		if (this.connectionMask.isConnectedBack()) {
			Motive.BlockBreaker.setBlockBounds(0, 0, 0, 1, 1, 1);
			return;
		}

		switch (getFacing()) {
		case NORTH:
			Motive.BlockBreaker.setBlockBounds(0, 0, 0, 1, 1, 0.5f + strutHeight);
			return;
		case SOUTH:
			Motive.BlockBreaker.setBlockBounds(0, 0, 0.5f - strutHeight, 1, 1, 1);
			return;
		case WEST:
			Motive.BlockBreaker.setBlockBounds(0, 0, 0, 0.5f + strutHeight, 1, 1);
			return;
		case EAST:
			Motive.BlockBreaker.setBlockBounds(0.5f - strutHeight, 0, 0, 1, 1, 1);
			return;
		case DOWN:
			Motive.BlockBreaker.setBlockBounds(0, 0, 0, 1, 0.5f + strutHeight, 1);
			return;
		case UP:
			Motive.BlockBreaker.setBlockBounds(0, 0.5f - strutHeight, 0, 1, 1, 1);
			return;
		default:
			Motive.BlockBreaker.setBlockBounds(0, 0, 0, 1, 1, 1);
		}
	}

	public void setConnectionMask(ConnectionMask connectionMask) {
		if (!getConnectionMask().equals(connectionMask)) {
			this.connectionMask = connectionMask;
			updateBlock();
		}
	}

	protected void updateConnections() {

		boolean connectedLeft;
		boolean connectedRight;
		boolean connectedUp;
		boolean connectedDown;
		boolean connectedBack;

		switch (getFacing()) {
		case NORTH:
			connectedLeft = checkIsConnected(ForgeDirection.WEST);
			connectedRight = checkIsConnected(ForgeDirection.EAST);
			connectedUp = checkIsConnected(ForgeDirection.UP);
			connectedDown = checkIsConnected(ForgeDirection.DOWN);
			connectedBack = checkIsConnected(ForgeDirection.SOUTH);
			break;
		case SOUTH:
			connectedLeft = checkIsConnected(ForgeDirection.EAST);
			connectedRight = checkIsConnected(ForgeDirection.WEST);
			connectedUp = checkIsConnected(ForgeDirection.UP);
			connectedDown = checkIsConnected(ForgeDirection.DOWN);
			connectedBack = checkIsConnected(ForgeDirection.NORTH);
			break;
		case WEST:
			connectedLeft = checkIsConnected(ForgeDirection.SOUTH);
			connectedRight = checkIsConnected(ForgeDirection.NORTH);
			connectedUp = checkIsConnected(ForgeDirection.UP);
			connectedDown = checkIsConnected(ForgeDirection.DOWN);
			connectedBack = checkIsConnected(ForgeDirection.EAST);
			break;
		case EAST:
			connectedLeft = checkIsConnected(ForgeDirection.NORTH);
			connectedRight = checkIsConnected(ForgeDirection.SOUTH);
			connectedUp = checkIsConnected(ForgeDirection.UP);
			connectedDown = checkIsConnected(ForgeDirection.DOWN);
			connectedBack = checkIsConnected(ForgeDirection.WEST);
			break;
		case UP:
			connectedLeft = checkIsConnected(ForgeDirection.WEST);
			connectedRight = checkIsConnected(ForgeDirection.EAST);
			connectedUp = checkIsConnected(ForgeDirection.SOUTH);
			connectedDown = checkIsConnected(ForgeDirection.NORTH);
			connectedBack = checkIsConnected(ForgeDirection.DOWN);
			break;
		case DOWN:
			connectedLeft = checkIsConnected(ForgeDirection.WEST);
			connectedRight = checkIsConnected(ForgeDirection.EAST);
			connectedUp = checkIsConnected(ForgeDirection.NORTH);
			connectedDown = checkIsConnected(ForgeDirection.SOUTH);
			connectedBack = checkIsConnected(ForgeDirection.UP);
			break;
		default:
			return;
		}

		setConnectionMask(new ConnectionMask(connectedLeft, connectedRight, connectedUp, connectedDown, connectedBack));
	}

	@Override
	public void updateEntity() {
		final Vector3i location = new Vector3i(this.xCoord, this.yCoord, this.zCoord).increment(BlockBase.getFacing(this.worldObj, this.xCoord, this.yCoord,
				this.zCoord));

		if (!isBreakableBlock(location))
			return;

		final Block block = Block.blocksList[this.worldObj.getBlockId(location.x, location.y, location.z)];

		final ArrayList<ItemStack> blockDropped = block.getBlockDropped(this.worldObj, location.x, location.y, location.z,
				this.worldObj.getBlockMetadata(location.x, location.y, location.z), 0);

		boolean breakBlock = true;

		for (ItemStack stack : blockDropped) {
			final IInventory inventory = findConnectedInventoryFor(stack);
			if (inventory == null || (stack = TileEntityHopper.insertStack(inventory, stack, -1)) != null) {
				if (DROP_IF_NO_ATTACHED_INVENTORY) {
					WorldUtils.dropItem(this.worldObj, location.x, location.y, location.z, stack);
				} else if (!BREAK_IF_NO_ATTACHED_INVENTORY) {
					breakBlock = false;
				}
			}
		}

		if (breakBlock) {
			this.worldObj.setBlockToAir(location.x, location.y, location.z);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setInteger("connectionMask", getConnectionMask().getValue());
	}
}