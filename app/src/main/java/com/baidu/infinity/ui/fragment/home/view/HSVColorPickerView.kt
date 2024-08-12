package com.baidu.infinity.ui.fragment.home.view

import android.content.Context
import android.media.MediaPlayer.OnSeekCompleteListener
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baidu.infinity.R
import com.baidu.infinity.ui.fragment.home.colorpicker.ColorAdapter
import com.baidu.infinity.ui.fragment.home.colorpicker.ColorPickerView
import com.baidu.infinity.ui.fragment.home.colorpicker.ItemAction
import com.baidu.infinity.ui.fragment.home.colorpicker.getDefaultColors
import org.w3c.dom.Text

class HSVColorPickerView(
    context: Context,
    attrs: AttributeSet? = null
):FrameLayout(context,attrs) {
    private lateinit var mColorPickerView: ColorPickerView
    private lateinit var mSaturationbar: AppCompatSeekBar
    private lateinit var mLightnessbar: AppCompatSeekBar
    private lateinit var mTVLightness: TextView
    private lateinit var mTVSaturation: TextView
    private lateinit var mRecyclerView: RecyclerView
    private val mColorAdapter = ColorAdapter()
    private val mColorList:ArrayList<Int> = arrayListOf()

    var pickColorCallback:(Int)->Unit = {}

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mColorPickerView = findViewById(R.id.pickerView)
        mSaturationbar = findViewById(R.id.saturationBar)
        mLightnessbar = findViewById(R.id.lightnessBar)
        mTVLightness = findViewById(R.id.tvLightness)
        mTVSaturation = findViewById(R.id.tvSaturation)
        mRecyclerView = findViewById(R.id.recyclerView)

        //添加颜色选择事件
        mColorPickerView.addPickColorListener { color ->
            pickColorCallback(color)
        }
        //饱和度改变的事件
        mSaturationbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mTVSaturation.text = "$progress"
                mColorPickerView.setSaturation(progress/100f)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        mLightnessbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mTVLightness.text = "$progress"
                mColorPickerView.setLightness(progress/100f)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        //配置recyclerView
        mRecyclerView.adapter = mColorAdapter
        mRecyclerView.layoutManager = GridLayoutManager(context,5,RecyclerView.VERTICAL,false)
        //加载数据
        mColorList.addAll(getDefaultColors())
        mColorAdapter.setDatas(mColorList)
        //监听点击事件（add delete）
        mColorAdapter.actionListener = { itemAction ->
            if (itemAction == ItemAction.ADD){
                mColorList.add(0,mColorPickerView.getCurrentColor())
            }else{
                if (mColorList.isNotEmpty()){
                    mColorList.removeFirst()
                }
            }
            //重新刷新数据源
            mColorAdapter.setDatas(mColorList)
        }
    }
}