package com.venture.client.sound;

import com.venture.client.config.VentureConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/** Our hit/crit sounds (not registered in the registry - looked up by id via sounds.json). */
public final class VentureSounds {

	public static final String[] HIT_LABELS = {"Punch", "Click", "Soft", "Własny (RP)"};
	public static final String[] CRIT_LABELS = {"Ping", "Chime", "Snap", "Własny (RP)"};

	private static final SoundEvent[] HIT = {ev("hit_punch"), ev("hit_click"), ev("hit_soft"), ev("hit_custom")};
	private static final SoundEvent[] CRIT = {ev("crit_ping"), ev("crit_chime"), ev("crit_snap"), ev("crit_custom")};

	private static final Set<SoundEvent> VANILLA_HIT = Collections.newSetFromMap(new IdentityHashMap<>());
	static {
		VANILLA_HIT.add(SoundEvents.PLAYER_ATTACK_STRONG);
		VANILLA_HIT.add(SoundEvents.PLAYER_ATTACK_WEAK);
		VANILLA_HIT.add(SoundEvents.PLAYER_ATTACK_KNOCKBACK);
		VANILLA_HIT.add(SoundEvents.PLAYER_ATTACK_SWEEP);
		VANILLA_HIT.add(SoundEvents.PLAYER_ATTACK_NODAMAGE);
	}

	private VentureSounds() {}

	private static SoundEvent ev(String path) {
		return SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("venture-client", path));
	}

	public static boolean isVanillaHit(SoundEvent e) { return VANILLA_HIT.contains(e); }
	public static boolean isVanillaCrit(SoundEvent e) { return e == SoundEvents.PLAYER_ATTACK_CRIT; }
	public static boolean isNoDamage(SoundEvent e) { return e == SoundEvents.PLAYER_ATTACK_NODAMAGE; }
	private static int clamp(int v) { return Math.max(0, Math.min(3, v)); }

	public static void playAt(double x, double y, double z, boolean crit) {
		VentureConfig c = VentureConfig.INSTANCE;
		boolean useCrit = crit && c.critSoundEnabled;
		SoundEvent e = useCrit ? CRIT[clamp(c.critSoundVariant)] : HIT[clamp(c.hitSoundVariant)];
		float vol = useCrit ? c.critSoundVolume : c.hitSoundVolume;
		float pitch = useCrit ? c.critSoundPitch : c.hitSoundPitch;
		if (vol <= 0.001f) return;
		Minecraft.getInstance().getSoundManager().play(
				new SimpleSoundInstance(e, SoundSource.PLAYERS, vol, pitch, RandomSource.create(), x, y, z));
	}

	public static void preview(boolean crit) {
		VentureConfig c = VentureConfig.INSTANCE;
		SoundEvent e = crit ? CRIT[clamp(c.critSoundVariant)] : HIT[clamp(c.hitSoundVariant)];
		float vol = crit ? c.critSoundVolume : c.hitSoundVolume;
		float pitch = crit ? c.critSoundPitch : c.hitSoundPitch;
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(e, pitch, vol));
	}
}
