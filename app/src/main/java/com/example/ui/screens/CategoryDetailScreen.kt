package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiagnosticData
import com.example.ui.components.MetricCard
import com.example.ui.viewmodel.DiagnosticViewModel
import java.util.Locale

enum class CategoryFilter(val label: String) {
    ALL("All Specs"),
    SYSTEM("System"),
    BATTERY("Battery"),
    CPU_GPU("Processor & GPU"),
    MEMORY("Memory & Storage"),
    DISPLAY("Display"),
    CAMERA("Camera"),
    SENSORS("Sensors"),
    NETWORK("Network"),
    AUDIO_DRM("Audio & DRM")
}

@Composable
fun CategoryDetailScreen(
    viewModel: DiagnosticViewModel,
    modifier: Modifier = Modifier
) {
    val diagnosticData by viewModel.diagnosticData.collectAsState()
    var selectedFilter by remember { mutableStateOf(CategoryFilter.ALL) }

    val data = diagnosticData ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 12.dp)
    ) {
        // Filter Chips Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("category_filter_row")
        ) {
            items(CategoryFilter.values()) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(text = filter.label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("filter_chip_${filter.name.lowercase()}")
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. SYSTEM INFORMATION
            if (selectedFilter == CategoryFilter.ALL || selectedFilter == CategoryFilter.SYSTEM) {
                item { SectionHeader(title = "1. SYSTEM & FIRMWARE", icon = Icons.Default.Android) }

                item {
                    MetricCard(
                        title = "Android Version & API Level",
                        value = "Android ${data.system.androidVersion} (API ${data.system.apiLevel})",
                        icon = Icons.Default.Android,
                        explanationText = "Official Android operating system version running on the device. API level indicates software feature compatibility.",
                        testTag = "metric_android_version"
                    )
                }

                item {
                    MetricCard(
                        title = "Security Patch Level",
                        value = data.system.securityPatch,
                        icon = Icons.Default.Security,
                        explanationText = "Date of the last installed Google Android security patch. Recent patches protect against system vulnerabilities.",
                        testTag = "metric_security_patch"
                    )
                }

                item {
                    MetricCard(
                        title = "Root Access Detection",
                        value = if (data.system.isRooted) "ROOTED" else "Not Rooted (Official)",
                        icon = Icons.Default.VpnKey,
                        statusText = if (data.system.isRooted) "Warning" else "Secure",
                        isStatusWarning = data.system.isRooted,
                        explanationText = data.system.rootCheckDetails,
                        testTag = "metric_root_status"
                    )
                }

                item {
                    MetricCard(
                        title = "Bootloader & SELinux",
                        value = "${data.system.bootloaderStatus} • SELinux: ${data.system.selinuxStatus}",
                        icon = Icons.Default.Lock,
                        explanationText = "Locked bootloaders prevent unauthorized firmware flashes. Enforcing SELinux ensures security isolation.",
                        testTag = "metric_bootloader"
                    )
                }

                item {
                    MetricCard(
                        title = "Build Number & Kernel",
                        value = "${data.system.buildNumber}\nKernel: ${data.system.kernelVersion.take(45)}...",
                        icon = Icons.Default.Code,
                        explanationText = "Unique compiled build ID and underlying Linux kernel string powering device hardware drivers.",
                        testTag = "metric_build_number"
                    )
                }
            }

            // 2. BATTERY HEALTH
            if (selectedFilter == CategoryFilter.ALL || selectedFilter == CategoryFilter.BATTERY) {
                item { SectionHeader(title = "2. BATTERY & POWER HEALTH", icon = Icons.Default.BatteryChargingFull) }

                item {
                    MetricCard(
                        title = "Battery Charge & Plug State",
                        value = "${data.battery.levelPercentage}% • ${if (data.battery.isPlugged) "Charging (${data.battery.plugType})" else "Discharging"}",
                        icon = Icons.Default.BatteryFull,
                        statusText = data.battery.healthStatus,
                        isStatusWarning = !data.battery.healthStatus.contains("Good", ignoreCase = true),
                        explanationText = "Current battery charge state, plugged power source (AC/USB/Wireless), and hardware health rating.",
                        testTag = "metric_battery_charge"
                    )
                }

                item {
                    MetricCard(
                        title = "Battery Capacity & Design",
                        value = "${data.battery.estimatedCapacityMah} mAh Remaining / ${data.battery.designCapacityMah} mAh Design",
                        icon = Icons.Default.ElectricBolt,
                        explanationText = "Estimated usable charge capacity versus factory original design capacity.",
                        testTag = "metric_battery_capacity"
                    )
                }

                item {
                    val tempF = (data.battery.temperatureCelsius * 1.8f) + 32f
                    val tempStr = String.format(Locale.US, "%.1f°C / %.1f°F", data.battery.temperatureCelsius, tempF)
                    MetricCard(
                        title = "Temperature & Voltage",
                        value = "$tempStr • ${data.battery.voltageMv} mV (${data.battery.technology})",
                        icon = Icons.Default.Thermostat,
                        statusText = if (data.battery.temperatureCelsius > 42f) "Hot" else "Normal Temp",
                        isStatusWarning = data.battery.temperatureCelsius > 42f,
                        explanationText = "Battery temperature and terminal voltage reading. Excessive heat indicates heavy load or battery degradation.",
                        testTag = "metric_battery_temp"
                    )
                }
            }

            // 3. PROCESSOR & GPU
            if (selectedFilter == CategoryFilter.ALL || selectedFilter == CategoryFilter.CPU_GPU) {
                item { SectionHeader(title = "3. PROCESSOR (CPU) & GPU", icon = Icons.Default.Memory) }

                item {
                    MetricCard(
                        title = "Chipset (SoC) & Architecture",
                        value = "${data.cpu.socName} (${data.cpu.processNanometer})",
                        icon = Icons.Default.DeveloperBoard,
                        statusText = data.cpu.architecture,
                        explanationText = "System on Chip model name and fabrication node (nm). Lower nanometer process equals better energy efficiency.",
                        testTag = "metric_cpu_soc"
                    )
                }

                item {
                    val coreSpeeds = data.cpu.cores.joinToString(" • ") { c ->
                        "${c.curFreqKhz / 1000} MHz"
                    }
                    MetricCard(
                        title = "CPU Core Frequencies (${data.cpu.totalCores} Cores)",
                        value = coreSpeeds.take(80) + if (coreSpeeds.length > 80) "..." else "",
                        icon = Icons.Default.Speed,
                        explanationText = "Real-time clock speed for each active CPU core in Megahertz. Governors adjust speed dynamically.",
                        testTag = "metric_cpu_cores"
                    )
                }

                item {
                    MetricCard(
                        title = "GPU Model & Vendor",
                        value = "${data.gpu.renderer}\nVendor: ${data.gpu.vendor}",
                        icon = Icons.Default.SportsEsports,
                        explanationText = "Graphics Processing Unit model responsible for 3D rendering, UI animations, and gaming performance.",
                        testTag = "metric_gpu_model"
                    )
                }

                item {
                    MetricCard(
                        title = "Graphics APIs (Vulkan & OpenGL)",
                        value = "${data.gpu.openGlEsVersion} • ${data.gpu.vulkanVersion}",
                        icon = Icons.Default.Tune,
                        explanationText = "Modern 3D graphics rendering engine compatibility for games and graphics intensive apps.",
                        testTag = "metric_gpu_vulkan"
                    )
                }
            }

            // 4. MEMORY & STORAGE
            if (selectedFilter == CategoryFilter.ALL || selectedFilter == CategoryFilter.MEMORY) {
                item { SectionHeader(title = "4. MEMORY (RAM) & STORAGE", icon = Icons.Default.SdStorage) }

                item {
                    val totalRamGb = data.memoryStorage.totalRamBytes / (1024 * 1024 * 1024.0)
                    val availRamMb = data.memoryStorage.availableRamBytes / (1024 * 1024.0)
                    val ramStr = String.format(Locale.US, "%.1f GB Total (%s) • %.0f MB Free", totalRamGb, data.memoryStorage.ramType, availRamMb)
                    MetricCard(
                        title = "Random Access Memory (RAM)",
                        value = ramStr,
                        icon = Icons.Default.Storage,
                        explanationText = "Total physical RAM capacity and technology type. Higher RAM allows smooth multitasking without closing apps.",
                        testTag = "metric_ram_details"
                    )
                }

                item {
                    val totalStorageGb = data.memoryStorage.totalInternalStorageBytes / (1024 * 1024 * 1024.0)
                    val availStorageGb = data.memoryStorage.availableInternalStorageBytes / (1024 * 1024 * 1024.0)
                    val storageStr = String.format(Locale.US, "%.1f GB Total • %.1f GB Free", totalStorageGb, availStorageGb)
                    MetricCard(
                        title = "Internal Storage (ROM)",
                        value = storageStr,
                        icon = Icons.Default.FolderZip,
                        statusText = data.memoryStorage.storageType,
                        explanationText = "Total internal storage space and memory bus protocol. UFS storage delivers high read/write transfer speeds.",
                        testTag = "metric_storage_details"
                    )
                }

                if (data.memoryStorage.hasSdCard) {
                    item {
                        val sdGb = data.memoryStorage.totalSdCardBytes / (1024 * 1024 * 1024.0)
                        val availSdGb = data.memoryStorage.availableSdCardBytes / (1024 * 1024 * 1024.0)
                        val sdStr = String.format(Locale.US, "%.1f GB Total • %.1f GB Free", sdGb, availSdGb)
                        MetricCard(
                            title = "External SD Card Storage",
                            value = sdStr,
                            icon = Icons.Default.SdCard,
                            testTag = "metric_sd_card"
                        )
                    }
                }
            }

            // 5. DISPLAY INFORMATION
            if (selectedFilter == CategoryFilter.ALL || selectedFilter == CategoryFilter.DISPLAY) {
                item { SectionHeader(title = "5. DISPLAY SPECIFICATIONS", icon = Icons.Default.StayCurrentPortrait) }

                item {
                    val ratesStr = data.display.supportedRefreshRatesHz.joinToString(", ") { "${it.toInt()}Hz" }
                    MetricCard(
                        title = "Resolution & Density",
                        value = "${data.display.resolutionWidthPx} x ${data.display.resolutionHeightPx} pixels • ${data.display.densityDpi} DPI",
                        icon = Icons.Default.DisplaySettings,
                        statusText = data.display.densityBucket,
                        explanationText = "Screen pixel dimensions and density in dots per inch. Higher DPI produces sharper text and graphics.",
                        testTag = "metric_display_res"
                    )
                }

                item {
                    MetricCard(
                        title = "Refresh Rate & Screen Size",
                        value = "Current: ${data.display.currentRefreshRateHz.toInt()}Hz (Supported: ${data.display.supportedRefreshRatesHz.joinToString { "${it.toInt()}Hz" }}) • ${data.display.screenSizeInches}\" Screen",
                        icon = Icons.Default.TouchApp,
                        explanationText = "Screen refresh rate in Hertz. 90Hz+ displays offer silky smooth scrolling and fluid touch responsiveness.",
                        testTag = "metric_display_refresh"
                    )
                }

                item {
                    MetricCard(
                        title = "Display Type & HDR",
                        value = "${data.display.displayType}\nHDR Modes: ${data.display.hdrSupport.joinToString(", ")}",
                        icon = Icons.Default.Tv,
                        explanationText = "Display panel technology and High Dynamic Range capabilities for vibrant video playback.",
                        testTag = "metric_display_hdr"
                    )
                }
            }

            // 6. CAMERA SPECIFICATIONS
            if (selectedFilter == CategoryFilter.ALL || selectedFilter == CategoryFilter.CAMERA) {
                item { SectionHeader(title = "6. CAMERA SUITE", icon = Icons.Default.PhotoCamera) }

                data.camera.cameras.forEach { cam ->
                    item {
                        MetricCard(
                            title = cam.facing,
                            value = "${cam.megapixels} MP (${cam.resolutionPx}) • ${cam.aperture} • ${cam.focalLengthMm}",
                            icon = Icons.Default.CameraAlt,
                            statusText = if (cam.hasOis) "OIS Stabilized" else "EIS",
                            explanationText = "Resolution, lens aperture rating, and max video mode: ${cam.maxVideoResolution}. Features: ${cam.features.joinToString()}",
                            testTag = "metric_camera_${cam.facing.lowercase().take(6)}"
                        )
                    }
                }
            }

            // 7. SENSOR SUITE
            if (selectedFilter == CategoryFilter.ALL || selectedFilter == CategoryFilter.SENSORS) {
                item { SectionHeader(title = "7. SENSOR SUITE (${data.sensors.totalSensors} Sensors)", icon = Icons.Default.Sensors) }

                item {
                    MetricCard(
                        title = "Biometrics & Security Hardware",
                        value = "${data.sensors.fingerprintType} • Face Unlock: ${if (data.sensors.hasFaceUnlock) "Available" else "Not Present"}",
                        icon = Icons.Default.Fingerprint,
                        explanationText = "Hardware biometric sensors available for secure user authentication and unlock.",
                        testTag = "metric_sensor_biometrics"
                    )
                }

                item {
                    val keySensors = mutableListOf<String>()
                    if (data.sensors.hasAccelerometer) keySensors.add("Accelerometer")
                    if (data.sensors.hasGyroscope) keySensors.add("Gyroscope")
                    if (data.sensors.hasMagnetometer) keySensors.add("Compass/Magnetometer")
                    if (data.sensors.hasProximity) keySensors.add("Proximity")
                    if (data.sensors.hasAmbientLight) keySensors.add("Light Sensor")
                    if (data.sensors.hasBarometer) keySensors.add("Barometer")

                    MetricCard(
                        title = "Core Hardware Sensors",
                        value = keySensors.joinToString(" • "),
                        icon = Icons.Default.Navigation,
                        explanationText = "Essential motion, orientation, and environmental sensors powering motion gestures, gaming, and navigation.",
                        testTag = "metric_core_sensors"
                    )
                }
            }

            // 8. NETWORK & CONNECTIVITY
            if (selectedFilter == CategoryFilter.ALL || selectedFilter == CategoryFilter.NETWORK) {
                item { SectionHeader(title = "8. NETWORK & CONNECTIVITY", icon = Icons.Default.Wifi) }

                item {
                    MetricCard(
                        title = "Wi-Fi & Bluetooth",
                        value = "${data.network.wifiVersion} (${data.network.wifiState}) • ${data.network.bluetoothVersion}",
                        icon = Icons.Default.Bluetooth,
                        explanationText = "Supported wireless network protocols and Bluetooth low energy audio/data connectivity.",
                        testTag = "metric_wifi_bt"
                    )
                }

                item {
                    MetricCard(
                        title = "Cellular & SIM Support",
                        value = "${data.network.cellularBands} • Dual SIM: ${if (data.network.dualSimSupport) "Supported" else "Single SIM"}",
                        icon = Icons.Default.CellTower,
                        statusText = if (data.network.is5gSupported) "5G Ready" else "4G LTE",
                        explanationText = "Mobile cellular modem generation and SIM card slot configuration.",
                        testTag = "metric_cellular"
                    )
                }

                item {
                    MetricCard(
                        title = "NFC & GPS Satellite Suite",
                        value = "NFC: ${if (data.network.nfcAvailable) "Available (Tap & Pay)" else "Not Present"}\nGPS: ${data.network.gpsCapabilities.joinToString(", ")}",
                        icon = Icons.Default.GpsFixed,
                        explanationText = "Near Field Communication for contactless payments and satellite location positioning systems.",
                        testTag = "metric_nfc_gps"
                    )
                }
            }

            // 9. ADDITIONAL HARDWARE & DRM
            if (selectedFilter == CategoryFilter.ALL || selectedFilter == CategoryFilter.AUDIO_DRM) {
                item { SectionHeader(title = "9. AUDIO, CHARGING & DRM", icon = Icons.Default.GraphicEq) }

                item {
                    MetricCard(
                        title = "Water Resistance & Protection",
                        value = data.additional.ipRatingEstimate,
                        icon = Icons.Default.WaterDrop,
                        explanationText = "Estimated IP ingress protection rating against dust and water submersion.",
                        testTag = "metric_ip_rating"
                    )
                }

                item {
                    MetricCard(
                        title = "Speakers & Audio DAC",
                        value = "${data.additional.speakerConfig} • ${data.additional.audioDacInfo}",
                        icon = Icons.Default.VolumeUp,
                        explanationText = "Speaker physical configuration and internal high-resolution audio digital-to-analog converter.",
                        testTag = "metric_audio"
                    )
                }

                item {
                    MetricCard(
                        title = "Charging Tech & Widevine DRM",
                        value = "${data.additional.maxChargingWattageEstimate} • Wireless Charging: ${if (data.additional.wirelessChargingSupport) "Yes" else "No"}\nDRM: ${data.additional.widevineDrmLevel}",
                        icon = Icons.Default.ElectricalServices,
                        explanationText = "Fast wired/wireless charging capability and Widevine DRM level required for streaming high-definition content.",
                        testTag = "metric_charging_drm"
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
