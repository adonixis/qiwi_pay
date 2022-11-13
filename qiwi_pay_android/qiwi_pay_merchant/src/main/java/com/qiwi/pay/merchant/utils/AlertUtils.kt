package com.qiwi.pay.merchant.utils

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.qiwi.pay.merchant.R

fun showCustomSnackbar(view: View,
                          callback: Snackbar.Callback?,
                          @ColorInt backgroundColor: Int,
                          @ColorInt textColor: Int,
                          text: String,
                          @ColorInt actionTextColor: Int,
                          actionText: String,
                          onClickListener: View.OnClickListener?) {
    var onClickListener = onClickListener
    if (onClickListener == null) {
        onClickListener = View.OnClickListener { }
    }
    val snackbar = Snackbar
            .make(view, text, Snackbar.LENGTH_LONG)
            .addCallback(callback!!)
            .setActionTextColor(actionTextColor)
            .setAction(actionText, onClickListener)
    val sbView = snackbar.view
    sbView.setBackgroundColor(backgroundColor)
    val textView = sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    textView.setTextColor(textColor)
    snackbar.show()

}

fun Activity.showErrorSnackbar(view: View, errorMessage: String) {
    showCustomSnackbar(view, Snackbar.Callback(),
        ContextCompat.getColor(this, R.color.red),
        Color.WHITE,
        errorMessage,
        Color.WHITE,
        getString(R.string.snackbar_action_hide), null
    )
}

fun Activity.showSuccessSnackbar(view: View, errorMessage: String) {
    showCustomSnackbar(view, Snackbar.Callback(),
        ContextCompat.getColor(this, R.color.green),
        Color.WHITE,
        errorMessage,
        Color.WHITE,
        getString(R.string.snackbar_action_hide), null
    )
}