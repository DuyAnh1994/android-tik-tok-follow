package com.solar.dev.tiktok.app.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import com.dev.anhnd.mybaselibrary.utils.app.onDebouncedClick
import com.solar.dev.tiktok.app.R

class EditTextTikTok @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(ctx, attrs, defStyle) {

    private val view: View
    var listener: EditTextTikTokListener? = null
    private var etInput: EditText
    private var ibCross: ImageButton

    init {
        setupAttributes(attrs, defStyle)
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.layout_edit_text, this)

        view.apply {
            etInput = findViewById(R.id.etUrl)
            ibCross = findViewById(R.id.ibCross)
        }

        registerListener()
    }

    private fun setupAttributes(attrs: AttributeSet?, defStyle: Int) {
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.appBarTikTok, defStyle, 0)
        ta.recycle()
    }

    private fun registerListener() {
        ibCross.onDebouncedClick { v ->
            listener?.onClickCross(v)
            etInput.text.clear()
            etInput.clearFocus()
        }
        etInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                listener?.beforeTextChanged(p0, p1, p2, p3)
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    ibCross.visibility = View.VISIBLE
                } else {
                    ibCross.visibility = View.GONE
                }
                listener?.onTextChanged(p0, p1, p2, p3)
            }

            override fun afterTextChanged(p0: Editable?) {
                listener?.afterTextChanged(p0)
            }
        })
    }

    fun setHint(char: String) {
        etInput.hint = char
    }

    fun getText() = etInput.text.toString()

    fun clearText() {
        etInput.apply {
            text.clear()
            clearFocus()
        }
    }

    interface EditTextTikTokListener {

        fun onClickCross(v: View) {}

        fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        fun afterTextChanged(p0: Editable?) {}
    }
}

