package com.test.festec.contactlist.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.test.festec.contactlist.model.Person
import com.test.festec.contactlist.R

class ListAdapter(
    var context: Context,
    var data: MutableList<Person>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val zw = 1
    private val py = 2

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            zw -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.user_zw_item, null)
                viewHolder = ZwViewHolder(view)
            }
            py -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.user_py_item, null)
                val params =
                    LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                view.background = context.resources.getDrawable(R.color.d5,null)
                view.layoutParams = params
                viewHolder = PyViewHolder(view)
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            zw -> {
                val zwViewHolder = (holder as ZwViewHolder)
                zwViewHolder.zwTextView.text = data[position].name
            }
            py -> {
                val pyViewHolder = (holder as PyViewHolder)
                pyViewHolder.pyTextView.text = data[position].pinyin
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (data[position].showPY) {
            return py
        } else {
            return zw
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ZwViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var zwTextView: TextView

        init {
            zwTextView = itemView.findViewById(R.id.zw_tv)
        }
    }

    class PyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pyTextView: TextView

        init {
            pyTextView = itemView.findViewById(R.id.py_tv)
        }
    }
}