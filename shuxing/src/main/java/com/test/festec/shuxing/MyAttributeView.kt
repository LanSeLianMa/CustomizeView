package com.test.festec.shuxing

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi

class MyAttributeView : View {

    var attrs: AttributeSet? = null
    var myName: String? = null
    var myAge: Int? = null
    var myPhoto: Bitmap? = null

    constructor(context: Context) : super(context)

    @SuppressLint("Recycle")
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        this.attrs = attributeSet

        // 获取属性的三种方式

        // 1、使用命名空间获取，返回值类型是String，需要强转
        // var myName : String = attrs?.getAttributeValue("http://schemas.android.com/apk/res-auto", "my_name")
        // var myAge : String = attrs?.getAttributeValue("http://schemas.android.com/apk/res-auto", "my_age")
        // var myPhoto : String = attrs?.getAttributeValue("http://schemas.android.com/apk/res-auto", "my_photo")
        // Log.e("TAG", "myName：$myName --- myAge：$myAge --- myPhoto：$myPhoto ---")


        // 2、遍历属性集合获取
        // for (index in 1..(attrs?.attributeCount ?: 0)) {
        //    Log.e("TAG", "Key：${attrs?.getAttributeName(index - 1)} --- Value：${attrs?.getAttributeValue(index - 1)}")
        // }

        // 3、使用系统工具获取
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.MyAttributeView)
        Log.e("TAG", "${typedArray.indexCount}")

        for (index in 0 until typedArray.indexCount) {
            val attr = typedArray.getIndex(index)
            when (attr) {
                R.styleable.MyAttributeView_my_name -> {
                    myName = typedArray.getString(attr)
                }
                R.styleable.MyAttributeView_my_age -> {
                    myAge = typedArray.getInt(attr, 10)
                }
                R.styleable.MyAttributeView_my_photo -> {
                    val drawable: Drawable? = typedArray.getDrawable(attr)
                    if (drawable != null) {
                        val bitmap = Bitmap.createBitmap(
                            drawable.intrinsicWidth,
                            drawable.intrinsicHeight,
                            Bitmap.Config.ARGB_8888
                        )

                        val canvas = Canvas(bitmap)
                        drawable.setBounds(0, 0, canvas.width, canvas.height)
                        drawable.draw(canvas)
                        myPhoto = bitmap
                    }
                }
            }
        }

        // 记得回收，TypedArray对象是复用的
        typedArray.recycle()

        // Log.e("TAG", "myName：$myName --- myAge：$myAge --- myPhoto：$myPhoto ---")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        val paint = Paint()

        paint.color = resources.getColor(R.color.purple_200, null)
        paint.textSize = 50f
        canvas?.drawText(myName.toString(), 0f, 50f, paint)
        canvas?.drawText(myAge.toString(), 200f, 50f, paint)

        canvas?.drawBitmap(myPhoto!!, 0f, 100f, paint)
    }

}