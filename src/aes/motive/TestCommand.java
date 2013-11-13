package aes.motive;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import aes.motive.tileentity.TileEntityBreaker;
import aes.motive.tileentity.TileEntityMover;
import aes.utils.Vector3i;

public class TestCommand extends Command {
	public static String name = "motive";

	ICommandSender sender;

	World world;

	Vector3i playerLocation;
	EntityPlayer player;

	private void clear(Vector3i start, ForgeDirection eastWestDirection, int eastWestDistance, ForgeDirection northSouthDirection, int northSouthDistance,
			int height) {
		set(start, eastWestDirection, eastWestDistance, northSouthDirection, northSouthDistance, 1, Block.dirt.blockID);
		set(start.increment(ForgeDirection.UP), eastWestDirection, eastWestDistance, northSouthDirection, northSouthDistance, height - 1, 0);
	}

	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "Usage: /" + name + " test miner [size]" + "\nor: /" + name + " test allblocks" + "\nor: /" + name
				+ " options breakerWithoutInventoryAvailable <drop|destroy|wait>";
	}

	@Override
	protected void processCommand(ICommandSender commandSender, List<String> arguments) {
		this.sender = commandSender;
		this.world = commandSender.getEntityWorld();
		this.playerLocation = new Vector3i(commandSender.getPlayerCoordinates());
		this.player = (EntityPlayer) commandSender;

		if (arguments.size() == 3 && arguments.get(0).equals("options") && arguments.get(1).equals("breakerWithoutInventoryAvailable")) {
			if (arguments.get(2).equals("drop")) {
				TileEntityBreaker.DROP_IF_NO_ATTACHED_INVENTORY = true;
				TileEntityBreaker.BREAK_IF_NO_ATTACHED_INVENTORY = false;
			}
			else if (arguments.get(2).equals("destroy")) {
				TileEntityBreaker.DROP_IF_NO_ATTACHED_INVENTORY = false;
				TileEntityBreaker.BREAK_IF_NO_ATTACHED_INVENTORY = true;
			}
			else if (arguments.get(2).equals("leave")) {
				TileEntityBreaker.DROP_IF_NO_ATTACHED_INVENTORY = false;
				TileEntityBreaker.BREAK_IF_NO_ATTACHED_INVENTORY = false;
			}
			else
			{
				sendChat(commandSender, getCommandUsage(commandSender));
			}
			return;
		}

		if (arguments.size() < 2 || arguments.size() > 3 || !arguments.get(0).equals("test")) {
			sendChat(commandSender, getCommandUsage(commandSender));
			return;
		}

		final String testName = arguments.get(1);

		if (testName.equals("allblocks")) {

			if (arguments.size() > 2) {
				sendChat(commandSender, getCommandUsage(commandSender));
				return;
			}

			sendChat(commandSender, "Running test allblocks");
			testAllKnownBlocks();
		} else if (testName.equals("miner")) {
			int gridSize = 74;
			if (arguments.size() > 2) {
				gridSize = parseInt(commandSender, arguments.get(2));
				if (gridSize < 2 || gridSize > 150) {
					sendChat(commandSender, "Invalid size. Allowable range is 2 to 150");
					sendChat(commandSender, getCommandUsage(commandSender));
					return;
				}
			}

			sendChat(commandSender, "Running test miner with size " + gridSize);
			testMiner(gridSize);
		} else {
			sendChat(commandSender, getCommandUsage(commandSender));
			return;
		}
		sendChat(this.sender, "Motive Test '" + testName + "' completed.");
	}

	private void set(Vector3i start, ForgeDirection eastWestDirection, int eastWestDistance, ForgeDirection northSouthDirection, int northSouthDistance,
			int height, int blockID) {
		set(start, eastWestDirection, eastWestDistance, northSouthDirection, northSouthDistance, height, blockID, 0);
	}

	private void set(Vector3i start, ForgeDirection eastWestDirection, int eastWestDistance, ForgeDirection northSouthDirection, int northSouthDistance,
			int height, int blockID, int meta) {

		sendChat(this.sender, "Setting " + eastWestDistance + " " + eastWestDirection.name() + " and " + northSouthDistance + " " + northSouthDirection.name()
				+ " and " + height + " UP from " + start + " to " + (blockID == 0 ? "air" : Block.blocksList[blockID].getLocalizedName()));

		final Vector3i end = start.increment(eastWestDirection, eastWestDistance + 1).increment(northSouthDirection, northSouthDistance + 1)
				.increment(ForgeDirection.UP, height + 1);

		for (int x = start.x; x != end.x; x += eastWestDirection.offsetX) {
			for (int z = start.z; z != end.z; z += northSouthDirection.offsetZ) {
				for (int y = start.increment(ForgeDirection.UP).y; y != end.y && y > 0 && y < 256; y += ForgeDirection.UP.offsetY) {
					if (this.world.getBlockId(x, y, z) != blockID || this.world.getBlockMetadata(x, y, z) != meta) {
						this.world.setBlock(x, y, z, blockID, meta, 2);
					}
				}
			}
		}
	}

	private void testAllKnownBlocks() {
		@SuppressWarnings("unchecked")
		List<IRecipe> recipesKnown = CraftingManager.getInstance().getRecipeList();

		Set<KnownBlock> recipeSet = new HashSet<KnownBlock>();

		for (IRecipe recipe : recipesKnown) {
			ItemStack result = recipe.getRecipeOutput();
			if (result != null) {
				Item item = result.getItem();
				if (item instanceof ItemBlock) {
					ItemBlock itemBlock = (ItemBlock) item;
					Block block = Block.blocksList[itemBlock.getBlockID()];

					if (block.blockHardness >= 0) {
						recipeSet.add(new KnownBlock(block, result.getItemDamage()));
					}
				}
			}
		}

		for (int id = 0; id < Block.blocksList.length; id++) {
			Block block = Block.blocksList[id];
			if (block != null && block.blockID == id) {
				if (block.blockHardness >= 0) {
					recipeSet.add(new KnownBlock(block, 0));
				}
			}
		}

		int count = recipeSet.size();
		List<KnownBlock> recipes = new LinkedList<KnownBlock>();
		recipes.addAll(recipeSet);

		int gridCount = (int) Math.ceil(Math.sqrt(count));

		int gridSize = gridCount * 3 + 2;

		clear(this.playerLocation.increment(ForgeDirection.DOWN, 3).increment(ForgeDirection.WEST, 10).increment(ForgeDirection.NORTH, 10),
				ForgeDirection.EAST, gridSize + 40, ForgeDirection.SOUTH, gridSize + 40, 255);

		final Vector3i platformStart = this.playerLocation.increment(ForgeDirection.UP, 1).increment(ForgeDirection.EAST, 10)
				.increment(ForgeDirection.SOUTH, 10);
		set(platformStart, ForgeDirection.EAST, gridSize, ForgeDirection.SOUTH, gridSize, 3, Block.stone.blockID);

		final Vector3i arrayStart = platformStart.increment(ForgeDirection.UP).increment(ForgeDirection.EAST).increment(ForgeDirection.SOUTH);
		set(arrayStart, ForgeDirection.EAST, gridSize - 2, ForgeDirection.SOUTH, gridSize - 2, 2, 0);

		final Vector3i end = arrayStart.increment(ForgeDirection.EAST, gridSize - 1).increment(ForgeDirection.SOUTH, gridSize - 1)
				.increment(ForgeDirection.UP, 2);

		int index = 0;

		for (int x = arrayStart.x; x != end.x; x += ForgeDirection.EAST.offsetX) {
			if (x % 3 == 0) {
				for (int z = arrayStart.z; z != end.z; z += ForgeDirection.SOUTH.offsetZ) {
					if (z % 3 == 0) {
						for (int y = arrayStart.increment(ForgeDirection.UP).y; y != end.y && y > 0 && y < 256; y += ForgeDirection.UP.offsetY) {

							while (true) {
								index++;
								if (index >= recipes.size()) {
									break;
								}
								KnownBlock recipe = recipes.get(index);
								Block block = recipe.block;

								Motive.log("setting " + x + "," + y + "," + z + " to " + block.getLocalizedName());
								this.world.setBlock(x, y, z, block.blockID, recipe.metadata, 3);

								this.world.setBlock(x, y, z + 1, Block.signPost.blockID, 0, 0);
								TileEntitySign sign = (TileEntitySign) this.world.getBlockTileEntity(x, y, z + 1);
								sign.signText[0] = block.getLocalizedName();
								sign.signText[1] = block.blockID + ":" + world.getBlockMetadata(x, y, z);
								this.world.markBlockForUpdate(x, y, z + 1);

								break;
							}
						}
					}
				}
			}
		}

		final Vector3i engineLocation = platformStart.increment(ForgeDirection.WEST).increment(ForgeDirection.UP);
		this.world.setBlock(engineLocation.x, engineLocation.y, engineLocation.z, Motive.BlockMover.blockID, 0, 2);

		final Vector3i powerLocation = engineLocation.increment(ForgeDirection.WEST);
		this.world.setBlock(powerLocation.x, powerLocation.y, powerLocation.z, Block.blockRedstone.blockID, 0, 2);

		final TileEntityMover mover = (TileEntityMover) this.world.getBlockTileEntity(engineLocation.x, engineLocation.y, engineLocation.z);
		mover.setLocked(true);
		mover.setSpeed(0f);

		player.inventory.setInventorySlotContents(0, new ItemStack(Motive.ItemMoverRemoteControl));
		
		if (index >= recipeSet.size() - 1) {
			sendChat(this.sender, "Placed all known (" + index + ") blocks.");
		}

		else {
			sendChat(this.sender, "Placed " + index + " of " + recipeSet.size() + " known blocks.");
		}
	}

	protected void testMiner(int sizeOfBreakerGrid) {
		final int areaSize = Math.max(80, sizeOfBreakerGrid + 10);

		clear(this.playerLocation.increment(ForgeDirection.DOWN, 3).increment(ForgeDirection.WEST, 10).increment(ForgeDirection.NORTH, 10),
				ForgeDirection.EAST, areaSize + 40, ForgeDirection.SOUTH, areaSize + 40, 255);

		final Vector3i platformStart = this.playerLocation.increment(ForgeDirection.UP, 1).increment(ForgeDirection.EAST, 10)
				.increment(ForgeDirection.SOUTH, 10);

		set(platformStart, ForgeDirection.EAST, 0, ForgeDirection.SOUTH, sizeOfBreakerGrid, sizeOfBreakerGrid, Motive.BlockBreaker.blockID,
				ForgeDirection.EAST.ordinal());

		final Vector3i fodderStartLocation = platformStart.increment(ForgeDirection.EAST, 5).increment(ForgeDirection.DOWN, 2)
				.increment(ForgeDirection.NORTH, 2);
		set(fodderStartLocation, ForgeDirection.EAST, 20, ForgeDirection.SOUTH, sizeOfBreakerGrid + 4, sizeOfBreakerGrid + 4, Block.blockDiamond.blockID);

		final Vector3i engineLocation = platformStart.increment(ForgeDirection.WEST).increment(ForgeDirection.UP);
		this.world.setBlock(engineLocation.x, engineLocation.y, engineLocation.z, Motive.BlockMover.blockID, 0, 2);

		final Vector3i powerLocation = engineLocation.increment(ForgeDirection.WEST);
		this.world.setBlock(powerLocation.x, powerLocation.y, powerLocation.z, Block.blockRedstone.blockID, 0, 2);

		final Vector3i lampLocation = powerLocation.increment(ForgeDirection.UP);
		this.world.setBlock(lampLocation.x, lampLocation.y, lampLocation.z, Block.redstoneLampActive.blockID, 0, 2);

		final Vector3i chestLocation = engineLocation.increment(ForgeDirection.SOUTH, 2);
		this.world.setBlock(chestLocation.x, chestLocation.y, chestLocation.z, Block.enderChest.blockID, 0, 2);

		final Vector3i chest2Location = this.playerLocation.increment(ForgeDirection.SOUTH, 2);
		this.world.setBlock(chest2Location.x, chest2Location.y, chest2Location.z, Block.enderChest.blockID, 0, 2);

		final TileEntityMover mover = (TileEntityMover) this.world.getBlockTileEntity(engineLocation.x, engineLocation.y, engineLocation.z);
		mover.setLocked(true);
		mover.setSpeed(0.5f);
		player.inventory.setInventorySlotContents(0, new ItemStack(Motive.ItemMoverRemoteControl));
	}
}
