package com.venture.client.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import com.venture.client.config.VentureConfig;
import com.venture.client.freecam.Freecam;
import com.venture.client.gui.VentureSettingsScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class VentureKeybinds {

	private static final KeyMapping.Category CATEGORY =
			KeyMapping.Category.register(Identifier.fromNamespaceAndPath("venture-client", "main"));

	public static final KeyMapping OPEN_SETTINGS = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.venture-client.open_settings", InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT, CATEGORY));
	public static final KeyMapping QUICK_SWAP = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.venture-client.quick_swap", InputConstants.Type.KEYSYM, InputConstants.KEY_C, CATEGORY));
	public static final KeyMapping FREECAM = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.venture-client.freecam", InputConstants.Type.KEYSYM, InputConstants.KEY_V, CATEGORY));

	private static int lastHotbarSlot = 0;

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) { Freecam.disable(); return; }

			while (OPEN_SETTINGS.consumeClick()) {
				if (client.screen == null) client.gui.setScreen(new VentureSettingsScreen(null));
			}
			while (FREECAM.consumeClick()) Freecam.toggle(client);
			while (QUICK_SWAP.consumeClick()) {
				if (VentureConfig.INSTANCE.quickSwapEnabled) handleQuickSwap(client);
			}
			Freecam.tick(client);
		});
	}

	/** Manual only: swaps hotbar slot when YOU press the key. */
	private static void handleQuickSwap(Minecraft client) {
		int current = client.player.getInventory().getSelectedSlot();
		int target = VentureConfig.INSTANCE.quickSwapTargetSlot;
		if (target < 0 || target > 8) return;
		if (current == target) {
			client.player.getInventory().setSelectedSlot(lastHotbarSlot);
		} else {
			lastHotbarSlot = current;
			client.player.getInventory().setSelectedSlot(target);
		}
	}
}
