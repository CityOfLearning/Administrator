package com.dyn.gui.network.messages;

import java.io.IOException;

import com.dyn.gui.GuiMods;
import com.dyn.gui.network.NetworkManager;
import com.dyn.gui.network.messages.AbstractMessage.AbstractServerMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.relauncher.Side;

public class RequestFreezePlayerMessage extends AbstractServerMessage<RequestFreezePlayerMessage> {

	private String username;
	private boolean freeze;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestFreezePlayerMessage() {
	}

	public RequestFreezePlayerMessage(String playerName, boolean frozen) {
		username = playerName;
		freeze = frozen;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			if ((username != null)
					&& (MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(username) != null)) {
				if (freeze) {
					GuiMods.frozenPlayers.add(username);
					MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
							"/p user " + username + " group add _FROZEN_");
				} else {
					GuiMods.frozenPlayers.remove(username);
					MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
							"/p user " + username + " group remove _FROZEN_");
				}

				player.addChatMessage(new ChatComponentText(
						String.format("You %s player %s", freeze ? "froze" : "unfroze", username)));
				NetworkManager.sendTo(new FreezePlayerMessage(freeze),
						MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(username));
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		username = buffer.readStringFromBuffer(buffer.readableBytes());
		freeze = buffer.readBoolean();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(username);
		buffer.writeBoolean(freeze);
	}
}
