package com.thaistoanf.beaconmorespace.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.thaistoanf.beaconmorespace.access.BeaconRangeAccess;
import com.thaistoanf.beaconmorespace.network.HandshakePayload;
import com.thaistoanf.beaconmorespace.range.BeaconRange;
import com.thaistoanf.beaconmorespace.range.PaymentTier;
import com.thaistoanf.beaconmorespace.screen.ExtendedBeaconPropertyDelegate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Stores a per-Beacon payment tier, persists it, extends the effect radius in
 * {@code applyEffects}, and exposes the tier to modded clients through the screen handler.
 */
@SuppressWarnings("null")
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
			((BlockEntity) (Object) this).setChanged();
		}
	}

	@Override
	public int beaconmorespace$getBonusRange() {
		return PaymentTier.byIndex(this.beaconmorespace$paymentTier).bonusRange();
	}

	// ---------------------------------------------------------------------------------------------
	// Persistence
	// ---------------------------------------------------------------------------------------------

	@Inject(method = "loadAdditional", at = @At("TAIL"))
	private void beaconmorespace$readPaymentTier(ValueInput view, CallbackInfo ci) {
		this.beaconmorespace$paymentTier = PaymentTier.byIndex(view.getIntOr(PAYMENT_TIER_KEY, PaymentTier.IRON.index())).index();
	}

	@Inject(method = "saveAdditional", at = @At("TAIL"))
	private void beaconmorespace$writePaymentTier(ValueOutput view, CallbackInfo ci) {
		view.putInt(PAYMENT_TIER_KEY, this.beaconmorespace$paymentTier);
	}

	// ---------------------------------------------------------------------------------------------
	// Effect radius: double d = beaconLevel * 10 + 10;  ->  d + bonusRange
	// The vanilla box logic (inflate(d) on X/Y/Z, then expandTowards upwards by the world height) is kept.
	// ---------------------------------------------------------------------------------------------

	@ModifyVariable(method = "applyEffects", at = @At("STORE"), ordinal = 0)
	private static double beaconmorespace$addBonusRange(
			double radius,
			@Local(argsOnly = true) Level world,
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
					target = "Lnet/minecraft/world/inventory/BeaconMenu;<init>(ILnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V"
			),
			index = 2
	)
	private ContainerData beaconmorespace$extendPropertyDelegate(
			ContainerData original,
			@Local(argsOnly = true) Player player
	) {
		if (player instanceof ServerPlayer serverPlayer && ServerPlayNetworking.canSend(serverPlayer, HandshakePayload.ID)) {
			return new ExtendedBeaconPropertyDelegate(original, this);
		}
		return original;
	}
}
