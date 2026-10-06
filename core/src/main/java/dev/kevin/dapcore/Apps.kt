package dev.kevin.dapcore

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

data class AppInfo(val packageName: String, val label: String, val icon: Drawable)

object Apps {

    fun launchIntent(context: Context, packageName: String): Intent? =
        context.packageManager.getLaunchIntentForPackage(packageName)

    fun label(context: Context, packageName: String): String? = try {
        val pm = context.packageManager
        pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }

    fun load(context: Context, packageName: String): AppInfo? = try {
        val pm = context.packageManager
        val info = pm.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
        AppInfo(packageName, pm.getApplicationLabel(info).toString(), pm.getApplicationIcon(info))
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }

    /** Apps visibles en el cajón, una por paquete, ordenadas por nombre. Llamar fuera del hilo principal. */
    fun loadLaunchable(context: Context, exclude: Set<String> = emptySet()): List<AppInfo> {
        val pm = context.packageManager
        val query = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return pm.queryIntentActivities(query, PackageManager.ResolveInfoFlags.of(0))
            .asSequence()
            .map { it.activityInfo.packageName }
            .distinct()
            .filterNot { it in exclude }
            .mapNotNull { load(context, it) }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.label })
            .toList()
    }
}
