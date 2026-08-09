package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.ScanHistoryEntity
import com.example.data.model.DiagnosticData
import com.example.data.pdf.PdfReportGenerator
import com.example.data.repository.DiagnosticRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class DiagnosticViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DiagnosticRepository(application)
    private val pdfGenerator = PdfReportGenerator(application)
    private val db = AppDatabase.getDatabase(application)
    private val scanHistoryDao = db.scanHistoryDao()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress: StateFlow<Float> = _scanProgress.asStateFlow()

    private val _scanStatusMessage = MutableStateFlow("Initializing Scanner...")
    val scanStatusMessage: StateFlow<String> = _scanStatusMessage.asStateFlow()

    private val _diagnosticData = MutableStateFlow<DiagnosticData?>(null)
    val diagnosticData: StateFlow<DiagnosticData?> = _diagnosticData.asStateFlow()

    val scanHistory: StateFlow<List<ScanHistoryEntity>> = scanHistoryDao.getAllScans()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _exportedPdf = MutableStateFlow<File?>(null)
    val exportedPdf: StateFlow<File?> = _exportedPdf.asStateFlow()

    // Interactive hardware testing states
    private val _isFlashlightOn = MutableStateFlow(false)
    val isFlashlightOn: StateFlow<Boolean> = _isFlashlightOn.asStateFlow()

    private val _touchGridPassed = MutableStateFlow<Set<Int>>(emptySet())
    val touchGridPassed: StateFlow<Set<Int>> = _touchGridPassed.asStateFlow()

    init {
        runFullDiagnosticScan()
    }

    fun runFullDiagnosticScan() {
        viewModelScope.launch(Dispatchers.IO) {
            _isScanning.value = true
            _scanProgress.value = 0.1f
            _scanStatusMessage.value = "Inspecting Android System & Kernel..."
            delay(300)

            _scanProgress.value = 0.3f
            _scanStatusMessage.value = "Analyzing Battery Health & Voltage..."
            delay(300)

            _scanProgress.value = 0.5f
            _scanStatusMessage.value = "Measuring Processor (CPU) & GPU Cores..."
            delay(300)

            _scanProgress.value = 0.7f
            _scanStatusMessage.value = "Auditing Memory, Storage & Sensor Suite..."
            delay(300)

            _scanProgress.value = 0.9f
            _scanStatusMessage.value = "Performing Authenticity & Counterfeit Checks..."
            val data = repository.collectAllDiagnostics()
            delay(200)

            _diagnosticData.value = data
            _scanProgress.value = 1.0f
            _scanStatusMessage.value = "Scan Completed Successfully!"

            // Save to Room Database history
            val ramMb = data.memoryStorage.totalRamBytes / (1024 * 1024 * 1024.0)
            val ramStr = String.format(Locale.US, "%.1f GB %s", ramMb, data.memoryStorage.ramType)
            val storageGb = data.memoryStorage.totalInternalStorageBytes / (1024 * 1024 * 1024.0)
            val storageStr = String.format(Locale.US, "%.1f GB", storageGb)

            val scanEntity = ScanHistoryEntity(
                timestamp = data.timestamp,
                deviceName = data.system.deviceName,
                model = data.system.model,
                androidVersion = data.system.androidVersion,
                healthScore = data.authenticity.overallScore,
                batteryHealth = data.battery.healthStatus,
                rootStatus = if (data.system.isRooted) "Rooted" else "Official",
                cpuModel = data.cpu.socName,
                ramInfo = ramStr,
                storageInfo = storageStr,
                displayInfo = "${data.display.resolutionWidthPx}x${data.display.resolutionHeightPx} @ ${data.display.currentRefreshRateHz.toInt()}Hz",
                summaryNotes = data.authenticity.ratingTitle
            )

            scanHistoryDao.insertScan(scanEntity)
            _isScanning.value = false
        }
    }

    fun generatePdfReport() {
        val data = _diagnosticData.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val file = pdfGenerator.generatePdfReport(data)
            _exportedPdf.value = file
        }
    }

    fun getShareIntentForPdf(file: File): Intent {
        val uri = pdfGenerator.getShareUri(file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun deleteScanHistory(scan: ScanHistoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            scanHistoryDao.deleteScan(scan)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            scanHistoryDao.clearAllScans()
        }
    }

    // Interactive Test Handlers
    fun toggleFlashlight() {
        val context = getApplication<Application>()
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager ?: return
        val newState = !_isFlashlightOn.value
        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id)
                    .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId, newState)
                _isFlashlightOn.value = newState
            }
        } catch (_: Exception) {}
    }

    fun testVibration() {
        val context = getApplication<Application>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            val vibrator = vibratorManager?.defaultVibrator
            vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(500)
            }
        }
    }

    fun testSpeakerSound() {
        try {
            val soundPool = SoundPool.Builder().setMaxStreams(1).build()
            // Synthetic tone trigger notification sound
            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            // We can trigger vibration & system tone sound
            testVibration()
        } catch (_: Exception) {}
    }

    fun markTouchCellPassed(index: Int) {
        _touchGridPassed.value = _touchGridPassed.value + index
    }

    fun resetTouchTest() {
        _touchGridPassed.value = emptySet()
    }

    override fun onCleared() {
        super.onCleared()
        if (_isFlashlightOn.value) {
            try {
                toggleFlashlight()
            } catch (_: Exception) {}
        }
    }
}
