package com.thaistoanf.beaconmorespace.network;

import com.thaistoanf.beaconmorespace.BeaconMoreSpace;
import net.minecraft.util.Identifier;

/**
 * Empty marker channel. It is never actually sent; the client only registers a receiver for it so
 * the server can detect (via {@code ServerPlayNetworking.canSend}) that the client has Beacon More
 * Space installed. Only those clients receive the extra Beacon screen property, which keeps
 * vanilla clients from crashing on an unknown property index.
 */
public final class HandshakePayload {
	public static final Identifier ID = new Identifier(BeaconMoreSpace.MOD_ID, "handshake");

	private HandshakePayload() {
	}
}
