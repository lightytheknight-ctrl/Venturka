package com.venture.client.mixin;

import com.venture.client.particle.VentureParticles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class)
public abstract class ParticleOptimizeMixin {

	@Inject(method = "createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;",
			at = @At("HEAD"), cancellable = true)
	private void venture$maybeCancel(ParticleOptions options, double x, double y, double z,
									  double vx, double vy, double vz, CallbackInfoReturnable<Particle> cir) {
		if (VentureParticles.shouldCancel(options.getType())) {
			cir.setReturnValue(null);
		}
	}
}
