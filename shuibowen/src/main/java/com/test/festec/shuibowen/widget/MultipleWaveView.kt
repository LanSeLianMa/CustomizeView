package com.test.festec.shuibowen.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import com.test.festec.shuibowen.util.DensityUtil
import java.util.Timer
import java.util.TimerTask


class MultipleWaveView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private var waves = mutableListOf<Wave>()

    private var handler = object : Handler(Looper.myLooper()!!) {

        override fun handleMessage(msg: Message) {
            waves.forEach {
                if (it.paint.alpha > 0) {
                    updateState(it)
                }
            }
        }

    }

    fun updateState(wave: Wave) {
        wave.radioScope += 5
        // 透明度递减，0 - 255
        var alpha = wave.paint.alpha
        alpha -= 5
        if (alpha < 0) {
            alpha = 0
        }

        wave.paint.alpha = alpha
        postInvalidate()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                // Log.e("TAG", "size：${waves.size}")
                waves.add(Wave(downX = event.x, downY = event.y, radioScope = 5))
                postInvalidate()
            }
        }
        return true
    }

    // 删除透明度为0的水波纹
    @RequiresApi(Build.VERSION_CODES.N)
    private fun clearAlpha0Wave() {
        waves.removeIf {
            it.paint.alpha == 0
        }
        // Log.e("TAG", "size：${waves.size}")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDraw(canvas: Canvas?) {
        if (waves.isNotEmpty()) {
            handler.removeMessages(0)
            waves.forEach {
                if (it.paint.alpha > 0) {
                    canvas?.drawCircle(
                        it.downX,
                        it.downY,
                        DensityUtil.dp2px(context, it.radioScope.toFloat()).toFloat(),
                        it.paint
                    )
                } else {
                    handler.postDelayed(Runnable {
                        clearAlpha0Wave()
                    },50)
                }
            }
            handler.sendEmptyMessageDelayed(0, 50)
        }
    }

    data class Wave(
        var paint: Paint = Paint().apply {
            color = Color.RED
            isAntiAlias = true
            style = Paint.Style.STROKE
            // 描边的宽度
            strokeWidth = 10f
        },
        var radioScope: Int = 0,
        var downX: Float = 0f,
        var downY: Float = 0f,
    )

}
