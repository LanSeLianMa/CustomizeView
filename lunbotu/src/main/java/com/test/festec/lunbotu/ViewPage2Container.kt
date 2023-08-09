package com.test.festec.lunbotu

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.RelativeLayout

class ViewPage2Container : RelativeLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context,attributeSet: AttributeSet): super(context,attributeSet)

    var touchEvent: OnTouchListener? = null
    fun setTouchE(tl: OnTouchListener) {
        touchEvent = tl
    }

    // 事件拦截
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        touchEvent?.onTouch(this,ev)
        return super.onInterceptTouchEvent(ev)
    }

}