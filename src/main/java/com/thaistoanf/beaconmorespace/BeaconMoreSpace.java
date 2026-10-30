package com.thaistoanf.beaconmorespace;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common entrypoint for Beacon More Space.
 *
 * <p>The gameplay changes are implemented entirely through mixins.</p>
 */
public final class BeaconMoreSpace implements ModInitializer {
	public static final String MOD_ID = "beaconmorespace";
	public static final Logger LOGGER = LoggerFactory.getLogger("Beacon More Space");

	@Override
	public void onInitialize() {
		LOGGER.info("Beacon More Space initialized: beacon range now scales with the payment item.");
	}
}
