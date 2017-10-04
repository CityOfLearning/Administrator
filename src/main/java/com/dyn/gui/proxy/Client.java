package com.dyn.gui.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.lwjgl.input.Keyboard;

import com.dyn.gui.GuiMods;
import com.dyn.gui.admin.gui.Home;
import com.dyn.gui.network.NetworkManager;
import com.dyn.gui.network.messages.RequestUserlistMessage;
import com.dyn.gui.network.messages.ServerCommandMessage;
import com.dyn.gui.student.StudentUI;
import com.dyn.gui.student.gui.Freeze;
import com.dyn.gui.utils.BooleanChangeListener;
import com.dyn.gui.utils.PlayerAccessLevel;
import com.rabbit.gui.RabbitGui;
import com.rabbit.gui.component.display.tabs.TextTab;
import com.rabbit.gui.component.hud.overlay.Overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class Client implements Proxy {

	private KeyBinding adminKey;
	private KeyBinding mentorKey;

	@Override
	public void addScheduledTask(Runnable runnable) {
		Minecraft.getMinecraft().addScheduledTask(runnable);
	}

	@Override
	public Map<String, ?> getKeyBindings() {
		Map<String, KeyBinding> keys = new HashMap();
		keys.put("admin", adminKey);
		keys.put("mentor", mentorKey);
		return keys;
	}

	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		// Note that if you simply return 'Minecraft.getMinecraft().thePlayer',
		// your packets will not work as expected because you will be getting a
		// client player even when you are on the server!
		// Sounds absurd, but it's true.

		// Solution is to double-check side before returning the player:
		return ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : ctx.getServerHandler().playerEntity;
	}

	@Override
	public String[] getServerUserlist() {
		return null;
	}

	@Override
	public List<EntityPlayerMP> getServerUsers() {
		return null;
	}

	@Override
	public IThreadListener getThreadFromContext(MessageContext ctx) {
		// this causes null pointers in single player...
		return Minecraft.getMinecraft();
	}

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		
		if (GuiMods.accessLevel == PlayerAccessLevel.ADMIN) {			
			adminKey = new KeyBinding("key.toggle.adminui", Keyboard.KEY_M, "key.categories.toggle");

			Overlay.addWidget(new TextTab(0, 0, 0, 0, "Admin", "GUI",
					"(" + Keyboard.getKeyName(adminKey.getKeyCode()) + ")"));
			
			ClientRegistry.registerKeyBinding(adminKey);
		} else if (GuiMods.accessLevel == PlayerAccessLevel.MENTOR) {
			mentorKey = new KeyBinding("key.toggle.mentorui", Keyboard.KEY_M, "key.categories.toggle");

			Overlay.addWidget(new TextTab(0, 0, 0, 0, "Mentor", "GUI",
					"(" + Keyboard.getKeyName(mentorKey.getKeyCode()) + ")"));
			
			ClientRegistry.registerKeyBinding(mentorKey);
		} else if ((GuiMods.accessLevel == PlayerAccessLevel.STUDENT)) {
			// homeKey = new KeyBinding("key.toggle.studentui", Keyboard.KEY_M,
			// "key.categories.toggle");
			// ClientRegistry.registerKeyBinding(homeKey);

			BooleanChangeListener listener = (event, show) -> {
				if (event.getDispatcher().getFlag()) {
					ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
					Minecraft.getMinecraft().thePlayer.addChatMessage(
							new ChatComponentText("You will thaw in 3 minutes or when your teacher unfreezes you"));
					Runnable task = () -> StudentUI.frozen.setFlag(false);
					executor.schedule(task, 3, TimeUnit.MINUTES);
				} else {
					Minecraft.getMinecraft().thePlayer
							.addChatMessage(new ChatComponentText("You are now free to move"));
					NetworkManager.sendToServer(new ServerCommandMessage(
							"/p user " + Minecraft.getMinecraft().thePlayer.getName() + " group remove _FROZEN_"));
				}
			};

			StudentUI.frozen.setFlag(false);
			StudentUI.frozen.addBooleanChangeListener(listener);
		}
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (Minecraft.getMinecraft().inGameHasFocus) {
			if (StudentUI.frozen.getFlag()) {
				Freeze.draw();
			}
			
			Overlay.draw();
		}
	}
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {

		if ((Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
			return;
		}
		if ((GuiMods.accessLevel == PlayerAccessLevel.ADMIN) && adminKey.isPressed()) {
			if (!Minecraft.getMinecraft().thePlayer.worldObj.isRemote) {
				NetworkManager.sendToServer(new RequestUserlistMessage());
			}
			RabbitGui.proxy.display(new Home());
		}
		if ((GuiMods.accessLevel == PlayerAccessLevel.MENTOR) && mentorKey.isPressed()) {
			if (!Minecraft.getMinecraft().thePlayer.worldObj.isRemote) {
				NetworkManager.sendToServer(new RequestUserlistMessage());
			}
			RabbitGui.proxy.display(new Home());
		}
		// if ((GuiMods.accessLevel == PlayerAccessLevel.STUDENT) &&
		// homeKey.isPressed()) {
		// RabbitGui.proxy.display(new Home());
		// }
	}

	@SubscribeEvent
	public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
		if (event.entity instanceof EntityPlayer) {
			if (event.entity == Minecraft.getMinecraft().thePlayer) {
				if (StudentUI.frozen.getFlag()) {
					event.setCanceled(true);
				}
			}
		}
	}
}