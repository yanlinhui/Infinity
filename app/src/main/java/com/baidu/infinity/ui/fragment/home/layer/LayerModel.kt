package com.baidu.infinity.ui.fragment.home.layer

import android.graphics.Bitmap
import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.item.ItemDrag
import com.drake.brv.item.ItemSwipe

/**
 * 图层视图中每个layer的数据模型
 */
data class LayerModel(
    val id: Int,
    val bitmap: Bitmap,
    var state: LayerState = LayerState.NORMAL,
    override var itemOrientationSwipe: Int = ItemOrientation.LEFT,
    override var itemOrientationDrag: Int = ItemOrientation.VERTICAL
): ItemSwipe,ItemDrag