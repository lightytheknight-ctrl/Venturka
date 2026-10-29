package com.venture.client.mixin;

import com.venture.client.freecam.Freecam;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** While freecam is on, WASD moves the camera, not your body. */
@Mixin(KeyboardInput.class)
public abstract class FreecamInputMixin extends ClientInput {

	@Inject(method = "tick", at = @At("TAIL"))
	private void venture$freezeBody(CallbackInfo ci) {
		if (Freecam.active) {
			this.keyPresses = Input.EMPTY;
			this.moveVector = Vec2.ZERO;
		}
	}
}
