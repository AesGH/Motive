package aes.motive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;

public abstract class Command extends CommandBase {
	public static void sendChat(ICommandSender commandSender, String message) {
		if (commandSender == MinecraftServer.getServer()) {
			Motive.log(commandSender.getEntityWorld(), message);
			return;
		}
		while (message != null) {
			final int nlIndex = message.indexOf('\n');
			String sent;
			if (nlIndex == -1) {
				sent = message;
				message = null;
			} else {
				sent = message.substring(0, nlIndex);
				message = message.substring(nlIndex + 1);
			}
			commandSender.sendChatToPlayer(new ChatMessageComponent().addText(sent));
		}
	}

	protected abstract void processCommand(ICommandSender commandSender, List<String> arguments);

	@Override
	public final void processCommand(ICommandSender commandSender, String[] astring) {
		processCommand(commandSender, new ArrayList<String>(Arrays.asList(astring)));
	}

	protected boolean requireOp() {
		return false;
	}
}
