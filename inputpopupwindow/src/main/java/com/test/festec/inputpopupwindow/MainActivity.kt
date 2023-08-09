package com.test.festec.inputpopupwindow

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.festec.inputpopupwindow.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var myAdapter: MyAdapter
    private lateinit var recyclerView: RecyclerView
    private var popupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = RecyclerView(this)
        recyclerView.setBackgroundResource(R.drawable.listview_background)
        var data = mutableListOf(
            "张三01", "张三02", "张三03",
            "张三04", "张三05", "张三06",
            "张三07", "张三08", "张三09"
        )
        myAdapter = MyAdapter(this, data, object : MyAdapter.ItemOnClick {

            @SuppressLint("NotifyDataSetChanged")
            override fun itemClickClear(position: Int) {
                super.itemClickClear(position)
                // Log.e("TAG","$position")
                data.removeAt(position)
                myAdapter.notifyDataSetChanged()
            }

            override fun itemClick(position: Int) {
                super.itemClick(position)
                binding.editText.text = Editable.Factory.getInstance().newEditable(data[position])
                popupWindow?.dismiss()
            }

            override fun onClick(v: View?) {}

        })
        recyclerView.adapter = myAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.icon.setOnClickListener {

            if (popupWindow == null) {
                // 菜单
                popupWindow = PopupWindow(this)
                popupWindow?.width = binding.editText.width
                // 默认自适应
                // popupWindow?.height = DensityUtil.dip2px(this, maxListHeight.toFloat())

                // 让pop在输入法下面
                popupWindow?.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED

                // 绑定内容
                popupWindow?.contentView = recyclerView
            }

            if (popupWindow?.isShowing == true) {
                popupWindow?.dismiss()
            } else {
                // 绑定触发view，并显示
                popupWindow?.showAsDropDown(binding.editText)
            }

        }

    }

    class MyAdapter(
        var context: Context,
        var data: MutableList<String>,
        itemClick: ItemOnClick?
    ) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private var itemOnClick: ItemOnClick? = null

        init {
            itemOnClick = itemClick
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.tv.text = data[position]
            holder.tv.setOnClickListener {
                itemOnClick?.itemClick(position)
            }
            holder.deleteIcon.setOnClickListener {
                itemOnClick?.itemClickClear(position)
            }
        }

        override fun getItemCount(): Int {
            return data.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var tv: TextView
            var deleteIcon: ImageView

            init {
                tv = itemView.findViewById(R.id.list_tv)
                deleteIcon = itemView.findViewById(R.id.delete_icon)
            }

        }

        interface ItemOnClick : View.OnClickListener {
            fun itemClickClear(position: Int) {}
            fun itemClick(position: Int) {}
        }
    }

}