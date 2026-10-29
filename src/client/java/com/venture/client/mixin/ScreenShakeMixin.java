package com.venture.client.mixin;

import com.venture.client.config.VentureConfig;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Removes the damage camera tilt. Visual only. No-op (with a log warning) if the method was renamed. */
@Mixin(GameRenderer.class)
public abstract class ScreenShakeMixin {

	@Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
	private void venture$noHurtTilt(CallbackInfo ci) {
		if (VentureConfig.INSTANCE.disableScreenShake) ci.cancel();
	}
}
