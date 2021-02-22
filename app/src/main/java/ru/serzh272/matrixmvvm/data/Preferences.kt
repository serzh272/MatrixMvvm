package ru.serzh272.matrixmvvm.data

data class Preferences(
    val mode:Int = 0,
    val precision:Int = 2,
    val rows:Int = 3,
    val columns:Int = 3
)