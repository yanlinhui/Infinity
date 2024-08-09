package com.baidu.infinity.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.baidu.infinity.model.IconModel
import com.baidu.infinity.ui.view.IconTextView
import java.lang.ref.WeakReference


class HomeViewModel: ViewModel() {
    val selectedIconModel:MutableLiveData<IconModel> = MutableLiveData(null)
    var lifecycleOwner: LifecycleOwner? = null
    var iconView : WeakReference<IconTextView>? = null

    //工具按钮被点击了，
    fun updateSelectedIconModel(model: IconModel){
        selectedIconModel.value = model
    }

    companion object {
        private var instance: HomeViewModel? = null
        //使用我这个之前必须先调用init方法
        //初始化
        fun init(owner: ViewModelStoreOwner,lifecycleOwner: LifecycleOwner){
            if (instance == null){
                instance = ViewModelProvider(owner).get(HomeViewModel::class.java)
                instance!!.lifecycleOwner = lifecycleOwner
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