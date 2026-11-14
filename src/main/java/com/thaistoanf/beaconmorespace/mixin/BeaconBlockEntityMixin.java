package com.thaistoanf.beaconmorespace.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.thaistoanf.beaconmorespace.access.BeaconRangeAccess;
import com.thaistoanf.beaconmorespace.network.HandshakePayload;
import com.thaistoanf.beaconmorespace.range.BeaconRange;
import com.thaistoanf.beaconmorespace.range.PaymentTier;
import com.thaistoanf.beaconmorespace.screen.ExtendedBeaconPropertyDelegate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Stores a per-Beacon payment tier, persists it, extends the effect radius in
 * {@code applyPlayerEffects}, and exposes the tier to modded clients through the screen handler.
 */
@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin implements BeaconRangeAccess {
	@Unique
	private static final String PAYMENT_TIER_KEY = "PaymentTier";

	@Unique
	private int beaconmorespace$paymentTier = PaymentTier.IRON.index();

	@Override
	public int beaconmorespace$getPaymentTier() {
		return this.beaconmorespace$paymentTier;
	}

	@Override
	public void beaconmorespace$setPaymentTier(int tier) {
		int sanitized = PaymentTier.byIndex(tier).index();
		if (sanitized != this.beaconmorespace$paymentTier) {
			this.beaconmorespace$paymentTier = sanitized;
			((BlockEntity) (Object) this).markDirty();
		}
	}

	@Override
	public int beaconmorespace$getBonusRange() {
		return PaymentTier.byIndex(this.beaconmorespace$paymentTier).bonusRange();
	}

	// ---------------------------------------------------------------------------------------------
	// Persistence
	// ---------------------------------------------------------------------------------------------

	@Inject(method = "readData", at = @At("TAIL"))
	private void beaconmorespace$readPaymentTier(ReadView view, CallbackInfo ci) {
		this.beaconmorespace$paymentTier = PaymentTier.byIndex(view.getInt(PAYMENT_TIER_KEY, PaymentTier.IRON.index())).index();
	}

	@Inject(method = "writeData", at = @At("TAIL"))
	private void beaconmorespace$writePaymentTier(WriteView view, CallbackInfo ci) {
		view.putInt(PAYMENT_TIER_KEY, this.beaconmorespace$paymentTier);
	}

	// ---------------------------------------------------------------------------------------------
	// Effect radius: double d = beaconLevel * 10 + 10;  ->  d + bonusRange
	// The vanilla box logic (expand(d) on X/Y/Z, then stretch upwards by the world height) is kept.
	// ---------------------------------------------------------------------------------------------

	@ModifyVariable(method = "applyPlayerEffects", at = @At("STORE"), ordinal = 0)
	private static double beaconmorespace$addBonusRange(
			double radius,
			@Local(argsOnly = true) World world,
			@Local(argsOnly = true) BlockPos pos
	) {
		return radius + BeaconRange.bonusRangeAt(world, pos);
	}

	// ---------------------------------------------------------------------------------------------
	// Client sync: append the tier as a 4th screen handler property for clients that have the mod.
	// ---------------------------------------------------------------------------------------------

	@ModifyArg(
			method = "createMenu",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/screen/BeaconScreenHandler;<init>(ILnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;Lnet/minecraft/screen/ScreenHandlerContext;)V"
			),
			index = 2
	)
	private PropertyDelegate beaconmorespace$extendPropertyDelegate(
			PropertyDelegate original,
			@Local(argsOnly = true) PlayerEntity player
	) {
		if (player instanceof ServerPlayerEntity serverPlayer && ServerPlayNetworking.canSend(serverPlayer, HandshakePayload.ID)) {
			return new ExtendedBeaconPropertyDelegate(original, this);
		}
		return original;
	}
}
