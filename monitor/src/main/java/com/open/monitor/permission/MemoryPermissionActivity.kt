package com.open.monitor.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.open.monitor.service.MonitorService
import com.open.monitor.util.MonitorHelper
import com.xx.monitor.R

/**
 * xingxiu.hou
 * 2021/5/27
 */
class MemoryPermissionActivity : Activity() {

    companion object {
        const val PERMISSION_CODE = 901

        fun start(context: Context) {
            context.startActivity(Intent(context, MemoryPermissionActivity::class.java).apply {
                if (context !is Activity) {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.memory_activity_permiission)

        findViewById<Button>(R.id.btn_permission_cancel).setOnClickListener { finish() }
        findViewById<Button>(R.id.btn_permission_done).setOnClickListener { checkPermission() }
    }

    private fun checkPermission() {
        if (!MonitorHelper.enable(this)) {
            val intent = Intent().apply {
                action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                data = Uri.parse("package:$packageName")
            }
            startActivityForResult(intent, PERMISSION_CODE)
        } else {
            startFloatViewService()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show()
                startFloatViewService()
            }
        }
    }

    private fun startFloatViewService() {
        MonitorService.start(this)
        finish()
    }

}