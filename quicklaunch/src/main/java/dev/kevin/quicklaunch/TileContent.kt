package dev.kevin.quicklaunch

import android.content.Context
import dev.kevin.dapcore.Apps
import dev.kevin.dapcore.R as CoreR

/** Lo que muestra un tile. Lo usan el tile real, la vista previa y la lista de slots. */
data class TileContent(
    val label: String,
    val subtitle: String?,
    val icon: TileIcon,
    val packageName: String?,
    val appLabel: String?,
    val launchable: Boolean,
) {
    companion object {
        fun of(context: Context, prefs: TilePrefs, slot: Int): TileContent {
            val pkg = prefs.app(slot)
            val appLabel = pkg?.let { Apps.label(context, it) }
            val launchable = pkg != null && Apps.launchIntent(context, pkg) != null
            val custom = prefs.label(slot)
            val label = custom ?: appLabel ?: pkg ?: context.getString(R.string.tile_set_up)
            val subtitle = when {
                pkg == null -> context.getString(R.string.slot_title, slot)
                !launchable -> context.getString(CoreR.string.dap_app_missing)
                else -> null
            }
            return TileContent(label, subtitle, TileIcon.forSlot(context, prefs, slot), pkg, appLabel, launchable)
        }
    }
}
