package com.baidu.infinity.ui.fragment.account.album

/**
 * 图片的模型数据
 */
data class PhotoModel(
    val thumbnailPath:String,
    val originalPath:String,
    var selectState: SelectState = SelectState.NORMAL
)
