package com.venture.client.gui;

import com.venture.client.config.VentureConfig;
import com.venture.client.particle.VentureParticles;
import com.venture.client.sound.VentureSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Neon-purple settings GUI (Right Shift): left tabs, two columns of toggles / sliders / cyclers. */
public class VentureSettingsScreen extends Screen {

	private static final int PURPLE = 0xFFB44CFF, PURPLE_DARK = 0xFF5A1FA0, PANEL = 0xD0200A3C, ROW_BG = 0xA0301260, WHITE = 0xFFFFFFFF;
	private static final int PANEL_W = 390, PANEL_H = 270, ROW_W = 172, ROW_H = 22;

	private enum Tab {
		PERFORMANCE("WYDAJNOŚĆ"), HUD("HUD"), SOUNDS("DŹWIĘKI"), PARTICLES("PARTICLE"), CAMERA("KAMERA"), PVP("PVP");
		final String label;
		Tab(String l) { label = l; }
	}

	private final Screen parent;
	private Tab tab = Tab.PERFORMANCE;
	private List<Row> rows = new ArrayList<>();
	private SliderRow activeSlider;

	public VentureSettingsScreen(Screen parent) {
		super(Component.literal("Venture Client"));
		this.parent = parent;
	}

	@Override
	protected void init() { rebuildRows(); }

	// ------------------------------------------------------------ rows

	private void rebuildRows() {
		VentureConfig c = VentureConfig.INSTANCE;
		rows = new ArrayList<>();
		switch (tab) {
			case PERFORMANCE -> {
				rows.add(toggle("Redukcja partikli", () -> c.reduceParticles, v -> c.reduceParticles = v));
				rows.add(toggle("Partikle wybuchów", () -> c.reduceExplosionParticles, v -> c.reduceExplosionParticles = v));
				rows.add(toggle("Bez partikli ataków", () -> c.reduceOwnAttackParticles, v -> c.reduceOwnAttackParticles = v));
				rows.add(toggle("Bez screen shake", () -> c.disableScreenShake, v -> c.disableScreenShake = v));
			}
			case HUD -> {
				rows.add(toggle("HP przeciwnika", () -> c.showOpponentHealth, v -> c.showOpponentHealth = v));
				rows.add(slider("Zasięg HP", 2, 20, "%.0f", () -> (float) c.opponentHealthRange, v -> c.opponentHealthRange = v));
				rows.add(toggle("Serduszko przy HP", () -> c.opponentHealthShowHearts, v -> c.opponentHealthShowHearts = v));
				rows.add(toggle("Keystrokes", () -> c.showKeystrokes, v -> c.showKeystrokes = v));
				rows.add(slider("Skala keystrokes", 0.5f, 2f, "%.2f", () -> c.keystrokesScale, v -> c.keystrokesScale = v));
				rows.add(toggle("Ukryj crosshair", () -> c.hideCrosshair, v -> c.hideCrosshair = v));
			}
			case SOUNDS -> {
				rows.add(toggle("Własne dźwięki trafień", () -> c.customHitSound, v -> c.customHitSound = v));
				rows.add(new ButtonRow("▶ Test trafienia", () -> VentureSounds.preview(false)));
				rows.add(cycle("Trafienie", VentureSounds.HIT_LABELS, () -> c.hitSoundVariant, v -> c.hitSoundVariant = v));
				rows.add(slider("Głośność", 0f, 2f, "%.2f", () -> c.hitSoundVolume, v -> c.hitSoundVolume = v));
				rows.add(slider("Wysokość", 0.5f, 2f, "%.2f", () -> c.hitSoundPitch, v -> c.hitSoundPitch = v));
				rows.add(toggle("Osobny dźwięk crita", () -> c.critSoundEnabled, v -> c.critSoundEnabled = v));
				rows.add(new ButtonRow("▶ Test crita", () -> VentureSounds.preview(true)));
				rows.add(cycle("Crit", VentureSounds.CRIT_LABELS, () -> c.critSoundVariant, v -> c.critSoundVariant = v));
				rows.add(slider("Głośność crita", 0f, 2f, "%.2f", () -> c.critSoundVolume, v -> c.critSoundVolume = v));
				rows.add(slider("Wysokość crita", 0.5f, 2f, "%.2f", () -> c.critSoundPitch, v -> c.critSoundPitch = v));
			}
			case PARTICLES -> {
				rows.add(toggle("Particle trafienia", () -> c.hitParticleEnabled, v -> c.hitParticleEnabled = v));
				rows.add(cycle("Typ", VentureParticles.TYPE_LABELS, () -> c.hitParticleType, v -> c.hitParticleType = v));
				rows.add(slider("Ilość (max 20)", 1, 20, "%.0f", () -> (float) c.hitParticleCount, v -> c.hitParticleCount = Math.round(v)));
				rows.add(slider("Kolor: czerwony", 0, 255, "%.0f", () -> (float) (c.hitParticleColor >> 16 & 255),
						v -> c.hitParticleColor = (c.hitParticleColor & 0x00FFFF) | (Math.round(v) << 16)));
				rows.add(slider("Kolor: zielony", 0, 255, "%.0f", () -> (float) (c.hitParticleColor >> 8 & 255),
						v -> c.hitParticleColor = (c.hitParticleColor & 0xFF00FF) | (Math.round(v) << 8)));
				rows.add(slider("Kolor: niebieski", 0, 255, "%.0f", () -> (float) (c.hitParticleColor & 255),
						v -> c.hitParticleColor = (c.hitParticleColor & 0xFFFF00) | Math.round(v)));
			}
			case CAMERA -> {
				rows.add(toggle("Freecam dostępny", () -> c.freecamEnabled, v -> c.freecamEnabled = v));
				rows.add(slider("Prędkość", 0.1f, 2f, "%.2f", () -> c.freecamSpeed, v -> c.freecamSpeed = v));
				rows.add(slider("Max dystans", 8, 128, "%.0f", () -> c.freecamMaxDistance, v -> c.freecamMaxDistance = v));
				rows.add(toggle("Wyjdź po obrażeniach", () -> c.freecamDisableOnDamage, v -> c.freecamDisableOnDamage = v));
			}
			case PVP -> {
				rows.add(toggle("Quick-swap (klawisz)", () -> c.quickSwapEnabled, v -> c.quickSwapEnabled = v));
				rows.add(slider("Slot quick-swap", 1, 9, "%.0f", () -> (float) (c.quickSwapTargetSlot + 1), v -> c.quickSwapTargetSlot = Math.round(v) - 1));
			}
		}
	}

	private Row toggle(String l, Supplier<Boolean> g, Consumer<Boolean> s) { return new ToggleRow(l, g, s); }
	private Row slider(String l, float mn, float mx, String f, Supplier<Float> g, Consumer<Float> s) { return new SliderRow(l, mn, mx, f, g, s); }
	private Row cycle(String l, String[] o, Supplier<Integer> g, Consumer<Integer> s) { return new CycleRow(l, o, g, s); }

	private int panelX() { return (this.width - PANEL_W) / 2 + 40; }
	private int panelY() { return (this.height - PANEL_H) / 2; }
	private int rowX(int i) { return panelX() + 14 + (i % 2) * (ROW_W + 10); }
	private int rowY(int i) { return panelY() + 62 + (i / 2) * (ROW_H + 6); }
	private int tabX() { return panelX() - 100; }
	private int tabY(int i) { return panelY() + 30 + i * 28; }

	// ------------------------------------------------------------ render

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
		Gfx.gradient(g, 0, 0, this.width, this.height, 0xF0080212, 0xF02A0B50);
		drawGrid(g);

		int px = panelX(), py = panelY();
		Gfx.fill(g, px, py, px + PANEL_W, py + PANEL_H, PANEL);
		Gfx.border(g, px, py, PANEL_W, PANEL_H, PURPLE);
		Gfx.border(g, px + 3, py + 3, PANEL_W - 6, PANEL_H - 6, PURPLE_DARK);
		Gfx.centered(g, this.font, Component.literal("VENTURE CLIENT").withStyle(ChatFormatting.BOLD), px + PANEL_W / 2, py + 14, WHITE);
		Gfx.centered(g, this.font, Component.literal("Fabric Client • PvP Optimized"), px + PANEL_W / 2, py + 28, 0xFFD9B3FF);
		Gfx.fill(g, px + 14, py + 48, px + PANEL_W - 14, py + 49, PURPLE_DARK);

		Tab[] tabs = Tab.values();
		for (int i = 0; i < tabs.length; i++) {
			int x = tabX(), y = tabY(i);
			boolean sel = tabs[i] == tab, hover = in(mx, my, x, y, 90, 20);
			Gfx.fill(g, x, y, x + 90, y + 20, sel ? PURPLE_DARK : (hover ? 0xA0401880 : ROW_BG));
			Gfx.border(g, x, y, 90, 20, sel ? WHITE : PURPLE);
			Gfx.centered(g, this.font, Component.literal(tabs[i].label), x + 45, y + 6, WHITE);
		}

		for (int i = 0; i < rows.size(); i++) rows.get(i).render(g, this.font, rowX(i), rowY(i), mx, my);

		int bx = px + PANEL_W / 2 - 60, by = py + PANEL_H - 30;
		boolean hb = in(mx, my, bx, by, 120, 20);
		Gfx.fill(g, bx, by, bx + 120, by + 20, hb ? PURPLE : PURPLE_DARK);
		Gfx.border(g, bx, by, 120, 20, WHITE);
		Gfx.centered(g, this.font, Component.literal("ZAPISZ I ZAMKNIJ"), bx + 60, by + 6, WHITE);
		Gfx.text(g, this.font, Component.literal("PVP OPTIMIZED").withStyle(ChatFormatting.BOLD), this.width - 110, this.height - 16, 0xFFD9B3FF);
	}

	private void drawGrid(GuiGraphicsExtractor g) {
		int horizon = this.height / 2 + 40;
		for (int k = 0; k < 12; k++) {
			int y = horizon + k * k * 3;
			if (y < this.height) Gfx.fill(g, 0, y, this.width, y + 1, 0x40B44CFF);
		}
		int cx = this.width / 2, steps = this.height - horizon;
		for (int k = -14; k <= 14; k++) {
			int xb = cx + k * 70;
			for (int s = 0; s < steps; s += 2) {
				int x = cx + (xb - cx) * s / steps;
				Gfx.fill(g, x, horizon + s, x + 1, horizon + s + 2, 0x30B44CFF);
			}
		}
	}

	private static boolean in(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	// ------------------------------------------------------------ input

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (event.button() != 0) return super.mouseClicked(event, doubleClick);
		double mx = event.x(), my = event.y();
		Tab[] tabs = Tab.values();
		for (int i = 0; i < tabs.length; i++) {
			if (in(mx, my, tabX(), tabY(i), 90, 20)) { tab = tabs[i]; rebuildRows(); return true; }
		}
		for (int i = 0; i < rows.size(); i++) {
			if (in(mx, my, rowX(i), rowY(i), ROW_W, ROW_H)) {
				Row r = rows.get(i);
				r.click(mx, rowX(i));
				if (r instanceof SliderRow s) activeSlider = s;
				return true;
			}
		}
		if (in(mx, my, panelX() + PANEL_W / 2 - 60, panelY() + PANEL_H - 30, 120, 20)) { onClose(); return true; }
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		if (activeSlider != null) {
			int idx = rows.indexOf(activeSlider);
			if (idx >= 0) activeSlider.click(event.x(), rowX(idx));
			return true;
		}
		return super.mouseDragged(event, dx, dy);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (activeSlider != null) { activeSlider = null; VentureConfig.INSTANCE.save(); }
		return super.mouseReleased(event);
	}

	@Override
	public void onClose() {
		VentureConfig.INSTANCE.save();
		this.minecraft.gui.setScreen(parent);
	}

	@Override
	public boolean isPauseScreen() { return false; }

	// ------------------------------------------------------------ row types

	private interface Row {
		void render(GuiGraphicsExtractor g, Font font, int x, int y, int mx, int my);
		void click(double mouseX, int x);
	}

	private static void rowBg(GuiGraphicsExtractor g, int x, int y, boolean hover) {
		Gfx.fill(g, x, y, x + ROW_W, y + ROW_H, hover ? 0xB0401880 : ROW_BG);
		Gfx.border(g, x, y, ROW_W, ROW_H, PURPLE_DARK);
	}

	private static class ToggleRow implements Row {
		final String label; final Supplier<Boolean> get; final Consumer<Boolean> set;
		ToggleRow(String l, Supplier<Boolean> g, Consumer<Boolean> s) { label = l; get = g; set = s; }
		public void render(GuiGraphicsExtractor g, Font font, int x, int y, int mx, int my) {
			rowBg(g, x, y, in(mx, my, x, y, ROW_W, ROW_H));
			Gfx.text(g, font, Component.literal(label), x + 6, y + 7, WHITE);
			int cx = x + ROW_W - 18, cy = y + 5;
			Gfx.border(g, cx, cy, 12, 12, PURPLE);
			if (get.get()) Gfx.fill(g, cx + 3, cy + 3, cx + 9, cy + 9, PURPLE);
		}
		public void click(double mouseX, int x) { set.accept(!get.get()); VentureConfig.INSTANCE.save(); }
	}

	private static class CycleRow implements Row {
		final String label; final String[] options; final Supplier<Integer> get; final Consumer<Integer> set;
		CycleRow(String l, String[] o, Supplier<Integer> g, Consumer<Integer> s) { label = l; options = o; get = g; set = s; }
		public void render(GuiGraphicsExtractor g, Font font, int x, int y, int mx, int my) {
			rowBg(g, x, y, in(mx, my, x, y, ROW_W, ROW_H));
			int i = Math.max(0, Math.min(options.length - 1, get.get()));
			Gfx.text(g, font, Component.literal(label + ": " + options[i] + " ›"), x + 6, y + 7, WHITE);
		}
		public void click(double mouseX, int x) {
			set.accept((get.get() + 1) % options.length);
			VentureConfig.INSTANCE.save();
		}
	}

	private static class ButtonRow implements Row {
		final String label; final Runnable action;
		ButtonRow(String l, Runnable a) { label = l; action = a; }
		public void render(GuiGraphicsExtractor g, Font font, int x, int y, int mx, int my) {
			boolean h = in(mx, my, x, y, ROW_W, ROW_H);
			Gfx.fill(g, x, y, x + ROW_W, y + ROW_H, h ? PURPLE : PURPLE_DARK);
			Gfx.border(g, x, y, ROW_W, ROW_H, PURPLE);
			Gfx.centered(g, font, Component.literal(label), x + ROW_W / 2, y + 7, WHITE);
		}
		public void click(double mouseX, int x) { action.run(); }
	}

	private static class SliderRow implements Row {
		final String label, fmt; final float min, max; final Supplier<Float> get; final Consumer<Float> set;
		SliderRow(String l, float mn, float mx, String f, Supplier<Float> g, Consumer<Float> s) {
			label = l; min = mn; max = mx; fmt = f; get = g; set = s;
		}
		public void render(GuiGraphicsExtractor g, Font font, int x, int y, int mx, int my) {
			Gfx.fill(g, x, y, x + ROW_W, y + ROW_H, ROW_BG);
			float pct = Math.max(0f, Math.min(1f, (get.get() - min) / (max - min)));
			Gfx.fill(g, x + 1, y + 1, x + 1 + (int) ((ROW_W - 2) * pct), y + ROW_H - 1, PURPLE_DARK);
			Gfx.border(g, x, y, ROW_W, ROW_H, PURPLE);
			Gfx.text(g, font, Component.literal(label + ": " + String.format(fmt, get.get())), x + 6, y + 7, WHITE);
		}
		public void click(double mouseX, int x) {
			float pct = Math.max(0f, Math.min(1f, (float) ((mouseX - x) / ROW_W)));
			set.accept(min + (max - min) * pct);
		}
	}
}
