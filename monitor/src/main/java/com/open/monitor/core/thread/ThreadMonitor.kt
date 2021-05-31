package com.open.monitor.core.thread

import android.content.Context
import com.open.monitor.core.IMonitorLoopTask

/**
 * xingxiu.hou
 * 2021/5/28
 */
class ThreadMonitor : IMonitorLoopTask<IThread>() {

    private val iThread = IThread()

    override fun process(context: Context): IThread {
        var threadGroup = Thread.currentThread().threadGroup
        var topGroup: ThreadGroup = threadGroup
        while (threadGroup != null) {
            topGroup = threadGroup
            threadGroup = threadGroup.parent
        }
        // 激活的线程数再加一倍，防止枚举时有可能刚好有动态线程生成
        val activeCount = topGroup.activeCount() * 2
        val threadArray = arrayOfNulls<Thread>(activeCount)
        // 获取根线程组下的所有线程，返回的actualSize便是最终的线程数
        val actualSize: Int = topGroup.enumerate(threadArray)
        iThread.threadNum = actualSize
        val tempThreadNames = mutableListOf<String>()
        threadArray.filterNotNull().forEach {
            it.stackTrace
        }
        iThread.threadNames = tempThreadNames

        return iThread
    }

}