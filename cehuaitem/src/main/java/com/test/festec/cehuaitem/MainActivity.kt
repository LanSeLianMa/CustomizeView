package com.test.festec.cehuaitem

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.test.festec.cehuaitem.databinding.ActivityMainBinding
import com.test.festec.cehuaitem.widget.SideslipContent
import com.test.festec.cehuaitem.widget.SideslipContainer
import com.test.festec.cehuaitem.widget.SideslipOption

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    private var options = mutableListOf("增加", "编辑", "删除")
    private var optionBg = mutableListOf(R.color.blue, R.color.orange, R.color.red)

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sideslipContainer01.addOnClickListener(object : SideslipContainer.SideslipContainerOnClick {

            override fun optionOnClick(optionTag: Any) {
                Toast.makeText(this@MainActivity,"optionTag：$optionTag", Toast.LENGTH_SHORT).show()
                binding.sideslipContainer01.sideslipRecover()
            }

            override fun contentOnClick() {
                Toast.makeText(this@MainActivity,"content", Toast.LENGTH_SHORT).show()
                binding.sideslipContainer01.sideslipRecover()
            }

        })

//        // 创建侧滑容器，配置基础参数
//        val sideslip = SideslipContainer(this, DensityUtil.dp2px(this, 65f))
//        val layoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            DensityUtil.dp2px(this, 70f)
//        )
//        sideslip.layoutParams = layoutParams
//
//        // 创建侧滑内容
//        val content = SideslipContent(this).apply {
//            background = resources.getDrawable(R.color.black,null)
//        }
//
//        // 加入侧滑容器中
//        sideslip.addView(content)
//
//        // 创建选项卡，并加入侧滑容器中
//        options.forEachIndexed { index, str ->
//            val option = SideslipOption(this,str)
//            option.text = str
//            option.background = resources.getDrawable(optionBg[index], null)
//            sideslip.addView(option)
//        }
//
//        // 点击监听
//        sideslip.addOnClickListener(object : SideslipContainer.SideslipContainerListener {
//            override fun optionOnClick(optionTag: Any) {
//                Log.e("TAG","optionTag：$optionTag")
//            }
//
//            override fun contentOnClick() {
//                Log.e("TAG","content")
//            }
//        })
//
//        binding.container.addView(sideslip)

        binding.list.setOnClickListener {
            val intent = Intent(this,ListActivity::class.java)
            startActivity(intent)
        }
    }

}