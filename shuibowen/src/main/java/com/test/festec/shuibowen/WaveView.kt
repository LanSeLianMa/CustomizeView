package com.test.festec.shuibowen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class WaveView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private var paint: Paint

    init {
        paint = Paint()
        paint.color = Color.RED
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(0f,0f,DensityUtil.dp2px(context,20f).toFloat() ,paint)
    }

}