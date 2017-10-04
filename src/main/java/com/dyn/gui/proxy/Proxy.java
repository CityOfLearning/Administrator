package com.dyn.gui.proxy;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface Proxy {
	public void addScheduledTask(Runnable runnable);

	public Map<String, ?> getKeyBindings();

	/**
	 * Returns a side-appropriate EntityPlayer for use during message handling
	 */
	public EntityPlayer getPlayerEntity(MessageContext ctx);

	public String[] getServerUserlist();

	public List<EntityPlayerMP> getServerUsers();

	public IThreadListener getThreadFromContext(MessageContext ctx);

	public void init();
}