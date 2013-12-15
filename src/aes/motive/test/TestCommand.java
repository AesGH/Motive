package aes.motive.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockNetherStalk;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockVine;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import aes.motive.Command;
import aes.motive.Motive;
import aes.motive.item.ItemMoverRemoteControl;
import aes.motive.tileentity.MoverMode;
import aes.motive.tileentity.TileEntityBreaker;
import aes.motive.tileentity.TileEntityMover;
import aes.utils.Vector3i;
import aes.utils.WorldUtils;

public class TestCommand extends Command {
	public static String name = "motive";

	ICommandSender sender;

	World world;

	Vector3i playerLocation;
	EntityPlayer player;

	protected TileEntityMover addPairedEngine(final Vector3i engineLocation) {
		set(engineLocation, Motive.BlockMover.blockID);
		final TileEntityMover tileEntityMover = (TileEntityMover) this.world.getBlockTileEntity(engineLocation.x, engineLocation.y, engineLocation.z);
		tileEntityMover.setMode(MoverMode.Remote);
		tileEntityMover.setLocked(true);
		tileEntityMover.setSpeed(0.5f);
		tileEntityMover.setActive(true);
		final ItemStack stack = new ItemStack(Motive.ItemMoverRemoteControl);
		this.player.inventory.setInventorySlotContents(0, stack);
		ItemMoverRemoteControl.pairWithMover(stack, tileEntityMover);
		return tileEntityMover;
	}

	private void clear(Vector3i start, ForgeDirection eastWestDirection, int eastWestDistance, ForgeDirection northSouthDirection, int northSouthDistance,
			int height) {
		set(start, eastWestDirection, eastWestDistance, northSouthDirection, northSouthDistance, 1, Block.dirt.blockID);
		set(start.increment(ForgeDirection.UP), eastWestDirection, eastWestDistance, northSouthDirection, northSouthDistance, height - 1, 0);
	}

	@SuppressWarnings("unchecked")
	protected List<KnownBlock> getAllKnownBlocks() {
		final Set<KnownBlock> recipeSet = new HashSet<KnownBlock>();

		for (final IRecipe recipe : (List<IRecipe>) CraftingManager.getInstance().getRecipeList()) {
			final ItemStack result = recipe.getRecipeOutput();
			if (result != null) {
				final Item item = result.getItem();
				if (item instanceof ItemBlock) {
					recipeSet.add(new KnownBlock(Block.blocksList[((ItemBlock) item).getBlockID()], result.getItemDamage()));
				}
			}
		}

		for (int id = 0; id < Block.blocksList.length; id++) {
			final Block block = Block.blocksList[id];
			if (block != null && block.blockID == id) {
				recipeSet.add(new KnownBlock(block, 0));
			}
		}

		final List<KnownBlock> recipes = new LinkedList<KnownBlock>();
		for (final KnownBlock block : recipeSet) {
			if (block.block.blockHardness >= 0 && !WorldUtils.isBlockFluid(block.block)) {
				recipes.add(block);
			}
		}
		return recipes;
	}

	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		String usage = "Usage:\n";
		// usage += "/" + name + " test clear [size]" + "\n";
		usage += "/" + name + " test miner [size]" + "\n";
		usage += "/" + name + " test allblocks" + "\n";
		usage += "/" + name + " test longthin" + "\n";
		usage += "/" + name + " options breakerWithoutInventoryAvailable <drop|destroy|wait>\n";

		return usage;
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
		} else if (testName.equals("longthin")) {

			if (arguments.size() > 2) {
				sendChat(commandSender, getCommandUsage(commandSender));
				return;
			}

			sendChat(commandSender, "Running test longthin");
			testLongThin();
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
		sendChat(this.sender, "Done");
	}

	private void set(Vector3i location, int blockId) {
		set(location, blockId, 0);
	}

	private void set(Vector3i location, int blockId, int meta) {
		set(location, blockId, meta, true);
	}

	private void set(Vector3i location, int blockId, int meta, boolean update) {
		if (this.world.getBlockId(location.x, location.y, location.z) != blockId || this.world.getBlockMetadata(location.x, location.y, location.z) != meta) {
			this.world.setBlock(location.x, location.y, location.z, blockId, meta, 3);
		}
	}

	private void setRandomOres(Vector3i start, ForgeDirection eastWestDirection, int eastWestDistance, ForgeDirection northSouthDirection,
			int northSouthDistance, int height) {

		final List<KnownBlock> ores = new ArrayList<KnownBlock>();

		for (final String oreName : OreDictionary.getOreNames()) {
			if (oreName.startsWith("ore")) {
				for (final ItemStack stack : OreDictionary.getOres(oreName)) {
					if (stack.itemID > 0 && stack.itemID < Block.blocksList.length) {
						final Block block = Block.blocksList[stack.itemID];
						if (block != null) {
							final int metadata = stack.getItemDamage();
							ores.add(new KnownBlock(block, metadata));
						}
					}
				}
			}
		}

		ores.add(new KnownBlock(Block.coalBlock, 0));
		ores.add(new KnownBlock(Block.cobblestone, 0));
		ores.add(new KnownBlock(Block.stone, 0));
		ores.add(new KnownBlock(Block.dirt, 0));
		ores.add(new KnownBlock(Block.gravel, 0));
		ores.add(new KnownBlock(Block.obsidian, 0));
		ores.add(new KnownBlock(Block.sandStone, 0));

		sendChat(this.sender, "Setting " + eastWestDistance + " " + eastWestDirection.name() + " and " + northSouthDistance + " " + northSouthDirection.name()
				+ " and " + height + " UP from " + start + " to random ores");

		final Vector3i end = start.increment(eastWestDirection, eastWestDistance).increment(northSouthDirection, northSouthDistance)
				.increment(ForgeDirection.UP, height);

		final Random random = new Random();

		for (int x = start.x; x != end.x; x += eastWestDirection.offsetX) {
			for (int z = start.z; z != end.z; z += northSouthDirection.offsetZ) {
				for (int y = start.y; y != end.y && y > 0 && y < 256; y += ForgeDirection.UP.offsetY) {
					final KnownBlock ore = ores.get(random.nextInt(ores.size()));
					set(new Vector3i(x, y, z), ore.block.blockID, ore.metadata);
				}
			}
		}
		sendChat(this.sender, "Done");
	}

	private void testAllKnownBlocks() {
		final List<KnownBlock> knownBlocks = getAllKnownBlocks();

		final int gridCount = (int) Math.ceil(Math.sqrt(knownBlocks.size()));

		final int gridSizeNS = gridCount * 3;

		clear(this.playerLocation.increment(ForgeDirection.DOWN, 1).increment(ForgeDirection.WEST, 5).increment(ForgeDirection.NORTH, 3), ForgeDirection.EAST,
				gridCount + 10, ForgeDirection.SOUTH, gridSizeNS + 10, 255);

		final Vector3i platformStart = this.playerLocation.increment(ForgeDirection.UP, 2).increment(ForgeDirection.EAST, 2).increment(ForgeDirection.SOUTH, 2);

		set(platformStart, ForgeDirection.EAST, gridCount + 2, ForgeDirection.SOUTH, gridSizeNS + 2, 1, Block.dirt.blockID);

		final Vector3i arrayStart = platformStart.increment(ForgeDirection.UP).increment(ForgeDirection.EAST).increment(ForgeDirection.SOUTH);

		final Vector3i end = arrayStart.increment(ForgeDirection.EAST, gridCount).increment(ForgeDirection.SOUTH, gridSizeNS).increment(ForgeDirection.UP, 1);

		int index = 0;

		for (int x = arrayStart.x; x != end.x; x += ForgeDirection.EAST.offsetX) {
			for (int z = arrayStart.z; z != end.z; z += ForgeDirection.SOUTH.offsetZ) {
				if ((z - arrayStart.z) % 3 == 0) {
					final int y = arrayStart.y;
					while (true) {
						index++;
						if (index >= knownBlocks.size()) {
							break;
						}
						final KnownBlock recipe = knownBlocks.get(index);
						final Block block = recipe.block;

						Motive.log("setting " + x + "," + y + "," + z + " to " + block.getLocalizedName());
						final Vector3i location = new Vector3i(x, y, z);

						if (block == Block.fire) {
							set(location, Block.dirt.blockID, 0);
							set(location.increment(ForgeDirection.UP), Block.dirt.blockID, 0);
							set(location.increment(ForgeDirection.UP, 2), Block.netherrack.blockID, 0);
							set(location.increment(ForgeDirection.UP, 3), block.blockID, recipe.metadata);
						} else if (block instanceof BlockDoor) {
							ItemDoor.placeDoorBlock(this.world, x, y, z, 0, block);
						} else if (block instanceof BlockBed) {
							set(location, recipe.block.blockID, 2);
							set(location.increment(ForgeDirection.NORTH), recipe.block.blockID, 10);
						} else if (block instanceof BlockLever) {
							set(location, recipe.block.blockID, 1);
						} else if (block instanceof BlockTrapDoor) {
							set(location.increment(ForgeDirection.DOWN), 0, 0);
							set(location, block.blockID, recipe.metadata);
						} else if (block instanceof BlockLilyPad) {
							set(location.increment(ForgeDirection.DOWN, 2), Block.dirt.blockID, 0);
							set(location.increment(ForgeDirection.DOWN), Block.waterStill.blockID, 0);
							set(location, block.blockID, recipe.metadata);
						} else if (block instanceof BlockReed || block instanceof BlockCactus || block instanceof BlockDeadBush) {
							set(location, Block.sand.blockID, 0);
							set(location.increment(ForgeDirection.UP), block.blockID, recipe.metadata);
						} else if (block instanceof BlockNetherStalk) {
							set(location, Block.slowSand.blockID, 0);
							set(location.increment(ForgeDirection.UP), block.blockID, recipe.metadata);
						} else if (block instanceof BlockLeaves) {
							set(location, Block.wood.blockID, 0);
							set(location.increment(ForgeDirection.UP), block.blockID, recipe.metadata);
						} else if (block instanceof BlockCocoa) {
							set(location, Block.wood.blockID, 3);
							set(location.increment(ForgeDirection.NORTH), block.blockID, recipe.metadata);
						} else if (block instanceof BlockStem) {
							set(location.increment(ForgeDirection.DOWN), Block.tilledField.blockID, 0);
							set(location, block.blockID, recipe.metadata);
						} else if (block instanceof BlockCrops) {
							set(location.increment(ForgeDirection.DOWN), Block.tilledField.blockID, 0);
							set(location, block.blockID, recipe.metadata);
						} else if (block instanceof BlockLadder) {
							set(location, Block.dirt.blockID, 0);
							set(location.increment(ForgeDirection.UP), Block.wood.blockID, 0);
							set(location.increment(ForgeDirection.UP, 1).increment(ForgeDirection.SOUTH), block.blockID, 3);
							set(location.increment(ForgeDirection.UP, 1).increment(ForgeDirection.NORTH), block.blockID, 2);
						} else if (block instanceof BlockVine) {
							set(location, Block.dirt.blockID, 0);
							set(location.increment(ForgeDirection.UP), Block.wood.blockID, 0);
							set(location.increment(ForgeDirection.UP, 1).increment(ForgeDirection.SOUTH), block.blockID, 4);
							set(location.increment(ForgeDirection.UP, 1).increment(ForgeDirection.NORTH), block.blockID, 1);
						} else {
							set(location, block.blockID, recipe.metadata);
						}

						set(location.increment(ForgeDirection.SOUTH), Block.signPost.blockID);
						final TileEntitySign sign = (TileEntitySign) this.world.getBlockTileEntity(x, y, z + 1);

						String name = block.getLocalizedName();
						if (name.startsWith("tile.")) {
							name = name.substring(5);
						}
						if (name.endsWith(".name")) {
							name = name.substring(0, name.length() - 5);
						}
						if (name.length() > 14) {
							sign.signText[0] = name.substring(0, 14);
							name = (name.substring(14) + "                            ").substring(0, 14);
							sign.signText[1] = name;
						} else {
							sign.signText[0] = name;
						}

						sign.signText[3] = block.blockID + ":" + this.world.getBlockMetadata(x, y, z);
						this.world.markBlockForUpdate(x, y, z + 1);

						break;
					}
				}
			}
		}

		final Vector3i engineLocation = platformStart.increment(ForgeDirection.WEST);
		set(engineLocation, Motive.BlockMover.blockID);

		final Vector3i powerLocation = engineLocation.increment(ForgeDirection.WEST);
		set(powerLocation, Block.blockRedstone.blockID);

		final TileEntityMover tileEntityMover = (TileEntityMover) this.world.getBlockTileEntity(engineLocation.x, engineLocation.y, engineLocation.z);
		tileEntityMover.setLocked(true);
		tileEntityMover.setSpeed(0f);

		final ItemStack stack = new ItemStack(Motive.ItemMoverRemoteControl);
		this.player.inventory.setInventorySlotContents(0, stack);
		ItemMoverRemoteControl.pairWithMover(stack, tileEntityMover);

		if (index >= knownBlocks.size() - 1) {
			sendChat(this.sender, "Placed all known (" + index + ") blocks.");
		}

		else {
			sendChat(this.sender, "Placed " + index + " of " + knownBlocks.size() + " known blocks.");
		}
	}

	private void testLongThin() {
		clear(this.playerLocation.increment(ForgeDirection.DOWN).increment(ForgeDirection.WEST, 10).increment(ForgeDirection.NORTH, 10), ForgeDirection.EAST,
				10, ForgeDirection.SOUTH, 120, 255);

		final Vector3i start = this.playerLocation.increment(ForgeDirection.EAST, 2).increment(ForgeDirection.SOUTH, 2).increment(ForgeDirection.UP);
		
		set(start, ForgeDirection.EAST, 1, ForgeDirection.SOUTH, 100, 1, Block.obsidian.blockID);
		set(start.increment(ForgeDirection.NORTH), Block.blockGold.blockID);
		set(start.increment(ForgeDirection.SOUTH, 100), Block.blockGold.blockID);
		addPairedEngine(start.increment(ForgeDirection.UP));
	}
	
	protected void testBoat() {
		final int areaSize = 10;
		clear(this.playerLocation.increment(ForgeDirection.DOWN).increment(ForgeDirection.WEST, 40).increment(ForgeDirection.NORTH, 40), ForgeDirection.EAST,
				areaSize + 80, ForgeDirection.SOUTH, areaSize + 80, 255);

		set(this.playerLocation.increment(ForgeDirection.DOWN).increment(ForgeDirection.WEST, 40).increment(ForgeDirection.NORTH, 40), ForgeDirection.EAST,
				areaSize + 80, ForgeDirection.SOUTH, areaSize + 80, 3, Block.waterStill.blockID);

		final Vector3i start = this.playerLocation.increment(ForgeDirection.EAST, 10).increment(ForgeDirection.SOUTH, 10);

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

		final Vector3i engineLocation = level5.increment(ForgeDirection.SOUTH, 3).increment(ForgeDirection.EAST, 3).increment(ForgeDirection.DOWN);
		addPairedEngine(engineLocation);
	}

	protected void testClear(int sizeOfBreakerGrid) {
		final int areaSize = Math.max(80, sizeOfBreakerGrid + 10);

		clear(this.playerLocation.increment(ForgeDirection.DOWN, 3).increment(ForgeDirection.WEST, 10).increment(ForgeDirection.NORTH, 10),
				ForgeDirection.EAST, areaSize + 40, ForgeDirection.SOUTH, areaSize + 40, 255);

	}

	protected void testMiner(int sizeOfBreakerGrid) {
		final int areaSize = Math.max(80, sizeOfBreakerGrid + 10);

		clear(this.playerLocation.increment(ForgeDirection.DOWN, 1).increment(ForgeDirection.WEST, 10).increment(ForgeDirection.NORTH, 10),
				ForgeDirection.EAST, areaSize + 40, ForgeDirection.SOUTH, areaSize + 40, 255);

		final Vector3i platformStart = this.playerLocation.increment(ForgeDirection.UP, 1).increment(ForgeDirection.EAST, 10)
				.increment(ForgeDirection.SOUTH, 10);

		set(platformStart, ForgeDirection.EAST, 1, ForgeDirection.SOUTH, sizeOfBreakerGrid, sizeOfBreakerGrid, Motive.BlockBreaker.blockID,
				ForgeDirection.EAST.ordinal());

		setRandomOres(platformStart.increment(ForgeDirection.EAST, 5).increment(ForgeDirection.DOWN, 2).increment(ForgeDirection.NORTH, 2),
				ForgeDirection.EAST, 20, ForgeDirection.SOUTH, sizeOfBreakerGrid + 4, sizeOfBreakerGrid + 4);

		final Vector3i engineLocation = platformStart.increment(ForgeDirection.WEST).increment(ForgeDirection.UP);
		set(engineLocation.increment(ForgeDirection.SOUTH, 2), Block.enderChest.blockID);
		set(this.playerLocation.increment(ForgeDirection.SOUTH, 2), Block.enderChest.blockID);

		addPairedEngine(engineLocation);
	}
}
