package ru.serzh272.matrixmvvm.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import ru.serzh272.matrixmvvm.App
import ru.serzh272.matrixmvvm.data.delegates.PrefDelegate

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PrefManager(context: Context = App.applicationContext()) {
    var mode by PrefDelegate(0)
    var isMixed by PrefDelegate(true)
    var precision by PrefDelegate(2)
    var rows by PrefDelegate(3)
    var cols by PrefDelegate(3)
    val settings: LiveData<AppSettings>
        get() {
            val mode = dataStore.data.map { it[intPreferencesKey(this::mode.name)] ?: 0 }
            val precision = dataStore.data.map { it[intPreferencesKey(this::precision.name)] ?: 2 }
            val isMixed =
                dataStore.data.map { it[booleanPreferencesKey(this::isMixed.name)] ?: true }
            val rows = dataStore.data.map { it[intPreferencesKey(this::rows.name)] ?: 3 }
            val cols = dataStore.data.map { it[intPreferencesKey(this::cols.name)] ?: 3 }
            val settings = AppSettings()
            return mode.zip(precision) { m, p ->
                settings.also {
                    it.mode = m
                    it.precision = p
                }
            }.zip(isMixed) { _, isM ->
                settings.also {
                    it.isMixedFraction = isM
                }
            }.zip(rows) { _, r ->
                settings.also {
                    it.rows = r
                }
            }.zip(cols) { _, c ->
                settings.also {
                    it.columns = c
                }
            }
                .distinctUntilChanged()
                .asLiveData()
        }
    val dataStore = context.dataStore


    private val errHandler = CoroutineExceptionHandler { _, th ->
        Log.d("M_PrefManager", "err ${th.message}")
        //TODO handle error this
    }
    internal val scope = CoroutineScope(SupervisorJob() + errHandler)
}