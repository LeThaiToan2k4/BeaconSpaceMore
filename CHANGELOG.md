# Changelog

## [1.0.0] - Fabric 26.3

### Overview
- Upgraded **Beacon More Space** to **Minecraft 26.3 (Fabric)**.
- Beacon effect range scales with the payment item used when confirming effects in the Beacon GUI.

### Features
- **Material-Scaled Beacon Range:**
  - **Iron Ingot:** `+0` blocks (`20` / `30` / `40` / `50` blocks at Pyramid Levels 1-4)
  - **Gold Ingot:** `+10` blocks (`30` / `40` / `50` / `60` blocks at Pyramid Levels 1-4)
  - **Emerald:** `+20` blocks (`40` / `50` / `60` / `70` blocks at Pyramid Levels 1-4)
  - **Diamond:** `+30` blocks (`50` / `60` / `70` / `80` blocks at Pyramid Levels 1-4)
  - **Netherite Ingot:** `+50` blocks (`70` / `80` / `90` / `100` blocks at Pyramid Levels 1-4)
- **Independent Multi-Beacon Persistence:**
  - Each Beacon independently stores and persists its own payment tier in block entity data (`PaymentTier`).
  - Multiple Beacons sharing the same pyramid operate with their own individual effect radius.
- **Beacon GUI Indicator & Live Preview:**
  - Displays the active material tier and current effect radius inside the Secondary Power panel of the Beacon screen.
  - Shows a live **Preview Range** when placing a payment item in the payment slot before confirming.
  - Outlines the active tier icon in green and the previewed tier icon in yellow.
  - Includes a hover tooltip displaying the current pyramid level and the full range table across all payment tiers.
- **Multiplayer & Vanilla Compatibility:**
  - Server-side effect range calculation works with both modded and vanilla clients.
  - Modded clients automatically synchronize the active tier via an extended `ContainerData` and `CustomPacketPayload` handshake.

### Technical Changes (26.3)
- **Toolchain & Dependencies:**
  - Aligned target to **Minecraft 26.3** (unobfuscated Mojang mappings), **Fabric Loader `0.20.1`**, and **Fabric API `0.166.2+26.3`**.
  - Configured build toolchain for **Java 25**, **Gradle `9.5.1`**, and **`net.fabricmc.fabric-loom` `1.17.21`**.
- **BlockEntity Persistence:**
  - Uses `loadAdditional(ValueInput)` and `saveAdditional(ValueOutput)` in `BeaconBlockEntityMixin`.
- **GUI & 2D Matrix Rendering:**
  - Uses `GuiGraphicsExtractor` (`pose()`, `text()`, `outline()`, `setComponentTooltipForNextFrame()`) and `BeaconScreen#extractLabels` / `extractRenderState` in `BeaconRangeHud` and `BeaconScreenMixin`.
- **Networking:**
  - Uses `CustomPacketPayload` and `StreamCodec` registered via `PayloadTypeRegistry.clientboundPlay()` in `HandshakePayload`.
- **Menu & Effects:**
  - Captures payment items on `boolean BeaconMenu#updateEffects(Optional<Holder<MobEffect>>, Optional<Holder<MobEffect>>)` using `CallbackInfoReturnable<Boolean>` right before `PaymentSlot#remove(1)` (after `BeaconBlockEntity#validateEffects` passes).

### Requirements
- **Minecraft Java Edition:** `26.3`
- **Java:** `25` or newer
- **Fabric Loader:** `0.20.1` or newer
- **Fabric API:** `0.166.2+26.3` or newer
