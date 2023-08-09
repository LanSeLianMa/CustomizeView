package com.test.festec.lunbotu

import DensityUtil
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.test.festec.lunbotu.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    // 是否轮播完成
    private var reset = false

    // 上一个轮播图索引
    private var prePosition = 0

    private var handler: Handler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0) {
                var item = 0
                if (binding.viewPage2.currentItem == (images.size - 1)) {
                    reset = true
                }

                if (reset) {
                    // 反向轮播
                    item = binding.viewPage2.currentItem - 1
                    if ((binding.viewPage2.currentItem - 1) <= 0) {
                        item = 0
                        reset = false
                    }
                } else {
                    // 正向轮播
                    item = binding.viewPage2.currentItem + 1
                }

                binding.viewPage2.currentItem = item
                sendEmptyMessageDelayed(0, 3000)
            }
        }
    }

    private var images: MutableList<Int> = mutableListOf(
        R.drawable.a,
        R.drawable.b,
        R.drawable.c,
        R.drawable.d,
        R.drawable.e
    )

    private var describes: MutableList<String> = mutableListOf(
        "R.drawable.a",
        "R.drawable.b",
        "R.drawable.c",
        "R.drawable.d",
        "R.drawable.e"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pointView()
        viewPage()
    }

    // 轮播图
    private fun viewPage() {
        val adapter = BannerAdapter(this, images)
        binding.viewPage2.adapter = adapter
        binding.viewPage2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.pointContainer.getChildAt(prePosition).isEnabled = false
                binding.pointContainer.getChildAt(position).isEnabled = true
                binding.describe.text = describes[position]
                prePosition = position
            }

            /**
             * 表示 ViewPager2 处于空闲、稳定状态。 当前页面
             * 完全在视图中，并且没有动画正在进行。
             */
            // int SCROLL_STATE_IDLE = 0;

            /**
             * 表示ViewPager2当前正在被用户拖动，或者以编程方式拖动
             * 通过假拖动功能。
             */
            // int SCROLL_STATE_DRAGGING = 1;

            /**
             * 表示 ViewPager2 正在确定最终位置。
             */
            // int SCROLL_STATE_SETTLING = 2;

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                when (state) {
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        handler.removeMessages(0)
                        handler.sendEmptyMessageDelayed(0, 3000)
                    }
                    ViewPager2.SCROLL_STATE_DRAGGING -> {
                        handler.removeMessages(0)
                    }
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                // Log.e("TAG","onPageScrolled")
            }

        })

        binding.viewPage2Container.setTouchE(object : View.OnTouchListener {

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Log.e("TAG", "onTouch==手指按下")
                        handler.removeMessages(0)
                    }
                    MotionEvent.ACTION_UP -> {
                        Log.e("TAG", "onTouch==手指离开")
                        handler.removeMessages(0)
                        handler.sendEmptyMessageDelayed(0, 3000)
                    }
                }
                return false
            }

        })
        handler.sendEmptyMessageDelayed(0, 3000)
    }

    // 底部高亮tag
    private fun pointView() {
        images.forEachIndexed { index, _ ->
            val point = ImageView(this)
            point.setBackgroundResource(R.drawable.point_selector)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.bottomMargin = DensityUtil.dp2px(this, 8f)

            if (index == 0) {
                point.isEnabled = true
                binding.describe.text = describes[index]
            } else {
                point.isEnabled = false
                params.leftMargin = DensityUtil.dp2px(this, 8f)
            }

            point.layoutParams = params
            binding.pointContainer.addView(point)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

}