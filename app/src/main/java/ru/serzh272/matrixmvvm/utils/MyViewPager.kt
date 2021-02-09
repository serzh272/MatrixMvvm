package ru.serzh272.matrixmvvm.utils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MyViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    companion object {
        private var initX: Float = 0f
        private var nextX: Float = 0f
    }

    private var mIsScrolling: Boolean = false
    private var mTouchSlope = ViewConfiguration.get(context).scaledTouchSlop

    constructor(context: Context) : this(context, null) {
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        MotionEventsDebugger.debugPrint("onTouchEvent", "viewPager", ev)
        return MotionEventsDebugger.debugReturnPrint("onTouchEvent", "viewPager", super.onTouchEvent(ev))
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        MotionEventsDebugger.debugPrint("dispatchTouchEvent", "viewPager", ev)
        return MotionEventsDebugger.debugReturnPrint("dispatchTouchEvent", "viewPager", super.dispatchTouchEvent(ev))
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        MotionEventsDebugger.debugPrint("onInterceptTouchEvent", "viewPager", ev)
        //return isScroll(ev)
        return MotionEventsDebugger.debugReturnPrint("onInterceptTouchEvent", "viewPager", super.onInterceptTouchEvent(ev))
    }



    fun isScroll(ev: MotionEvent?):Boolean{
        return when (ev?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initX = ev.x
                mIsScrolling = false
                false
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsScrolling = false
                false
            }
            MotionEvent.ACTION_MOVE -> {
                if (mIsScrolling) {
                    true
                } else {
                    val xDelta = calcDelta(ev)
                    //Log.d("M_MyViewPager", "xDelta=$xDelta mTouchSlope=$mTouchSlope")
                    if (xDelta > mTouchSlope) {
                        mIsScrolling = true
                        true
                    } else {
                        false
                    }
                }
            }
            else -> false
        }
    }

    private fun calcDelta(ev: MotionEvent): Int {
        return abs(initX - ev.x).toInt()

    }
}