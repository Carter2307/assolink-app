package com.assolink.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class ThemeManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("assolink_theme_prefs", Context.MODE_PRIVATE)

    companion object {
        const val MODE_SYSTEM = 0
        const val MODE_LIGHT = 1
        const val MODE_DARK = 2
    }

    fun setThemeMode(mode: Int) {
        saveThemeMode(mode)

        // Puis appliquer le mode
        when (mode) {
            MODE_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            MODE_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
    }

    fun saveThemeMode(mode: Int) {
        prefs.edit().putInt("theme_mode", mode).apply()
    }

    fun getThemeMode(): Int {
        return prefs.getInt("theme_mode", MODE_SYSTEM)
    }

    fun isNightModeActive(): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    fun applyTheme() {
        val currentMode = getThemeMode()
        val isCurrentlyNight = isNightModeActive()

        // Éviter de changer le thème inutilement
        when (currentMode) {
            MODE_LIGHT -> {
                if (isCurrentlyNight) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            MODE_DARK -> {
                if (!isCurrentlyNight) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else -> {
                // Pour le mode système, on laisse Android gérer
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
    }
}