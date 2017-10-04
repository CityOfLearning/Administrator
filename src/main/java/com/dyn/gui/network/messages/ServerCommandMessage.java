package com.dyn.gui.network.messages;

import java.io.IOException;

import com.dyn.gui.network.messages.AbstractMessage.AbstractServerMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.relauncher.Side;

public class ServerCommandMessage extends AbstractServerMessage<ServerCommandMessage> {
	private String command;

	// The basic, no-argument constructor MUST be included to use the new
	// automated handling
	public ServerCommandMessage() {
	}

	public ServerCommandMessage(String mentor_command) {
		command = mentor_command.replace("  ", " ");
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), command);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataInputStream
		command = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataOutputStream
		buffer.writeString(command);
	}
}
