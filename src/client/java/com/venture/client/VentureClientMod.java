package com.venture.client;

import com.venture.client.freecam.Freecam;
import com.venture.client.hud.VentureHud;
import com.venture.client.keybind.VentureKeybinds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class VentureClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		VentureKeybinds.register();
		VentureHud.register();
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> Freecam.disable());
	}
}
