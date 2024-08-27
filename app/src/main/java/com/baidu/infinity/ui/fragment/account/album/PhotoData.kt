package com.baidu.infinity.ui.fragment.account.album

import android.util.Log
import com.baidu.infinity.ui.fragment.home.file.FileManager

fun loadPhotoModels():ArrayList<PhotoModel>{
    val models = arrayListOf<PhotoModel>()

    //files/pxd/thumbnail/1.jpg
    //files/pxd/origin/1.jpg
    Log.v("pxd","----${FileManager.instance.loadThumbnailImages().size}")
    FileManager.instance.loadThumbnailImages().forEach { thumbnailPath ->
        //获取文件路劲中最后一个斜杠的索引
        val index = thumbnailPath.lastIndexOf("/")
        //获取子字符串
        val name = thumbnailPath.substring(index+1)
        Log.v("pxd","$name")
        val model = PhotoModel(thumbnailPath,FileManager.instance.getOriginalPathForFile(name))
        models.add(model)
    }

    return models
}