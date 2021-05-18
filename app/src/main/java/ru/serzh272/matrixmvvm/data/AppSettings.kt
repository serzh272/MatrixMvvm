package ru.serzh272.matrixmvvm.data

data class AppSettings(
    var mode:Int = 0,
    var isMixedFraction:Boolean = true,
    var precision:Int = 2,
    var rows:Int = 3,
    var columns:Int = 3
)