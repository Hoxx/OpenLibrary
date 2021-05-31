package com.open.monitor.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

/**
 * xingxiu.hou
 * 2021/5/28
 */
object AppUtils {

    data class AppData(
        val appPackageName: String,
        val appName: String,
        val appVersionCode: Int,
        val appVersionName: String
    )

    /**
     * 获取应用程序名称
     */
    @Synchronized
    fun getAppData(context: Context): AppData? {
        try {
            val packageManager: PackageManager = context.packageManager
            val packageInfo: PackageInfo = packageManager.getPackageInfo(context.packageName, 0)
            val labelRes: Int = packageInfo.applicationInfo.labelRes
            return AppData(
                context.packageName,
                context.resources.getString(labelRes),
                packageInfo.versionCode,
                packageInfo.versionName
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}