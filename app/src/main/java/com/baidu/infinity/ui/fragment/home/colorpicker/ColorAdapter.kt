package com.baidu.infinity.ui.fragment.home.colorpicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.baidu.infinity.R
import com.baidu.infinity.databinding.ColorItemAddLayoutBinding
import com.baidu.infinity.databinding.ColorItemLayoutBinding
import com.baidu.infinity.viewmodel.HomeViewModel

class ColorAdapter: RecyclerView.Adapter<ColorAdapter.MyViewHolder>() {
    private var colorList: List<Int> = emptyList()
    private val TYPE_ADD = 888888
    private val TYPE_DELETE = 888887
    private val TYPE_NORMAL = 888886
    var actionListener:(ItemAction)->Unit = {}

    inner class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        private val colorView:View = itemView.findViewById(R.id.colorView)
        fun bind(color: Int, type:Int){
            colorView.setBackgroundColor(color)
            itemView.setOnClickListener {
                when (type){
                    TYPE_ADD -> actionListener(ItemAction.ADD)
                    TYPE_DELETE -> actionListener(ItemAction.DELETE)
                    TYPE_NORMAL -> HomeViewModel.instance().mColor = color
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        var view: View? = null
        when(viewType){
            TYPE_ADD -> {
                view = layoutInflater.inflate(R.layout.color_item_add_layout,parent,false)
            }
            TYPE_DELETE ->{
                view = layoutInflater.inflate(R.layout.color_item_delete_layout,parent,false)
            }
            TYPE_NORMAL -> {
                view = layoutInflater.inflate(R.layout.color_item_layout,parent,false)
            }
        }
        return MyViewHolder(view!!)
    }

    override fun getItemCount(): Int {
        return colorList.size + 2
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> TYPE_ADD
            1 -> TYPE_DELETE
            else -> TYPE_NORMAL
        }
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        when (position){
            0 -> holder.bind(0,TYPE_ADD)
            1 -> holder.bind(0,TYPE_DELETE)
            else -> holder.bind(colorList[position-2],TYPE_NORMAL)
        }
    }

    fun setDatas(colors: List<Int>){
        colorList = colors
        notifyDataSetChanged()
    }
}