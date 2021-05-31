package com.open.monitor.view

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.view.*
import android.widget.TextView
import com.open.monitor.core.activity.IActivity
import com.open.monitor.core.frame.IFrame
import com.open.monitor.core.memory.IMemory
import com.open.monitor.core.thread.IThread
import com.open.monitor.util.AppUtils
import com.xx.monitor.R

/**
 * xingxiu.hou
 * 2021/5/28
 */
class MemoryMainFloatView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseSystemFloatGroup(context, attrs, defStyleAttr) {

    private val moveRect = Rect()

    private var layoutParams: WindowManager.LayoutParams? = null

    private lateinit var view: View

    private var toggleOpen: Boolean = true

    private var stopCall: (() -> Unit)? = null

    init {
        context.resources.displayMetrics.apply {
            moveRect.set(0, 0, this.widthPixels, this.heightPixels)
        }
    }

    fun show(windowManager: WindowManager) {
        show(windowManager, layoutParams)
    }

    override fun getMoveRect(): Rect = moveRect


    override fun onViewReLayout() {
        //创建WindowLayoutParams
        if (layoutParams == null) {
            val x = moveRect.width() - dp(300F)
            layoutParams = getLayoutParams(dp(300F), dp(300F), x / 2, 0)
        }
        updateLayoutParams(layoutParams)
    }

    override fun initialization() {
        view = LayoutInflater.from(context).inflate(R.layout.float_memory_window, null)
        initView(view)
        //添加View
        addView(
            view,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                gravity = Gravity.CENTER
            })
    }

    override fun viewTouchEvent(event: MotionEvent?) {
        super.viewTouchEvent(event)
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.setBackgroundColor(Color.parseColor("#88000000"))
                }
                MotionEvent.ACTION_UP -> {
                    view.setBackgroundColor(Color.parseColor("#22000000"))
                }
            }
        }
    }

    private fun initView(view: View) {
        val monitorToggle = view.findViewById<TextView>(R.id.tv_monitor_title)
        AppUtils.getAppData(context)?.let {
            monitorToggle.text = "指标监控 { ${it.appName}(${it.appVersionName}) }"
        }
        /*关闭*/
        view.findViewById<TextView>(R.id.btn_expand_toggle)
            .setOnClickListener { stopCall?.invoke() }
        /*展开和收起*/
        monitorToggle.setOnClickListener { toggle() }
    }

    private fun toggle() {
        layoutParams = if (toggleOpen) {
            view.findViewById<TextView>(R.id.btn_expand_toggle).visibility = View.GONE
            view.findViewById<TextView>(R.id.tv_monitor_title).layoutParams.width = dp(80F)
            getLayoutParams(dp(120F), dp(40F), position.x, position.y)
        } else {
            view.findViewById<TextView>(R.id.btn_expand_toggle).visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.tv_monitor_title).layoutParams.width =
                ViewGroup.LayoutParams.WRAP_CONTENT
            getLayoutParams(dp(300F), dp(300F), position.x, position.y)
        }
        updateLayoutParams(layoutParams)
        toggleOpen = !toggleOpen
    }

    fun listenStop(stopCall: (() -> Unit)? = null) {
        this.stopCall = stopCall
    }

    fun setMemoryData(iMemory: IMemory) {
        view.findViewById<TextView>(R.id.tv_memory_content).text = iMemory.result()
    }

    fun setThreadContent(iThread: IThread) {
        view.findViewById<TextView>(R.id.tv_thread_count).text = iThread.result()
    }

    fun setCurrentActivity(iActivity: IActivity) {
        view.findViewById<TextView>(R.id.tv_current_activity).text = iActivity.result()
    }

    fun setFrameData(iFrame: IFrame) {
        view.findViewById<TextView>(R.id.tv_frame_data).text = iFrame.result()
    }

    override fun onClick() {

    }

    override fun onPosition(x: Int, y: Int) {

    }

}