package com.example.data.pdf

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.DiagnosticData
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PdfReportGenerator(private val context: Context) {

    fun generatePdfReport(data: DiagnosticData): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 dimensions in points
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        val textPaint = Paint()

        // Background
        paint.color = Color.rgb(248, 250, 252)
        canvas.drawRect(0f, 0f, 595f, 842f, paint)

        // Header Banner
        paint.color = Color.rgb(15, 23, 42) // Slate 900
        canvas.drawRect(0f, 0f, 595f, 100f, paint)

        // Header Accent line
        paint.color = Color.rgb(99, 102, 241) // Indigo 500
        canvas.drawRect(0f, 96f, 595f, 100f, paint)

        // Title text
        textPaint.color = Color.WHITE
        textPaint.textSize = 22f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("PHONE INSPECTOR PRO", 30f, 45f, textPaint)

        textPaint.textSize = 12f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Complete Hardware & System Verification Diagnostic Report", 30f, 70f, textPaint)

        // Date and Score box in header
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateStr = dateFormat.format(Date(data.timestamp))
        textPaint.textSize = 10f
        textPaint.color = Color.rgb(203, 213, 225)
        canvas.drawText("Generated: $dateStr", 380f, 45f, textPaint)

        // Health Score Badge
        val scoreColor = if (data.authenticity.overallScore >= 80) Color.rgb(34, 197, 94) else Color.rgb(239, 68, 68)
        paint.color = scoreColor
        canvas.drawRoundRect(460f, 55f, 565f, 85f, 8f, 8f, paint)

        textPaint.color = Color.WHITE
        textPaint.textSize = 13f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("SCORE: ${data.authenticity.overallScore}/100", 470f, 75f, textPaint)

        var y = 130f

        // Device Summary Section
        drawSectionHeader(canvas, "1. DEVICE IDENTIFICATION & VERIFICATION", y)
        y += 25f

        drawRow(canvas, "Device Name / Model:", "${data.system.deviceName} (${data.system.model})", y)
        y += 18f
        drawRow(canvas, "Manufacturer & Brand:", "${data.system.manufacturer} / ${data.system.brand}", y)
        y += 18f
        drawRow(canvas, "Android OS Version:", "Android ${data.system.androidVersion} (API ${data.system.apiLevel})", y)
        y += 18f
        drawRow(canvas, "Security Patch Level:", data.system.securityPatch, y)
        y += 18f
        drawRow(canvas, "Root Status:", if (data.system.isRooted) "ROOTED (Warning)" else "Not Rooted (Official)", y)
        y += 18f
        drawRow(canvas, "Bootloader Status:", data.system.bootloaderStatus, y)
        y += 28f

        // Hardware Specs Section
        drawSectionHeader(canvas, "2. INTERNAL HARDWARE SPECIFICATIONS", y)
        y += 25f

        val ramMb = data.memoryStorage.totalRamBytes / (1024 * 1024 * 1024.0)
        val ramStr = String.format(Locale.US, "%.1f GB %s", ramMb, data.memoryStorage.ramType)
        drawRow(canvas, "Processor (SoC):", "${data.cpu.socName} (${data.cpu.totalCores} Cores)", y)
        y += 18f
        drawRow(canvas, "RAM Capacity:", ramStr, y)
        y += 18f

        val storageGb = data.memoryStorage.totalInternalStorageBytes / (1024 * 1024 * 1024.0)
        val storageStr = String.format(Locale.US, "%.1f GB (%s)", storageGb, data.memoryStorage.storageType)
        drawRow(canvas, "Internal Storage:", storageStr, y)
        y += 18f

        drawRow(canvas, "Display Panel:", "${data.display.resolutionWidthPx}x${data.display.resolutionHeightPx} @ ${data.display.currentRefreshRateHz.toInt()}Hz (${data.display.displayType})", y)
        y += 18f
        drawRow(canvas, "Battery Health:", "${data.battery.healthStatus} (${data.battery.levelPercentage}% Charge, ${data.battery.designCapacityMah} mAh)", y)
        y += 18f
        drawRow(canvas, "Sensors Detected:", "${data.sensors.totalSensors} Hardware Sensors Verified", y)
        y += 18f
        drawRow(canvas, "DRM Protection:", data.additional.widevineDrmLevel, y)
        y += 28f

        // Buyer's Authenticity Checklist Section
        drawSectionHeader(canvas, "3. BUYER'S AUTHENTICITY CHECKLIST", y)
        y += 25f

        textPaint.color = Color.rgb(30, 41, 59)
        textPaint.textSize = 10f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        for (pass in data.authenticity.passChecklist.take(4)) {
            paint.color = Color.rgb(34, 197, 94)
            canvas.drawCircle(38f, y - 4f, 4f, paint)
            textPaint.color = Color.rgb(30, 41, 59)
            canvas.drawText(pass, 50f, y, textPaint)
            y += 16f
        }

        for (warn in data.authenticity.warningChecklist.take(3)) {
            paint.color = Color.rgb(239, 68, 68)
            canvas.drawCircle(38f, y - 4f, 4f, paint)
            textPaint.color = Color.rgb(185, 28, 28)
            canvas.drawText(warn, 50f, y, textPaint)
            y += 16f
        }

        y += 15f
        // Buyer Summary Box
        paint.color = Color.rgb(238, 242, 255) // Light indigo tint
        canvas.drawRoundRect(30f, y, 565f, y + 60f, 8f, 8f, paint)

        textPaint.color = Color.rgb(49, 46, 129)
        textPaint.textSize = 10f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        val summaryLines = breakTextIntoLines(data.authenticity.buyerSummaryText, 85)
        var lineY = y + 20f
        for (line in summaryLines) {
            canvas.drawText(line, 45f, lineY, textPaint)
            lineY += 14f
        }

        // Footer
        paint.color = Color.rgb(226, 232, 240)
        canvas.drawLine(30f, 810f, 565f, 810f, paint)

        textPaint.color = Color.rgb(148, 163, 184)
        textPaint.textSize = 9f
        canvas.drawText("Phone Inspector Pro • Official Diagnostic Document • Verifiable Offline Report", 30f, 825f, textPaint)

        pdfDocument.finishPage(page)

        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()

        val fileName = "Phone_Inspector_Report_${System.currentTimeMillis()}.pdf"
        val pdfFile = File(reportsDir, fileName)

        val fos = FileOutputStream(pdfFile)
        pdfDocument.writeTo(fos)
        pdfDocument.close()
        fos.close()

        return pdfFile
    }

    private fun drawSectionHeader(canvas: android.graphics.Canvas, title: String, y: Float) {
        val paint = Paint()
        paint.color = Color.rgb(226, 232, 240)
        canvas.drawRect(30f, y - 12f, 565f, y + 8f, paint)

        val textPaint = Paint()
        textPaint.color = Color.rgb(15, 23, 42)
        textPaint.textSize = 12f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(title, 35f, y, textPaint)
    }

    private fun drawRow(canvas: android.graphics.Canvas, label: String, value: String, y: Float) {
        val paint = Paint()
        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(label, 35f, y, paint)

        val valPaint = Paint()
        valPaint.color = Color.rgb(15, 23, 42)
        valPaint.textSize = 10f
        valPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(value, 180f, y, valPaint)
    }

    private fun breakTextIntoLines(text: String, maxCharsPerLine: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            if ((currentLine + word).length <= maxCharsPerLine) {
                currentLine += if (currentLine.isEmpty()) word else " $word"
            } else {
                lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }
        return lines
    }

    fun getShareUri(file: File): android.net.Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
