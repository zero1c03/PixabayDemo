package com.weber.pixabaydemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    private var loading: View? = null

    fun setLoading(visible: Int, view: ViewGroup) {
        if (loading == null) {
            loading = LayoutInflater.from(this).inflate(
                R.layout.widget_progressbar_center_filled, view, false
            )
        }
        if (visible == View.GONE) {
            view.removeView(loading)
        } else if (visible == View.VISIBLE) {
            view.removeView(loading)
            view.addView(loading)
        }
    }
}