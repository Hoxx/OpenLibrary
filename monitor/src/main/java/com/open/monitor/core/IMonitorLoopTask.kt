package com.open.monitor.core

import android.content.Context
import android.os.Handler

/**
 * xingxiu.hou
 * 2021/5/28
 */
abstract class IMonitorLoopTask<T> : Runnable {

    private lateinit var taskHandler: Handler
    private var loopDelayTime: Long = -1
    private var callback: MainCallback<T>? = null
    private var needStop = false
    private lateinit var context: Context

    fun init(context: Context, mainHandler: Handler, callback: ((T) -> Unit)? = null) {
        this.context = context
        this.callback = MainCallback(mainHandler, callback)
    }

    fun runWith(taskHandler: Handler, loopDelayTime: Long = -1) {
        this.taskHandler = taskHandler
        this.loopDelayTime = loopDelayTime
        needStop = false
        taskHandler.post(this)
    }

    override fun run() {
        val data = process(context)
        callback?.invoke(data)
        if (loopDelayTime >= 0 && !needStop) {
            taskHandler.postDelayed(this, loopDelayTime)
        }
    }

    fun stop() {
        needStop = true
        taskHandler.removeCallbacks(this)
    }

    protected abstract fun process(context: Context): T

    fun invoke(): IMonitorLoopTask<*> {
        return IMonitorLoopTask::class.java.newInstance()
    }

    class MainCallback<T>(
        private val mainHandler: Handler,
        private val callback: ((T) -> Unit)? = null
    ) :
        Runnable {

        private var data: T? = null

        fun invoke(data: T) {
            this.data = data
            mainHandler.post(this)
        }

        override fun run() {
            data?.let { callback?.invoke(it) }
        }
    }

}