package com.open.monitor.core.activity

import android.graphics.Color
import android.text.Spannable
import com.open.monitor.util.SpannableHelper

/**
 * xingxiu.hou
 * 2021/5/30
 */
class IActivity {

    internal var activityName: String = ""
    internal var fragmentName: String = ""

    fun result(): Spannable {
        val result = SpannableHelper.Builder()
        if (activityName.isNotEmpty()) {
            result.text("Activity:\n").text("$activityName\n").color(Color.GREEN)
        }
        if (fragmentName.isNotEmpty()) {
            result.text("Fragment:\n").text("$fragmentName\n").color(Color.GREEN)
        }
        return result.build()
    }

}