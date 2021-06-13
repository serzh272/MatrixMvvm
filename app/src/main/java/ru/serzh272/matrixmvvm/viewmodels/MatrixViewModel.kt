package ru.serzh272.matrixmvvm.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.serzh272.matrixmvvm.repositories.Repository
import ru.serzh272.matrixmvvm.utils.Matrix

@ExperimentalUnsignedTypes
class MatrixViewModel:ViewModel() {
    private val repository = Repository
    private val data = repository.getData()
    var titles = repository.getTitles()

    val titlesLiveData: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>(titles)
    }
    val matrixLiveData1: MutableLiveData<Matrix> by lazy {
        MutableLiveData<Matrix>(data[0])
    }
    val matrixLiveData2: MutableLiveData<Matrix> by lazy {
        MutableLiveData<Matrix>(data[1])
    }
    val matrixLiveData3: MutableLiveData<Matrix> by lazy {
        MutableLiveData<Matrix>(data[2])
    }

    fun saveMatrix(ind:Int, matr:Matrix){
        val titles = this.titles as MutableList
        when(ind){
            0 -> {
                matrixLiveData1.value = matr
                titles[0] = "A\n(${matr.numRows}x${matr.numColumns})"
            }
            1 -> {
                matrixLiveData2.value = matr
                titles[1] = "B\n(${matr.numRows}x${matr.numColumns})"
            }
            2 -> {
                matrixLiveData3.value = matr
                titles[2] = "Result\n(${matr.numRows}x${matr.numColumns})"
            }
        }

        titlesLiveData.value = titles
        repository.saveItemData(matr, ind)
        repository.saveTitles(titles)
    }



    fun loadMatrix(ind:Int):Matrix{
        return repository.getItemData(ind)
    }

    fun loadData(): List<Matrix?> {
        return  listOf(matrixLiveData1.value, matrixLiveData2.value, matrixLiveData3.value)
    }
}