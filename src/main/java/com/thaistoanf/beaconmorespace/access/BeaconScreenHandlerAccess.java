package com.thaistoanf.beaconmorespace.access;

/**
 * Duck interface implemented on {@link net.minecraft.screen.BeaconScreenHandler}, used by the
 * client GUI to read the synchronized payment tier of the open Beacon.
 */
public interface BeaconScreenHandlerAccess {
	/**
	 * @return the synchronized payment tier index (0-4), or {@code -1} when the server did not send
	 *     one (for example when playing on a server without Beacon More Space).
	 */
	int beaconmorespace$getSyncedTier();
}
