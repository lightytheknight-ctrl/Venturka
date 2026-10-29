package com.venture.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** All options. Plain public fields = zero overhead when read in hot paths. */
public class VentureConfig {

	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("venture-client.json");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static VentureConfig INSTANCE = load();

	// ---- Performance ----
	public boolean reduceParticles = true;
	public boolean reduceExplosionParticles = true;
	public boolean reduceOwnAttackParticles = true; // cancels VANILLA attack particles
	public boolean disableScreenShake = true;

	// ---- HUD ----
	public boolean showOpponentHealth = true;
	public double opponentHealthRange = 8.0;
	public boolean opponentHealthShowHearts = true;
	public boolean showKeystrokes = true;
	public int keystrokesX = -10;
	public int keystrokesY = 10;
	public float keystrokesScale = 1.0f;
	public int keystrokesBgColor = 0x80000000;
	public int keystrokesPressedColor = 0xFFFFFFFF;
	public int keystrokesIdleColor = 0x80FFFFFF;
	public boolean hideCrosshair = false;

	// ---- Sounds (true = mute vanilla hit sounds and play ours) ----
	public boolean customHitSound = true;
	public int hitSoundVariant = 0;   // 0 punch, 1 click, 2 soft, 3 custom (resource pack)
	public float hitSoundVolume = 1.0f;
	public float hitSoundPitch = 1.0f;
	public boolean critSoundEnabled = true;
	public int critSoundVariant = 0;  // 0 ping, 1 chime, 2 snap, 3 custom (resource pack)
	public float critSoundVolume = 1.0f;
	public float critSoundPitch = 1.0f;

	// ---- Hit particles ----
	public boolean hitParticleEnabled = true;
	public int hitParticleType = 4;   // 0 crit, 1 enchant, 2 heart, 3 end rod, 4 custom colour dust
	public int hitParticleCount = 6;  // hard-capped at 20
	public int hitParticleColor = 0xB44CFF;

	// ---- Freecam ----
	public boolean freecamEnabled = true;
	public float freecamSpeed = 0.5f;          // blocks per tick
	public float freecamMaxDistance = 32f;     // max distance from your body
	public boolean freecamDisableOnDamage = true;

	// ---- Quick swap (manual, keybind) ----
	public boolean quickSwapEnabled = true;
	public int quickSwapTargetSlot = 1;

	public static VentureConfig load() {
		if (Files.exists(FILE)) {
			try (Reader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
				VentureConfig cfg = GSON.fromJson(r, VentureConfig.class);
				if (cfg != null) return cfg;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		VentureConfig fresh = new VentureConfig();
		fresh.save();
		return fresh;
	}

	public void save() {
		try {
			Files.createDirectories(FILE.getParent());
			try (Writer w = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
				GSON.toJson(this, w);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
