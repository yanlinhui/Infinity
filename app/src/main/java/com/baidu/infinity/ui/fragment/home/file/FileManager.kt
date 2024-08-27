package com.baidu.infinity.ui.fragment.home.file

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.baidu.infinity.ui.util.TimeUtil
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * 管理文件的操作
 * 创建目录
 * 创建文件
 * 删除文件
 * 读取文件
 *
 * external_file_dir 提供给外部访问的路径
 * external_cache_dir 提供给外部访问的路径
 * files_dir 永久存储
 * cache_dir 临时目录 程序退出自动清除
 */
class FileManager private constructor(){
    private lateinit var mContext: Context
    private var mUserName: String? = null  //保存登录用户的用户名

    companion object{
        //缩略图文件夹名称
        private val THUMBNAIL = "thumbnail"
        //原图文件夹名称
        private val ORIGINAL = "origin"
        val instance: FileManager by lazy { FileManager() }

        //必须提前对FileManager调用init方法
        fun init(application: Application){
            instance.mContext = application
        }
    }

    /**
     * files/pxd/thumbnail
     * files/pxd/origin
     * 获取用户缩略图路径
     */
    private fun getThumbnailPath():String{
        return "${mContext.filesDir.path}/$mUserName/${THUMBNAIL}"
    }

    /*
    * 获取用户原图路径
     */
    private fun getOriginalPath():String{
        return "${mContext.filesDir.path}/$mUserName/${ORIGINAL}"
    }
    /**
     * 获取用户文件夹路径
     * files/pxd
     */
    private fun getUserRootPath():String{
        return "${mContext.filesDir.path}/$mUserName"
    }

    /**
     * 用户登录
     * 创建用户文件夹
     */
    fun login(username:String){
        //保存登录用户信息
        mUserName = username

        //检查这个用户对应的文件夹是否存在
        val userPath = getUserRootPath()
        val file = File(userPath)
        if (file.exists()) return

        //如果不存在 表示第一次登录
        //需要创建这个用户的文件夹 和 缩略图 原图文件夹
        File(getThumbnailPath()).mkdirs()
        File(getOriginalPath()).mkdirs()
    }

    /**
     * 退出登录
     */
    fun logout(){
        mUserName = null
    }

    /**
     * 注销
     */
    fun logOff(){
        //删除所有文件
        //。。。
        mUserName = null
    }

    /**
     * 保存图片
     * files/pxd/thumbnail/1.jpg
     */
    fun saveBitmap(bitmap: Bitmap,name:String, isOrigin:Boolean){
        val path = if (isOrigin){
            getOriginalPath()
        }else{
            getThumbnailPath()
        }
        val filePath = "$path/$name"

        BufferedOutputStream(FileOutputStream(filePath)).use { bos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos)
        }
    }

    /**
     * 读取当前登录用户下的所有图片信息
     * files/pxd/thumbnail/1.jpg
     */
    fun loadThumbnailImages(): List<String>{
        val files = arrayListOf<String>()

        if (mUserName != null){
            //获取缩略图路径
            val thumbnailPath = getThumbnailPath()
            //获取缩略图路径下所有的文件的名字
            File(thumbnailPath).list()?.forEach { name ->
                val filePath = "$thumbnailPath/$name"
                files.add(filePath)
            }
        }
        return files
    }

    /**
     * 根据文件名获取文件的原始路径
     */
    fun getOriginalPathForFile(name:String):String{
        return "${getOriginalPath()}/$name"
    }

    /**
     * 删除图片
     */
    fun removeFile(path: String){
        File(path).also { file ->
            if (file.exists()){
                file.delete()
            }
        }
    }
}