package ru.semper_viventem.roundedbackgroundspan

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val colors = listOf(
        Color.YELLOW,
        Color.BLUE,
        Color.RED,
        Color.GREEN,
        Color.GRAY,
        Color.CYAN
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawSpan.setOnClickListener { initSpannableText() }
        initSpannableEditText()
    }

    private fun initSpannableText() {
        val span = RoundedBackgroundColorSpan(colors.random(), dp(5), dp(5))
        with(spanText) {
            setShadowLayer(dp(10), 0f, 0f, 0) // it's impotent for padding working

            text = buildSpannedString { inSpans(span) { append(text.toString()) } }
        }
    }

    private fun initSpannableEditText() {
        val span = RoundedBackgroundColorSpan(Color.CYAN, dp(5), dp(10))

        spanEditText.setShadowLayer(dp(10), 0f, 0f, 0) // it's impotent for padding working

        spanEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                text?.setSpan(span, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }
        })
    }

    private fun Context.dp(dp: Number): Float {
        return dp.toFloat() * resources.displayMetrics.density
    }
}
