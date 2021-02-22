package ru.serzh272.matrixmvvm.repositories

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import ru.serzh272.matrixmvvm.App
import ru.serzh272.matrixmvvm.data.Preferences

object PreferencesRepository {
    private const val MODE = "MODE"
    private const val ROWS = "ROWS"
    private const val COLUMNS = "COLUMNS"
    private const val PRECISION = "PRECISION"

    private val prefs: SharedPreferences by lazy {
        val context = App.applicationContext()
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getPrefs(): Preferences {
        return Preferences(
            prefs.getInt(MODE, 0),
            prefs.getInt(PRECISION, 2),
            prefs.getInt(ROWS, 3),
            prefs.getInt(COLUMNS, 3)
        )
    }

    fun savePrefs(preferences: Preferences) {
        with(preferences) {
            putValue(MODE to this.mode)
            putValue(ROWS to this.rows)
            putValue(COLUMNS to this.columns)
            putValue(PRECISION to this.precision)
        }
    }

    private fun putValue(pair: Pair<String, Any>) = with(prefs.edit()) {
        val key = pair.first
        val value = pair.second
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            else -> error("only primitives types can be stored in Shared Preferences")
        }
        apply()
    }
}