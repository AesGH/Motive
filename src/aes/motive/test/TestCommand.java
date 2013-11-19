package aes.motive.test;

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
import aes.motive.Command;
import aes.motive.Motive;
import aes.motive.item.ItemMoverRemoteControl;
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
			} else if (arguments.get(2).equals("destroy")) {
				TileEntityBreaker.DROP_IF_NO_ATTACHED_INVENTORY = false;
				TileEntityBreaker.BREAK_IF_NO_ATTACHED_INVENTORY = true;
			} else if (arguments.get(2).equals("leave")) {
				TileEntityBreaker.DROP_IF_NO_ATTACHED_INVENTORY = false;
				TileEntityBreaker.BREAK_IF_NO_ATTACHED_INVENTORY = false;
			} else {
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
		} else if (testName.equals("boat")) {

			if (arguments.size() > 2) {
				sendChat(commandSender, getCommandUsage(commandSender));
				return;
			}

			sendChat(commandSender, "Running test boat");
			testBoat();
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
		} else if (testName.equals("clear")) {
			int gridSize = 74;
			if (arguments.size() > 2) {
				gridSize = parseInt(commandSender, arguments.get(2));
				if (gridSize < 2 || gridSize > 150) {
					sendChat(commandSender, "Invalid size. Allowable range is 2 to 150");
					sendChat(commandSender, getCommandUsage(commandSender));
					return;
				}
			}

			sendChat(commandSender, "Running test clear with size " + gridSize);
			testClear(gridSize);
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

		final Vector3i end = start.increment(eastWestDirection, eastWestDistance).increment(northSouthDirection, northSouthDistance)
				.increment(ForgeDirection.UP, height);

		for (int x = start.x; x != end.x; x += eastWestDirection.offsetX) {
			for (int z = start.z; z != end.z; z += northSouthDirection.offsetZ) {
				for (int y = start.y; y != end.y && y > 0 && y < 256; y += ForgeDirection.UP.offsetY) {
					set(new Vector3i(x, y, z), blockID, meta);
				}
			}
		}
	}

	private void set(Vector3i location, int blockId) {
		set(location, blockId, 0);
	}

	private void set(Vector3i location, int blockId, int meta) {
		this.world.setBlock(location.x, location.y, location.z, blockId, meta, 3);
	}

	private void testAllKnownBlocks() {
		@SuppressWarnings("unchecked")
		final List<IRecipe> recipesKnown = CraftingManager.getInstance().getRecipeList();

		final Set<KnownBlock> recipeSet = new HashSet<KnownBlock>();

		for (final IRecipe recipe : recipesKnown) {
			final ItemStack result = recipe.getRecipeOutput();
			if (result != null) {
				final Item item = result.getItem();
				if (item instanceof ItemBlock) {
					final ItemBlock itemBlock = (ItemBlock) item;
					final Block block = Block.blocksList[itemBlock.getBlockID()];

					if (block.blockHardness >= 0) {
						recipeSet.add(new KnownBlock(block, result.getItemDamage()));
					}
				}
			}
		}

		for (int id = 0; id < Block.blocksList.length; id++) {
			final Block block = Block.blocksList[id];
			if (block != null && block.blockID == id) {
				if (block.blockHardness >= 0) {
					recipeSet.add(new KnownBlock(block, 0));
				}
			}
		}

		final int count = recipeSet.size();
		final List<KnownBlock> recipes = new LinkedList<KnownBlock>();
		recipes.addAll(recipeSet);

		final int gridCount = (int) Math.ceil(Math.sqrt(count));

		final int gridSize = gridCount * 3 + 2;

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
								final KnownBlock recipe = recipes.get(index);
								final Block block = recipe.block;

								Motive.log("setting " + x + "," + y + "," + z + " to " + block.getLocalizedName());
								final Vector3i location = new Vector3i(x, y, z);
								set(location, block.blockID, recipe.metadata);

								set(location.increment(ForgeDirection.SOUTH), Block.signPost.blockID);
								final TileEntitySign sign = (TileEntitySign) this.world.getBlockTileEntity(x, y, z + 1);

								final String name = block.getLocalizedName();
								if (name.length() > 14) {
									sign.signText[0] = name.substring(0, 14);
									sign.signText[1] = (name.substring(14) + "                            ").substring(0, 14);
								} else {
									sign.signText[0] = block.getLocalizedName();
								}

								sign.signText[3] = block.blockID + ":" + this.world.getBlockMetadata(x, y, z);
								this.world.markBlockForUpdate(x, y, z + 1);

								break;
							}
						}
					}
				}
			}
		}

		final Vector3i engineLocation = platformStart.increment(ForgeDirection.WEST).increment(ForgeDirection.UP);
		set(engineLocation, Motive.BlockMover.blockID);

		final Vector3i powerLocation = engineLocation.increment(ForgeDirection.WEST);
		set(powerLocation, Block.blockRedstone.blockID);

		final TileEntityMover tileEntityMover = (TileEntityMover) this.world.getBlockTileEntity(engineLocation.x, engineLocation.y, engineLocation.z);
		tileEntityMover.setLocked(true);
		tileEntityMover.setSpeed(0f);

		final ItemStack stack = new ItemStack(Motive.ItemMoverRemoteControl);
		this.player.inventory.setInventorySlotContents(0, stack);
		ItemMoverRemoteControl.pairWithMover(stack, tileEntityMover);

		if (index >= recipeSet.size() - 1) {
			sendChat(this.sender, "Placed all known (" + index + ") blocks.");
		}

		else {
			sendChat(this.sender, "Placed " + index + " of " + recipeSet.size() + " known blocks.");
		}
	}

	protected void testBoat() {
		final int areaSize = 10;
		clear(this.playerLocation.increment(ForgeDirection.DOWN, 3).increment(ForgeDirection.WEST, 10).increment(ForgeDirection.NORTH, 10),
				ForgeDirection.EAST, areaSize + 40, ForgeDirection.SOUTH, areaSize + 40, 255);

		final Vector3i start = this.playerLocation.increment(ForgeDirection.EAST, 10).increment(ForgeDirection.SOUTH, 10);

		set(start, ForgeDirection.EAST, 4, ForgeDirection.SOUTH, 3, 1, Block.planks.blockID);

		final Vector3i level2 = start.increment(ForgeDirection.UP).increment(ForgeDirection.WEST);
		set(level2, ForgeDirection.EAST, 6, ForgeDirection.SOUTH, 3, 1, Block.planks.blockID);

		set(level2.increment(ForgeDirection.UP), ForgeDirection.EAST, 6, ForgeDirection.SOUTH, 3, 1, 0);
		set(level2.increment(ForgeDirection.EAST).increment(ForgeDirection.SOUTH), ForgeDirection.EAST, 4, ForgeDirection.SOUTH, 1, 1, 0);

		final Vector3i level3 = level2.increment(ForgeDirection.UP).increment(ForgeDirection.NORTH);
		set(level3, ForgeDirection.EAST, 6, ForgeDirection.SOUTH, 5, 1, Block.planks.blockID);
		set(level3.increment(ForgeDirection.WEST, 2).increment(ForgeDirection.SOUTH), ForgeDirection.EAST, 10, ForgeDirection.SOUTH, 3, 1, Block.planks.blockID);
		set(level3.increment(ForgeDirection.WEST, 4).increment(ForgeDirection.SOUTH, 2), ForgeDirection.EAST, 13, ForgeDirection.SOUTH, 1, 1,
				Block.planks.blockID);

		final Vector3i level4 = level3.increment(ForgeDirection.UP).increment(ForgeDirection.NORTH).increment(ForgeDirection.WEST, 1);
		set(level4, ForgeDirection.EAST, 8, ForgeDirection.SOUTH, 7, 1, Block.planks.blockID);
		set(level4.increment(ForgeDirection.WEST, 2).increment(ForgeDirection.SOUTH), ForgeDirection.EAST, 12, ForgeDirection.SOUTH, 5, 1, Block.planks.blockID);
		set(level4.increment(ForgeDirection.WEST, 3).increment(ForgeDirection.SOUTH, 2), ForgeDirection.EAST, 14, ForgeDirection.SOUTH, 3, 1,
				Block.planks.blockID);
		set(level4.increment(ForgeDirection.WEST, 6).increment(ForgeDirection.SOUTH, 3), ForgeDirection.EAST, 17, ForgeDirection.SOUTH, 1, 1,
				Block.planks.blockID);

		set(level4.increment(ForgeDirection.EAST, 1).increment(ForgeDirection.SOUTH, 1), ForgeDirection.EAST, 6, ForgeDirection.SOUTH, 5, 1, 0);
		set(level4.increment(ForgeDirection.WEST, 1).increment(ForgeDirection.SOUTH, 2), ForgeDirection.EAST, 10, ForgeDirection.SOUTH, 3, 1, 0);

		final Vector3i level5 = level4.increment(ForgeDirection.UP);

		set(level5.increment(ForgeDirection.EAST, 5), ForgeDirection.EAST, 5, ForgeDirection.SOUTH, 7, 1, Block.planks.blockID);
		set(level5.increment(ForgeDirection.EAST, 7).increment(ForgeDirection.SOUTH, 1), ForgeDirection.EAST, 4, ForgeDirection.SOUTH, 5, 1,
				Block.planks.blockID);
		set(level5.increment(ForgeDirection.EAST, 5).increment(ForgeDirection.SOUTH, 1), ForgeDirection.EAST, 4, ForgeDirection.SOUTH, 5, 1, 0);
		set(level5.increment(ForgeDirection.EAST, 7).increment(ForgeDirection.SOUTH, 2), ForgeDirection.EAST, 3, ForgeDirection.SOUTH, 3, 1, 0);

		set(level5, ForgeDirection.EAST, 1, ForgeDirection.SOUTH, 7, 1, Block.planks.blockID);
		set(level5.increment(ForgeDirection.SOUTH, 1), ForgeDirection.EAST, 1, ForgeDirection.SOUTH, 5, 1, 0);

		set(level5.increment(ForgeDirection.WEST, 2).increment(ForgeDirection.SOUTH), ForgeDirection.EAST, 2, ForgeDirection.SOUTH, 5, 1, Block.planks.blockID);
		set(level5.increment(ForgeDirection.WEST, 2).increment(ForgeDirection.SOUTH, 2), ForgeDirection.EAST, 2, ForgeDirection.SOUTH, 3, 1, 0);

		set(level5.increment(ForgeDirection.WEST, 3).increment(ForgeDirection.SOUTH, 2), ForgeDirection.EAST, 1, ForgeDirection.SOUTH, 3, 1,
				Block.planks.blockID);
		set(level5.increment(ForgeDirection.WEST, 3).increment(ForgeDirection.SOUTH, 3), ForgeDirection.EAST, 1, ForgeDirection.SOUTH, 1, 1, 0);
		set(level5.increment(ForgeDirection.WEST, 8).increment(ForgeDirection.SOUTH, 3), ForgeDirection.EAST, 5, ForgeDirection.SOUTH, 1, 1,
				Block.planks.blockID);

		final Vector3i level6 = level5.increment(ForgeDirection.UP);

		set(level6.increment(ForgeDirection.EAST, 6), ForgeDirection.EAST, 4, ForgeDirection.SOUTH, 7, 1, Block.planks.blockID);
		set(level6.increment(ForgeDirection.EAST, 7).increment(ForgeDirection.SOUTH, 1), ForgeDirection.EAST, 4, ForgeDirection.SOUTH, 5, 1,
				Block.planks.blockID);
		set(level6.increment(ForgeDirection.EAST, 6).increment(ForgeDirection.SOUTH, 1), ForgeDirection.EAST, 3, ForgeDirection.SOUTH, 5, 1, 0);
		set(level6.increment(ForgeDirection.EAST, 7).increment(ForgeDirection.SOUTH, 2), ForgeDirection.EAST, 4, ForgeDirection.SOUTH, 3, 1, 0);

		final Vector3i level7 = level6.increment(ForgeDirection.UP);

		set(level7.increment(ForgeDirection.EAST, 7), ForgeDirection.EAST, 3, ForgeDirection.SOUTH, 7, 1, Block.planks.blockID);
		set(level7.increment(ForgeDirection.EAST, 7).increment(ForgeDirection.SOUTH, 1), ForgeDirection.EAST, 4, ForgeDirection.SOUTH, 5, 1,
				Block.planks.blockID);
		// set(level6.increment(ForgeDirection.EAST,
		// 7).increment(ForgeDirection.SOUTH, 1), ForgeDirection.EAST, 2,
		// ForgeDirection.SOUTH, 5, 1, 0);
		// set(level6.increment(ForgeDirection.EAST,
		// 7).increment(ForgeDirection.SOUTH, 2), ForgeDirection.EAST, 3,
		// ForgeDirection.SOUTH, 3, 1, 0);

		/*
		 * set(level5.increment(ForgeDirection.EAST,
		 * 7).increment(ForgeDirection.SOUTH, 2), ForgeDirection.EAST, 3,
		 * ForgeDirection.SOUTH, 3, 1, Block.planks.blockID);
		 * set(level5.increment(ForgeDirection.EAST,
		 * 7).increment(ForgeDirection.SOUTH, 2), ForgeDirection.EAST, 2,
		 * ForgeDirection.SOUTH, 3, 1, 0);
		 * 
		 * 
		 * set(level5.increment(ForgeDirection.EAST,
		 * 7).increment(ForgeDirection.SOUTH, 1).increment(ForgeDirection.UP,
		 * 1), ForgeDirection.EAST, 3, ForgeDirection.SOUTH, 5, 1,
		 * Block.planks.blockID);
		 */

		// set(level3.increment(ForgeDirection.EAST,
		// 7).increment(ForgeDirection.SOUTH, 1).increment(ForgeDirection.UP),
		// ForgeDirection.EAST, 1, ForgeDirection.SOUTH, 3, 1,
		// Block.planks.blockID);

		/*
		 * set(level3, 0); set(level3.increment(ForgeDirection.EAST, 7), 0);
		 * set(level3.increment(ForgeDirection.SOUTH, 4), 0);
		 * set(level3.increment(ForgeDirection.EAST,
		 * 7).increment(ForgeDirection.SOUTH, 4), 0);
		 */}

	protected void testClear(int sizeOfBreakerGrid) {
		final int areaSize = Math.max(80, sizeOfBreakerGrid + 10);

		clear(this.playerLocation.increment(ForgeDirection.DOWN, 3).increment(ForgeDirection.WEST, 10).increment(ForgeDirection.NORTH, 10),
				ForgeDirection.EAST, areaSize + 40, ForgeDirection.SOUTH, areaSize + 40, 255);

	}

	protected void testMiner(int sizeOfBreakerGrid) {
		final int areaSize = Math.max(80, sizeOfBreakerGrid + 10);

		clear(this.playerLocation.increment(ForgeDirection.DOWN, 3).increment(ForgeDirection.WEST, 10).increment(ForgeDirection.NORTH, 10),
				ForgeDirection.EAST, areaSize + 40, ForgeDirection.SOUTH, areaSize + 40, 255);

		final Vector3i platformStart = this.playerLocation.increment(ForgeDirection.UP, 1).increment(ForgeDirection.EAST, 10)
				.increment(ForgeDirection.SOUTH, 10);

		set(platformStart, ForgeDirection.EAST, 1, ForgeDirection.SOUTH, sizeOfBreakerGrid, sizeOfBreakerGrid, Motive.BlockBreaker.blockID,
				ForgeDirection.EAST.ordinal());

		set(platformStart.increment(ForgeDirection.EAST, 5).increment(ForgeDirection.DOWN, 2).increment(ForgeDirection.NORTH, 2), ForgeDirection.EAST, 20,
				ForgeDirection.SOUTH, sizeOfBreakerGrid + 4, sizeOfBreakerGrid + 4, Block.blockDiamond.blockID);

		final Vector3i engineLocation = platformStart.increment(ForgeDirection.WEST).increment(ForgeDirection.UP);
		set(engineLocation, Motive.BlockMover.blockID);

		final Vector3i powerLocation = engineLocation.increment(ForgeDirection.WEST);
		set(powerLocation, Block.blockRedstone.blockID);

		set(powerLocation.increment(ForgeDirection.UP), Block.redstoneLampActive.blockID);
		set(engineLocation.increment(ForgeDirection.SOUTH, 2), Block.enderChest.blockID);
		set(this.playerLocation.increment(ForgeDirection.SOUTH, 2), Block.enderChest.blockID);

		final TileEntityMover tileEntityMover = (TileEntityMover) this.world.getBlockTileEntity(engineLocation.x, engineLocation.y, engineLocation.z);
		tileEntityMover.setLocked(true);
		tileEntityMover.setSpeed(0.5f);
		final ItemStack stack = new ItemStack(Motive.ItemMoverRemoteControl);
		this.player.inventory.setInventorySlotContents(0, stack);
		ItemMoverRemoteControl.pairWithMover(stack, tileEntityMover);
	}
}
