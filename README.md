# Phone Inspector Pro 📱🔍

[![Android](https://img.shields.io/badge/Android-8.0%2B-green)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-blue)](https://developer.android.com/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

**Phone Inspector Pro** is a comprehensive, modern Android hardware & system diagnostic tool designed for inspecting pre-owned or new mobile devices. It provides complete technical insights into battery health, processor architecture, display performance, camera sensors, root status, memory/storage specs, hardware sensor states, and enables instant PDF report generation for resale inspection.

---

## ✨ Key Features & Modules

### 📊 System Dashboard & Health Score
- **Android System Specs**: Android version, API level, kernel version, security patch level, and system build fingerprints.
- **Root & Security Inspection**: Multi-point root detection (su binary checks, test-keys build tags, Magisk/SuperSU paths) and bootloader lock state verification.
- **Overall Device Health Index**: Automated diagnostic score based on battery state, RAM usage, storage health, and sensor tests.

### 🔋 Battery Diagnostics
- **Health & Capacity**: Charge percentage, health status (Good, Overheat, Dead, Overvoltage), technology (Li-ion/Li-poly), and voltage/temperature monitoring.
- **Charging State & Thermal Monitoring**: Live voltage (mV) and thermal status tracking with safety color alerts.

### 🧠 Processor & Graphics (CPU & GPU)
- **CPU Architecture & Core Breakdown**: Chipset model (Qualcomm Snapdragon, MediaTek Dimensity, Google Tensor, Exynos), core count, hardware platform, and architecture (ARM64-v8a / ABI).
- **GPU Info & Driver Capabilities**: GPU renderer (Adreno, Mali, Immortalis), vendor specs, and OpenGL ES version verification.

### 🖥️ Display Metrics
- **Screen Specs**: Resolution (pixels), screen density (DPI/PPI), refresh rate (Hz), logical density scale, and multi-touch capabilities.
- **Hardware Display Tests**: Interactive touch grid visualizer, color uniformity panel, and multi-touch point detector.

### 📸 Camera Hardware Inspector
- **Rear & Front Camera Specs**: Megapixel resolution, lens aperture ($f$-number), focal length, field of view (FOV), and supported hardware feature flags.
- **Advanced Capabilities**: Auto-focus support, flash availability, video recording modes, and optical/electronic image stabilization flags.

### 💾 Memory & Storage Inspection
- **RAM Analytics**: Total system RAM, currently used RAM, free RAM, and low-memory warning indicators.
- **Internal Storage**: Internal ROM capacity, available storage, and usage breakdown.

### 📡 Network, DRM & Security
- **Connectivity**: Wi-Fi status, Bluetooth capability, cellular network type, IP address details, and NFC presence.
- **Media DRM & Widevine**: DRM Widevine Security Level verification (L1/L3) for high-definition streaming support.
- **Biometrics & Sensors**: Fingerprint hardware status, face lock capability, and live telemetry for Accelerometer, Gyroscope, Magnetometer, Light, and Proximity sensors.

### 📄 Inspection PDF Report Export
- **Export Diagnostic Certificate**: One-click generation of a cleanly formatted PDF diagnostic summary report containing complete device telemetry, timestamp, and health checklist.
- **Android Native Sharing**: Native file sharing intent support to export reports directly to WhatsApp, Gmail, Drive, or print.

---

## 🛠️ Architecture & Tech Stack

- **Language:** Kotlin 2.0+
- **UI Framework:** Jetpack Compose with Material Design 3
- **Architecture Pattern:** MVVM (Model-View-ViewModel) + Coroutines & Flow
- **Data Persistence:** Android Room Database + KSP
- **Design Theme:** Material Design 3 Polish Theme with adaptive dark/light mode and 24dp elevated rounded cards
- **Target SDK:** 34 / Min SDK: 26 (Android 8.0+)

---

## 🔒 Permissions & Security Declarations

| Permission | Purpose |
| :--- | :--- |
| `CAMERA` | Camera sensor specs and hardware capability detection |
| `READ_PHONE_STATE` | Phone model specs & hardware identification |
| `ACCESS_NETWORK_STATE` | Wi-Fi and network connectivity diagnostic checks |
| `ACCESS_WIFI_STATE` | Detailed Wi-Fi module status |
| `BATTERY_STATS` | Battery health and charge cycle diagnostic stats |

---

## 🚀 How to Build & Export

### Generating the APK in Google AI Studio
1. In the **Google AI Studio** workspace header / top bar, click **Settings** or **Export**.
2. Select **Generate APK / AAB** or **Push to GitHub**.
3. Download the compiled universal `app-debug.apk` or `app-release.apk` directly to your Android device or system.

### Building from Source in Android Studio
1. Clone or export the repository structure:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Phone-Inspector-Pro.git
   ```
2. Open the project in **Android Studio Jellyfish / Ladybug (2024.1+)**.
3. Allow Gradle to sync dependencies.
4. Navigate to **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. Locate the generated APK under `app/build/outputs/apk/debug/` or `app/build/outputs/apk/release/`.

---

## 📄 License

This project is licensed under the **MIT License** - see the `LICENSE` file for details.

*Disclaimer: Phone Inspector Pro is intended for diagnostic and information purposes. Sensor availability and specific battery cycle counters depend on manufacturer hardware HAL capabilities.*
