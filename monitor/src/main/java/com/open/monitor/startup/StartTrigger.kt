package com.open.monitor.startup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * xingxiu.hou
 * 2021/5/30
 */
internal object StartTrigger {

    /*5秒内：音量先开启到最大，然后再关闭到最小，然后再开启到最大,即可开启监控*/
    fun listen(context: Context, callback: (Context) -> Unit) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        context.registerReceiver(
            TriggerReceiver(audioManager, callback),
            IntentFilter("android.media.VOLUME_CHANGED_ACTION")
        )
    }

    private class TriggerReceiver(
        val audioManager: AudioManager, val callback: (Context) -> Unit
    ) : BroadcastReceiver() {

        private var triggerStep: Int = 0
        private var maxVolume: Int = -1

        init {
            maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        }

        override fun onReceive(context: Context, intent: Intent) {
            //如果音量发生变化则更改seekbar的位置
            if (intent.action == "android.media.VOLUME_CHANGED_ACTION") {
                val currentVolume: Int = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                openTrigger(context, currentVolume)
            }
        }

        private fun openTrigger(context: Context, currentVolume: Int) {
            //开启到最大
            if (triggerStep == 0 && maxVolume == currentVolume) {
                triggerStep = 1
                GlobalScope.launch {
                    delay(5_000)
                    triggerStep = 0
                }
            }
            if (triggerStep == 1 && currentVolume <= 0) {
                triggerStep = 2
            }
            if (triggerStep == 2 && currentVolume >= maxVolume) {
                triggerStep = 0
                callback.invoke(context)
            }
        }


    }
}