package com.dyn.gui;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.dyn.gui.network.NetworkManager;
import com.dyn.gui.proxy.Proxy;
import com.dyn.gui.reference.MetaData;
import com.dyn.gui.reference.Reference;
import com.dyn.gui.utils.BooleanListener;
import com.dyn.gui.utils.PlayerAccessLevel;
import com.google.gson.JsonObject;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class GuiMods {
	public static BooleanListener playerStatusReturned = new BooleanListener(false);
	public static BooleanListener serverUserlistReturned = new BooleanListener(false);

	public static PlayerAccessLevel accessLevel = PlayerAccessLevel.ADMIN;

	public static List<String> usernames = new ArrayList<>();
	public static List<String> frozenPlayers = new ArrayList<>();
	public static List<String> mutedPlayers = new ArrayList<>();

	public static JsonObject playerStatus;

	public static Logger logger;
	
	@Mod.Instance(Reference.MOD_ID)
	public static GuiMods instance;

	@SidedProxy(modId = Reference.MOD_ID, clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static Proxy proxy;

	@Mod.EventHandler
	public void onInit(FMLInitializationEvent event) {
		GuiMods.proxy.init();
		MinecraftForge.EVENT_BUS.register(this);

	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MetaData.init(event.getModMetadata());

		logger = event.getModLog();
		
		NetworkManager.registerPackets();
		NetworkManager.registerMessages();
	}
	
	@SubscribeEvent
	public void loginEvent(PlayerEvent.PlayerLoggedInEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			if(net.minecraft.client.Minecraft.getMinecraft().playerController.getCurrentGameType() == GameType.CREATIVE) {
				accessLevel = PlayerAccessLevel.ADMIN;
			}
		} else {
			String[] players = MinecraftServer.getServer().getConfigurationManager().getOppedPlayerNames();
			for(String player : players) {
				if(event.player.getName().equals(player)) {
					accessLevel = PlayerAccessLevel.ADMIN;
				}
			}
		}
	}
}
