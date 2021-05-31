package com.open.monitor.core

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper


/**
 * xingxiu.hou
 * 2021/5/28
 */
object MonitorCore {

    private val taskHandler: Handler
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        val handlerThread = HandlerThread("monitor_core")
        handlerThread.start()
        taskHandler = Handler(handlerThread.looper)
    }

    private val tasks: MutableMap<String, IMonitorLoopTask<*>> = mutableMapOf()

    private fun <T> createTask(clazz: Class<out IMonitorLoopTask<T>>): IMonitorLoopTask<T> {
        val invoke = clazz.newInstance()
        tasks[clazz.name] = invoke
        return invoke
    }

    fun <T> listenTask(
        context: Context,
        clazz: Class<out IMonitorLoopTask<T>>,
        loopDelay: Long = -1,
        callback: ((T) -> Unit)? = null
    ) {
        val task = tasks.getOrPut(clazz.name) { createTask(clazz) } as IMonitorLoopTask<T>
        task.init(context, mainHandler, callback)
        task.runWith(taskHandler, loopDelay)
    }

    fun <T> stopTask(clazz: Class<out IMonitorLoopTask<T>>) {
        val key = clazz.name
        tasks[key]?.stop()
    }

    /*结束*/
    fun stopAll() {
        tasks.values.forEach { it.stop() }
        taskHandler.removeCallbacksAndMessages(null)
    }

}


