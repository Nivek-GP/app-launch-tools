package dev.kevin.quicklaunch

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import dev.kevin.dapcore.Apps

/** Tile que abre la app asignada a su slot; sin app asignada abre la edición del slot. */
abstract class AppTileService(private val slot: Int) : TileService() {

    override fun onTileAdded() = refresh()

    override fun onStartListening() = refresh()

    private fun refresh() {
        val tile = qsTile ?: return
        val content = TileContent.of(this, TilePrefs(this), slot)
        tile.label = content.label
        tile.subtitle = content.subtitle
        tile.icon = Icon.createWithResource(this, content.icon.res)
        tile.state = Tile.STATE_INACTIVE
        tile.updateTile()
    }

    override fun onClick() {
        val intent = TilePrefs(this).app(slot)?.let { Apps.launchIntent(this, it) }
            ?: Intent(this, SlotActivity::class.java)
                .putExtra(SlotActivity.EXTRA_SLOT, slot)
                .putExtra(SlotActivity.EXTRA_PICK_APP, true)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (isLocked) unlockAndRun { open(intent) } else open(intent)
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    private fun open(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pending = PendingIntent.getActivity(
                this,
                slot,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
            startActivityAndCollapse(pending)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}

class Tile1Service : AppTileService(1)
class Tile2Service : AppTileService(2)
class Tile3Service : AppTileService(3)
class Tile4Service : AppTileService(4)
