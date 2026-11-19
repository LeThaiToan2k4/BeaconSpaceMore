package com.thaistoanf.beaconmorespace.mixin;

import com.thaistoanf.beaconmorespace.access.BeaconRangeAccess;
import com.thaistoanf.beaconmorespace.access.BeaconScreenHandlerAccess;
import com.thaistoanf.beaconmorespace.range.PaymentTier;
import com.thaistoanf.beaconmorespace.screen.ExtendedBeaconPropertyDelegate;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
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
@SuppressWarnings("null")
@Mixin(BeaconMenu.class)
public abstract class BeaconScreenHandlerMixin implements BeaconScreenHandlerAccess {
	@Shadow
	@Final
	private Container beacon;

	@Shadow
	@Final
	private ContainerLevelAccess access;

	@Shadow
	@Final
	private ContainerData beaconData;

	/**
	 * Client constructor: {@code new SimpleContainerData(3)} becomes {@code new SimpleContainerData(4)}
	 * so the tier property sent by a modded server has a slot to land in.
	 */
	@ModifyConstant(method = "<init>(ILnet/minecraft/world/Container;)V", constant = @Constant(intValue = 3))
	private static int beaconmorespace$expandClientProperties(int original) {
		return ExtendedBeaconPropertyDelegate.SIZE;
	}

	/**
	 * Runs before {@code this.paymentSlot.remove(1)} consumes the item, so the payment stack is
	 * still available to inspect.
	 */
	@Inject(method = "updateEffects", at = @At("HEAD"))
	private void beaconmorespace$capturePaymentTier(
			Optional<Holder<MobEffect>> primary,
			Optional<Holder<MobEffect>> secondary,
			CallbackInfo ci
	) {
		ItemStack stack = this.beacon.getItem(0);
		if (stack.isEmpty()) {
			return;
		}

		PaymentTier tier = PaymentTier.fromStack(stack);
		this.access.execute((world, pos) -> {
			if (world.getBlockEntity(pos) instanceof BeaconRangeAccess beaconEntity) {
				beaconEntity.beaconmorespace$setPaymentTier(tier.index());
			}
		});
	}

	@Override
	public int beaconmorespace$getSyncedTier() {
		if (this.beaconData.getCount() <= ExtendedBeaconPropertyDelegate.TIER_INDEX) {
			return -1;
		}
		int raw = this.beaconData.get(ExtendedBeaconPropertyDelegate.TIER_INDEX);
		return raw <= 0 ? -1 : PaymentTier.byIndex(raw - 1).index();
	}
}
