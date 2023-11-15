package com.ajiananta.submisiintermediate.view.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.ajiananta.submisiintermediate.R
import java.util.regex.Pattern

class CustomEmailTextEdit : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setHint(R.string.input_your_email)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}"
                if (!Pattern.matches(emailPattern, s)) {
                    setError(context.getString(R.string.email_not_valid), null)
                } else {
                    error = null
                }
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
    }
}