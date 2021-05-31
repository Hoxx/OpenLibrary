package com.open.monitor.core.memory

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Debug
import com.open.monitor.core.IMonitorLoopTask

/**
 * xingxiu.hou
 * 2021/5/28
 */
class MemoryMonitor : IMonitorLoopTask<IMemory>() {

    private val iMemory = IMemory()
    private lateinit var activityManager: ActivityManager

    override fun process(context: Context): IMemory {
        activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo().also { activityManager.getMemoryInfo(it) }
        iMemory.lowMemory = memoryInfo.lowMemory
        iMemory.appMaxMemory = Runtime.getRuntime().maxMemory()
        iMemory.appTotalMemory = Runtime.getRuntime().totalMemory()
        iMemory.appFreeMemory = Runtime.getRuntime().freeMemory()

        //已使用的内存大小
        iMemory.nativeHeapAllocatedSize = Debug.getNativeHeapAllocatedSize()
        //内存使用总量
        iMemory.totalPss = Debug.getPss().toInt()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //art.gc.gc-count 垃圾收集运行的次数
            iMemory.gcCount = Debug.getRuntimeStat("art.gc.gc-count")
        }
        return iMemory
    }

}