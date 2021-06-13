package ru.serzh272.matrixmvvm.repositories

import ru.serzh272.matrixmvvm.utils.Matrix

@ExperimentalUnsignedTypes
object Repository {
    private val prefs = PreferencesRepository.getSettings()
    private val matrixList = mutableListOf(Matrix(prefs.rows, prefs.columns), Matrix(prefs.rows, prefs.columns), Matrix(prefs.rows, prefs.columns))
    private var matrixViewTitles = listOf("A", "B", "Result")
        get() = listOf(
            "A\n(${matrixList[0].numRows}x${matrixList[0].numColumns})",
            "B\n(${matrixList[1].numRows}x${matrixList[1].numColumns})",
            "Result\n(${matrixList[2].numRows}x${matrixList[2].numColumns})"
        )


    fun getData(): List<Matrix> {
        return matrixList
    }

    fun getTitles(): List<String> {
        return matrixViewTitles
    }


    fun saveTitles(data: List<String>) {
        matrixViewTitles = data as MutableList<String>
    }

    fun getItemData(pos: Int): Matrix {
        return matrixList[pos]
    }

    fun saveItemData(matrix: Matrix, pos: Int = 0) {
        if (pos in matrixList.indices) {
            matrixList[pos] = matrix
        }
    }
}