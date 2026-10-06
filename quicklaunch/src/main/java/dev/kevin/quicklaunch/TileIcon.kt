package dev.kevin.quicklaunch

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/** Iconos disponibles para los tiles. Se guardan por nombre, así que no renombrar. */
enum class TileIcon(@param:DrawableRes val res: Int, @param:StringRes val label: Int) {
    BOLT(R.drawable.ic_tile, R.string.icon_bolt),
    HOME(R.drawable.ic_tile_home, R.string.icon_home),
    APPS(R.drawable.ic_tile_apps, R.string.icon_apps),
    PLAY(R.drawable.ic_tile_play, R.string.icon_play),
    MUSIC(R.drawable.ic_tile_music, R.string.icon_music),
    ALBUM(R.drawable.ic_tile_album, R.string.icon_album),
    HEADPHONES(R.drawable.ic_tile_headphones, R.string.icon_headphones),
    EQUALIZER(R.drawable.ic_tile_equalizer, R.string.icon_equalizer),
    MIC(R.drawable.ic_tile_mic, R.string.icon_mic),
    FOLDER(R.drawable.ic_tile_folder, R.string.icon_folder),
    SETTINGS(R.drawable.ic_tile_settings, R.string.icon_settings),
    TUNE(R.drawable.ic_tile_tune, R.string.icon_tune),
    BLUETOOTH(R.drawable.ic_tile_bluetooth, R.string.icon_bluetooth),
    STAR(R.drawable.ic_tile_star, R.string.icon_star),
    ;

    companion object {

        fun fromKey(key: String?): TileIcon? = entries.firstOrNull { it.name == key }

        /** Icono efectivo del slot: el elegido o, en "Automático", uno sugerido por la app. */
        fun forSlot(context: Context, prefs: TilePrefs, slot: Int): TileIcon =
            fromKey(prefs.iconKey(slot)) ?: suggest(context, prefs.app(slot))

        fun suggest(context: Context, packageName: String?): TileIcon {
            if (packageName == null) return BOLT
            if (packageName == SETTINGS_PACKAGE) return SETTINGS
            val pm = context.packageManager
            val home = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setPackage(packageName)
            if (pm.queryIntentActivities(home, PackageManager.ResolveInfoFlags.of(0)).isNotEmpty()) return HOME
            val category = try {
                pm.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0)).category
            } catch (e: PackageManager.NameNotFoundException) {
                ApplicationInfo.CATEGORY_UNDEFINED
            }
            return when (category) {
                ApplicationInfo.CATEGORY_AUDIO -> MUSIC
                ApplicationInfo.CATEGORY_VIDEO -> PLAY
                else -> BOLT
            }
        }

        private const val SETTINGS_PACKAGE = "com.android.settings"
    }
}
