package aes.motive;

import net.minecraftforge.client.MinecraftForgeClient;
import aes.motive.render.ItemMoverRemoteControlRenderer;
import aes.motive.render.TileEntityBreakerRenderer;
import aes.motive.render.TileEntityMoteRenderer;
import aes.motive.render.TileEntityMoverRenderer;
import aes.motive.render.TileEntityVeRenderer;
import aes.motive.tileentity.TileEntityBreaker;
import aes.motive.tileentity.TileEntityMote;
import aes.motive.tileentity.TileEntityMover;
import aes.motive.tileentity.TileEntityVe;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ProxyClient extends aes.motive.ProxyServer {
	@Override
	public void registerRenderers() {
		final TileEntityMoverRenderer moverRenderer = new TileEntityMoverRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMover.class, moverRenderer);
		MinecraftForgeClient.registerItemRenderer(Motive.BlockMover.blockID, moverRenderer);

		final TileEntityVeRenderer veRenderer = new TileEntityVeRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVe.class, veRenderer);
		MinecraftForgeClient.registerItemRenderer(Motive.BlockVe.blockID, veRenderer);

		final TileEntityMoteRenderer moteRenderer = new TileEntityMoteRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMote.class, moteRenderer);
		MinecraftForgeClient.registerItemRenderer(Motive.BlockMote.blockID, moteRenderer);

		final TileEntityBreakerRenderer breakerRenderer = new TileEntityBreakerRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBreaker.class, breakerRenderer);
		MinecraftForgeClient.registerItemRenderer(Motive.BlockBreaker.blockID, breakerRenderer);

		final ItemMoverRemoteControlRenderer remoteRenderer = new ItemMoverRemoteControlRenderer();
		MinecraftForgeClient.registerItemRenderer(Motive.ItemMoverRemoteControl.itemID, remoteRenderer);
	}
}