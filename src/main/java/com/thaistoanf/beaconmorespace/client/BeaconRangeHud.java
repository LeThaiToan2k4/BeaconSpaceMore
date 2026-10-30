package com.thaistoanf.beaconmorespace.client;

import com.thaistoanf.beaconmorespace.access.BeaconScreenHandlerAccess;
import com.thaistoanf.beaconmorespace.range.BeaconRange;
import com.thaistoanf.beaconmorespace.range.PaymentTier;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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

	// Positions of the payment item icons drawn by vanilla BeaconScreen#drawBackground.
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

	/** Called at the end of {@code BeaconScreen#drawForeground} (matrix already at GUI origin). */
	public static void drawForeground(DrawContext context, TextRenderer textRenderer, BeaconScreenHandler handler) {
		int syncedTier = ((BeaconScreenHandlerAccess) handler).beaconmorespace$getSyncedTier();
		if (syncedTier < 0) {
			// Server without Beacon More Space: nothing reliable to show.
			return;
		}

		PaymentTier tier = PaymentTier.byIndex(syncedTier);
		int level = handler.getProperties();
		PaymentTier preview = getPreviewTier(handler);

		int y = AREA_TOP + 2;
		drawCenteredScaled(context, textRenderer, tierLine(tier), y, COLOR_LABEL);
		y += LINE_SPACING;

		if (level > 0) {
			drawCenteredScaled(context, textRenderer, rangeLine(level, tier), y, COLOR_RANGE);
		} else {
			drawCenteredScaled(context, textRenderer, Text.translatable("beaconmorespace.gui.range_inactive"), y, COLOR_INACTIVE);
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

	/** Called at the end of {@code BeaconScreen#render} with absolute screen coordinates. */
	public static void drawTooltip(
			DrawContext context, TextRenderer textRenderer, BeaconScreenHandler handler,
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
		int level = handler.getProperties();
		PaymentTier preview = getPreviewTier(handler);

		List<Text> lines = new ArrayList<>();
		lines.add(Text.translatable("beaconmorespace.gui.title").formatted(Formatting.GOLD));
		lines.add(tierLine(tier));
		lines.add(Text.translatable("beaconmorespace.gui.level", level).formatted(Formatting.GRAY));
		if (level > 0) {
			lines.add(rangeLine(level, tier));
		} else {
			lines.add(Text.translatable("beaconmorespace.gui.range_inactive").formatted(Formatting.GRAY));
		}
		if (preview != null) {
			lines.add(previewLine(level, preview).formatted(Formatting.GREEN));
		}

		if (level > 0) {
			lines.add(Text.empty());
			lines.add(Text.translatable("beaconmorespace.gui.table_header", level).formatted(Formatting.GRAY));
			for (PaymentTier entry : PaymentTier.values()) {
				MutableText line = Text.translatable(
						"beaconmorespace.gui.table_entry",
						Text.translatable(entry.translationKey()).styled(style -> style.withColor(entry.color())),
						BeaconRange.totalRange(level, entry),
						entry.bonusRange()
				);
				lines.add(entry == tier ? line.formatted(Formatting.WHITE) : line.formatted(Formatting.DARK_GRAY));
			}
		}

		context.drawTooltip(textRenderer, lines, mouseX, mouseY);
	}

	private static PaymentTier getPreviewTier(BeaconScreenHandler handler) {
		ItemStack stack = handler.getSlot(0).getStack();
		return stack.isEmpty() ? null : PaymentTier.fromStack(stack);
	}

	private static MutableText tierLine(PaymentTier tier) {
		return Text.translatable(
				"beaconmorespace.gui.tier",
				Text.translatable(tier.translationKey()).styled(style -> style.withColor(tier.color()))
		);
	}

	private static MutableText rangeLine(int level, PaymentTier tier) {
		return Text.translatable("beaconmorespace.gui.range", BeaconRange.totalRange(level, tier), tier.bonusRange());
	}

	private static MutableText previewLine(int level, PaymentTier preview) {
		if (level > 0) {
			return Text.translatable("beaconmorespace.gui.preview", BeaconRange.totalRange(level, preview), preview.bonusRange());
		}
		return Text.translatable("beaconmorespace.gui.preview_inactive", preview.bonusRange());
	}

	private static void drawCenteredScaled(DrawContext context, TextRenderer textRenderer, Text text, int y, int color) {
		int width = textRenderer.getWidth(text);
		if (width <= 0) {
			return;
		}
		float scale = Math.min(MAX_SCALE, MAX_LINE_WIDTH / (float) width);

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(AREA_CENTER_X, y, 0.0F);
		matrices.scale(scale, scale, 1.0F);
		context.drawTextWithShadow(textRenderer, text, -width / 2, 0, color);
		matrices.pop();
	}

	private static void drawIconOutline(DrawContext context, PaymentTier tier, int color) {
		int x = switch (tier) {
			case IRON -> ICON_X_IRON;
			case GOLD -> ICON_X_GOLD;
			case EMERALD -> ICON_X_EMERALD;
			case DIAMOND -> ICON_X_DIAMOND;
			case NETHERITE -> ICON_X_NETHERITE;
		};
		context.drawBorder(x - 1, ICON_Y - 1, 18, 18, color);
	}
}
