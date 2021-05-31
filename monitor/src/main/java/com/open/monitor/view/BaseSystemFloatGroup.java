package com.open.monitor.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

/**
 * 2019/12/2
 *
 * @author Houxingxiu
 * <p>
 * Description:
 */
public abstract class BaseSystemFloatGroup extends FrameLayout {

    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    private boolean isAddToWindow;
    /**
     * View 移动参数
     */
    //可移动范围大小
    protected Rect mRect;
    //监听
    private onFloatAction mFloatAction;
    //最小移动像素
    protected int scaledTouchSlop;
    //坐标
    private float downX;
    private float downY;
    private float lastEventX;
    private float lastEventY;
    //回到边缘动画
    private ValueAnimator edgeAnimator;
    //设置是否显示到刘海区域
    private boolean showCutoutModeShortEdges = true;


    public BaseSystemFloatGroup(Context context) {
        this(context, null);
    }

    public BaseSystemFloatGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseSystemFloatGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        initialization();
        scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mRect = getMoveRect();
        //回归边缘动画
        edgeAnimator = new ValueAnimator();
        edgeAnimator.setDuration(100);
        edgeAnimator.setInterpolator(new LinearInterpolator());
        edgeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object value = animation.getAnimatedValue();
                if (value instanceof Integer) {
                    mLayoutParams.x = (int) value;
                    updateView();
                }
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        onViewReLayout();
        mRect = getMoveRect();
    }

    /**
     * 可移动范围
     */
    public abstract Rect getMoveRect();

    public abstract void onViewReLayout();

    public abstract void initialization();

    public void setShowCutoutModeShortEdges(boolean showCutoutModeShortEdges) {
        this.showCutoutModeShortEdges = showCutoutModeShortEdges;
    }

    public void show(WindowManager windowManager, WindowManager.LayoutParams layoutParams) {
        if (windowManager == null) {
            return;
        }
        if (layoutParams == null) {
            getLayoutParams(0, 0, 0, 0);
        } else {
            mLayoutParams = layoutParams;
        }
        this.mWindowManager = windowManager;
        try {
            if (isAddToWindow) {
                return;
            }
            mWindowManager.addView(this, mLayoutParams);
            isAddToWindow = true;
        } catch (Throwable ignore) {
        }
    }

    public void close(WindowManager windowManager) {
        if (windowManager == null) {
            return;
        }
        this.mWindowManager = windowManager;
        try {
            if (!isAddToWindow) {
                return;
            }
            mWindowManager.removeView(this);
            isAddToWindow = false;
        } catch (Throwable ignore) {
        }
    }

    public WindowManager.LayoutParams getDefaultLayoutParams() {
        return getLayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, 0, 0);
    }

    public WindowManager.LayoutParams getLayoutParams(int width, int height, int x, int y) {
        if (mLayoutParams == null) {
            mLayoutParams = new WindowManager.LayoutParams();
        }
        mLayoutParams.width = width <= 0 ? width == WindowManager.LayoutParams.MATCH_PARENT ? WindowManager.LayoutParams.MATCH_PARENT : WindowManager.LayoutParams.WRAP_CONTENT : width;
        mLayoutParams.height = height <= 0 ? width == WindowManager.LayoutParams.MATCH_PARENT ? WindowManager.LayoutParams.MATCH_PARENT : WindowManager.LayoutParams.WRAP_CONTENT : height;
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
        ;
        if (showCutoutModeShortEdges && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            //刘海屏全屏显示
            mLayoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Android 8.0
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            //其他版本
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //设置图片格式，效果为背景透明
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.x = x;
        mLayoutParams.y = y;
        return mLayoutParams;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAddToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAddToWindow = false;
    }


    //---------------------------------------------View移动--------------------------------------------------

    protected abstract void onClick();

    protected abstract void onPosition(int x, int y);

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                viewDownEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                viewUpEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                viewMoveEvent(event);
                break;
            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return true;
    }

    /**
     * view的点击事件
     *
     * @param event 触摸事件
     */
    private void viewDownEvent(MotionEvent event) {
        //记录最近一次Event的坐标
        downX = event.getRawX();
        downY = event.getRawY();
        lastEventX = event.getRawX();
        lastEventY = event.getRawY();
    }

    /**
     * view的抬起事件
     *
     * @param event 触摸事件
     */
    private void viewUpEvent(MotionEvent event) {
        //点击与抬起的坐标不超过3像素，视为点击事件
        if (Math.abs(event.getRawX() - downX) < scaledTouchSlop && Math.abs(event.getRawY() - downY) < scaledTouchSlop) {
            updateView();
            if (mFloatAction != null) {
                mFloatAction.onClick();
            }
            onClick();
        } else {//滑动事件
            scrollToEdge();
        }
    }

    /**
     * 滑动到边缘(距离最小的一边)
     */
    private void scrollToEdge() {
        if (edgeAnimator.isRunning()) {
            edgeAnimator.cancel();
        }
        if ((mLayoutParams.x + mLayoutParams.width / 2) < mRect.centerX()) {//距离左侧最近
            edgeAnimator.setIntValues(mLayoutParams.x, mRect.left);
            edgeAnimator.start();
        } else {//距离右侧最近，或者等于到右边缘的距离
            edgeAnimator.setIntValues(mLayoutParams.x, mRect.right - mLayoutParams.width);
            edgeAnimator.start();
        }
    }

    /**
     * view的移动事件
     *
     * @param event 触摸事件
     */
    private void viewMoveEvent(MotionEvent event) {
        if (mWindowManager == null || mLayoutParams == null || !isAddToWindow) {
            return;
        }
        int moveX = (int) (event.getRawX() - lastEventX);
        int moveY = (int) (event.getRawY() - lastEventY);

        mLayoutParams.x = mLayoutParams.x + moveX;
        mLayoutParams.y = mLayoutParams.y + moveY;
        computerEdge();
        updateView();

        //移动监听回调
        if (mFloatAction != null) {
            mFloatAction.onMove();
        }

        //记录最近一次Event的坐标
        if (!isXEdge()) {//不是X的边界时才获取X坐标
            lastEventX = event.getRawX();
        }
        if (!isYEdge()) {//不是Y的边界时才获取Y坐标
            lastEventY = event.getRawY();
        }
    }

    /**
     * 更新View
     */
    protected void updateView() {
        try {
            if (isAddToWindow && mWindowManager != null && mLayoutParams != null) {
                mWindowManager.updateViewLayout(this, mLayoutParams);
                onPosition(mLayoutParams.x, mLayoutParams.y);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 处理边缘
     */
    private void computerEdge() {
        //到达上边缘
        if (mLayoutParams.y <= mRect.top) {
            mLayoutParams.y = mRect.top;
        }
        //到达下边缘
        if (mLayoutParams.y >= mRect.bottom) {
            mLayoutParams.y = mRect.bottom;
        }
        //到达左边缘
        if (mLayoutParams.x <= mRect.left) {
            mLayoutParams.x = mRect.left;
        }
        //到达右边缘
        if (mLayoutParams.x >= mRect.right) {
            mLayoutParams.x = mRect.right;
        }
    }

    /**
     * 是否是X轴边缘
     */
    private boolean isXEdge() {
        return mLayoutParams.x >= mRect.right || mLayoutParams.x <= mRect.left;
    }

    /**
     * 是否是Y轴边缘
     */
    private boolean isYEdge() {
        return mLayoutParams.y <= mRect.top || mLayoutParams.y >= mRect.bottom;
    }

    public void viewTouchEvent(MotionEvent event) {

    }

    /**
     * 获取当前的位置
     */
    public Point getPosition() {
        return new Point(mLayoutParams.x, mLayoutParams.y);
    }

    public void setFloatAction(onFloatAction floatAction) {
        mFloatAction = floatAction;
    }

    public interface onFloatAction {

        default void onClick() {
        }

        default void onMove() {
        }
    }

    //---------------------------------------------View移动--------------------------------------------------


    protected void updateLayoutParams(WindowManager.LayoutParams layoutParams) {
        if (layoutParams != null && mWindowManager != null && isAddToWindow) {
            System.out.println("GAME-PARAM:--updateLayoutParams");
            mWindowManager.updateViewLayout(this, layoutParams);
        }
    }

    protected int dp(float value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getContext().getResources().getDisplayMetrics());
    }
}
