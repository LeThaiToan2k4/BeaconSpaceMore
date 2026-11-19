package com.thaistoanf.beaconmorespace.mixin.client;

import com.thaistoanf.beaconmorespace.client.BeaconRangeHud;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BeaconMenu;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Draws the tier / range indicator and the hover tooltip on the vanilla Beacon screen. */
@SuppressWarnings("null")
@Mixin(BeaconScreen.class)
public abstract class BeaconScreenMixin extends AbstractContainerScreen<BeaconMenu> {
	private BeaconScreenMixin(BeaconMenu handler, Inventory inventory, Component title) {
		super(handler, inventory, title);
	}

	@Inject(method = "extractLabels", at = @At("TAIL"))
	private void beaconmorespace$drawRangeIndicator(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, CallbackInfo ci) {
		BeaconRangeHud.drawForeground(context, this.font, this.menu);
	}

	@Override
	public void extractRenderState(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
		super.extractRenderState(context, mouseX, mouseY, deltaTicks);
		BeaconRangeHud.drawTooltip(context, this.font, this.menu, this.leftPos, this.topPos, mouseX, mouseY);
	}
}
