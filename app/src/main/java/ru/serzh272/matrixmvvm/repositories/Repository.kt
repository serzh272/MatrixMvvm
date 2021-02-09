package ru.serzh272.matrixmvvm.repositories

import android.util.Log
import ru.serzh272.matrixmvvm.utils.Matrix
@ExperimentalUnsignedTypes
object Repository {
    private val matrixList = mutableListOf(Matrix(), Matrix(), Matrix())
    private var titles = mutableListOf<String>("A", "B", "rez")

    fun getData(): List<Matrix> {
        return matrixList
    }

    fun getTitles():MutableList<String>{
        return titles
    }

    fun saveTitles(data:List<String>){
        titles = data as MutableList<String>
    }

    fun getItemData(pos:Int):Matrix{
        return matrixList[pos]
    }

    fun saveItemData(matrix: Matrix, pos: Int = 0) {
        if (pos in matrixList.indices){
            matrixList[pos] = matrix
        }
        Log.d("M_MainActivity", "Repository has data $matrixList")
    }
}