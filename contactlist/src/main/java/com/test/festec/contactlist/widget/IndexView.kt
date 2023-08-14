package com.test.festec.contactlist.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.test.festec.contactlist.util.DensityUtil

class IndexView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private val words = mutableListOf<String>(
        "A", "B", "C", "D", "E", "F", "G", "H", "R",
        "J", "K", "L", "M", "N", "O", "P", "Q", "R",
        "S", "T", "U", "V", "W", "X", "Y", "Z",
    )

    private val wordLocation = mutableListOf<RecordLocation>()
    private val paint = Paint()
    private var touchListener : IndexView.TouchListener? = null

    init {
        paint.isAntiAlias = true
        paint.color = Color.WHITE
        paint.textSize = DensityUtil.sp2px(context, 16f).toFloat()
    }

    fun addTouchListener(listener: TouchListener) {
        if (touchListener == null) {
            touchListener = listener
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val pair = averageLocation(event)
                touchListener?.onDown(pair.first,pair.second)
            }
            MotionEvent.ACTION_UP -> {}
            MotionEvent.ACTION_MOVE -> {
                val pair = averageLocation(event)
                touchListener?.onMove(pair.first,pair.second)
            }
        }
        return true
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        words.forEachIndexed { index, element ->
            // 利用getTextBounds获取到的值包括：
            // rect.left --- rect.right
            // rect1.top --- rect.bottom
            // rect.width --- rect.height

            val rect = Rect()
            // 0,1的取一个字母
            paint.getTextBounds(element, 0, 1, rect)

            // 字母的高和宽
            val wordWidth = rect.width()
            val wordHeight = rect.height()

            // 计算每个字母在视图上的位置
            val wordX = (width / 2) - (wordWidth / 2)
            val wordY = ((height / words.size) * (index + 1)) - (wordHeight / 2)
            canvas?.drawText(element, wordX.toFloat(), wordY.toFloat(), paint)

            val averageHeight = (height / words.size)
            wordLocation.add(
                RecordLocation(
                    index * averageHeight,
                    (index * averageHeight) + averageHeight
                )
            )
        }
    }

    private fun averageLocation(event: MotionEvent?): Pair<String,Int> {
        var selectZm: String = ""
        var position: Int = 0
        wordLocation.forEachIndexed { index, element ->
            if (event!!.y >= element.top && event.y < element.bottom) {
                selectZm = words[index]
                position = index
                return@forEachIndexed
            }
        }
        return Pair(selectZm,position)
    }

    data class RecordLocation(var top: Int, var bottom: Int)

    interface TouchListener {
        fun onDown(tag: String, position: Int)
        fun onMove(tag: String, position: Int)
    }

}