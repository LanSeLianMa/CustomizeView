package com.test.festec.cehuaitem.model

data class Info(
    var id: Int,
    var title: String,
    var describe: String,
    // 等级
    // 0 1 2
    // 0：没有权限
    // 1：编辑权限
    // 2：所有权限
    var level: Int
)