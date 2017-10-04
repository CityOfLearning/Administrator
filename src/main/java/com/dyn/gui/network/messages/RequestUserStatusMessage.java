package com.dyn.gui.network.messages;

import java.io.IOException;

import com.dyn.gui.GuiMods;
import com.dyn.gui.network.NetworkManager;
import com.dyn.gui.network.messages.AbstractMessage.AbstractServerMessage;
import com.dyn.gui.utils.PlayerUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class RequestUserStatusMessage extends AbstractServerMessage<RequestUserStatusMessage> {

	// better to be empty than null...
	private String username = "";

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestUserStatusMessage() {
	}

	public RequestUserStatusMessage(String username) {
		if (username != null) {
			this.username = username;
		}
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			EntityPlayerMP student = PlayerUtil.getPlayerByUsername(username);
			if (student != null) {
				NetworkManager.sendTo(new PlayerStatusMessage(GuiMods.frozenPlayers.contains(username),
						PlayerUtil.getPersistedTag(student, true).getBoolean("mute"),
						student.capabilities.isCreativeMode), (EntityPlayerMP) player);
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		username = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(username);
	}
}
