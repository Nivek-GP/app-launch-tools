package dev.kevin.shortcutlauncher

import android.content.Context
import androidx.core.content.edit

class LauncherPrefs(context: Context) {

    private val prefs = context.getSharedPreferences("launcher", Context.MODE_PRIVATE)

    var targetPackage: String?
        get() = prefs.getString(KEY_TARGET, null)
        set(value) = prefs.edit { putString(KEY_TARGET, value) }

    var startupDelaySeconds: Int
        get() = prefs.getInt(KEY_DELAY, 0)
        set(value) = prefs.edit { putInt(KEY_DELAY, value) }

    /** Último BOOT_COUNT visto, para aplicar el retraso solo en el primer Home tras arrancar. */
    var lastBootCount: Int
        get() = prefs.getInt(KEY_BOOT_COUNT, -1)
        set(value) = prefs.edit { putInt(KEY_BOOT_COUNT, value) }

    private companion object {
        const val KEY_TARGET = "target_package"
        const val KEY_DELAY = "startup_delay_seconds"
        const val KEY_BOOT_COUNT = "last_boot_count"
    }
}
