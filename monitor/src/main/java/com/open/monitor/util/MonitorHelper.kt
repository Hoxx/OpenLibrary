package com.open.monitor.util

import android.content.Context
import android.os.Build
import android.provider.Settings
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * xingxiu.hou
 * 2021/5/28
 */
object MonitorHelper {

    /*检查权限*/
    @JvmStatic
    fun enable(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(context))
    }

    private val format: DecimalFormat = DecimalFormat().apply {
        roundingMode = RoundingMode.FLOOR
    }

    @JvmStatic
    fun formatF(value: Float, num: Int): Float {
        return format(value, num).toFloat()
    }

    @JvmStatic
    fun format(value: Float, num: Int): String {
        val b = StringBuilder("0.")
        for (i in 0 until num) {
            b.append("#")
        }
        format.applyPattern(b.toString())
        return format.format(value)
    }

}