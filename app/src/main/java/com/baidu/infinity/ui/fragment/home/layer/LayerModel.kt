package com.baidu.infinity.ui.fragment.home.layer

import android.graphics.Bitmap

/**
 * 图层视图中每个layer的数据模型
 */
data class LayerModel(
    val id: Int,
    val bitmap: Bitmap,
    var state: LayerState = LayerState.NORMAL
)