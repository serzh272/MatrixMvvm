package ru.serzh272.matrixmvvm.repositories

import ru.serzh272.matrixmvvm.utils.Matrix
@ExperimentalUnsignedTypes
object Repository {
    private val matrixList = mutableListOf(Matrix(), Matrix(), Matrix())
    private val titles = mutableListOf("A", "B", "Result")

    fun getData(): List<Matrix> {
        return matrixList
    }

    fun getTitles():List<String>{
        return titles
    }

    fun saveItemData(matrix: Matrix, pos: Int = 0) {
        if (pos in matrixList.indices){
            matrixList[pos] = matrix
        }
    }
}