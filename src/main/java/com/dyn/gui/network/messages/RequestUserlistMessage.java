package com.dyn.gui.network.messages;

import java.io.IOException;

import com.dyn.gui.GuiMods;
import com.dyn.gui.network.NetworkManager;
import com.dyn.gui.network.messages.AbstractMessage.AbstractServerMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class RequestUserlistMessage extends AbstractServerMessage<RequestUserlistMessage> {

	// this has no data since its a request

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestUserlistMessage() {
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			NetworkManager.sendTo(new ServerUserlistMessage(GuiMods.proxy.getServerUserlist()),
					(EntityPlayerMP) player);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
	}
}
