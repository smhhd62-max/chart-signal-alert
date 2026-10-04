package com.chartsignal.alert

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color

enum class SignalType {
    NONE,
    GREEN_TRIANGLE,
    RED_TRIANGLE
}

class SignalDetector(private val context: Context) {

    // تنظیمات قابل تغییر از SharedPreferences
    private fun getGreenMin(): Int = getPref("green_min", 80)
    private fun getRedMin(): Int = getPref("red_min", 80)
    private fun getMinArea(): Int = getPref("min_area", 200)

    fun detect(bitmap: Bitmap): SignalType {
        val width = bitmap.width
        val height = bitmap.height

        // ناحیه‌ای که احتمالاً سیگنال‌ها هستند (وسط چارت)
        val startY = (height * 0.15).toInt()
        val endY = (height * 0.85).toInt()

        var greenPixels = 0
        var redPixels = 0

        // نمونه‌گیری از پیکسل‌ها
        val step = 4
        var y = startY
        while (y < endY) {
            var x = 0
            while (x < width) {
                val color = bitmap.getPixel(x, y)
                if (isGreen(color)) greenPixels++
                if (isRed(color)) redPixels++
                x += step
            }
            y += step
        }

        return when {
            greenPixels > getGreenMin() && greenPixels > redPixels -> SignalType.GREEN_TRIANGLE
            redPixels > getRedMin() && redPixels > greenPixels -> SignalType.RED_TRIANGLE
            else -> SignalType.NONE
        }
    }

    private fun isGreen(color: Int): Boolean {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return g > 140 && g > r + 40 && g > b + 40
    }

    private fun isRed(color: Int): Boolean {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return r > 140 && r > g + 40 && r > b + 40
    }

    private fun getPref(key: String, def: Int): Int {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getInt(key, def)
    }
}
