package com.core.utils.textWatcher


import android.text.Editable
import android.text.TextWatcher
import com.core.widget.TextInputLayoutCustom

/**
 * developed by AmirAli Changizi - November 2020
 */
class TextWatcherHelper(
    private val inputLayout: TextInputLayoutCustom,
    private val text: (String) -> Unit
) {

    init {
        inputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                text(
                    try {
                        inputLayout.getText()
                    } catch (ignore: Exception) {
                        ""
                    }
                )
            }
        })
    }
}