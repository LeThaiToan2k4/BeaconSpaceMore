package com.thaistoanf.beaconmorespace.range;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Payment item tiers and the bonus range each one grants to a Beacon.
 *
 * <p>The {@link #index()} value is what gets persisted in the Beacon block entity data and
 * synchronized to the client.</p>
 */
public enum PaymentTier {
	IRON(0, 0, "iron", 0xD8D8D8, Items.IRON_INGOT),
	GOLD(1, 10, "gold", 0xFFD83D, Items.GOLD_INGOT),
	EMERALD(2, 20, "emerald", 0x3EE07A, Items.EMERALD),
	DIAMOND(3, 30, "diamond", 0x5FE6E0, Items.DIAMOND),
	NETHERITE(4, 50, "netherite", 0xC29A9A, Items.NETHERITE_INGOT);

	private static final PaymentTier[] BY_INDEX = values();

	private final int index;
	private final int bonusRange;
	private final String name;
	private final int color;
	private final Item item;

	PaymentTier(int index, int bonusRange, String name, int color, Item item) {
		this.index = index;
		this.bonusRange = bonusRange;
		this.name = name;
		this.color = color;
		this.item = item;
	}

	/** Persisted / synchronized tier id (0 = Iron ... 4 = Netherite). */
	public int index() {
		return this.index;
	}

	/** Extra blocks added on top of the vanilla {@code level * 10 + 10} radius. */
	public int bonusRange() {
		return this.bonusRange;
	}

	/** RGB color (no alpha) used when rendering the tier name. */
	public int color() {
		return this.color;
	}

	/** The payment item that unlocks this tier. */
	public Item item() {
		return this.item;
	}

	public String translationKey() {
		return "beaconmorespace.tier." + this.name;
	}

	/** Returns the tier for an index, clamping invalid values to {@link #IRON}. */
	public static PaymentTier byIndex(int index) {
		return index >= 0 && index < BY_INDEX.length ? BY_INDEX[index] : IRON;
	}

	/**
	 * Determines the tier of a payment stack. Unknown payment items (for example ones added to the
	 * {@code beacon_payment_items} tag by a data pack) fall back to {@link #IRON}.
	 */
	public static PaymentTier fromStack(ItemStack stack) {
		for (PaymentTier tier : BY_INDEX) {
			if (stack.isOf(tier.item)) {
				return tier;
			}
		}
		return IRON;
	}
}
