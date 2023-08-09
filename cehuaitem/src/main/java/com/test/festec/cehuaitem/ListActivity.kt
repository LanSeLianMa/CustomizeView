package com.test.festec.cehuaitem

import android.app.Activity
import android.os.Bundle
import com.test.festec.cehuaitem.adapter.MyListAdapter
import com.test.festec.cehuaitem.databinding.ListLayoutBinding
import com.test.festec.cehuaitem.layoutmanager.CustomLinerLayoutManager
import com.test.festec.cehuaitem.model.Info

class ListActivity : Activity() {

    private lateinit var binding: ListLayoutBinding

    private val data: MutableList<Info> = mutableListOf(
        Info(0, "title", "content", 2),
        Info(1, "title", "content", 1),
        Info(2, "title", "content", 2),
        Info(3, "title", "content", 2),
        Info(4, "title", "content", 2),
        Info(5, "title", "content", 1),
        Info(6, "title", "content", 1),
        Info(7, "title", "content", 2),
        Info(8, "title", "content", 2),
        Info(9, "title", "content", 1),
        Info(10, "title", "content", 1),
        Info(11, "title", "content", 2),
        Info(12, "title", "content", 2),
        Info(13, "title", "content", 1),
        Info(14, "title", "content", 1),
        Info(15, "title", "content", 2),
        Info(16, "title", "content", 2),
        Info(17, "title", "content", 2),
        Info(18, "title", "content", 2),
        Info(19, "title", "content", 1),
        Info(20, "title", "content", 2),
        Info(21, "title", "content", 1),
        Info(22, "title", "content", 2),
        Info(23, "title", "content", 2),
        Info(24, "title", "content", 2),
        Info(25, "title", "content", 1),
        Info(26, "title", "content", 2),
        Info(27, "title", "content", 1),
        Info(28, "title", "content", 2),
        Info(29, "title", "content", 2),
        Info(30, "title", "content", 2),
        Info(31, "title", "content", 1),
        Info(32, "title", "content", 1),
        Info(33, "title", "content", 2),
        Info(34, "title", "content", 2),
        Info(35, "title", "content", 1),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = MyListAdapter(this, data)
        val linearLayoutManager = CustomLinerLayoutManager(this)
        binding.root.layoutManager = linearLayoutManager
        binding.root.adapter = adapter
    }

}