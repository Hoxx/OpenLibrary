package com.open.monitor.core.activity

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.open.monitor.core.IMonitorLoopTask

/**
 * xingxiu.hou
 * 2021/5/28
 */
class ActivityMonitor : IMonitorLoopTask<IActivity>() {

    private val iActivity = IActivity()

    override fun process(context: Context): IActivity {
        MonitorActivityProvider.getCurrentActivity()?.let {
            iActivity.activityName = it.javaClass.name
            iActivity.fragmentName = getCurrentFragment(it)
        }
        return iActivity
    }

    private fun getCurrentFragment(activity: Activity): String {
        return if (activity is FragmentActivity) {
            val fragments = activity.supportFragmentManager.fragments
            if (fragments.isNotEmpty()) {
                val first = fragments.first()
                if (first.javaClass.name.equals("androidx.navigation.fragment.NavHostFragment")) {
                    first.childFragmentManager.fragments.firstOrNull()?.javaClass?.name ?: ""
                } else {
                    first.javaClass.name
                }
            } else {
                ""
            }
        } else {
            ""
        }
    }

}