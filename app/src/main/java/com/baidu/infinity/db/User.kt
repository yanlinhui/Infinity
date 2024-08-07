package com.baidu.infinity.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

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
    var isLogin:Boolean = false
):Parcelable