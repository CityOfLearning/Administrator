package com.dyn.gui.network.messages;

import java.io.IOException;

import com.dyn.gui.GuiMods;
import com.dyn.gui.network.messages.AbstractMessage.AbstractClientMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class ServerUserlistMessage extends AbstractClientMessage<ServerUserlistMessage> {

	// the info needed to increment a requirement
	private String data = "";

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public ServerUserlistMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public ServerUserlistMessage(String[] users) {
		if (users != null) {
			for (String s : users) {
				data += " " + s;
			}
		}
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {
			String[] users = data.split(" ");
			GuiMods.usernames.clear();
			for (String u : users) {
				if ((u != null) && !u.equals("null") && !u.isEmpty()) {
					GuiMods.usernames.add(u);
				}
			}
			GuiMods.usernames.remove(null);
			GuiMods.serverUserlistReturned.setFlag(true);

		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		data = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(data);
	}
}
