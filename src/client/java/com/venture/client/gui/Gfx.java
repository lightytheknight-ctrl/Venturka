package com.venture.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/** Thin wrapper so a rename in Minecraft's draw API only needs fixing here. */
public final class Gfx {
	private Gfx() {}

	public static void fill(GuiGraphicsExtractor g, int x1, int y1, int x2, int y2, int color) {
		g.fill(x1, y1, x2, y2, color);
	}

	public static void gradient(GuiGraphicsExtractor g, int x1, int y1, int x2, int y2, int c1, int c2) {
		g.fillGradient(x1, y1, x2, y2, c1, c2);
	}

	public static void text(GuiGraphicsExtractor g, Font f, Component c, int x, int y, int color) {
		g.text(f, c, x, y, color);
	}

	public static void centered(GuiGraphicsExtractor g, Font f, Component c, int cx, int y, int color) {
		g.centeredText(f, c, cx, y, color);
	}

	public static void border(GuiGraphicsExtractor g, int x, int y, int w, int h, int color) {
		fill(g, x, y, x + w, y + 1, color);
		fill(g, x, y + h - 1, x + w, y + h, color);
		fill(g, x, y, x + 1, y + h, color);
		fill(g, x + w - 1, y, x + w, y + h, color);
	}
}
