package com.example.data.model

data class DiagnosticData(
    val timestamp: Long = System.currentTimeMillis(),
    val system: SystemInfo,
    val battery: BatteryInfo,
    val cpu: CpuInfo,
    val gpu: GpuInfo,
    val memoryStorage: MemoryStorageInfo,
    val display: DisplayInfo,
    val camera: CameraInfo,
    val sensors: SensorSuiteInfo,
    val network: NetworkInfo,
    val additional: AdditionalInfo,
    val authenticity: AuthenticityReport
)

data class SystemInfo(
    val brand: String,
    val model: String,
    val manufacturer: String,
    val deviceName: String,
    val androidVersion: String,
    val apiLevel: Int,
    val securityPatch: String,
    val buildNumber: String,
    val kernelVersion: String,
    val isRooted: Boolean,
    val rootCheckDetails: String,
    val bootloaderStatus: String,
    val selinuxStatus: String,
    val buildDate: String
)

data class BatteryInfo(
    val levelPercentage: Int,
    val healthStatus: String, // Good, Overheat, Dead, Overvoltage, Unspecified
    val isPlugged: Boolean,
    val plugType: String,
    val estimatedCapacityMah: Int,
    val designCapacityMah: Int,
    val cycleCount: Int, // if reported or estimated
    val temperatureCelsius: Float,
    val voltageMv: Int,
    val technology: String
)

data class CpuCoreInfo(
    val coreIndex: Int,
    val minFreqKhz: Long,
    val maxFreqKhz: Long,
    val curFreqKhz: Long,
    val isOnline: Boolean
)

data class CpuInfo(
    val socName: String,
    val model: String,
    val manufacturer: String,
    val architecture: String,
    val totalCores: Int,
    val governor: String,
    val cores: List<CpuCoreInfo>,
    val processNanometer: String,
    val cpuTemperatureCelsius: Float
)

data class GpuInfo(
    val renderer: String,
    val vendor: String,
    val clockFrequencyMhz: Int,
    val openGlEsVersion: String,
    val vulkanSupported: Boolean,
    val vulkanVersion: String
)

data class MemoryStorageInfo(
    val totalRamBytes: Long,
    val availableRamBytes: Long,
    val ramType: String, // LPDDR4X / LPDDR5
    val totalInternalStorageBytes: Long,
    val availableInternalStorageBytes: Long,
    val storageType: String, // UFS 3.1 / UFS 4.0 / eMMC
    val hasSdCard: Boolean,
    val totalSdCardBytes: Long,
    val availableSdCardBytes: Long
)

data class DisplayInfo(
    val resolutionWidthPx: Int,
    val resolutionHeightPx: Int,
    val densityDpi: Int,
    val densityBucket: String,
    val currentRefreshRateHz: Float,
    val supportedRefreshRatesHz: List<Float>,
    val displayType: String,
    val screenSizeInches: Double,
    val hdrSupport: List<String>,
    val brightnessPercent: Int,
    val isAutoBrightness: Boolean
)

data class CameraDetail(
    val facing: String, // Rear or Front
    val megapixels: Double,
    val resolutionPx: String,
    val aperture: String,
    val focalLengthMm: String,
    val maxVideoResolution: String,
    val hasOis: Boolean,
    val hasFlash: Boolean,
    val features: List<String>
)

data class CameraInfo(
    val cameras: List<CameraDetail>
)

data class SensorItem(
    val name: String,
    val type: Int,
    val vendor: String,
    val powerMa: Float,
    val maxRange: Float,
    val resolution: Float,
    val isAvailable: Boolean
)

data class SensorSuiteInfo(
    val totalSensors: Int,
    val sensorList: List<SensorItem>,
    val fingerprintType: String,
    val hasFaceUnlock: Boolean,
    val hasNfc: Boolean,
    val hasBarometer: Boolean,
    val hasGyroscope: Boolean,
    val hasAccelerometer: Boolean,
    val hasMagnetometer: Boolean,
    val hasProximity: Boolean,
    val hasAmbientLight: Boolean
)

data class NetworkInfo(
    val wifiVersion: String,
    val wifiState: String,
    val bluetoothVersion: String,
    val is5gSupported: Boolean,
    val cellularBands: String,
    val dualSimSupport: Boolean,
    val nfcAvailable: Boolean,
    val gpsCapabilities: List<String>
)

data class AdditionalInfo(
    val ipRatingEstimate: String,
    val speakerConfig: String,
    val audioDacInfo: String,
    val maxChargingWattageEstimate: String,
    val wirelessChargingSupport: Boolean,
    val widevineDrmLevel: String
)

data class AuthenticityReport(
    val overallScore: Int, // 0 to 100
    val ratingTitle: String, // "Excellent Authenticity", "Good Condition", "Suspicious / Modified"
    val playProtectStatus: String,
    val serialImeiGuidance: String,
    val counterfeitRiskFlags: List<String>,
    val passChecklist: List<String>,
    val warningChecklist: List<String>,
    val buyerSummaryText: String
)
