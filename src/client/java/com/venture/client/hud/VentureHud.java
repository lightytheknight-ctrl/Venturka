package com.venture.client.hud;

import com.venture.client.config.VentureConfig;
import com.venture.client.gui.Gfx;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** Lightweight HUD: Component objects are cached, the HP string is rebuilt only when it changes. */
public class VentureHud {

	private static final Component T_W = Component.literal("W"), T_A = Component.literal("A"),
			T_S = Component.literal("S"), T_D = Component.literal("D");
	private static final Component T_LMB = Component.literal("LMB"), T_RMB = Component.literal("RMB"),
			T_SPACE = Component.literal("SPACE");

	private static String lastName = "";
	private static int lastHp = -1, lastMax = -1;
	private static boolean lastHearts;
	private static Component cachedHpText = Component.empty();

	public static void register() {
		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("venture-client", "hud"), (g, tick) -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.player == null) return;
			VentureConfig cfg = VentureConfig.INSTANCE;
			if (cfg.showKeystrokes) renderKeystrokes(g, mc, cfg);
			if (cfg.showOpponentHealth) renderOpponentHealth(g, mc, cfg);
		});
	}

	private static void renderKeystrokes(GuiGraphicsExtractor g, Minecraft mc, VentureConfig cfg) {
		int s = Math.max(12, (int) (18 * cfg.keystrokesScale));
		int h = s - 2;
		int sw = mc.getWindow().getGuiScaledWidth();
		int x0 = cfg.keystrokesX < 0 ? sw + cfg.keystrokesX - s * 3 : cfg.keystrokesX;
		int y0 = cfg.keystrokesY;

		key(g, mc, x0 + s, y0, s - 2, h, T_W, mc.options.keyUp.isDown(), cfg);
		key(g, mc, x0, y0 + s, s - 2, h, T_A, mc.options.keyLeft.isDown(), cfg);
		key(g, mc, x0 + s, y0 + s, s - 2, h, T_S, mc.options.keyDown.isDown(), cfg);
		key(g, mc, x0 + s * 2, y0 + s, s - 2, h, T_D, mc.options.keyRight.isDown(), cfg);
		int half = (s * 3 - 4) / 2;
		key(g, mc, x0, y0 + s * 2, half, h, T_LMB, mc.options.keyAttack.isDown(), cfg);
		key(g, mc, x0 + half + 2, y0 + s * 2, half, h, T_RMB, mc.options.keyUse.isDown(), cfg);
		key(g, mc, x0, y0 + s * 3, s * 3 - 2, h, T_SPACE, mc.options.keyJump.isDown(), cfg);
	}

	private static void key(GuiGraphicsExtractor g, Minecraft mc, int x, int y, int w, int h, Component label, boolean down, VentureConfig cfg) {
		Gfx.fill(g, x, y, x + w, y + h, down ? 0x60FFFFFF : cfg.keystrokesBgColor);
		Gfx.centered(g, mc.font, label, x + w / 2, y + (h - 8) / 2, down ? cfg.keystrokesPressedColor : cfg.keystrokesIdleColor);
	}

	private static void renderOpponentHealth(GuiGraphicsExtractor g, Minecraft mc, VentureConfig cfg) {
		if (mc.level == null) return;
		HitResult hit = mc.hitResult;
		if (!(hit instanceof EntityHitResult ehr)) return;
		Entity e = ehr.getEntity();
		if (!(e instanceof LivingEntity living) || living == mc.player) return;
		if (mc.player.distanceTo(living) > cfg.opponentHealthRange) return;

		int hp = Mth.ceil(living.getHealth()), max = Mth.ceil(living.getMaxHealth());
		String name = living instanceof Player p ? p.getName().getString() : living.getType().getDescription().getString();
		if (hp != lastHp || max != lastMax || cfg.opponentHealthShowHearts != lastHearts || !name.equals(lastName)) {
			lastHp = hp; lastMax = max; lastName = name; lastHearts = cfg.opponentHealthShowHearts;
			cachedHpText = Component.literal(name + "  " + hp + " / " + max + (lastHearts ? " \u2764" : ""));
		}

		int cx = mc.getWindow().getGuiScaledWidth() / 2, y = mc.getWindow().getGuiScaledHeight() / 2 - 40;
		Gfx.centered(g, mc.font, cachedHpText, cx, y, 0xFFFF5555);
		int bw = 60, bx = cx - bw / 2, by = y + 10;
		float pct = max <= 0 ? 0 : Mth.clamp(living.getHealth() / living.getMaxHealth(), 0, 1);
		Gfx.fill(g, bx, by, bx + bw, by + 3, 0x80000000);
		Gfx.fill(g, bx, by, bx + (int) (bw * pct), by + 3, 0xFF55FF55);
	}
}
