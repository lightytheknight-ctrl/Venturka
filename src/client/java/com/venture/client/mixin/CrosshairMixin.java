package com.venture.client.mixin;

import com.venture.client.config.VentureConfig;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Hides the vanilla crosshair. Both the old and the new (extract*) method names are targeted. */
@Mixin(Gui.class)
public abstract class CrosshairMixin {

	@Inject(method = {"renderCrosshair", "extractCrosshair"}, at = @At("HEAD"), cancellable = true)
	private void venture$maybeHide(CallbackInfo ci) {
		if (VentureConfig.INSTANCE.hideCrosshair) ci.cancel();
	}
}
