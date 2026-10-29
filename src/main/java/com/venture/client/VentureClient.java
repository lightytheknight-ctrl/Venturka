package com.venture.client;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common entrypoint. Venture Client is entirely client-side (no gameplay
 * logic that needs to run on a server), so this just logs startup.
 */
public class VentureClient implements ModInitializer {
	public static final String MOD_ID = "venture-client";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Venture Client loaded (common entrypoint).");
	}
}
