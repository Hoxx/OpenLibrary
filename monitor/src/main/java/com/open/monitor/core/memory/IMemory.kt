package com.open.monitor.core.memory

import android.graphics.Color
import android.text.Spannable
import com.open.monitor.util.MonitorHelper
import com.open.monitor.util.SpannableHelper

/**
 * xingxiu.hou
 * 2021/5/28
 */
class IMemory {
    internal var gcCount: String = ""
    internal var totalPss: Int = 0

    internal var lowMemory: Boolean = false
    internal var appMaxMemory: Long = 0
    internal var appTotalMemory: Long = 0
    internal var appFreeMemory: Long = 0
    internal var nativeHeapAllocatedSize: Long = 0

    /*使用内存占比*/
    private fun getUsedHeapPercentage(): Float {
        val t = totalPss * 1.0F / 1024F
        val a = appMaxMemory * 1.0F / (1024 * 1024)
        return MonitorHelper.formatF(t / a, 2)
    }

    /*Java层内存占比*/
    private fun getJavaHeap(): Float {
        return formatByteToM(appTotalMemory - appFreeMemory)
    }

    /*Java层内存占比*/
    private fun getNativeHeap(): Float {
        return formatByteToM(nativeHeapAllocatedSize)
    }

    /*其他内存占比*/
    private fun getOtherHeap(): Float {
        return formatByteToM(appMaxMemory) - formatKToM(totalPss)
    }

    fun result(): Spannable {
        val builder = SpannableHelper.Builder()
        builder.text("内存指标:\n")
        builder.text("系统预警:")
        if (lowMemory) {
            builder.text("内存不足").color(Color.RED).bold(true).text("\n")
        } else {
            builder.text("内存充足").color(Color.GREEN).text("\n")
        }
        if (appMaxMemory > 0) {
            builder.text("最大内存:").text("${formatByteToM(appMaxMemory)}MB").color(Color.GREEN)
                .text("\n")
        }
        if (totalPss > 0) {
            builder.text("实际使用:").text("${formatKToM(totalPss)}MB").color(Color.GREEN).text("\n")
        }
        val usedHeapPercentage = getUsedHeapPercentage()
        builder.text("当前占比:")
        if (usedHeapPercentage > 0.70F) {
            builder.text("${usedHeapPercentage * 100}%").color(Color.RED).bold(true).text("\n")
        } else {
            builder.text("${usedHeapPercentage * 100}%").color(Color.GREEN).text("\n")
        }
//        if (getJavaHeap() > 0) {
//            builder.text("Java使用:").text("${getJavaHeap()}MB").color(Color.GREEN).text("\n")
//        }
//        if (getNativeHeap() > 0) {
//            builder.text("Native使用:").text("${getNativeHeap()}MB")
//                .color(Color.GREEN).text("\n")
//        }
//        if (getOtherHeap() > 0) {
//            builder.text("其他使用:").text("${getOtherHeap()}MB").color(Color.GREEN).text("\n")
//        }
        if (gcCount.isNotEmpty()) {
            builder.text("GC次数:").text(gcCount).color(Color.GREEN)
        }
        return builder.build()
    }

    private fun formatByteToM(value: Long): Float {
        val temp = value * 1.0F / (1024 * 1024)
        return MonitorHelper.formatF(temp, 2)
    }

    private fun formatKToM(value: Int): Float {
        val temp = value * 1.0F / 1024F
        return MonitorHelper.formatF(temp, 2)
    }
}
