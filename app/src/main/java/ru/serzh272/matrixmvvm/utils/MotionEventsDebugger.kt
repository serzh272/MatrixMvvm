package ru.serzh272.matrixmvvm.utils

import android.util.Log
import android.view.MotionEvent

object MotionEventsDebugger {
    fun debugPrint(func:String, objName:String, ev:MotionEvent?){
        val act = when(ev?.actionMasked){
            MotionEvent.ACTION_DOWN -> "DOWN"
            MotionEvent.ACTION_UP -> "UP"
            MotionEvent.ACTION_MOVE -> "MOVE"
            MotionEvent.ACTION_CANCEL -> "CANCEL"
            else -> "${ev?.actionMasked}"
        }
        Log.d("M_EventDebug", "${"%-16s".format(objName)} \t--- ${"%-21s".format(func)} \t--> \t$act")
    }
    fun debugReturnPrint(func:String, objName:String, retValue:Boolean):Boolean{
        Log.d("M_EventDebug", "${"%-16s".format(objName)} \t--- ${"%-21s".format(func)} \tret \t$retValue")
        return retValue
    }
}