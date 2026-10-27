package com.thaistoanf.beaconmorespace.screen;

import com.thaistoanf.beaconmorespace.access.BeaconRangeAccess;
import net.minecraft.screen.PropertyDelegate;

/**
 * Wraps the vanilla Beacon {@link PropertyDelegate} (level, primary, secondary) and appends one
 * extra property holding the payment tier, so it is synchronized to the client automatically by
 * the screen handler property system.
 *
 * <p>The tier is encoded as {@code tier + 1} so that a value of {@code 0} on the client means
 * "not synchronized" (server without the mod).</p>
 */
public final class ExtendedBeaconPropertyDelegate implements PropertyDelegate {
	public static final int VANILLA_SIZE = 3;
	public static final int TIER_INDEX = 3;
	public static final int SIZE = 4;

	private final PropertyDelegate vanilla;
	private final BeaconRangeAccess beacon;

	public ExtendedBeaconPropertyDelegate(PropertyDelegate vanilla, BeaconRangeAccess beacon) {
		this.vanilla = vanilla;
		this.beacon = beacon;
	}

	@Override
	public int get(int index) {
		if (index == TIER_INDEX) {
			return this.beacon.beaconmorespace$getPaymentTier() + 1;
		}
		return this.vanilla.get(index);
	}

	@Override
	public void set(int index, int value) {
		if (index == TIER_INDEX) {
			if (value > 0) {
				this.beacon.beaconmorespace$setPaymentTier(value - 1);
			}
			return;
		}
		this.vanilla.set(index, value);
	}

	@Override
	public int size() {
		return SIZE;
	}
}
