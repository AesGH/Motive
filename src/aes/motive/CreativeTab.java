package aes.motive;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CreativeTab extends CreativeTabs {

	public CreativeTab() {
		super("Motive");
		LanguageRegistry.instance().addStringLocalization("itemGroup.Motive", "Motive");
	}

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(Motive.BlockMover, 1);
	}
}
