package com.venture.client.mixin;

import com.venture.client.freecam.Freecam;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

	@Shadow protected abstract void setPosition(double x, double y, double z);
	@Shadow protected abstract void setRotation(float yRot, float xRot);
	@Shadow private boolean detached;

	@Inject(method = {"setup", "update"}, at = @At("TAIL"))
	private void venture$freecam(CallbackInfo ci) {
		if (!Freecam.active) return;
		float t = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
		setPosition(Freecam.px + (Freecam.x - Freecam.px) * t,
				Freecam.py + (Freecam.y - Freecam.py) * t,
				Freecam.pz + (Freecam.z - Freecam.pz) * t);
		setRotation(Freecam.yaw, Freecam.pitch);
		this.detached = true; // so your own body is rendered
	}
}
