package com.test.festec.cehuaitem.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import com.test.festec.cehuaitem.R
import com.test.festec.cehuaitem.model.Info
import com.test.festec.cehuaitem.util.DensityUtil
import com.test.festec.cehuaitem.widget.SideslipContainer
import com.test.festec.cehuaitem.widget.SideslipContent
import com.test.festec.cehuaitem.widget.SideslipOption

class MyListAdapter(
    var context: Context,
    var data: MutableList<Info>
) : RecyclerView.Adapter<MyListAdapter.MyViewHolder>() {

    private var options = mutableListOf("增加", "编辑", "删除")
    private var optionBg =
        mutableListOf(R.color.blue, R.color.orange, R.color.red)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        val sideslip = sideslipContainer(itemView)
        return MyViewHolder(sideslip)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sideslip = holder.itemView as SideslipContainer

        // 根据不同权限，添加不同的选项卡
        val optionsView = mutableListOf<SideslipOption>()
        if (data[position].level == 0) {
            optionsView.clear()
        } else if (data[position].level == 1) {
            val option = SideslipOption(context, options[1])
            option.text = options[1]
            option.background = context.resources.getDrawable(optionBg[1], null)
            optionsView.add(option)
        } else {
            options.forEachIndexed { index, str ->
                val option = SideslipOption(context, str)
                option.text = str
                option.background = context.resources.getDrawable(optionBg[index], null)
                optionsView.add(option)
            }
        }
        sideslip.addMultipleOption(optionsView)

        // 点击回调
        sideslip.addOnClickListener(object : SideslipContainer.SideslipContainerOnClick {
            override fun optionOnClick(optionTag: Any) {
                Toast.makeText(context,"${holder.adapterPosition} -- optionTag：$optionTag",Toast.LENGTH_SHORT).show()
                sideslip.sideslipRecover()
            }

            override fun contentOnClick() {
                Toast.makeText(context,"${holder.adapterPosition} - content",Toast.LENGTH_SHORT).show()
                sideslip.sideslipRecover()
            }
        })
        holder.idTv.text = data[position].id.toString()
        holder.titleTv.text = data[position].title
        holder.describeTv.text = data[position].describe
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun sideslipContainer(itemView: View): SideslipContainer {
        // 创建侧滑容器，配置基础参数
        val sideslip = SideslipContainer(context)
        sideslip.setOptionWidth(DensityUtil.dp2px(context, 65f))

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            DensityUtil.dp2px(context, 70f)
        )
        sideslip.layoutParams = layoutParams

        // 创建侧滑内容
        val content = SideslipContent(context)
        content.addView(itemView)

        // 加入侧滑容器中
        sideslip.addView(content)

        // 创建选项卡，并加入侧滑容器中
        // options.forEachIndexed { index, str ->
        //    val option = SideslipOption(context, str)
        //    option.text = str
        //    option.background = context.resources.getDrawable(optionBg[index], null)
        //    sideslip.addView(option)
        // }
        return sideslip
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var idTv: TextView
        var titleTv: TextView
        var describeTv: TextView

        init {

            idTv = itemView.findViewById(R.id.id_tv)
            titleTv = itemView.findViewById(R.id.title_tv)
            describeTv = itemView.findViewById(R.id.describe_tv)
        }
    }
}