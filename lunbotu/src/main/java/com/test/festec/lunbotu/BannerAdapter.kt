package com.test.festec.lunbotu

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class BannerAdapter(
    private var context: Context,
    private var imgUrls : MutableList<Int>
    ) : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout_banner,parent,false))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: BannerAdapter.ViewHolder, position: Int) {
        holder.ivImg.setImageResource(imgUrls[position])
//         holder.ivImg.setOnClickListener {
//             itemOnClick.click(position)
//            // Toast.makeText(context,"${imgUrls[position]}",Toast.LENGTH_SHORT).show()
//         }
//        holder.ivImg.setOnTouchListener(object : View.OnTouchListener {
//
//            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//                when (event!!.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        Log.e("TAG", "onTouch==手指按下")
//                    }
//                    MotionEvent.ACTION_MOVE -> {
//                        Log.e("TAG", "onTouch==移动")
//                    }
//                    MotionEvent.ACTION_CANCEL -> {
//                        Log.e("TAG", "onTouch==事件取消")
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        Log.e("TAG", "onTouch==手指离开")
//                    }
//                }
//                return false
//            }
//
//        })

    }

    override fun getItemCount(): Int {
        return imgUrls.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var ivImg : ImageView

        init {
            ivImg = itemView.findViewById(R.id.item_banner_img)
        }
    }

}