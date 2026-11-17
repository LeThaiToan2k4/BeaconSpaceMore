package com.thaistoanf.beaconmorespace.network;

import com.thaistoanf.beaconmorespace.BeaconMoreSpace;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Empty marker payload. It is never actually sent; the client only registers a receiver for it so
 * the server can detect (via {@code ServerPlayNetworking.canSend}) that the client has Beacon More
 * Space installed. Only those clients receive the extra Beacon screen property, which keeps
 * vanilla clients from crashing on an unknown property index.
 */
public record HandshakePayload() implements CustomPacketPayload {
	public static final HandshakePayload INSTANCE = new HandshakePayload();
	public static final CustomPacketPayload.Type<HandshakePayload> ID =
			new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(BeaconMoreSpace.MOD_ID, "handshake"));
	public static final StreamCodec<RegistryFriendlyByteBuf, HandshakePayload> CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
