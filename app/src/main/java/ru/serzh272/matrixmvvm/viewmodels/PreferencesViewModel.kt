package ru.serzh272.matrixmvvm.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.serzh272.matrixmvvm.data.AppSettings
import ru.serzh272.matrixmvvm.repositories.PreferencesRepository

class PreferencesViewModel: ViewModel() {
    val prefsRepo = PreferencesRepository
    val prefsLiveData:MutableLiveData<AppSettings> by lazy {
        MutableLiveData<AppSettings>(prefsRepo.getSettings())
    }

    fun handleSetPrecision(prec:Int){
        val appSettings = prefsRepo.getSettings()
        if (prec != appSettings.precision) {
            appSettings.precision = prec
            prefsRepo.updateSettings(appSettings)
            prefsLiveData.value = appSettings
        }
    }

    fun handleSetIsMixed(isMix:Boolean){
        val appSettings = prefsRepo.getSettings()
        if (isMix != appSettings.isMixedFraction) {
            appSettings.isMixedFraction = isMix
            prefsRepo.updateSettings(appSettings)
            prefsLiveData.value = appSettings
        }
    }

    fun handleSetMode(mode:Int){
        val appSettings = prefsRepo.getSettings()
        if (mode != appSettings.mode) {
            appSettings.mode = mode
            prefsRepo.updateSettings(appSettings)
            prefsLiveData.value = appSettings
        }
    }

    fun handleSetCols(cols:Int){
        val appSettings = prefsRepo.getSettings()
        if (cols != appSettings.columns) {
            appSettings.columns = cols
            prefsRepo.updateSettings(appSettings)
            prefsLiveData.value = appSettings
        }
    }

    fun handleSetRows(rows:Int){
        val appSettings = prefsRepo.getSettings()
        if (rows != appSettings.rows) {
            appSettings.rows = rows
            prefsRepo.updateSettings(appSettings)
            prefsLiveData.value = appSettings
        }
    }

}