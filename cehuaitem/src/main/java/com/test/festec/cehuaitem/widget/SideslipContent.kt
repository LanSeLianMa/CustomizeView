package com.test.festec.cehuaitem.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.children

/**
 * 侧滑内容
 *
 *  宽高随便填，它们的值由SideslipContainer控制
 *  layout_width
 *  layout_height
 */
class SideslipContent : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

}