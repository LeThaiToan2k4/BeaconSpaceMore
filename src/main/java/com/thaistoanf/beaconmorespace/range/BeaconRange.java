package com.thaistoanf.beaconmorespace.range;

import com.thaistoanf.beaconmorespace.access.BeaconRangeAccess;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Range formulas shared by the server logic and the client GUI. */
public final class BeaconRange {
	private BeaconRange() {
	}

	/** Vanilla radius: Level 1 = 20, Level 2 = 30, Level 3 = 40, Level 4 = 50. */
	public static int baseRange(int beaconLevel) {
		return beaconLevel * 10 + 10;
	}

	/** Total radius {@code R = baseRange + bonusRange}. */
	public static int totalRange(int beaconLevel, PaymentTier tier) {
		return baseRange(beaconLevel) + tier.bonusRange();
	}

	/** Looks up the bonus range stored on the Beacon at {@code pos}, or 0 if there is none. */
	public static int bonusRangeAt(World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BeaconRangeAccess access) {
			return access.beaconmorespace$getBonusRange();
		}
		return 0;
	}
}
