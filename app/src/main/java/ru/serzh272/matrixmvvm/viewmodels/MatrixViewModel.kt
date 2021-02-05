package ru.serzh272.matrixmvvm.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.serzh272.matrixmvvm.repositories.Repository
import ru.serzh272.matrixmvvm.utils.Matrix
@ExperimentalUnsignedTypes
class MatrixViewModel:ViewModel() {
    private val repository = Repository
    private val data = repository.getData()

    val matrA: MutableLiveData<Matrix> by lazy {
        MutableLiveData<Matrix>(data[0])
    }
    val matrB: MutableLiveData<Matrix> by lazy {
        MutableLiveData<Matrix>(data[1])
    }
    val matrRez: MutableLiveData<Matrix> by lazy {
        MutableLiveData<Matrix>(data[2])
    }

    fun setMatr(ind:Int, matr:Matrix){
        when(ind){
            0 -> matrA.value = matr
            1 -> matrB.value = matr
            2 -> matrRez.value = matr
        }
        repository.saveItemData(matr, ind)
    }
    fun getMatr(ind:Int):Matrix{
        return data[ind]
    }
}