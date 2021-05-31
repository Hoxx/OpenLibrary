package com.open.monitor.core.frame

import android.content.Context
import android.view.Choreographer
import com.open.monitor.core.IMonitorLoopTask

/**
 * xingxiu.hou
 * 2021/5/30
 */
class FrameMonitor : IMonitorLoopTask<IFrame>(), Choreographer.FrameCallback {

    private val iFrame = IFrame()

    private var lastFrameTimeNanos: Long = 0
    private var currentFrameTimeNanos: Long = 0
    private var fps: Int = 0

    init {
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun process(context: Context): IFrame {
        if (lastFrameTimeNanos > 0) {
            val times: Long = (currentFrameTimeNanos - lastFrameTimeNanos) / 1_000_000_000
            if (times > 16) {
                iFrame.isDropFrame = true
                iFrame.dropFrames = (times / 16).toInt()
            } else {
                iFrame.isDropFrame = false
                iFrame.dropFrames = 0
            }
            iFrame.frameFate = fps * 1F / times * 1F
            lastFrameTimeNanos = currentFrameTimeNanos
        }
        lastFrameTimeNanos = currentFrameTimeNanos
        fps = 0
        return iFrame
    }

    override fun doFrame(frameTimeNanos: Long) {
        fps++
        currentFrameTimeNanos = frameTimeNanos
        Choreographer.getInstance().postFrameCallback(this)
    }


}