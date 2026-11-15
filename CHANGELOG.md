# Changelog

## [1.0.0] - Fabric 1.21.8

### Overview
- Upgraded **Beacon More Space** to **Minecraft 1.21.8 (Fabric)**.
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
  - Modded clients automatically synchronize the active tier via an extended `PropertyDelegate` and `CustomPayload` handshake.

### Technical Changes (1.21.8)
- **Toolchain & Dependencies:**
  - Aligned target to **Minecraft 1.21.8**, **Yarn `1.21.8+build.1`**, **Fabric Loader `0.20.1`**, and **Fabric API `0.136.1+1.21.8`**.
  - Configured build toolchain for **Java 21**, **Gradle `8.12`**, and **Fabric Loom `1.10-SNAPSHOT`**.
- **BlockEntity Persistence:**
  - Uses `readData(ReadView)` and `writeData(WriteView)` in `BeaconBlockEntityMixin`.
- **GUI & 2D Matrix Rendering:**
  - Uses JOML 2D `Matrix3x2fStack` (`pushMatrix`, `translate`, `scale`, `popMatrix`) via `DrawContext#getMatrices()` in `BeaconRangeHud`.
- **Networking:**
  - Uses `CustomPayload` and `PacketCodec` registered via `PayloadTypeRegistry.playS2C()` in `HandshakePayload`.
- **Screen Handler & Effects:**
  - Captures payment items on `setEffects(Optional<RegistryEntry<StatusEffect>>, Optional<RegistryEntry<StatusEffect>>)`.

### Requirements
- **Minecraft Java Edition:** `1.21.8`
- **Java:** `21` or newer
- **Fabric Loader:** `0.20.1` or newer
- **Fabric API:** `0.136.1+1.21.8` or newer
