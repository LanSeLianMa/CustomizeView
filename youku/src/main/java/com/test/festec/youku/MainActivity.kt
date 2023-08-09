package com.test.festec.youku

import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.test.festec.youku.databinding.ActivityMainBinding

class MainActivity : Activity(),View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private var viewInitComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.iconHome.setOnClickListener(this)
        binding.iconMenu.setOnClickListener(this)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!viewInitComplete) {
            binding.level2.tag = true
            binding.level3.tag = true
            MyViewAnimator.hideView(binding.level2,0)
            MyViewAnimator.hideView(binding.level3,0)
        }
        viewInitComplete = true
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.iconHome -> {
                // Toast.makeText(this,"iconHome", Toast.LENGTH_SHORT).show()
                binding.level2.tag = if ((binding.level2.tag as Boolean)) {
                    MyViewAnimator.showView(binding.level2)
                    false
                } else {
                    MyViewAnimator.hideView(binding.level2)
                    if (!(binding.level3.tag as Boolean)) {
                        MyViewAnimator.hideView(binding.level3)
                        binding.level3.tag = true
                    }
                    true
                }

            }
            binding.iconMenu -> {
                // Toast.makeText(this,"iconMenu",Toast.LENGTH_SHORT).show()
                binding.level3.tag = if (binding.level3.tag as Boolean) {
                    MyViewAnimator.showView(binding.level3)
                    false
                } else {
                    MyViewAnimator.hideView(binding.level3)
                    true
                }
            }
        }
    }

    class MyViewAnimator {

        companion object {
            fun hideView(view: ViewGroup,durationTime: Long = 1000) {
                val rotation = ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 180f).apply {
                    duration = durationTime
                }
                rotation.start()
                // 旋转中心点：顶部中间
                view.pivotX = (view.width / 2).toFloat()
                view.pivotY = view.height.toFloat()
            }

            fun showView(view: ViewGroup,durationTime: Long = 1000) {
                val rotation = ObjectAnimator.ofFloat(view, View.ROTATION, 180f, 360f).apply {
                    duration = durationTime
                }
                rotation.start()
                // 旋转中心点：顶部中间
                view.pivotX = (view.width / 2).toFloat()
                view.pivotY = view.height.toFloat()
            }
        }

    }

}
