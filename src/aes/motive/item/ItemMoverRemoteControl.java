package aes.motive.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import aes.motive.Motive;
import aes.motive.tileentity.TileEntityMover;
import aes.motive.tileentity.TileEntityMoverBase;
import aes.utils.Vector3i;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMoverRemoteControl extends Item {
	public ItemMoverRemoteControl(int id) {
		super(id);
		this.maxStackSize = 1;
		setCreativeTab(Motive.CreativeTab);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.none;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		float closest = java.lang.Float.MAX_VALUE;
		TileEntityMover closestMover = null;

		final Vector3i playerLocation = new Vector3i((int) par3EntityPlayer.posX, (int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
		for (final TileEntityMoverBase tileEntityMoverBase : TileEntityMoverBase.getMovers(par2World).values()) {
			if (tileEntityMoverBase instanceof TileEntityMover) {
				final float distance = tileEntityMoverBase.getLocation().distanceTo(playerLocation);
				if (distance < closest) {
					closest = distance;
					closestMover = (TileEntityMover) tileEntityMoverBase;
				}
			}
		}

		if (closestMover != null) {
			par3EntityPlayer.openGui(Motive.instance, 0, closestMover.worldObj, closestMover.xCoord, closestMover.yCoord, closestMover.zCoord);
		}

		// Motive.log("onItemRightClick");
		return par1ItemStack;
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8,
			float par9, float par10) {
		// Motive.log("onItemUse");
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon("motive:moverRemoteIcon");
	}
}