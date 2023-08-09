package com.test.festec.cehuaitem.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView

/**
 * 侧滑选项卡
 *
 * 参数：
 *  android:background="背景" （选填）
 *  android:text="选项卡名称"（选填）
 *  android:tag="标识"（必填）
 *
 *  宽高随便填，它们的值由SideslipContainer控制
 *  layout_width
 *  layout_height
 */
@SuppressLint("AppCompatCustomView")
class SideslipOption : TextView {

    constructor(context: Context, parameterTag: Any) : super(context) {
        tag = parameterTag
    }
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onDraw(canvas: Canvas?) {
        if (tag == null) {
            throw IllegalArgumentException(
                """
                SideslipOption的Tag属性不能为null，此参数必填，
                用于SideslipListItemListener.optionOnClick(optionTag: Any)返回optionTag
            """.trimIndent()
            )
        }

        paint.color = Color.WHITE

        // 文字居中
        val x = (width / 2f) - textSize
        val y = height / 2f + (textSize / 3)
        canvas?.drawText(text.toString(), x, y, paint)
    }

}