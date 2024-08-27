package com.baidu.infinity.application

import android.app.Application
import com.baidu.infinity.ui.fragment.home.file.FileManager

/**
 * 定义类继承与Application
 * 提前配置好程序运行所需的上下文 --
 */
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        //配置FileManager
        FileManager.init(this)
    }
}