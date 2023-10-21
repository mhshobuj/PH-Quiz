package com.mhs.phquiz.Utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

fun View.isVisible(isShowLoading: Boolean, container: View) {
    if (isShowLoading) {
        this.visibility = View.VISIBLE
        container.visibility = View.GONE
    } else {
        this.visibility = View.GONE
        container.visibility = View.VISIBLE
    }
}
