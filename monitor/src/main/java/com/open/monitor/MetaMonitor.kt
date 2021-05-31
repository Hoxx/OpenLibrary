package com.open.monitor

import android.app.Application
import android.content.Context
import com.open.monitor.core.activity.MonitorActivityProvider
import com.open.monitor.permission.MemoryPermissionActivity
import com.open.monitor.service.MonitorService
import com.open.monitor.startup.StartTrigger
import com.open.monitor.util.MonitorHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * xingxiu.hou
 * 2021/5/27
 */
object MetaMonitor {

    fun install(application: Application) {
        start(application)
        application.registerActivityLifecycleCallbacks(MonitorActivityProvider)
        StartTrigger.listen(application) { start(application) }
    }

    private fun start(context: Context, delay: Long = 0) {
        GlobalScope.launch(Dispatchers.Main) {
            delay(delay)
            if (MonitorHelper.enable(context)) {
                MonitorService.start(context)
            } else {
                /*请求权限*/
                MemoryPermissionActivity.start(context)
            }
        }
    }
}
