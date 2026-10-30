package com.thaistoanf.beaconmorespace.mixin;

import com.thaistoanf.beaconmorespace.access.BeaconRangeAccess;
import com.thaistoanf.beaconmorespace.access.BeaconScreenHandlerAccess;
import com.thaistoanf.beaconmorespace.range.PaymentTier;
import com.thaistoanf.beaconmorespace.screen.ExtendedBeaconPropertyDelegate;
import java.util.Optional;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Captures the payment item when the player confirms new effects, and gives the client-side
 * screen handler room for the extra synchronized tier property.
 */
@Mixin(BeaconScreenHandler.class)
public abstract class BeaconScreenHandlerMixin implements BeaconScreenHandlerAccess {
	@Shadow
	@Final
	private Inventory payment;

	@Shadow
	@Final
	private ScreenHandlerContext context;

	@Shadow
	@Final
	private PropertyDelegate propertyDelegate;

	/**
	 * Client constructor: {@code new ArrayPropertyDelegate(3)} becomes {@code new ArrayPropertyDelegate(4)}
	 * so the tier property sent by a modded server has a slot to land in.
	 */
	@ModifyConstant(method = "<init>(ILnet/minecraft/inventory/Inventory;)V", constant = @Constant(intValue = 3))
	private static int beaconmorespace$expandClientProperties(int original) {
		return ExtendedBeaconPropertyDelegate.SIZE;
	}

	/**
	 * Runs before {@code this.paymentSlot.takeStack(1)} consumes the item, so the payment stack is
	 * still available to inspect.
	 */
	@Inject(method = "setEffects", at = @At("HEAD"))
	private void beaconmorespace$capturePaymentTier(
			Optional<StatusEffect> primary,
			Optional<StatusEffect> secondary,
			CallbackInfo ci
	) {
		ItemStack stack = this.payment.getStack(0);
		if (stack.isEmpty()) {
			return;
		}

		PaymentTier tier = PaymentTier.fromStack(stack);
		this.context.run((world, pos) -> {
			if (world.getBlockEntity(pos) instanceof BeaconRangeAccess beacon) {
				beacon.beaconmorespace$setPaymentTier(tier.index());
			}
		});
	}

	@Override
	public int beaconmorespace$getSyncedTier() {
		if (this.propertyDelegate.size() <= ExtendedBeaconPropertyDelegate.TIER_INDEX) {
			return -1;
		}
		int raw = this.propertyDelegate.get(ExtendedBeaconPropertyDelegate.TIER_INDEX);
		return raw <= 0 ? -1 : PaymentTier.byIndex(raw - 1).index();
	}
}
