package com.venture.client.particle;

import com.venture.client.config.VentureConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public final class VentureParticles {

	public static final String[] TYPE_LABELS = {"Crit", "Enchant", "Serca", "End Rod", "Własny kolor"};

	/** True while WE spawn particles, so the optimizer mixin never cancels them. */
	public static boolean spawning = false;
	private static long lastTick = -1;

	private static final Set<ParticleType<?>> DECORATIVE = set(ParticleTypes.SMOKE, ParticleTypes.LARGE_SMOKE,
			ParticleTypes.FLAME, ParticleTypes.CLOUD, ParticleTypes.ASH, ParticleTypes.PORTAL);
	private static final Set<ParticleType<?>> EXPLOSION = set(ParticleTypes.EXPLOSION,
			ParticleTypes.EXPLOSION_EMITTER, ParticleTypes.POOF);
	private static final Set<ParticleType<?>> VANILLA_ATTACK = set(ParticleTypes.CRIT, ParticleTypes.ENCHANTED_HIT,
			ParticleTypes.DAMAGE_INDICATOR, ParticleTypes.SWEEP_ATTACK);

	private VentureParticles() {}

	private static Set<ParticleType<?>> set(ParticleType<?>... types) {
		Set<ParticleType<?>> s = Collections.newSetFromMap(new IdentityHashMap<>());
		Collections.addAll(s, types);
		return s;
	}

	/** O(1) identity lookups, no string work - safe in the particle hot path. */
	public static boolean shouldCancel(ParticleType<?> t) {
		if (spawning) return false;
		VentureConfig c = VentureConfig.INSTANCE;
		if (c.reduceParticles && (DECORATIVE.contains(t) || (c.reduceExplosionParticles && EXPLOSION.contains(t)))) return true;
		return c.reduceOwnAttackParticles && VANILLA_ATTACK.contains(t);
	}

	public static void spawnHit(LivingEntity target) {
		VentureConfig c = VentureConfig.INSTANCE;
		Minecraft mc = Minecraft.getInstance();
		if (!c.hitParticleEnabled || mc.level == null) return;

		int count = Math.max(0, Math.min(20, c.hitParticleCount)); // hard cap
		if (count == 0) return;

		long now = mc.level.getGameTime();
		if (now == lastTick) return; // max once per tick
		lastTick = now;

		ParticleOptions effect = switch (c.hitParticleType) {
			case 0 -> ParticleTypes.CRIT;
			case 1 -> ParticleTypes.ENCHANTED_HIT;
			case 2 -> ParticleTypes.HEART;
			case 3 -> ParticleTypes.END_ROD;
			default -> new DustParticleOptions(c.hitParticleColor & 0xFFFFFF, 1.0f);
		};

		RandomSource r = mc.level.random;
		double cx = target.getX(), cy = target.getY(0.6), cz = target.getZ();
		spawning = true;
		try {
			for (int i = 0; i < count; i++) {
				mc.particleEngine.createParticle(effect,
						cx + (r.nextDouble() - 0.5) * 0.6, cy + (r.nextDouble() - 0.5) * 0.5, cz + (r.nextDouble() - 0.5) * 0.6,
						(r.nextDouble() - 0.5) * 0.3, r.nextDouble() * 0.25, (r.nextDouble() - 0.5) * 0.3);
			}
		} finally {
			spawning = false;
		}
	}
}
