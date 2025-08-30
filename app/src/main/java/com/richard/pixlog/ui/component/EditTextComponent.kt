package com.richard.pixlog.ui.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.richard.pixlog.R
import com.richard.pixlog.utils.dpToPx
import com.richard.pixlog.utils.isValidStringEmail

class EditTextComponent : AppCompatEditText, View.OnTouchListener {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var textColorDefault = 0
    private var textColorError = 0
    private var bgDefault : Drawable
    private var bgError : Drawable
    private var isError : Boolean = false
    private var currentHint = ""
    private var bgFocus : Drawable
    private var isFocused = false
    private var icEye : Drawable
    private var icEyeOff : Drawable
    private var isVisible  = false
    private var icError: Drawable? = null
    private var errorText : CharSequence? = null
    private var errorTextView: TextView? = null

    init {
        minHeight = 48.dpToPx(context)
        setPadding(16.dpToPx(context),0, 16.dpToPx(context), 0)
        textColorDefault = ContextCompat.getColor(context, R.color.purple_30)
        textColorError = ContextCompat.getColor(context, R.color.red_40)
        bgDefault = ContextCompat.getDrawable(context, R.drawable.bg_rounded_edit_text) as Drawable
        bgError = ContextCompat.getDrawable(context, R.drawable.bg_rounded_edit_text_error) as Drawable
        bgFocus = ContextCompat.getDrawable(context, R.drawable.bg_rounded_edit_text_focus) as Drawable
        icEye = ContextCompat.getDrawable(context, R.drawable.ic_visibility_24px) as Drawable
        icEyeOff = ContextCompat.getDrawable(context, R.drawable.ic_visibility_off_24px) as Drawable
        icError = ContextCompat.getDrawable(context, R.drawable.ic_error_input_24) as Drawable
        
        currentHint = hint.toString()
        if(currentHint == "password"){
            setOnTouchListener(this)
            showEyeBtn(icEye)
        }

        setBackgroundDrawable(bgDefault)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if(s.toString().isNotEmpty()){
                    val erorEmailText = context.getString(R.string.error_email_text)
                    val errorPasswordText = context.getString(R.string.error_password_text)
                    if(this@EditTextComponent.currentHint == "email"){
                        if(isValidStringEmail(s.toString())){
                            isError = false
                            hideError()
                        }else{
                            isError = true
                            showError(erorEmailText)
                        }
                    }else if (currentHint == "password"){
                        if(s.toString().length >= 8){
                            isError = false
                            hideError()
                        }else{
                            isError = true
                            showError(errorPasswordText)
                        }
                    }
                }else{
                    isError = false
                    hideError()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Not implemented
            }
        })
    }

    override fun setError(error: CharSequence?) {
        this.errorText = error
        if (error != null) {
            showError(error.toString())
        } else {
            hideError()
        }
    }

    private fun showError(message: String) {
        if (errorTextView == null) {
            createErrorTextView()
        }
        errorTextView?.text = message
        errorTextView?.visibility = VISIBLE
        setBackgroundDrawable(bgError)
        setTextColor(textColorError)
        icEye.setTint(textColorError)
        icEyeOff.setTint(textColorError)
    }

    private fun hideError() {
        errorTextView?.visibility = GONE
        if (isFocused) {
            setBackgroundDrawable(bgFocus)
        } else {
            setBackgroundDrawable(bgDefault)
        }
        setTextColor(textColorDefault)
        icEye.setTint(textColorDefault)
        icEyeOff.setTint(textColorDefault)
    }
    // create text view for error message
    private fun createErrorTextView() {
        errorTextView = TextView(context).apply {
            textSize = 12f
            setTextColor(ContextCompat.getColor(context, R.color.red_40))
            visibility = GONE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val parentView = parent
        when (parentView) {
            is LinearLayout -> {
                val currentIndex = parentView.indexOfChild(this)
                (errorTextView?.layoutParams as? LinearLayout.LayoutParams)?.apply {
                    setMargins(16.dpToPx(context),0,0,0)
                    errorTextView?.layoutParams = this
                }
                parentView.addView(errorTextView, currentIndex + 1)
            }
            is androidx.constraintlayout.widget.ConstraintLayout -> {
                val params = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                    androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT,
                    androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                params.topToBottom = id
                params.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                params.endToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                params.setMargins(16.dpToPx(context), 4.dpToPx(context), 16.dpToPx(context), 8.dpToPx(context))

                errorTextView?.layoutParams = params
                parentView.addView(errorTextView)
            }
            else -> {
                // Last resort: try to add to any ViewGroup
                try {
                    (errorTextView?.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                        setMargins(16.dpToPx(context), 4.dpToPx(context), 16.dpToPx(context), 8.dpToPx(context))
                        errorTextView?.layoutParams = this
                    }
                    (parentView as? ViewGroup)?.addView(errorTextView)
                } catch (e: Exception) {
                    Log.e("EditTextComponent", "Could not add error TextView: ${e.message}")
                }
            }
        }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        isFocused = focused
        if (!isError) {
            if (focused) {
                setBackgroundDrawable(bgFocus)
            } else {
                setBackgroundDrawable(bgDefault)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Background and text color are now handled in showError/hideError methods
    }

    private fun showEyeBtn(endOfTheText: Drawable? = null) {
        setButtonDrawables(endOfTheText = endOfTheText)
    }

    private fun setButtonDrawables(startOfTheText: Drawable? = null, topOfTheText:Drawable? = null, endOfTheText: Drawable? = null, bottomOfTheText: Drawable? = null){
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isBtnEyeClicked = false

            //check is touched
            if (layoutDirection == LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (icEye.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isBtnEyeClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - icEye.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isBtnEyeClicked = true
                }
            }

            if (isBtnEyeClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if(isVisible){
                            showEyeBtn(endOfTheText = icEyeOff)
                            setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                        }else{
                            showEyeBtn(endOfTheText = icEye)
                            setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        }
                        Log.d("EDIT TEXT COMPONENT", "$isVisible")
                        isVisible = !isVisible
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }
}