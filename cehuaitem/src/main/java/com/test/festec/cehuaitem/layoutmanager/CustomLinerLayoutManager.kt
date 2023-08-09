package com.test.festec.cehuaitem.layoutmanager

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

// 重写LayoutManager，动态让RecyclerView 禁止/恢复 Y轴滚动
open class CustomLinerLayoutManager(context: Context) : LinearLayoutManager(context) {

    private var isScrollEnabled = true

    fun getEnabled(): Boolean {
        return isScrollEnabled
    }

    fun setScrollEnabled(flag: Boolean) {
        isScrollEnabled = flag
    }

    override fun canScrollVertically(): Boolean {
        return isScrollEnabled && super.canScrollVertically();
    }

}