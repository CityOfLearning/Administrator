package com.dyn.gui.student.gui;

import com.rabbit.gui.component.hud.Hud;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class Freeze extends Hud {

	private static final ResourceLocation FREEZE = new ResourceLocation("dyn", "textures/gui/frozenBorder.png");

	public static void draw() {
		updateWindowScale();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableTexture2D();
		GlStateManager.disableLighting();

		mc.getTextureManager().bindTexture(FREEZE);
		drawScaledTexturedRect(0, 0, -1, windowWidth, windowHeight);

		GlStateManager.disableLighting();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
	}

}
