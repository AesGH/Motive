package aes.motive;

import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import aes.motive.block.BlockBreaker;
import aes.motive.block.BlockMote;
import aes.motive.block.BlockMover;
import aes.motive.block.BlockVe;
import aes.motive.item.ItemMoverRemoteControl;
import aes.motive.test.TestCommand;
import aes.motive.tileentity.TileEntityMover;
import aes.motive.tileentity.TileEntityMoverBase;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@Mod(modid = "motive", modLanguage = "java", name = "Motive", version = "@VERSION@.@BUILD_NUMBER@", dependencies = "required-after:motivecore")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
// @Name("motive")
@MCVersion("1.6.4")
public class Motive {
	@Instance("motive")
	public static Motive instance = new Motive();

	@SidedProxy(serverSide = "aes.motive.ProxyServer", clientSide = "aes.motive.ProxyClient")
	public static ProxyServer proxy;

	private static final Logger logger = Logger.getLogger(Motive.class.getName());

	public static final CreativeTabs CreativeTab = new CreativeTab();

	// public static Block BlockTest;
	public static Block BlockMote;
	public static Block BlockVe;
	public static Block BlockMover;
	public static Block BlockBreaker;

	public static Item ItemMoverRemoteControl;
	public static ResourceLocation resourceMoverTexture = new ResourceLocation("motive", "textures/blocks/mover.png");
	public static ResourceLocation resourceMoteTexture = new ResourceLocation("motive", "textures/blocks/mote.png");
	public static ResourceLocation resourcePairedMoteTexture = new ResourceLocation("motive", "textures/blocks/pairedMote.png");
	public static ResourceLocation resourceStrutTexture = new ResourceLocation("motive", "textures/blocks/strut.png");
	public static ResourceLocation resourceGlassCaseTexture = new ResourceLocation("motive", "textures/blocks/glassCase.png");
	public static ResourceLocation resourceFrontCaseTexture = new ResourceLocation("motive", "textures/blocks/veFrame.png");
	public static ResourceLocation resourceBreakerTexture = new ResourceLocation("motive", "textures/blocks/breakerFront.png");
	public static ResourceLocation resourceRemoteControlTexture = new ResourceLocation("motive", "textures/items/moverRemoteIcon.png");
	public static ResourceLocation resourceBoltedFrameTexture = new ResourceLocation("motive", "textures/blocks/boltedFrame.png");
	public static ResourceLocation resourceMoverIconTexture = new ResourceLocation("motive", "textures/blocks/moverIcon.png");
	public static PacketHandler packetHandler = new PacketHandler("Motive", TileEntityMover.class);

	public static void log(String message) {
		logger.info(message);
	}

	public static void log(World world, String message) {
		logger.info((world == null ? "" : world.isRemote ? "CLIENT-" + Minecraft.getMinecraft().thePlayer.getEntityName() : "SERVER") + ":"
				+ (world == null ? "" : world.getWorldTime() + ": ") + message);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		logger.setParent(FMLLog.getLogger());

		final Configuration config = new Configuration(e.getSuggestedConfigurationFile());
		config.load();

		TestCommand.name = config.get(Configuration.CATEGORY_GENERAL, "testCommandName", TestCommand.name, "Name of the command to be used for the testing.")
				.getString();

		BlockMover = new BlockMover(config.getBlock("BlockMover", 3388).getInt()).setUnlocalizedName("Motive Engine");
		// BlockTest = new BlockTest(config.getBlock("BlockTest",
		// 3393).getInt()).setUnlocalizedName("Motive Test");
		BlockBreaker = new BlockBreaker(config.getBlock("BlockBreaker", 3389).getInt()).setUnlocalizedName("Motive Breaker");
		ItemMoverRemoteControl = new ItemMoverRemoteControl(config.getItem("MoverRemoteControl", 3390).getInt()).setUnlocalizedName("Motive Engine Remote");
		BlockMote = new BlockMote(config.getBlock("BlockMote", 3391).getInt()).setUnlocalizedName("Mote");
		BlockVe = new BlockVe(config.getBlock("BlockVe", 3392).getInt()).setUnlocalizedName("Motive Frame");

		ConnectedBlocks.MAX_BLOCKS_CAN_MOVE = config.get("Mover", "MaximumBlocksMovable", ConnectedBlocks.MAX_BLOCKS_CAN_MOVE).getInt();

		config.save();

		proxy.registerBlocks();
		proxy.registerTileEntities();
		proxy.registerItems();
		proxy.registerRecipes();
		proxy.registerRenderers();
		proxy.registerChannels();
		proxy.registerGuis();
		proxy.registerGenerators();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		((ServerCommandManager) event.getServer().getCommandManager()).registerCommand(new TestCommand());
	}

	@EventHandler
	public void serverStopped(FMLServerStoppedEvent e) {
		TileEntityMoverBase.serverStopped();
	}
}
