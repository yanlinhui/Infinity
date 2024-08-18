package com.baidu.infinity.ui.fragment.home.view.bg_image

import androidx.annotation.DrawableRes
import com.baidu.infinity.ui.fragment.home.layer.LayerState

/**
 * bitmap
 * resource id
 */
data class ImageModel(
    @DrawableRes val id: Int,
    var state: LayerState = LayerState.NORMAL //记录状态
)