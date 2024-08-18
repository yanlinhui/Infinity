package com.baidu.infinity.ui.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtil {
    //将当前时间当做名字使用
    fun getTimeName():String{
        //需要将Date的格式转化为自己需要的格式
        //定义格式转化的对象 202408181629
        val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA)
        //将Date按照formatter格式提取
        return formatter.format(Date())
    }
}