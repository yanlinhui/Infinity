package com.baidu.infinity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.baidu.infinity.ui.util.toast

/**
 * 定义一个接收器
 */
class IconClickReceiver: BroadcastReceiver() {
    //当接收到这个广播的时候，会调用这个方法
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.toast("接收到广播了")
    }
}