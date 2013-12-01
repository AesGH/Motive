package aes.motive.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;

import org.lwjgl.input.Mouse;

import aes.motive.Motive;
import aes.motive.tileentity.MoverMode;
import aes.motive.tileentity.TileEntityMover;
import aes.motive.tileentity.TileEntityMoverBase;
import aes.utils.Vector3i;
import aes.utils.WorldUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMoverRemoteControl extends Item {
	public static TileEntityMover getPairedMover(World world, ItemStack stack) {
		final String uid = getPairedMoverUid(stack);
		if (uid == null)
			return null;

		final TileEntityMoverBase tileEntityMoverBase = TileEntityMoverBase.getMover(world, uid);
		if (tileEntityMoverBase instanceof TileEntityMover)
			return (TileEntityMover) tileEntityMoverBase;
		return null;
	}

	public static String getPairedMoverUid(ItemStack stack) {
		if (stack.stackTagCompound == null)
			return null;
		if (!stack.stackTagCompound.hasKey("pair"))
			return null;
		return ((NBTTagCompound) ((NBTTagList) stack.stackTagCompound.getTag("pair")).tagAt(0)).getString("moverUid");
	}

	public static void pairWithMover(ItemStack itemStack, TileEntityMover tileEntityMover) {
		if (itemStack.stackTagCompound == null) {
			itemStack.setTagCompound(new NBTTagCompound());
		}

		// if (!itemStack.stackTagCompound.hasKey("pair")) {
		itemStack.stackTagCompound.setTag("pair", new NBTTagList("ench"));
		// }

		final NBTTagList nbttaglist = (NBTTagList) itemStack.stackTagCompound.getTag("pair");
		final NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setString("moverUid", tileEntityMover.getUid());
		nbttaglist.appendTag(nbttagcompound);
	}

	public static void playerUsedRemote(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
		final TileEntityMover tileEntityMoverPairedTo = ItemMoverRemoteControl.getPairedMover(world, stack);
		if (tileEntityMoverPairedTo != null) {
			if (tileEntityMoverPairedTo.mode == MoverMode.Remote) {
				final Vector3i directionLooking = WorldUtils.getDirectionLooking(player);
				if (tileEntityMoverPairedTo.getRequestedDirection().equals(directionLooking)) {
					tileEntityMoverPairedTo.setRequestedDirection(new Vector3i());
				} else {
					tileEntityMoverPairedTo.setRequestedDirection(directionLooking);
				}
			}
		}
	}

	private static void unpair(ItemStack stack) {
		if (stack.stackTagCompound == null)
			return;
		if (!stack.stackTagCompound.hasKey("pair"))
			return;
		stack.stackTagCompound.removeTag("pair");
	}

	private boolean clicking;

	public ItemMoverRemoteControl(int id) {
		super(id);
		this.maxStackSize = 1;
		setCreativeTab(Motive.CreativeTab);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.block;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			TileEntityMover tileEntityMover;

			final World world = ((EntityPlayerMP) player).getServerForPlayer();

			final TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityMover) {
				tileEntityMover = (TileEntityMover) tileEntity;

				if (tileEntityMover.getUid().equals(getPairedMoverUid(stack))) {
					unpair(stack);
					return true;
				}

				pairWithMover(stack, tileEntityMover);
				return true;
			}

			final TileEntityMover tileEntityMoverPairedTo = ItemMoverRemoteControl.getPairedMover(world, stack);
			if (tileEntityMoverPairedTo == null) {
				sendUsage(player);
				return true;
			}

			final Vector3i cursorLocation = new Vector3i(x, y, z);
			if (tileEntityMoverPairedTo.isConnectedTo(cursorLocation)) {
				if (!tileEntityMoverPairedTo.toggleConnectedBlock(cursorLocation)) {
					player.sendChatToPlayer(new ChatMessageComponent().addText("Could not remove block."));
				}
				return true;
			}

			if (tileEntityMoverPairedTo.canConnectTo(cursorLocation)) {
				if (!tileEntityMoverPairedTo.toggleConnectedBlock(cursorLocation)) {
					player.sendChatToPlayer(new ChatMessageComponent().addText("Could not add block."));
				}
				return true;
			}

			player.sendChatToPlayer(new ChatMessageComponent().addText("Use on a block connected to the paired " + Motive.BlockMover.getLocalizedName()
					+ " to disconnect it, or a block touching a connected block to add it."));
		}
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) {
			Motive.log(world, "onItemRightClick");

			final String uid = getPairedMoverUid(stack);
			if (uid == null) {
				sendUsage(player);
				return stack;
			}

			final TileEntityMoverBase tileEntityMoverBase = TileEntityMoverBase.getMover(world, uid);
			if (tileEntityMoverBase == null) {
				player.sendChatToPlayer(new ChatMessageComponent().addText("Paired " + Motive.BlockMover.getLocalizedName() + " is not in range."));
				return stack;
			}

			if (tileEntityMoverBase instanceof TileEntityMover) {
				player.openGui(Motive.instance, 0, world, tileEntityMoverBase.xCoord, tileEntityMoverBase.yCoord, tileEntityMoverBase.zCoord);
			}
		}

		return stack;
	};

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		Motive.log(world, "onItemFirst");
		if (!world.isRemote) {

			/*
			 * final TileEntityMover tileEntityMoverPairedTo =
			 * ItemMoverRemoteControl.getPairedMover(world, stack); if
			 * (tileEntityMoverPairedTo != null) {
			 * if(tileEntityMoverPairedTo.mode == MoverMode.Remote) { Vec3
			 * lookVec = player.getLookVec();
			 * tileEntityMoverPairedTo.setPowered(new
			 * Vector3i((int)lookVec.xCoord, (int)lookVec.yCoord,
			 * (int)lookVec.zCoord)); } }
			 */
			// isClickingOnBlock = true;

			// Motive.packetHandler.sendPlayerUsedRemote(player.inventory.currentItem,
			// world.provider.dimensionId, x, y, z);
		}
		return false;

		/*
		 * Motive.log(world, "onItemUseFirst"); onRightClick(stack, player,
		 * world, x, y, z);
		 */
		// return false;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);

		if (!par2World.isRemote)
			return;

		if (this.clicking) {
			if (!Mouse.isButtonDown(0) || !Minecraft.getMinecraft().inGameHasFocus) {
				this.clicking = false;
			}
			return;
		}

		if (Mouse.isButtonDown(0) && Minecraft.getMinecraft().objectMouseOver == null && Minecraft.getMinecraft().inGameHasFocus) {
			this.clicking = true;
			Motive.packetHandler.leftClickedOpenSpaceWithItem((EntityPlayer) par3Entity);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon("motive:moverRemoteIcon");
	}

	protected void sendUsage(EntityPlayer player) {
		player.sendChatToPlayer(new ChatMessageComponent().addText("Use on a " + Motive.BlockMover.getLocalizedName()
				+ " to pair to it, then you can remotely open it, or individually connect or disconnect blocks from it."));
	}
}