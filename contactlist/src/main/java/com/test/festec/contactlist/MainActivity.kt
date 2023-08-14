package com.test.festec.contactlist

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.festec.contactlist.adapter.ListAdapter
import com.test.festec.contactlist.databinding.ActivityMainBinding
import com.test.festec.contactlist.model.Person
import com.test.festec.contactlist.widget.IndexView
import java.util.*

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    // 汉字 + 拼音首字母
    private lateinit var persons: MutableList<Person>

    // 每个拼音首字母和在列表中的位置
    private lateinit var pairs: MutableMap<Int, String>

    private val handler = object : Handler(Looper.myLooper()!!) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        val adapter = ListAdapter(this, persons)
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        binding.indexView.addTouchListener(object : IndexView.TouchListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDown(tag: String, position: Int) {
                if (tag.isEmpty()) {
                    return
                }

                queryZmLocation(tag)

                binding.zmTv.visibility = View.VISIBLE
                binding.zmTv.text = tag

                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        binding.zmTv.visibility = View.GONE
                    }

                }, 2000)
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onMove(tag: String, position: Int) {
                if (tag.isEmpty()) {
                    return
                }

                queryZmLocation(tag)

                binding.zmTv.visibility = View.VISIBLE
                binding.zmTv.text = tag

                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        binding.zmTv.visibility = View.GONE
                    }

                }, 2000)
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun queryZmLocation(tag: String) {
        var position: Int = -1
        pairs.forEach { k, v ->
            if (tag == v) {
                position = k
                return@forEach
            }
        }

        if (position == -1) {
            return
        }

        smoothMoveToPosition(binding.recyclerView,position)

    }

    /**
     * 滑动到指定位置
     */
    private fun smoothMoveToPosition(mRecyclerView: RecyclerView, position : Int) {

        // 第一个可见位置
        val firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0))
        // 最后一个可见位置
        val lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.childCount - 1))
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前
            mRecyclerView.smoothScrollToPosition(position)
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后
            val movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.childCount) {
                val top = mRecyclerView.getChildAt(movePosition).top
                mRecyclerView.smoothScrollBy(0, top)
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            // mRecyclerView.smoothScrollToPosition(position)
            mRecyclerView.smoothScrollToPosition(persons.size - 1)
        }
    }

    private fun initData() {
        val originalPersons = mutableListOf<Person>(
            Person("张晓飞"), Person("杨光福"),
            Person("胡继群"), Person("刘畅"),
            Person("钟泽兴"), Person("尹革新"),
            Person("安传鑫"), Person("张骞壬"),
            Person("温松"), Person("李凤秋"),
            Person("刘甫"), Person("娄全超"),
            Person("张猛"), Person("王英杰"),
            Person("李振南"), Person("孙仁政"),
            Person("唐春雷"), Person("牛鹏伟"),
            Person("姜宇航"), Person("刘挺"),
            Person("张洪瑞"), Person("张建忠"),
            Person("侯亚帅"), Person("刘帅"),
            Person("乔竞飞"), Person("徐雨健"),
            Person("吴亮"), Person("王兆霖"),
            Person("阿三"), Person("李博俊")
        )


        // 排序（首字母相同的放在一起）
        Collections.sort(originalPersons, object : Comparator<Person> {

            // 如果主字符串和另一个字符串相等，返回0
            // 如果另一个字符串的ASCII值比主字符串大，则返回一个负数
            // 如果另一个字符串的ASCII值小于主字符串，则返回一个正数。
            override fun compare(o1: Person?, o2: Person?): Int {
                return o1?.pinyin?.compareTo(o2?.pinyin!!)!!
            }

        })

        persons = mutableListOf<Person>()
        pairs = mutableMapOf<Int, String>()

        persons.add(0, Person(originalPersons[0].pinyin?.substring(0, 1)!!, true))
        pairs[0] = originalPersons[0].pinyin?.substring(0, 1)!!

        var count = 0

        // 插入拼音首字母
        originalPersons.forEachIndexed { index, element ->
            var i = if ((index + 1) > (originalPersons.size - 1)) {
                (originalPersons.size - 1)
            } else {
                index + 1
            }
            val currentZM = element.pinyin?.substring(0, 1)
            val nextZM = originalPersons[i].pinyin?.substring(0, 1)
            persons.add(element)

            // 如果上一个拼音首字母和下一个拼音首字母不一样
            // 就插入下一个拼音的首字母
            if (currentZM != nextZM) {
                // 记录当前已经插入了几个拼音首字母
                count++
                persons.add(Person(nextZM!!, true))
                pairs[i + count] = nextZM
            }
        }
    }
}