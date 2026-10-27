package com.thaistoanf.beaconmorespace.access;

/**
 * Duck interface implemented on {@link net.minecraft.block.entity.BeaconBlockEntity} by
 * {@code BeaconBlockEntityMixin}. Each Beacon stores its own payment tier, so several Beacons on
 * the same pyramid each apply their own radius independently.
 *
 * <p>Tier mapping: 0 = Default / Iron (+0), 1 = Gold (+10), 2 = Emerald (+20),
 * 3 = Diamond (+30), 4 = Netherite (+50).</p>
 */
public interface BeaconRangeAccess {
	int beaconmorespace$getPaymentTier();

	void beaconmorespace$setPaymentTier(int tier);

	int beaconmorespace$getBonusRange();
}
