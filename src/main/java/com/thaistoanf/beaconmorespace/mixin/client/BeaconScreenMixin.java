package com.thaistoanf.beaconmorespace.mixin.client;

import com.thaistoanf.beaconmorespace.client.BeaconRangeHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Draws the tier / range indicator and the hover tooltip on the vanilla Beacon screen. */
@Mixin(BeaconScreen.class)
public abstract class BeaconScreenMixin extends HandledScreen<BeaconScreenHandler> {
	private BeaconScreenMixin(BeaconScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Inject(method = "drawForeground", at = @At("TAIL"))
	private void beaconmorespace$drawRangeIndicator(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
		BeaconRangeHud.drawForeground(context, this.textRenderer, this.handler);
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void beaconmorespace$drawRangeTooltip(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
		BeaconRangeHud.drawTooltip(context, this.textRenderer, this.handler, this.x, this.y, mouseX, mouseY);
	}
}
