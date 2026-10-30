package com.thaistoanf.beaconmorespace.client;

import com.thaistoanf.beaconmorespace.network.HandshakePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/** Client entrypoint for Beacon More Space. */
public final class BeaconMoreSpaceClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Registering a receiver advertises the channel to the server, which then knows this client
		// understands the extra Beacon screen property. The channel itself carries no data.
		ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, (client, handler, buf, responseSender) -> {
		});
	}
}
