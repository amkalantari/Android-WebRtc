package com.core.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class TextInputLayoutCustom : TextInputLayout {


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        create(context, attrs)
    }

    fun setText(text: CharSequence?) {
        editText?.setText(text)
    }


    fun getText(): String {
        return editText?.text?.toString() ?: ""
    }

    fun clearText() {
        this.editText?.clearComposingText()
    }

    fun create(context: Context, set: AttributeSet) {
        val editText = TextInputEditText(context, set)
        editText.setText("")
        editText.hint = ""
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            editText.focusable = this.focusable
        }
        this.addView(editText)
    }

}