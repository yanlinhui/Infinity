package com.baidu.infinity.ui.fragment.home.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.baidu.infinity.R
import com.baidu.infinity.ui.fragment.home.colorpicker.ColorPickerView

class HSVColorPickerView(
    context: Context,
    attrs: AttributeSet? = null
):FrameLayout(context,attrs) {
    private lateinit var mColorPickerView: ColorPickerView
    var pickColorCallback:(Int)->Unit = {}

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mColorPickerView = findViewById<ColorPickerView>(R.id.pickerView)
        mColorPickerView.addPickColorListener { color ->
            pickColorCallback(color)
        }
    }
}