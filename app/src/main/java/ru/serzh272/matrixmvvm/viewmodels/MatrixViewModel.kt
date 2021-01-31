package ru.serzh272.matrixmvvm.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.serzh272.matrixmvvm.repositories.Repository
import ru.serzh272.matrixmvvm.utils.Matrix
@ExperimentalUnsignedTypes
class MatrixViewModel:ViewModel() {

    val data = Repository.getData()
    val matrices: MutableLiveData<List<Matrix>> by lazy {
        MutableLiveData<List<Matrix>>()
    }
}