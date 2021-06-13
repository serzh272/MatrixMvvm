package ru.serzh272.matrixmvvm.repositories

import ru.serzh272.matrixmvvm.data.AppSettings
import ru.serzh272.matrixmvvm.data.PrefManager

object PreferencesRepository {
    private val prefs = PrefManager()

    fun updateSettings(appSettings: AppSettings){
        prefs.mode = appSettings.mode
        prefs.isMixed = appSettings.isMixedFraction
        prefs.precision = appSettings.precision
        prefs.cols = appSettings.columns
        prefs.rows = appSettings.rows
    }

    fun getSettings():AppSettings{
        return AppSettings(mode = prefs.mode,
            isMixedFraction = prefs.isMixed,
            precision = prefs.precision,
            columns = prefs.cols,
            rows = prefs.rows)
    }
}