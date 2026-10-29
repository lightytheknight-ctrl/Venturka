package com.venture.client.mixin;

import com.venture.client.freecam.Freecam;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** While freecam is on, the mouse turns the camera, not your body. */
@Mixin(Entity.class)
public abstract class FreecamLookMixin {

	@Inject(method = "turn", at = @At("HEAD"), cancellable = true)
	private void venture$freecamLook(double yRot, double xRot, CallbackInfo ci) {
		if (Freecam.active && (Object) this == Minecraft.getInstance().player) {
			Freecam.look(yRot, xRot);
			ci.cancel();
		}
	}
}
