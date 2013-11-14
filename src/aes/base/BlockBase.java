package aes.base;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import aes.motive.Motive;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBase extends BlockContainer {
	public static ForgeDirection getFacing(World worldObj, int x, int y, int z) {
		return ForgeDirection.getOrientation(worldObj.getBlockMetadata(x, y, z));
	}

	public static void setFacing(World worldObj, int x, int y, int z, ForgeDirection facing) {
		worldObj.setBlockMetadataWithNotify(x, y, z, facing.ordinal(), 3);
	}

	protected BlockBase(int id, Material material) {
		super(id, material);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int oldBlockId, int oldMetadata) {
		final TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityBase) {
			((TileEntityBase) tileEntity).removeTileEntity();
			return;
		}
		super.breakBlock(world, x, y, z, oldBlockId, oldMetadata);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
//		Motive.log(world, "createNewTileEntity: " + this.getClass().getName());
		return null;
	}

	@Override
	public int damageDropped(int par1) {
		return 0;
	}

	@Override
	public int getLightOpacity(World world, int x, int y, int z) {
		return 1;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return super.getPickBlock(target, world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean hasTileEntity() {
		return true;
	}

	@Override
	public boolean isBlockNormalCube(World world, int x, int y, int z) {
		return false;
	};

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void onBlockPlacedBy(World par1World, int x, int y, int z, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
		setFacing(par1World, x, y, z, ForgeDirection.getOrientation(BlockPistonBase.determineOrientation(par1World, x, y, z, par5EntityLivingBase)));
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which
	 * neighbor changed (coordinates passed are their own) Args: x, y, z,
	 * neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
		super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
		final TileEntityBase tileEntity = (TileEntityBase) par1World.getBlockTileEntity(par2, par3, par4);

		if (tileEntity != null) {
			tileEntity.onBlockNeighborChange();
		}
	}

	@Override
	public void registerIcons(IconRegister iconRegister) {
		setCreativeTab(Motive.CreativeTab);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis) {
		setFacing(worldObj, x, y, z, getFacing(worldObj, x, y, z).getRotation(axis));
		return true;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return false;
	}
}
