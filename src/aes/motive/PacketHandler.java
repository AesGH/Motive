package aes.motive;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInstance;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import aes.base.TileEntityBase;
import aes.motive.item.ItemMoverRemoteControl;
import aes.motive.tileentity.TileEntityMoverBase;
import aes.utils.Obfuscation;
import aes.utils.PrivateFieldAccess;
import aes.utils.Vector2i;
import aes.utils.Vector3i;
import aes.utils.WorldUtils;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IConnectionHandler, IPacketHandler {
	private final String channelName;
	private final Class<?> tileEntityClass;

	public static LinkedList<MethodToInvokeOnTileEntity> methodsToInvokeNextGameLoop = new LinkedList<MethodToInvokeOnTileEntity>();

	public static Method getMethod(String className, String name) throws ClassNotFoundException {
		final Class<?> classType = Class.forName(className);
		for (final Method method : classType.getMethods()) {
			if (method.getName().equals(name))
				return method;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static void sendToPlayerIfAllBlocksLoaded(WorldServer world, String playerName, Vector3i[] affectedBlocks) {
		final Set<Vector2i> chunkLocations = WorldUtils.getChunksForLocations(affectedBlocks);

		final PlayerManager manager = world.getPlayerManager();

		for (final Object playerObj : world.playerEntities) {
			final EntityPlayerMP player = (EntityPlayerMP) playerObj;
			if (player.getEntityName().equals(playerName)) {
				boolean send = player.dimension == world.provider.dimensionId;
				if (send) {
					for (final Vector2i location : chunkLocations) {
						final PlayerInstance playerinstance = manager.getOrCreateChunkWatcher(location.x, location.y, false);

						if ((playerinstance == null || !((List<EntityPlayerMP>) PrivateFieldAccess.getValue(playerinstance,
								Obfuscation.getSrgName("playersInChunk"))).contains(player))
								&& !player.loadedChunks.contains(new ChunkCoordIntPair(location.x, location.y))) {
							send = false;
							break;
						}
					}
				}
				if (send) {
					Motive.log(world, "Moved into range of player " + playerName + ", sending blocks");

					for (final Vector3i location : affectedBlocks) {
						world.markBlockForUpdate(location.x, location.y, location.z);
					}
				}
			}
		}
	}

	public PacketHandler(String channelName, Class<?> tileEntityClass) {
		super();
		this.channelName = channelName;
		this.tileEntityClass = tileEntityClass;
	}

	private void checkParameterTypes(Method method, Object... args) throws Exception {
		final Class<?>[] parameterTypes = method.getParameterTypes();

		if (parameterTypes.length != args.length)
			throw new Exception("incorrect arg count. Expected " + parameterTypes.length + ", got " + args.length);

		for (int i = 0; i < args.length; i++) {
			if (args[i].getClass().getName().equals(parameterTypes[i]))
				throw new Exception("Incorrect arg type. Expected " + parameterTypes[i - 1].getName() + ", got " + args[i].getClass().getName());
		}
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
	}

	@Override
	public void connectionClosed(INetworkManager manager) {
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
		return null;
	}

	public void onPacket(World world, EntityPlayer player, Packet250CustomPayload packet, DataInputStream inputStream) {
		try {
			final String command = inputStream.readUTF();
			if ("Remote".equals(command)) {
				final int currentItem = inputStream.readInt();
				final int x = inputStream.readInt();
				final int y = inputStream.readInt();
				final int z = inputStream.readInt();

				final ItemStack stack = player.inventory.getStackInSlot(currentItem);
				if (stack.itemID == Motive.ItemMoverRemoteControl.itemID) {
					ItemMoverRemoteControl.playerUsedRemote(stack, player, world, x, y, z);
				}
				return;
			}
			if ("SendMethod".equals(command)) {
				final String uid = inputStream.readUTF();

				final String action = inputStream.readUTF();

				final TileEntityBase tileEntity = TileEntityMoverBase.getMover(world, uid);

				if (tileEntity == null) {
					if (world.isRemote) {
						final ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
						final DataOutputStream outputStream = new DataOutputStream(bos);

						outputStream.writeUTF(uid);
						outputStream.writeUTF("sendIfInRange");
						outputStream.writeUTF(player.getEntityName());

						final Packet250CustomPayload packet1 = new Packet250CustomPayload();
						packet1.channel = this.channelName;
						packet1.data = bos.toByteArray();
						packet1.length = bos.size();

						PacketDispatcher.sendPacketToServer(packet1);
						return;
					}
					throw new Exception("No chatty tile entity found for uid " + uid);
				}

				Method m = null;
				final Method[] methods = this.tileEntityClass.getMethods();
				for (final Method method : methods) {
					if (method.getName().equals(action)) {
						m = method;
						break;
					}
				}

				if (m == null)
					throw new Exception("No method " + action + " found in tile entity");

				final Object[] args = new Object[m.getParameterTypes().length];
				int i = 0;
				for (final Class<?> argType : m.getParameterTypes()) {
					final String name = argType.getName();
					if (name.equals("java.lang.Float") || name.equals("float")) {
						args[i] = inputStream.readFloat();
					} else if (name.equals("java.lang.Integer") || name.equals("int")) {
						args[i] = inputStream.readInt();
					} else if (name.equals("java.lang.Boolean") || name.equals("boolean")) {
						args[i] = inputStream.readBoolean();
					} else if (name.equals("java.lang.String") || name.equals("String")) {
						args[i] = inputStream.readUTF();
					} else if (name.equals(Vector3i.class.getName())) {
						final int x = inputStream.readInt();
						final int y = inputStream.readInt();
						final int z = inputStream.readInt();
						args[i] = new Vector3i(x, y, z);
					} else
						throw new Exception("unexpected type " + name + ". expecting Float, Integer, Boolean, String or Vector3i.");
					i++;
				}
				if (tileEntity != null) {
					final MethodToInvokeOnTileEntity method = new MethodToInvokeOnTileEntity(m, tileEntity, args);

					try {
						method.method.invoke(method.tileEntity, method.args);
					} catch (final IllegalAccessException e) {
						e.printStackTrace();
					} catch (final IllegalArgumentException e) {
						e.printStackTrace();
					} catch (final InvocationTargetException e) {
						e.printStackTrace();
					}
					/*
					 * synchronized (methodsToInvokeNextGameLoop) {
					 * methodsToInvokeNextGameLoop.add(new MethodToInvoke(m,
					 * tileEntity, args)); }
					 */}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		final EntityPlayer ePlayer = (EntityPlayer) player;
		final World world = ePlayer.worldObj;

		final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		onPacket(world, ePlayer, packet, inputStream);
	}

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
	}

	@SuppressWarnings("unchecked")
	private void sendPacketToPlayersWithBlocksLoaded(WorldServer world, Vector3i[] affectedBlocks, Packet250CustomPayload packet) {
		final Set<Vector2i> chunkLocations = WorldUtils.getChunksForLocations(affectedBlocks);

		final PlayerManager manager = world.getPlayerManager();

		for (final Object playerObj : world.playerEntities) {
			final EntityPlayerMP player = (EntityPlayerMP) playerObj;
			final boolean send = player.dimension == world.provider.dimensionId;
			if (send) {
				for (final Vector2i location : chunkLocations) {
					final PlayerInstance playerinstance = manager.getOrCreateChunkWatcher(location.x, location.y, false);

					if (playerinstance != null
							&& ((List<EntityPlayerMP>) PrivateFieldAccess.getValue(playerinstance, Obfuscation.getSrgName("playersInChunk"))).contains(player)
							|| player.loadedChunks.contains(new ChunkCoordIntPair(location.x, location.y))) {
						PacketDispatcher.sendPacketToPlayer(packet, (Player) player);
						break;
					}
				}
			}
		}
	}

	public void sendPlayerUsedRemote(int currentItem, int dimensionId, int x, int y, int z) {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		final DataOutputStream outputStream = new DataOutputStream(bos);

		try {
			outputStream.writeUTF("Remote");
			outputStream.writeInt(currentItem);
			outputStream.writeInt(x);
			outputStream.writeInt(y);
			outputStream.writeInt(z);

			final Packet250CustomPayload packet1 = new Packet250CustomPayload();
			packet1.channel = this.channelName;
			packet1.data = bos.toByteArray();
			packet1.length = bos.size();

			PacketDispatcher.sendPacketToServer(packet1);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void sendThisMethod(boolean fromRemote, World worldObj, TileEntityMoverBase tileEntity, Object... args) {
		if (worldObj.isRemote != fromRemote)
			return;
		final StackTraceElement callingMethod = Thread.currentThread().getStackTrace()[3];
		Method method;
		try {
			method = getMethod(callingMethod.getClassName(), callingMethod.getMethodName());
			if (method == null)
				throw new Exception("Couldn't find method " + callingMethod.getMethodName() + " on type " + callingMethod.getClassName());

			checkParameterTypes(method, args);

			final ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
			final DataOutputStream outputStream = new DataOutputStream(bos);

			outputStream.writeUTF("SendMethod");
			outputStream.writeUTF(tileEntity.getUid());
			/*
			 * outputStream.writeInt(worldObj.provider.dimensionId);
			 * outputStream.writeInt(tileEntity.xCoord);
			 * outputStream.writeInt(tileEntity.yCoord);
			 * outputStream.writeInt(tileEntity.zCoord);
			 */outputStream.writeUTF(method.getName());

			for (final Object arg : args) {
				if (arg.getClass().getName() == "java.lang.Float") {
					outputStream.writeFloat((Float) arg);
				} else if (arg.getClass().getName() == "java.lang.Integer") {
					outputStream.writeFloat((Integer) arg);
				} else if (arg.getClass().getName() == "java.lang.Boolean") {
					outputStream.writeBoolean((Boolean) arg);
				} else if (arg.getClass().getName() == "java.lang.String") {
					outputStream.writeUTF((String) arg);
				} else if (arg instanceof Vector3i) {
					final Vector3i vector = (Vector3i) arg;
					outputStream.writeInt(vector.x);
					outputStream.writeInt(vector.y);
					outputStream.writeInt(vector.z);
				} else
					throw new Exception("unexpected type " + arg.getClass().getName() + ". expecting Float, Integer, Boolean, String or Vector3i.");
			}
			final Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = this.channelName;
			packet.data = bos.toByteArray();
			packet.length = bos.size();

			if (fromRemote) {
				PacketDispatcher.sendPacketToServer(packet);
			} else {
				sendPacketToPlayersWithBlocksLoaded((WorldServer) worldObj, tileEntity.getAffectedBlocks(), packet);
				// PacketDispatcher.sendPacketToAllAround(tileEntity.xCoord,
				// tileEntity.yCoord, tileEntity.zCoord, 256,
				// worldObj.provider.dimensionId, packet);
			}

			// ModMotive.logger.info("!!!! Sent method " + method.getName() +
			// " from " + (fromRemote ? "client" : "server"));
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public void sendThisMethodToClient(World worldObj, TileEntityMoverBase tileEntity, Object... args) {
		sendThisMethod(false, worldObj, tileEntity, args);
	}

	public void sendThisMethodToServer(World worldObj, TileEntityMoverBase tileEntity, Object... args) {
		sendThisMethod(true, worldObj, tileEntity, args);
	}

	/*
	 * @Override public void updateEntity() { synchronized
	 * (methodsToInvokeNextGameLoop) { for (final MethodToInvoke method :
	 * methodsToInvokeNextGameLoop) { try {
	 * method.method.invoke(method.tileEntity, method.args); } catch (final
	 * IllegalAccessException e) { e.printStackTrace(); } catch (final
	 * IllegalArgumentException e) { e.printStackTrace(); } catch (final
	 * InvocationTargetException e) { e.printStackTrace(); } }
	 * 
	 * methodsToInvokeNextGameLoop.clear(); } super.updateEntity(); }
	 */
}