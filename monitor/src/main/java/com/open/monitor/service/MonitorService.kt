package com.open.monitor.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.view.WindowManager
import com.open.monitor.core.MonitorCore
import com.open.monitor.core.activity.ActivityMonitor
import com.open.monitor.core.frame.FrameMonitor
import com.open.monitor.core.memory.MemoryMonitor
import com.open.monitor.core.thread.ThreadMonitor
import com.open.monitor.permission.MemoryPermissionActivity
import com.open.monitor.util.MonitorHelper
import com.open.monitor.view.MemoryMainFloatView
import java.util.concurrent.atomic.AtomicBoolean

/**
 * xingxiu.hou
 * 2021/5/27
 */
class MonitorService : Service() {

    companion object {
        fun start(context: Context) {
            context.startService(Intent(context, MonitorService::class.java))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, MonitorService::class.java))
        }
    }

    private var lastStartId: Int = 0

    private lateinit var windowManager: WindowManager

    /*是否是已经开启*/
    private val isOpened = AtomicBoolean(false)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        this.lastStartId = startId
        if (MonitorHelper.enable(this)) {
            // 获取WindowManager服务
            showFloatingWindow()
        } else {
            MemoryPermissionActivity.start(this)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showFloatingWindow() {
        if (isOpened.getAndSet(true)) {
            return
        }
        kotlin.runCatching {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            MemoryMainFloatView(this).apply {
                show(windowManager)
                monitor(this)
            }
        }.getOrElse {
            println("MONITOR-SERVICE:$it")
        }
    }

    private fun monitor(floatView: MemoryMainFloatView) {
        /*关闭服务*/
        floatView.listenStop {
            if (stopSelfResult(lastStartId)) {
                stop(floatView.context)
            }
            floatView.close(windowManager)
        }
        /*监控内存*/
        MonitorCore.listenTask(this, MemoryMonitor::class.java, 1_000L)
        { floatView.setMemoryData(it) }
        /*监控线程*/
        MonitorCore.listenTask(this, ThreadMonitor::class.java, 1_000L)
        { floatView.setThreadContent(it) }
        /*Activity监控*/
        MonitorCore.listenTask(this, ActivityMonitor::class.java, 1_000L)
        { floatView.setCurrentActivity(it) }
        /*帧率检查*/
        MonitorCore.listenTask(this, FrameMonitor::class.java, 1_000L)
        { floatView.setFrameData(it) }

    }

    override fun onDestroy() {
        super.onDestroy()
        isOpened.set(false)
        MonitorCore.stopAll()
    }


}