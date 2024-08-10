package com.baidu.infinity.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.baidu.infinity.model.IconModel
import com.baidu.infinity.ui.fragment.home.draw.LayerManager
import com.baidu.infinity.ui.util.dp2pxF
import com.baidu.infinity.ui.view.IconTextView
import java.lang.ref.WeakReference


class HomeViewModel(application: Application): AndroidViewModel(application) {
    //提供默认的画笔尺寸
    var mStrokeWidth = application.dp2pxF(1)
    //画笔颜色
    var mColor = Color.BLACK
    //填充方式
    var mStrokeStyle = Paint.Style.FILL_AND_STROKE
    //保存图层管理器
    val mLayerManager = LayerManager()

    companion object {
        private var instance: HomeViewModel? = null
        //使用我这个之前必须先调用init方法
        //初始化
        fun init(owner: ViewModelStoreOwner){
            if (instance == null){
                instance = ViewModelProvider(owner).get(HomeViewModel::class.java)
            }
        }
        //获取单例
        fun instance(): HomeViewModel {
            if (instance == null) {
                throw Exception("必须要先调用init方法")
            }
            return instance!!
        }
    }
}