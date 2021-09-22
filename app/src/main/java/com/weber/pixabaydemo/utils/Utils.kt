package com.weber.pixabaydemo.utils

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager


object Utils {
    fun hideSoftKeyboard(context: Context, view: View?) {
        if (view != null) {
            val imm =
                context.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}