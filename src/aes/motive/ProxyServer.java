package aes.motive;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import aes.motive.gui.GuiHandlerMover;
import aes.motive.tileentity.TileEntityBreaker;
import aes.motive.tileentity.TileEntityMote;
import aes.motive.tileentity.TileEntityMover;
import aes.motive.tileentity.TileEntityVe;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ProxyServer {
	public void registerBlocks() {
		GameRegistry.registerBlock(Motive.BlockMote, "MoteBlock");
		LanguageRegistry.addName(Motive.BlockMote, "Mote");

		GameRegistry.registerBlock(Motive.BlockVe, "VeBlock");
		LanguageRegistry.addName(Motive.BlockVe, "Ve");

		GameRegistry.registerBlock(Motive.BlockMover, "MoverBlock");
		LanguageRegistry.addName(Motive.BlockMover, "Mover");

		GameRegistry.registerBlock(Motive.BlockBreaker, "BreakerBlock");
		LanguageRegistry.addName(Motive.BlockBreaker, "Breaker");
	}

	public void registerChannels() {
		NetworkRegistry.instance().registerChannel(Motive.packetHandler, "Motive");
	}

	public void registerGenerators() {
		GameRegistry.registerWorldGenerator(new WorldGenerator());
	}

	public void registerGuis() {
		NetworkRegistry.instance().registerGuiHandler(Motive.instance, new GuiHandlerMover());
	}

	public void registerItems() {
		GameRegistry.registerItem(Motive.ItemMoverRemoteControl, "ItemMoverRemoteControl");
		LanguageRegistry.addName(Motive.ItemMoverRemoteControl, "Mover Remote Control");
	}

	public void registerRecipes() {
		GameRegistry.addRecipe(new ItemStack(Motive.BlockMover, 1), new Object[] { "VGV", "GMG", "VGV", 'V', Motive.BlockVe, 'M', Motive.BlockMote, 'G',
				Block.glass });
		GameRegistry.addRecipe(new ItemStack(Motive.BlockBreaker, 1), new Object[] { "VGV", "G D", "VGV", 'V', Motive.BlockVe, 'D', Item.diamond, 'G',
				Block.glass });
		GameRegistry.addRecipe(new ItemStack(Motive.BlockBreaker, 1), new Object[] { "VGV", "D G", "VGV", 'V', Motive.BlockVe, 'D', Item.diamond, 'G',
				Block.glass });
		GameRegistry.addRecipe(new ItemStack(Motive.BlockBreaker, 1), new Object[] { "VDV", "G G", "VGV", 'V', Motive.BlockVe, 'D', Item.diamond, 'G',
				Block.glass });
		GameRegistry.addRecipe(new ItemStack(Motive.BlockBreaker, 1), new Object[] { "VGV", "G G", "VDV", 'V', Motive.BlockVe, 'D', Item.diamond, 'G',
				Block.glass });
		GameRegistry.addRecipe(new ItemStack(Motive.ItemMoverRemoteControl, 1), new Object[] { "G", "R", "V", 'V', Motive.BlockVe, 'R', Item.redstone, 'G',
				Block.glass });
	}

	public void registerRenderers() {
	}

	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityMote.class, "TileEntityMote");
		GameRegistry.registerTileEntity(TileEntityVe.class, "TileEntityVe");
		GameRegistry.registerTileEntity(TileEntityMover.class, "TileEntityMover");
		GameRegistry.registerTileEntity(TileEntityBreaker.class, "TileEntityBreaker");
	}
}
