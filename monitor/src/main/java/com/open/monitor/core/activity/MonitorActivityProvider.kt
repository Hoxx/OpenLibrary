package com.open.monitor.core.activity

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * xingxiu.hou
 * 2021/5/30
 */
internal object MonitorActivityProvider : Application.ActivityLifecycleCallbacks {

    private var reference: WeakReference<Activity>? = null

    fun getCurrentActivity(): Activity? {
        return reference?.get()
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity?) {

    }

    override fun onActivityResumed(activity: Activity?) {
        reference = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity?) {

    }

    override fun onActivityStopped(activity: Activity?) {

    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

    }

    override fun onActivityDestroyed(activity: Activity?) {

    }
}