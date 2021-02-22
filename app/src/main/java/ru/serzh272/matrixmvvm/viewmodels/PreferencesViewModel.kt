package ru.serzh272.matrixmvvm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.serzh272.matrixmvvm.data.Preferences
import ru.serzh272.matrixmvvm.repositories.PreferencesRepository

class PreferencesViewModel: ViewModel() {
    val prefsRepo = PreferencesRepository
    val prefsLiveData:MutableLiveData<Preferences> by lazy {
        MutableLiveData<Preferences>(prefsRepo.getPrefs())
    }

}