package com.test.festec.customizebutton

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.util.*

class MyButton : View, View.OnClickListener {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        // 加载图片资源
        btnBackground = BitmapFactory.decodeResource(resources, R.drawable.switch_background)
        button = BitmapFactory.decodeResource(resources, R.drawable.slide_button)

        paint = Paint()
        // 抗锯齿
        paint.isAntiAlias = true

        // 点击事件
        setOnClickListener(this)

        antiShake = AntiShake(500)
    }

    private var btnBackground: Bitmap
    private var button: Bitmap
    private var buttonWidth: Int = 0
    private var paint: Paint
    private val antiShake : AntiShake

    // X轴最大移动距离
    private var maxMoveV: Int = 0

    // 时时的移动距离
    private var translateX: Float = 0f

    // 方向：向左false 向右true
    private var direction = false

    // 手指按下时的X轴位置
    var startX: Float = 0f

    // 手指按下时的Y轴位置
    var startY: Float = 0f

    // 用来记录上一个move坐标，用来判断，左滑还是右滑
    private var tempV: Float = 0f

    // 按钮的left
    private var btnLeft: Float = 0f

    // 按钮的right
    private var btnRight: Float = button.width.toFloat()

    // 当前手指按下的位置
    private var currentLocation: Float = 0f

    // 按下的区域，是否处于按钮区域
    private var isBtn = false

    // offOrNo：开true 关false
    var offOrNo = false

    // 是否可以触发点击事件
    var isOpenClick = false

    // 平移动画是否结束
    var animatorComplete = true

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthModel = MeasureSpec.getMode(widthMeasureSpec)

        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightModel = MeasureSpec.getMode(heightMeasureSpec)

        if (widthModel == MeasureSpec.AT_MOST && heightModel == MeasureSpec.AT_MOST) {
            setMeasuredDimension(btnBackground.width, btnBackground.height)
        } else if (widthModel == MeasureSpec.AT_MOST) {
            setMeasuredDimension(btnBackground.width, heightSize)
        } else if (heightModel == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, btnBackground.height)
        } else {
            setMeasuredDimension(widthSize, heightSize)
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        // 改变图片尺寸
        btnBackground = Bitmap.createScaledBitmap(
            btnBackground, width, height, true
        )
        // 按钮width没有覆盖背景一半区域，手动增加按钮width
        buttonWidth = (width / 2) + DensityUtil.dip2px(context, 12f)
        button = Bitmap.createScaledBitmap(
            button, buttonWidth, height, true
        )

        canvas?.drawBitmap(btnBackground, 0f, 0f, paint)
        canvas?.drawBitmap(button, translateX, 0f, paint)

        // 获取按钮，最大X轴移动值
        maxMoveV = width - buttonWidth
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // 手指离开时的X轴位置
        var endX: Float = 0f

        super.onTouchEvent(event)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                tempV = event.x

                isBtnContent(event)
                if (isBtn && animatorComplete) {
                    currentLocation = startX - btnLeft
                }
            }
            MotionEvent.ACTION_UP -> {
                if (antiShake.isFastClick()) {
                    endX = event.x
                    tempV = event.x

                    isOpenClick = startX == endX

                    if (isBtn && !isOpenClick && animatorComplete) {
                        confirmCoordinate()
                    }
                }

            }
            MotionEvent.ACTION_MOVE -> {
                if (isBtn && animatorComplete) {
                    // getDirection(event)
                    translateCalculate(event)
                    invalidate()
                }
            }
        }
        return true
    }

    override fun onClick(v: View?) {
        if (isOpenClick && animatorComplete) {
            if (offOrNo) {
                translateAnimator((btnLeft + maxMoveV) - translateX, 0f)
                offOrNo = false
            } else {
                translateAnimator(btnLeft, (btnLeft + maxMoveV))
                offOrNo = true
            }
        }
    }

    // 判断是否在按钮区域
    private fun isBtnContent(event: MotionEvent) {
        getBtnLeftRight()
        isBtn = event.x > btnLeft && event.x < btnRight
    }

    // 确认停留的坐标（手指离开屏幕后）
    private fun confirmCoordinate() {

        // 更新按钮的最新位置
        getBtnLeftRight()

        // 获取百分比
        val round = ((translateX / maxMoveV) * 100).toInt()

        if (round == 0 || round == 100) {
            offOrNo = round == 100
            return
        }

        var animatorStartX = 0f
        var animatorEndX = 0f

        // 超过50%
        // 这里一定要写成 >= 50，只写 > 50，因为有可能刚好是50
        if (round >= 50) {
            // 停留在右边
            animatorStartX = btnLeft
            animatorEndX = maxMoveV.toFloat()
            offOrNo = true
        } else {
            // 停留在左边
            animatorStartX = btnLeft
            animatorEndX = 0f
            offOrNo = false
        }

        translateAnimator(animatorStartX, animatorEndX)
        getBtnLeftRight(true)
    }

    // 平移动画
    private fun translateAnimator(
        animatorStartX: Float,
        animatorEndX: Float,
        duration: Long = 300
    ) {

        val animator = ValueAnimator.ofFloat(animatorStartX, animatorEndX)
        animator.duration = duration

        animator.addUpdateListener {
            translateX = it.animatedValue as Float
            // Log.e("TAG", "$translateX")
            invalidate()
        }

        animator.addListener(object : Animator.AnimatorListener {

            // 动画开始
            override fun onAnimationStart(animation: Animator?) {
                // Log.e("TAG","onAnimationStart")
                animatorComplete = false
            }

            // 动画结束
            override fun onAnimationEnd(animation: Animator?) {
                animatorComplete = true
                // Log.e("TAG","onAnimationEnd")
            }

            // 动画取消
            override fun onAnimationCancel(animation: Animator?) {
                animatorComplete = true
                // Log.e("TAG","onAnimationCancel")
            }

            // 动画重复
            override fun onAnimationRepeat(animation: Animator?) {
                animatorComplete = false
            }

        })

        animator.start()

    }

    // 获取滑动方向
    private fun getDirection(event: MotionEvent) {
        if (event.x > tempV) {
            if (translateX == maxMoveV.toFloat()) {
                // Log.e("TAG", "已右滑至最大值")
                return
            }

            // 向右滑动
            // Log.e("TAG", "向右滑动：$translateX")

            if (!direction) {
                direction = true
            }

            tempV = event.x
        } else if (event.x < tempV) {
            if (translateX == 0f) {
                // Log.e("TAG", "已左滑至最大值")
                return
            }
            // 向左滑动
            // Log.e("TAG", "向左滑动：$translateX")

            if (direction) {
                direction = false
            }
            tempV = event.x
        }
    }

    // 计算平移距离
    private fun translateCalculate(event: MotionEvent) {
        // 平移的距离
        translateX = event.x - currentLocation

        // 屏蔽非法值
        if (translateX >= maxMoveV) {
            translateX = maxMoveV.toFloat()
        } else if (translateX <= 0) {
            translateX = 0f
        }

        getBtnLeftRight()

    }

    // 获取按钮的位置
    // complete：动画是否结束
    // offOrNo：开true 关false
    private fun getBtnLeftRight(complete: Boolean = false) {
        if (complete) {
            if (offOrNo) {
                translateX = maxMoveV.toFloat()
            } else {
                translateX = 0f
            }
        }
        btnRight = translateX + buttonWidth
        btnLeft = btnRight - buttonWidth
    }

    // 防止快速点击
    class AntiShake(minTime: Int = 1000) {

        // 两次点击间隔不能少于1000ms
        private var MIN_DELAY_TIME : Int

        private var lastClickTime: Long = 0

        init {
            MIN_DELAY_TIME = minTime
        }

        fun isFastClick(): Boolean {
            var flag = true
            val currentClickTime = System.currentTimeMillis()
            if ((currentClickTime - lastClickTime) < MIN_DELAY_TIME) {
                flag = false
            }
            lastClickTime = currentClickTime
            return flag
        }

    }

}