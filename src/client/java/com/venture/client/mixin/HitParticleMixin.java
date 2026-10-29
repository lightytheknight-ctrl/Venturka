package com.venture.client.mixin;

import com.venture.client.config.VentureConfig;
import com.venture.client.particle.VentureParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class HitParticleMixin {

	@Inject(method = "attack", at = @At("HEAD"))
	private void venture$hitParticles(Entity target, CallbackInfo ci) {
		if (!VentureConfig.INSTANCE.hitParticleEnabled) return;
		if ((Object) this != Minecraft.getInstance().player) return;
		if (target instanceof LivingEntity le && le.isAlive() && target.isAttackable()) {
			VentureParticles.spawnHit(le);
		}
	}
}
