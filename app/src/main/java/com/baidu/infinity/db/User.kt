package com.baidu.infinity.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidu.infinity.ui.util.PasswordType
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    @ColumnInfo(name = "pin_password")
    val pinPassword: String,
    @ColumnInfo(name = "pic_password")
    val picPassword: String,
    var isLogin:Boolean = false,
    var loginDate:Date = Date(), //登录时间
    val validate:Long = 10000*1000, //有效时间
    var passwordType:Int = 0 //登录类型 0:PIN  1:PIC
):Parcelable