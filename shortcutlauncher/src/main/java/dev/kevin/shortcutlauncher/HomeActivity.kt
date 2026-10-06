package dev.kevin.shortcutlauncher

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import dev.kevin.dapcore.Apps

/**
 * Recibe el intent HOME (arranque y botón Home) y abre la app configurada.
 * Si no hay app válida abre los ajustes en vez de reintentar, para no entrar en bucle.
 */
class HomeActivity : Activity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        route()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        route()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun route() {
        handler.removeCallbacksAndMessages(null)
        val prefs = LauncherPrefs(this)
        val launch = prefs.targetPackage
            ?.takeIf { it != packageName }
            ?.let { Apps.launchIntent(this, it) }
        if (launch == null) {
            openSettings()
            return
        }

        val delayMs = startupDelayMs(prefs)
        if (delayMs > 0) {
            handler.postDelayed({ launchTarget(launch) }, delayMs)
        } else {
            launchTarget(launch)
        }
    }

    private fun startupDelayMs(prefs: LauncherPrefs): Long {
        val seconds = prefs.startupDelaySeconds
        if (seconds <= 0) return 0
        val bootCount = Settings.Global.getInt(contentResolver, Settings.Global.BOOT_COUNT, -1)
        val firstSinceBoot = if (bootCount >= 0) {
            (bootCount != prefs.lastBootCount).also { prefs.lastBootCount = bootCount }
        } else {
            SystemClock.elapsedRealtime() < BOOT_WINDOW_MS
        }
        return if (firstSinceBoot) seconds * 1000L else 0
    }

    private fun launchTarget(intent: Intent) {
        try {
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED))
        } catch (e: ActivityNotFoundException) {
            openSettings()
            return
        } catch (e: SecurityException) {
            openSettings()
            return
        }
        finishQuietly()
    }

    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        finishQuietly()
    }

    @Suppress("DEPRECATION")
    private fun finishQuietly() {
        finish()
        overridePendingTransition(0, 0)
    }

    private companion object {
        const val BOOT_WINDOW_MS = 2 * 60 * 1000L
    }
}
