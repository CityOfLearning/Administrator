package com.dyn.gui.network.messages;

import java.io.IOException;

import com.dyn.gui.network.messages.AbstractMessage.AbstractServerMessage;
import com.dyn.gui.utils.PlayerUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class RemoveEffectsMessage extends AbstractServerMessage<RemoveEffectsMessage> {

	private String player_name;

	// The basic, no-argument constructor MUST be included to use the new
	// automated handling
	public RemoveEffectsMessage() {
	}

	public RemoveEffectsMessage(String username) {
		player_name = username;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// using the message instance gives access to 'this.id'
		if (side.isServer()) {
			EntityPlayerMP student = PlayerUtil.getPlayerByUsername(player_name);
			if (student != null) {
				student.curePotionEffects(new ItemStack(Items.milk_bucket));
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataInputStream
		player_name = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataOutputStream
		buffer.writeString(player_name);
	}
}
