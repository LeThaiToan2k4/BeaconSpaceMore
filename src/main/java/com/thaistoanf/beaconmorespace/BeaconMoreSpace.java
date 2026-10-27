package com.thaistoanf.beaconmorespace;

import com.thaistoanf.beaconmorespace.network.HandshakePayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common entrypoint for Beacon More Space.
 *
 * <p>The gameplay changes are implemented entirely through mixins. This entrypoint only registers
 * the handshake payload type that lets the server know whether a connected client has the mod
 * installed (and can therefore receive the extra Beacon screen property).</p>
 */
public final class BeaconMoreSpace implements ModInitializer {
	public static final String MOD_ID = "beaconmorespace";
	public static final Logger LOGGER = LoggerFactory.getLogger("Beacon More Space");

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playS2C().register(HandshakePayload.ID, HandshakePayload.CODEC);
		LOGGER.info("Beacon More Space initialized: beacon range now scales with the payment item.");
	}
}
