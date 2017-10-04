package com.dyn.gui.network.messages;

import java.io.IOException;

import com.dyn.gui.GuiMods;
import com.dyn.gui.network.messages.AbstractMessage.AbstractClientMessage;
import com.google.gson.JsonObject;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class PlayerStatusMessage extends AbstractClientMessage<PlayerStatusMessage> {

	private boolean frozen;
	private boolean muted;
	private boolean mode;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public PlayerStatusMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public PlayerStatusMessage(boolean freeze, boolean muted, boolean isCreative) {
		frozen = freeze;
		this.muted = muted;
		mode = isCreative;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {
			JsonObject status = new JsonObject();
			status.addProperty("frozen", frozen);
			status.addProperty("muted", muted);
			status.addProperty("mode", mode);
			GuiMods.playerStatus = status;
			GuiMods.playerStatusReturned.setFlag(true);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		frozen = buffer.readBoolean();
		muted = buffer.readBoolean();
		mode = buffer.readBoolean();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeBoolean(frozen);
		buffer.writeBoolean(muted);
		buffer.writeBoolean(mode);
	}
}
