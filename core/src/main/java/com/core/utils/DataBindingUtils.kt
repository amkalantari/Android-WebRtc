package com.core.utils

import androidx.databinding.BindingAdapter
import com.core.widget.TextViewCustom

class DataBindingUtils {

    companion object {

        @JvmStatic
        @BindingAdapter("timer")
        fun TextViewCustom.setLongTimer(value : Long?){
            this.setTimer(value)
        }
    }

}