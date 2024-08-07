package com.baidu.infinity.ui.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


//延迟time时间后在主线程执行action任务
fun delayTask(time:Long,action:()->Unit){
    CoroutineScope(Dispatchers.IO).launch {
        delay(time)
        withContext(Dispatchers.Main){
            action()
        }
    }
}
