package com.test.festec.cehuaitem.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.test.festec.cehuaitem.layoutmanager.CustomLinerLayoutManager

class MyRecyclerView : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {

            /**
             *
             * public static final int SCROLL_STATE_IDLE = 0;  :  RecyclerView 当前未滚动。
             *
             * public static final int SCROLL_STATE_DRAGGING = 1;  :  RecyclerView 当前正在被外部输入（例如用户触摸输入）拖动。
             *
             * public static final int SCROLL_STATE_SETTLING = 2;  :  RecyclerView 当前正在动画到最终位置，而不是在外部控制。
             */
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    childrenRecover()
                }
            }
        })
    }

    // view初始化
    private var viewInit = false

    // 上一会触摸的子View
    var originalChild: SideslipContainer? = null

    // 当前触摸的子View
    var currentChild: SideslipContainer? = null

    private var customLayoutManager: CustomLinerLayoutManager? = null

    private var childMoveCallback = object : ChildOnTouchCallback {
        override fun currentChildMove() {
            childrenRecover()
        }

        override fun originalChild(originalSideslip: SideslipContainer?) {
            originalChild = originalSideslip
        }

        override fun currentChild(currentContainer: SideslipContainer?) {
            currentChild = currentContainer
        }

        override fun listStopYScroll() {
            // Log.e("TAG", "List停止滚动")
            if (customLayoutManager!!.getEnabled()) {
                customLayoutManager?.setScrollEnabled(false)
            }
        }

        override fun listRecoverYScroll() {
            // Log.e("TAG", "List恢复滚动")
            if (!(customLayoutManager!!.getEnabled())) {
                customLayoutManager?.setScrollEnabled(true)
            }
        }

    }

    // 子View恢复
    private fun childrenRecover() {
        children.forEach {
            (it as SideslipContainer).sideslipRecover()
        }
    }

    override fun onViewAdded(child: View?) {
        // Log.e("TAG","onViewAdded")
        val sideslipContainer = (child as SideslipContainer)
        sideslipContainer.addOnChildMoveCallback(childMoveCallback)
    }

    // 当复用item，彻底超出屏幕，不可见时执行
    override fun onViewRemoved(child: View?) {
        // Log.e("TAG","onViewRemoved")
        (child as SideslipContainer).sideslipStateRest()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        // Log.e("TAG","onWindowFocusChanged")
        super.onWindowFocusChanged(hasWindowFocus)
        if (!viewInit) {
            customLayoutManager = (layoutManager as CustomLinerLayoutManager)
        }
        viewInit = true
    }

    interface ChildOnTouchCallback {

        // 有子View侧滑了
        fun currentChildMove()

        // 上一个触摸的子View
        fun originalChild(originalSideslip: SideslipContainer?)

        // 当前触摸的子View
        fun currentChild(currentContainer: SideslipContainer?)

        // 列表停止Y轴滚动
        fun listStopYScroll()

        // 列表恢复Y轴滚动
        fun listRecoverYScroll()

    }

}