package com.thaistoanf.beaconmorespace.network;

import com.thaistoanf.beaconmorespace.BeaconMoreSpace;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Empty marker payload. It is never actually sent; the client only registers a receiver for it so
 * the server can detect (via {@code ServerPlayNetworking.canSend}) that the client has Beacon More
 * Space installed. Only those clients receive the extra Beacon screen property, which keeps
 * vanilla clients from crashing on an unknown property index.
 */
public record HandshakePayload() implements CustomPayload {
	public static final HandshakePayload INSTANCE = new HandshakePayload();
	public static final CustomPayload.Id<HandshakePayload> ID =
			new CustomPayload.Id<>(Identifier.of(BeaconMoreSpace.MOD_ID, "handshake"));
	public static final PacketCodec<RegistryByteBuf, HandshakePayload> CODEC = PacketCodec.unit(INSTANCE);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
