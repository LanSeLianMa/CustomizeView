package com.test.festec.contactlist.model

import com.test.festec.contactlist.util.PinYinUtils

class Person(var name: String,var showPY : Boolean = false) {

    var pinyin: String? = null
    get() = PinYinUtils.getPinYin(name)

    override fun toString(): String {
        return "Person(name='$name', showPY=$showPY, pinyin=$pinyin)"
    }
}