package com.securedoc.app.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WatermarkView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 36f
        alpha = 60
    }

    private val handler = Handler(Looper.getMainLooper())
    private var username: String = ""
    private var watermarkText: String = ""

    private val tickRunnable = object : Runnable {
        override fun run() {
            updateWatermark(username)
            handler.postDelayed(this, 60_000)
        }
    }

    fun updateWatermark(username: String) {
        this.username = username
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val timestamp = formatter.format(Date())
        watermarkText = "SECUREDOC • $username • $timestamp"
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Refresh watermark text every minute.
        handler.post(tickRunnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacks(tickRunnable)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (watermarkText.isBlank()) return

        canvas.save()
        canvas.rotate(-30f, width / 2f, height / 2f)

        val textWidth = paint.measureText(watermarkText)
        val stepX = textWidth + 80f
        val stepY = 120f

        var y = -height.toFloat()
        while (y < height * 2) {
            var x = -width.toFloat()
            while (x < width * 2) {
                canvas.drawText(watermarkText, x, y, paint)
                x += stepX
            }
            y += stepY
        }

        canvas.restore()
    }
}
