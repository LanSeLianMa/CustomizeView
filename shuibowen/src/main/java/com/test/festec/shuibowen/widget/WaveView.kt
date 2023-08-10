package com.test.festec.shuibowen.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.test.festec.shuibowen.util.DensityUtil

class WaveView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private var paint: Paint = Paint()
    private var downX: Float = 0f
    private var downY: Float = 0f
    private var radioScope: Int = 0

    private fun initView() {
        radioScope = 5
        paint.color = Color.RED
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE

        // 描边的宽度
        paint.strokeWidth = 10f
    }

    private var handler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            // 圆角范围递增
            radioScope += 5

            // 透明度递减，0 - 255
            var alpha = paint.alpha
            alpha -= 5
            if (alpha < 0) {
                alpha = 0
            }

            paint.alpha = alpha
            // invalidate()
            postInvalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                initView()
                // invalidate()
                postInvalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        if (paint.alpha > 0 && downX > 0 && downY > 0) {
            handler.removeMessages(0)
            canvas?.drawCircle(
                downX,
                downY,
                DensityUtil.dp2px(context, radioScope.toFloat()).toFloat(),
                paint
            )
            handler.sendEmptyMessageDelayed(0, 20)
        }
    }

}