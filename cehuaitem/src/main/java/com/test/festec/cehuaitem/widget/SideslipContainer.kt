package com.test.festec.cehuaitem.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import com.test.festec.cehuaitem.R
import java.util.*
import kotlin.math.roundToInt

/**
 * 侧滑容器
 *
 * 参数：
 *  android:layout_width="侧滑内容宽度" （选填）
 *  android:option_width="单个侧滑选项卡宽度"（选填）
 *
 *  android:layout_height="侧滑容器高度 + 侧滑选项卡高度"（选填）
 */
class SideslipContainer : ViewGroup, View.OnClickListener {

    constructor(context: Context) : super(context)

    @SuppressLint("Recycle", "CustomViewStyleable")
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SideslipContainer)
        for (i in 0 until typedArray.indexCount) {
            val attr = typedArray.getIndex(i)
            when (attr) {
                R.styleable.SideslipContainer_option_width -> {
                    optionWidth = typedArray.getDimension(i, 0f).toInt()
                }
            }
        }
        typedArray.recycle()
    }

    init {
        setOnClickListener(this)
    }

    // 选项宽度
    private var optionWidth: Int = 0

    // 最大左滑距离
    private var maxToLeft: Int = 0

    // 最大长度
    private var maxWidth: Int = 0

    // 平移动画时间
    private var animatorDuration: Long = 350

    // 按下时的X坐标
    private var startX: Float = 0f

    // 按下时的Y坐标
    private var startY: Float = 0f

    // 用来记录上一个move坐标，用来判断，左滑还是右滑
    private var tempV: Float = 0f

    // 方向：向左false 向右true
    private var direction = false

    // 是否可以触发点击事件
    private var isOpenClick = false

    // 动画是否结束
    private var animatorComplete = true

    // 父级RecyclerView
    private var parentView: MyRecyclerView? = null

    // 所有子View
    private var items: MutableList<View> = mutableListOf()

    // 记录滑动过程中，子view的范围
    private var recordList: MutableList<Record> = mutableListOf()

    // 点击回调
    private var clickCallback: SideslipContainerOnClick? = null

    // 滑动回调
    private var moveCallback: MyRecyclerView.ChildOnTouchCallback? = null

    // 手势识别器
    private var gestureDetector: GestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                scrollBy(distanceX.toInt(), 0)
                return true
            }
        })

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        children.forEach {
            it.measure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var content: SideslipContent? = null
        val options: MutableList<SideslipOption> = mutableListOf()
        items.clear()

        children.forEachIndexed { _, view ->
            if (view is SideslipContent) {
                content = view
            }
            if (view is SideslipOption) {
                options.add(view)
            }
        }

        if (content == null) {
            throw IllegalArgumentException("缺少侧滑内容View ---> SideslipContent()")
        }

        // 确保content，在索引0位
        items.add(content!!)
        items.addAll(options)

        val initLayoutInfo = initLayout()
        items.forEachIndexed { index, view ->
            val leftV = initLayoutInfo[index].left
            val rightV = initLayoutInfo[index].right
            view.layout(leftV, 0, rightV, height)
            recordList.add(Record(leftV, rightV))
        }

        maxWidth = addUpChildWidth(childCount)
        if (parent is MyRecyclerView) {
            parentView = parent as MyRecyclerView
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!animatorComplete) {
            return true
        }
        gestureDetector.onTouchEvent(event)
        super.onTouchEvent(event)
        var endX: Float = 0f
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                // Log.e("TAG","ACTION_DOWN --- ${hashCode()}")
                moveCallback?.currentChild(this)
                startX = event.x
                startY = event.y
                tempV = event.x
            }
            MotionEvent.ACTION_UP -> {
                // Log.e("TAG","ACTION_UP --- ${hashCode()}")
                moveCallback?.currentChild(null)
                moveCallback?.originalChild(this)
                endX = event.x
                tempV = event.x

                isOpenClick = startX == endX

                if (!isOpenClick && childCount > 1) {
                    confirmLocation(event)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                // Log.e("TAG","ACTION_MOVE --- ${hashCode()}")
                // 滑动时，RecyclerView停止滚动

                val scale = getScale()
                if (scale == 0 || scale == 100) {
                    moveCallback?.listRecoverYScroll()
                } else {
                    moveCallback?.listStopYScroll()
                    moveCallback?.currentChildMove()
                }
                getDirection(event)
                listenerChildLocation()
            }
        }
        return true
    }

    override fun onClick(v: View?) {
        if (isOpenClick && animatorComplete) {
            getChild()
        }
    }

    // 平移动画
    // direction(方向)
    // false：向左滑
    // true 向右滑
    @SuppressLint("Recycle")
    private fun translateAnimator(remaining: Float, direction: Boolean) {
        if (!animatorComplete) {
            return
        }
        val fromX: Float = scrollX.toFloat()
        val toX = if (!direction) {
            scrollX.toFloat() + remaining
        } else {
            scrollX.toFloat() - remaining
        }

        val animator = ValueAnimator.ofFloat(fromX, toX)
        animator.addUpdateListener {
            val fv = it.animatedValue.toString().toFloat()
            scrollTo(fv.toInt(), 0)
            listenerChildLocation()
        }
        animator.duration = animatorDuration
        animator.addListener(object : Animator.AnimatorListener {
            // 动画开始
            override fun onAnimationStart(animation: Animator?) {
                animatorComplete = false
            }

            // 动画结束
            override fun onAnimationEnd(animation: Animator?) {
                animatorComplete = true
                moveCallback?.listRecoverYScroll()
            }

            // 动画取消
            override fun onAnimationCancel(animation: Animator?) {
                animatorComplete = true
                moveCallback?.listRecoverYScroll()
            }

            // 动画重复
            override fun onAnimationRepeat(animation: Animator?) {
                animatorComplete = false
            }

        })
        animator.start()
    }

    // 初始化布局位置
    fun initLayout() : MutableList<Record> {
        var initList: MutableList<Record> = mutableListOf()
        children.forEachIndexed { index, _ ->
            val leftV = getChildLeft(index)
            val rightV = getChildRight(index)
            initList.add(Record(leftV,rightV))
        }
        return initList
    }

    // 设置平移动画时间
    fun setAnimatorDuration(duration: Long) {
        animatorDuration = duration
    }

    // 传入点击事件监听
    fun addOnClickListener(listener: SideslipContainerOnClick) {
        if (clickCallback == null) {
            clickCallback = listener
        }
    }

    // 传入点击事件监听
    fun addOnChildMoveCallback(callback: MyRecyclerView.ChildOnTouchCallback) {
        if (moveCallback == null) {
            moveCallback = callback
        }
    }

    // 设置选项卡宽度
    fun setOptionWidth(optionW: Int) {
        optionWidth = optionW
    }

    // 状态重置为初始化
    fun sideslipStateRest() {
        scrollTo(0, 0)
    }

    // 滑动的Item还原
    fun sideslipRecover() {
        val currentSideslip = parentView?.currentChild

        if (currentSideslip.hashCode() != hashCode()) {
            val scale = getScale()
            if (scale > 0) {
                translateAnimator(scrollX.toFloat(), true)
            }
        }
    }

    // 删除所有选项卡
    fun removeAllOption() {
        val sideslipContainer = children.first()
        removeAllViews()
        addView(sideslipContainer)
    }

    // 添加选项卡
    fun addOption(options: SideslipOption) {
        removeAllOption()
        addView(options)
    }

    // 添加多个选项卡
    fun addMultipleOption(options: MutableList<SideslipOption>) {
        removeAllOption()
        options.forEach {
            addView(it)
        }
    }

    // 回弹（手指离开屏幕后）
    private fun confirmLocation(event: MotionEvent) {

        // 手指离开前，滑动的方向
        var lastDirection: Boolean = false
        if (startX > event.x) {
            // 向左滑
            lastDirection = false
        } else if (startX < event.x) {
            // 向右滑
            lastDirection = true
        }

        val scale = getScale()
        if (scale == 0 || scale == 100) {
            return
        }

        // 剩余距离
        var remaining: Float = 0f

        // 向左滑 -- 超过10%
        // 这里一定要写成 >= 10，只写 > 10，因为有可能刚好是10
        if (!lastDirection && (scale >= 10)) {
            remaining = (maxToLeft - scrollX).toFloat()
            translateAnimator(remaining, false)
            return
        } else {
            if (!lastDirection) {
                remaining = scrollX.toFloat()
                translateAnimator(remaining, true)
                return
            }
        }

        if (lastDirection && (scale <= 90)) {
            // 向右滑 --- 超过10%
            remaining = scrollX.toFloat()
            translateAnimator(remaining, true)
        } else {
            if (lastDirection) {
                remaining = (maxToLeft - scrollX).toFloat()
                translateAnimator(remaining, false)
            }
        }

    }

    // 获取滑动百分比
    private fun getScale(): Int {
        // 没有选项
        if (children.last() is SideslipContent) {
            return 0
        }
        computeScroll()
        return ((scrollX.toFloat() / maxToLeft.toFloat()) * 100).roundToInt()
    }

    // 计算滚动，滚动时，时时监听
    override fun computeScroll() {
        // 最大左滑距离
        maxToLeft = addUpChildWidth(childCount) - width

        // 屏蔽滑动非法值
        if (scrollX > maxToLeft) {
            scrollTo(maxToLeft, 0)
        } else if (scrollX < 0) {
            scrollTo(0, 0)
        }

    }

    // 获取子View的left
    private fun getChildLeft(currentIndex: Int): Int {
        if (currentIndex == 0) {
            return 0
        } else {
            return addUpChildWidth(currentIndex)
        }
    }

    // 获取子View的right
    private fun getChildRight(currentIndex: Int): Int {
        if (currentIndex == 0) {
            return width
        } else {
            return addUpChildWidth(currentIndex + 1)
        }

    }

    // 累计子View的width
    // longer：忽略child[0]的计算
    private fun addUpChildWidth(currentIndex: Int, longer: Boolean = false): Int {
        var totalWidth = 0
        for (i in 0 until currentIndex) {
            var childWidth = optionWidth
            if (i == 0) {
                childWidth = width
            }
            totalWidth += childWidth

        }
        if (longer) {
            totalWidth -= width
        }
        return totalWidth
    }

    // 监听滚动时，子view的位置
    private fun listenerChildLocation() {
        computeScroll()
        items.forEachIndexed { index, view ->
            val left = if ((view.left - scrollX) < 0) {
                0
            } else {
                (view.left - scrollX)
            }
            val right = view.right - scrollX
            recordList[index].left = left
            recordList[index].right = right
        }
    }

    // 获取触摸的区域
    private fun getChild() {
        recordList.forEachIndexed { index, record ->
            val currentLeft = record.left
            val currentRight = record.right
            val currentTop = 0
            val currentBottom = height

            if ((startX >= currentLeft && startX <= currentRight)
                && (startY >= currentTop && startY <= currentBottom)
            ) {
                if (index == 0) {
                    clickCallback?.contentOnClick()
                } else {
                    val sideslipOption = items[index] as SideslipOption
                    clickCallback?.optionOnClick(sideslipOption.tag)
                }
                return
            }
        }
    }

    // 时时获取滑动方向
    private fun getDirection(event: MotionEvent) {
        computeScroll()
        if (event.x > tempV) {
            if (!direction) {
                direction = true
            }

            if (scrollX == 0) {
                // Log.e("TAG", "已右滑至最大值")
                return
            }

            // 向右滑动
            // Log.e("TAG", "向右滑动：$scrollX --- $direction")

            tempV = event.x
        } else if (event.x < tempV) {
            if (direction) {
                direction = false
            }

            if (scrollX == maxToLeft) {
                // Log.e("TAG", "已左滑至最大值")
                return
            }

            // 向左滑动
            // Log.e("TAG", "向左滑动：$scrollX --- $direction")

            tempV = event.x
        }
    }

    // 记录滚动时，子view的范围
    data class Record(var left: Int = 0, var right: Int = 0)

    // 点击回调
    interface SideslipContainerOnClick {

        // 选项点击事件
        fun optionOnClick(optionTag: Any)

        // 内容区域点击事件
        fun contentOnClick()

    }

}
