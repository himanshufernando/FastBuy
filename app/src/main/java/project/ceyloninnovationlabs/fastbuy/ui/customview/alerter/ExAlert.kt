package com.tapadoo.alerter.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import androidx.annotation.DimenRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import project.ceyloninnovationlabs.fastbuy.ui.customview.alerter.Alert
import project.ceyloninnovationlabs.fastbuy.R


fun Alert.getDimenPixelSize(@DimenRes id: Int) = resources.getDimensionPixelSize(id)

@RequiresApi(Build.VERSION_CODES.P)
fun Alert.notchHeight() = rootWindowInsets?.displayCutout?.safeInsetTop
        ?: 0

fun Context.getRippleDrawable(): Drawable? {
	val typedValue = TypedValue()
	theme.resolveAttribute(R.attr.selectableItemBackground, typedValue, true)
	val imageResId = typedValue.resourceId
	return ContextCompat.getDrawable(this, imageResId)
}