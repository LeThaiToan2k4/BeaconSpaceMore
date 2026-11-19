package com.thaistoanf.beaconmorespace.client;

import com.thaistoanf.beaconmorespace.access.BeaconScreenHandlerAccess;
import com.thaistoanf.beaconmorespace.range.BeaconRange;
import com.thaistoanf.beaconmorespace.range.PaymentTier;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Renders the Beacon More Space indicator inside the vanilla Beacon GUI.
 *
 * <p>The text sits in the empty lower half of the "Secondary Power" panel (GUI-relative area
 * x 118-219, y 71-96). Each line is scaled down to fit the panel width, and hovering the area
 * shows a full-size tooltip with every tier's range for the current pyramid level. The payment
 * item icons along the bottom are outlined: green for the active tier, yellow for the previewed
 * tier in the payment slot.</p>
 */
public final class BeaconRangeHud {
	// Info area inside the secondary power panel (GUI-relative coordinates).
	private static final int AREA_LEFT = 118;
	private static final int AREA_RIGHT = 219;
	private static final int AREA_TOP = 71;
	private static final int AREA_BOTTOM = 97;
	private static final int AREA_CENTER_X = 169;
	private static final int MAX_LINE_WIDTH = AREA_RIGHT - AREA_LEFT - 4;
	private static final float MAX_SCALE = 0.8F;
	private static final int LINE_SPACING = 8;

	// Positions of the payment item icons drawn by vanilla BeaconScreen#extractBackground.
	private static final int ICON_Y = 109;
	private static final int ICON_X_IRON = 108;
	private static final int ICON_X_GOLD = 86;
	private static final int ICON_X_DIAMOND = 63;
	private static final int ICON_X_EMERALD = 41;
	private static final int ICON_X_NETHERITE = 20;

	private static final int COLOR_LABEL = 0xFFE0E0E0;
	private static final int COLOR_RANGE = 0xFFFFFFFF;
	private static final int COLOR_INACTIVE = 0xFFA0A0A0;
	private static final int COLOR_PREVIEW = 0xFF7CFC9A;
	private static final int COLOR_ACTIVE_OUTLINE = 0xFF3EE07A;
	private static final int COLOR_PREVIEW_OUTLINE = 0xFFFFD83D;

	private BeaconRangeHud() {
	}

	/** Called at the end of {@code BeaconScreen#extractLabels} (matrix already at GUI origin). */
	public static void drawForeground(@NonNull GuiGraphicsExtractor context, @NonNull Font textRenderer, @NonNull BeaconMenu handler) {
		int syncedTier = ((BeaconScreenHandlerAccess) handler).beaconmorespace$getSyncedTier();
		if (syncedTier < 0) {
			// Server without Beacon More Space: nothing reliable to show.
			return;
		}

		PaymentTier tier = PaymentTier.byIndex(syncedTier);
		int level = handler.getLevels();
		PaymentTier preview = getPreviewTier(handler);

		int y = AREA_TOP + 2;
		drawCenteredScaled(context, textRenderer, tierLine(tier), y, COLOR_LABEL);
		y += LINE_SPACING;

		if (level > 0) {
			drawCenteredScaled(context, textRenderer, rangeLine(level, tier), y, COLOR_RANGE);
		} else {
			drawCenteredScaled(context, textRenderer, Component.translatable("beaconmorespace.gui.range_inactive"), y, COLOR_INACTIVE);
		}
		y += LINE_SPACING;

		if (preview != null) {
			drawCenteredScaled(context, textRenderer, previewLine(level, preview), y, COLOR_PREVIEW);
		}

		drawIconOutline(context, tier, COLOR_ACTIVE_OUTLINE);
		if (preview != null && preview != tier) {
			drawIconOutline(context, preview, COLOR_PREVIEW_OUTLINE);
		}
	}

	/** Called at the end of {@code BeaconScreen#extractRenderState} with absolute screen coordinates. */
	public static void drawTooltip(
			@NonNull GuiGraphicsExtractor context, @NonNull Font textRenderer, @NonNull BeaconMenu handler,
			int guiX, int guiY, int mouseX, int mouseY
	) {
		int syncedTier = ((BeaconScreenHandlerAccess) handler).beaconmorespace$getSyncedTier();
		if (syncedTier < 0) {
			return;
		}

		int relX = mouseX - guiX;
		int relY = mouseY - guiY;
		if (relX < AREA_LEFT || relX > AREA_RIGHT || relY < AREA_TOP || relY > AREA_BOTTOM) {
			return;
		}

		PaymentTier tier = PaymentTier.byIndex(syncedTier);
		int level = handler.getLevels();
		PaymentTier preview = getPreviewTier(handler);

		List<@NonNull Component> lines = new ArrayList<>();
		lines.add(Component.translatable("beaconmorespace.gui.title").withStyle(ChatFormatting.GOLD));
		lines.add(tierLine(tier));
		lines.add(Component.translatable("beaconmorespace.gui.level", level).withStyle(ChatFormatting.GRAY));
		if (level > 0) {
			lines.add(rangeLine(level, tier));
		} else {
			lines.add(Component.translatable("beaconmorespace.gui.range_inactive").withStyle(ChatFormatting.GRAY));
		}
		if (preview != null) {
			lines.add(previewLine(level, preview).withStyle(ChatFormatting.GREEN));
		}

		if (level > 0) {
			lines.add(Component.empty());
			lines.add(Component.translatable("beaconmorespace.gui.table_header", level).withStyle(ChatFormatting.GRAY));
			for (PaymentTier entry : PaymentTier.values()) {
				MutableComponent line = Component.translatable(
						"beaconmorespace.gui.table_entry",
						Component.translatable(entry.translationKey()).withColor(entry.color()),
						BeaconRange.totalRange(level, entry),
						entry.bonusRange()
				);
				lines.add(entry == tier ? line.withStyle(ChatFormatting.WHITE) : line.withStyle(ChatFormatting.DARK_GRAY));
			}
		}

		context.setComponentTooltipForNextFrame(textRenderer, lines, mouseX, mouseY);
	}

	private static @Nullable PaymentTier getPreviewTier(BeaconMenu handler) {
		ItemStack stack = handler.getSlot(0).getItem();
		return stack.isEmpty() ? null : PaymentTier.fromStack(stack);
	}

	private static @NonNull MutableComponent tierLine(PaymentTier tier) {
		return Component.translatable(
				"beaconmorespace.gui.tier",
				Component.translatable(tier.translationKey()).withColor(tier.color())
		);
	}

	private static @NonNull MutableComponent rangeLine(int level, PaymentTier tier) {
		return Component.translatable("beaconmorespace.gui.range", BeaconRange.totalRange(level, tier), tier.bonusRange());
	}

	private static @NonNull MutableComponent previewLine(int level, PaymentTier preview) {
		if (level > 0) {
			return Component.translatable("beaconmorespace.gui.preview", BeaconRange.totalRange(level, preview), preview.bonusRange());
		}
		return Component.translatable("beaconmorespace.gui.preview_inactive", preview.bonusRange());
	}

	private static void drawCenteredScaled(@NonNull GuiGraphicsExtractor context, @NonNull Font textRenderer, @NonNull Component text, int y, int color) {
		int width = textRenderer.width(text);
		if (width <= 0) {
			return;
		}
		float scale = Math.min(MAX_SCALE, MAX_LINE_WIDTH / (float) width);

		Matrix3x2fStack matrices = context.pose();
		matrices.pushMatrix();
		matrices.translate(AREA_CENTER_X, y);
		matrices.scale(scale, scale);
		context.text(textRenderer, text, -width / 2, 0, color);
		matrices.popMatrix();
	}

	private static void drawIconOutline(GuiGraphicsExtractor context, PaymentTier tier, int color) {
		int x = switch (tier) {
			case IRON -> ICON_X_IRON;
			case GOLD -> ICON_X_GOLD;
			case EMERALD -> ICON_X_EMERALD;
			case DIAMOND -> ICON_X_DIAMOND;
			case NETHERITE -> ICON_X_NETHERITE;
		};
		context.outline(x - 1, ICON_Y - 1, 18, 18, color);
	}
}
