package com.baidu.infinity.ui.fragment.home.draw

import android.graphics.Path

object ArrowPath {
    /*
    从起始点到终点画一条带箭头的线
     */
    fun addArrowToPath(path: Path, startX:Float, startY:Float, endX:Float, endY:Float, arrowLength:Float){
        //从上一个点到当前点联接一条线
        path.moveTo(startX,startY)
        path.lineTo(endX,endY)
        //计算直线的长度
        // 计算箭头头部的两边
        val angle = Math.atan2((endY - startY).toDouble(), (endX - startX).toDouble())

        val arrowX1 = endX - arrowLength * Math.cos(angle - Math.PI / 6).toFloat()
        val arrowY1 = endY - arrowLength * Math.sin(angle - Math.PI / 6).toFloat()

        val arrowX2 = endX - arrowLength * Math.cos(angle + Math.PI / 6).toFloat()
        val arrowY2 = endY - arrowLength * Math.sin(angle + Math.PI / 6).toFloat()

        //画左边
        path.lineTo(arrowX1,arrowY1)
        //回到终点
        path.moveTo(endX,endY)
        //画右边
        path.lineTo(arrowX2,arrowY2)
    }
}