package com.open.monitor.core.frame

import android.graphics.Color
import android.text.Spannable
import com.open.monitor.util.SpannableHelper

/**
 * xingxiu.hou
 * 2021/5/30
 */
class IFrame {

    /*是否丢帧*/
    internal var isDropFrame: Boolean = false

    /*丢帧个数*/
    internal var dropFrames: Int = 0

    /*帧率(FPS)*/
    internal var frameFate: Float = 0F

    fun result(): Spannable {
        val result = SpannableHelper.Builder()
        result.text("是否丢帧:")
        val color = if (isDropFrame) {
            result.text("是").color(Color.RED).text(":$dropFrames").color(Color.RED).text("\n")
            Color.RED
        } else {
            result.text("否").color(Color.GREEN).text("\n")
            Color.GREEN
        }
        result.text("帧率/FPS:").text("$frameFate").color(color).text("\n")
        return result.build()
    }

}