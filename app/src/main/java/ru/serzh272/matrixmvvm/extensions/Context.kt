package ru.serzh272.matrixmvvm.extensions

import android.content.Context

fun Context.dpToPx(dp:Int):Float{
    return dp.toFloat() * this.resources.displayMetrics.density
}

fun Context.pxToDp(px:Float):Int{
    return (px / this.resources.displayMetrics.density).toInt()
}