package com.venture.client.mixin;

import com.venture.client.config.VentureConfig;
import com.venture.client.sound.VentureSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Cancels the server's vanilla attack sounds and plays ours at the same position. */
@Mixin(ClientPacketListener.class)
public abstract class SoundPacketMixin {

	@Inject(method = "handleSoundEvent", at = @At("HEAD"), cancellable = true)
	private void venture$replaceHitSound(ClientboundSoundPacket packet, CallbackInfo ci) {
		if (!VentureConfig.INSTANCE.customHitSound) return;
		SoundEvent ev = packet.getSound().value();
		boolean crit = VentureSounds.isVanillaCrit(ev);
		if (!crit && !VentureSounds.isVanillaHit(ev)) return;

		// vanilla re-runs this handler on the main thread; only act there
		if (!Minecraft.getInstance().isSameThread()) return;

		ci.cancel();
		if (!VentureSounds.isNoDamage(ev)) {
			VentureSounds.playAt(packet.getX(), packet.getY(), packet.getZ(), crit);
		}
	}
}
