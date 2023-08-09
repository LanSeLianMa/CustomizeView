package com.test.festec.myviewpager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.festec.myviewpager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var testView: View
    private val images = mutableListOf(
        R.drawable.a1,R.drawable.a2,R.drawable.a3,
        R.drawable.a4,R.drawable.a5,R.drawable.a6,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadChildrenView()
        pagerViewListener()
    }

    // Pager监听
    private fun pagerViewListener() {
        binding.myViewPager.setPagerListener(object : MyViewPager.MyViewPagerListener {
            override fun addUpdatePagerListener(position: Int) {
                // Log.e("TAG","$position")
                binding.rgMain.check(position)
            }
        })
    }

    // 加载子View
    private fun loadChildrenView() {
        images.forEachIndexed { _, element ->
            var imageView = ImageView(this)
            imageView.setBackgroundResource(element)
            binding.myViewPager.addView(imageView)
        }

        testView = View.inflate(this, R.layout.activity_list, null)
        binding.myViewPager.addView(testView,2)
        initRecyclerView()

        for (index in 0..images.size) {
            var radioButton = RadioButton(this)
            radioButton.setOnClickListener {
                binding.myViewPager.fromToPager(index)
            }
            radioButton.id = index

            if (index == 0) {
                radioButton.isChecked = true
            }

            binding.rgMain.addView(radioButton)
        }
    }

    // 初始化RecyclerView
    private fun initRecyclerView() {
        val source = mutableListOf(
            "张三01", "张三02", "张三03",
            "张三04", "张三05", "张三06",
            "张三07", "张三08", "张三09",
            "张三10", "张三11", "张三12"
        )

        // RecyclerView必须要填的参数：
        //      setLayoutManager：布局管理器
        //      setAdapter：数据适配器
        val myAdapter = MyAdapter(source)

        val recyclerView = testView.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = myAdapter

        val llManager = LinearLayoutManager(this)
        // 默认垂直展示，这里设置为水平显示
        // llManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = llManager
    }

    class MyAdapter(var data : MutableList<String>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.indexTextView.text = position.toString()
            holder.textView.text = data[position]
        }

        override fun getItemCount(): Int {
            return data.size
        }

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

            var textView : TextView
            var indexTextView : TextView

            init {
                textView = itemView.findViewById(R.id.tv)
                indexTextView = itemView.findViewById(R.id.index)
            }

        }

    }

}