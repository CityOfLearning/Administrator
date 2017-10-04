package com.dyn.gui.network.messages;

import java.io.IOException;

import com.dyn.gui.network.messages.AbstractMessage.AbstractClientMessage;
import com.dyn.gui.student.StudentUI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.relauncher.Side;

public class FreezePlayerMessage extends AbstractClientMessage<FreezePlayerMessage> {

	// the info needed to increment a requirement
	private boolean frozen;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public FreezePlayerMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public FreezePlayerMessage(boolean freeze) {
		frozen = freeze;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {
			player.addChatMessage(
					new ChatComponentText(String.format("You were %s by the teacher", frozen ? "frozen" : "unfrozen")));
			StudentUI.frozen.setFlag(frozen);
			player.capabilities.allowEdit = frozen;
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		frozen = buffer.readBoolean();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeBoolean(frozen);
	}
}
