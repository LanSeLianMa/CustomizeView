package com.test.festec.myviewpager

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.children
import kotlin.math.abs
import kotlin.math.roundToInt

class MyViewPager : ViewGroup {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        loadGestureDetector()
    }

    private var currentIndex = 0
    private var maxWidth: Int = 0
    private var maxIndex: Int = 0
    private var averageViewWidth : Int = 0
    private var gestureDetector: GestureDetector? = null
    private fun loadGestureDetector() {
        gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

                /**
                 * @param e1 开始滚动的第一个向下运动事件。
                 * @param e2 触发当前onScroll的移动动作事件。
                 *
                 * @param distanceX 自上次以来沿X轴滚动的距离
                 * @param distanceY 自上次以来沿Y轴滚动的距离
                 *
                 * 注意：scrollBy(x,y)，移动的坐标值和传统方式不一样，是相反的，
                 *
                 * 向左移动 --> 正数
                 * 向右移动 --> 负数
                 *
                 * 向上移动 --> 正数
                 * 向下移动 --> 负数
                 */
                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent?,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {

                    // 绝对定位
                    // scrollTo()

                    // Log.e("TAG",distanceX.toString())

                    // 有个bug
                    // 如果子view有滚动控件，水平反向滑滚动控件，distanceX有时会出现，非常大的值，导致反向水平滚动
                    // 目前测试正常情况下，最大的值不超过70
                    if (abs(distanceX) < 80) {
                        // 相对定位
                        scrollBy(distanceX.toInt(), 0)
                    }

                    return true
                }

            })
    }
    private var pagerListener : MyViewPagerListener? = null

    // 对外暴露监听
    fun setPagerListener(listener : MyViewPagerListener) {
        pagerListener = listener
    }

    // 测量子View
    // 如果不测量，除了一级子view，其他子孙view不显示，
    // 因为一级子view在onLayout中已经指定了位置，而其他子孙view需要其父级测量
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        children.forEach {
            it.measure(widthMeasureSpec,heightMeasureSpec)
        }
    }

    // 子View布局
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // 每个子view独占一页，水平布局
        children.forEachIndexed { index, _ ->
            val view = getChildAt(index)
            view.layout((index * width), 0, ((index + 1) * width), height)
        }

        // 最大索引
        getMaxIndex()

        // X轴，最大移动距离
        getMaxWidth()

        // 每个view的最大width
        getAverageViewWidth()
    }

    private var childStartX = 0f
    private var childEndX = 0f
    private var childStartY = 0f
    private var childEndY = 0f

    // 事件拦截器
    // 处理和子view的滑动事件冲突
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                childStartX = ev.x
                childStartY = ev.y
            }
            MotionEvent.ACTION_UP -> {
                childEndX = ev.x
                childEndY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                var distanceX = abs(childEndX - childStartX)
                var distanceY = abs(childEndY - childStartY)

                if (distanceX > distanceY) {
                    return true
                }

                // Log.e("TAG","distanceX：$distanceX --- distanceY：$distanceY")
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

    // 触摸事件
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)

        // 将事件传递给手势识别器
        gestureDetector?.onTouchEvent(event)

        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                // Log.e("TAG","离开")
                scrollToPager()
            }
        }
        return true
    }

    // 手指离开屏幕后，确定当前停留的View
    private fun scrollToPager() {

        // 这里加上一个view的width,是因为最后一个view不能滑
        var currentScrollX = (scrollX + averageViewWidth).toFloat()

        // 滑动的距离 / 每个view的最大width = 当前页.另一页的width百分比
        // 四舍五入
        currentIndex = (currentScrollX / averageViewWidth).roundToInt() - 1

        // 屏蔽索引非法值
        if (currentIndex <= 0) {
            currentIndex = 0
        } else if (currentIndex >= maxIndex) {
            currentIndex = maxIndex
        }
        pagerAnimator()

    }

    // 页面之间切换的过渡动画
    private fun pagerAnimator() {
        // 最终回到哪个页面的X坐标
        var confirmXV = (averageViewWidth * currentIndex)

        // 页面之间过渡的距离，取绝对值
        // var confirmDistance = abs(currentScrollX - (averageViewWidth * (currentIndex + 1)))

        // 页面之间过渡动画
        var animator = ValueAnimator.ofInt(scrollX, confirmXV)
        animator.addUpdateListener {
            scrollTo(it.animatedValue as Int, 0)
        }
        animator.duration = 200
        animator.start()

        pagerListener?.addUpdatePagerListener(currentIndex)
    }

    // 屏蔽滑动非法值，避免滑动越界
    private fun maxMoveX() {
        if (scrollX <= 0) {
            scrollTo(0, 0)
        } else if (scrollX >= maxWidth) {
            scrollTo(maxWidth, 0)
        }
    }

    // 计算视图滑动
    // 滑动时视图，时时调用
    override fun computeScroll() {
        super.computeScroll()
        // Log.e("TAG","currentIndex：$currentIndex --- scrollX：$scrollX")
        maxMoveX()
    }

    // 前往指定页
    fun fromToPager(position : Int) {
        // 屏蔽索引非法值
        if (position < 0 || position > maxIndex) {
            throw IllegalArgumentException("数组越界")
        }

        currentIndex = position
        pagerAnimator()
    }

    // 最大索引
    private fun getMaxIndex() {
        maxIndex = (childCount - 1)
    }

    // X轴，最大移动宽度
    private fun getMaxWidth() {
        // X轴，最大移动宽度，最后一个view不能滑，所以不算
        // maxWidth = (childCount * width) - width
        maxWidth = maxIndex * width
    }

    // 每个view的最大width
    private fun getAverageViewWidth() {
        averageViewWidth = maxWidth / maxIndex
    }

    // View改变监听
    interface MyViewPagerListener {

        fun addUpdatePagerListener(position : Int)

    }

}