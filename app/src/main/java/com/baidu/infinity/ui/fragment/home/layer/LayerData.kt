package com.baidu.infinity.ui.fragment.home.layer

import com.baidu.infinity.viewmodel.HomeViewModel

/**
 * 加载所有图层数据
 */
fun getLayerData(): List<LayerModel> {
    //准备一个容器 用于保存所有的图层模型
    val dataList:ArrayList<LayerModel> = arrayListOf()

    //获取所有的layer 封装对应的LayerModel
    HomeViewModel.instance()
        .mLayerManager
        .getLayers()
        .forEach { layer ->
            dataList.add(LayerModel(layer.id, layer.getBitmap()))
        }

    //设置最后一个LayerModel为目前正在操作的图层
    //dataList[dataList.size - 1].state = LayerState.SELECTED

    //反转一下
    //dataList.reverse()

    return dataList
}