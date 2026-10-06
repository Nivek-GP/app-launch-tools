package dev.kevin.quicklaunch

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.service.quicksettings.TileService
import androidx.core.content.edit

object TileSlots {

    private val services = listOf(
        Tile1Service::class.java,
        Tile2Service::class.java,
        Tile3Service::class.java,
        Tile4Service::class.java,
    )

    val all: IntRange = 1..services.size

    fun component(context: Context, slot: Int) = ComponentName(context, services[slot - 1])

    fun slotFor(component: ComponentName?): Int? =
        services.indexOfFirst { it.name == component?.className }.takeIf { it >= 0 }?.plus(1)

    /** Pide a SystemUI que vuelva a llamar a onStartListening() para actualizar el tile. */
    fun refresh(context: Context, slot: Int) {
        if (isVisible(context, slot)) TileService.requestListeningState(context, component(context, slot))
    }

    /**
     * Un tile oculto es un TileService desactivado: SystemUI lo quita del panel y de la
     * lista de edición. El estado vive en el propio componente, no en las preferencias.
     * Sin cambios del usuario manda el manifest: solo el tile 1 viene activado.
     */
    fun isVisible(context: Context, slot: Int): Boolean {
        val pm = context.packageManager
        val component = component(context, slot)
        return when (pm.getComponentEnabledSetting(component)) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT ->
                pm.getServiceInfo(
                    component,
                    PackageManager.ComponentInfoFlags.of(PackageManager.MATCH_DISABLED_COMPONENTS.toLong()),
                ).enabled
            else -> false
        }
    }

    fun setVisible(context: Context, slot: Int, visible: Boolean) {
        context.packageManager.setComponentEnabledSetting(
            component(context, slot),
            if (visible) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP,
        )
    }
}

class TilePrefs(context: Context) {

    private val prefs = context.getSharedPreferences("tiles", Context.MODE_PRIVATE)

    fun app(slot: Int): String? = prefs.getString(key(slot), null)

    fun setApp(slot: Int, packageName: String?) = prefs.edit {
        if (packageName == null) remove(key(slot)) else putString(key(slot), packageName)
    }

    /** Texto personalizado del tile; null usa el nombre de la app. */
    fun label(slot: Int): String? = prefs.getString(key(slot, "label"), null)

    fun setLabel(slot: Int, label: String?) = prefs.edit {
        if (label.isNullOrBlank()) remove(key(slot, "label")) else putString(key(slot, "label"), label.trim())
    }

    /** Nombre de [TileIcon] elegido; null es "Automático". */
    fun iconKey(slot: Int): String? = prefs.getString(key(slot, "icon"), null)

    fun setIconKey(slot: Int, iconKey: String?) = prefs.edit {
        if (iconKey == null) remove(key(slot, "icon")) else putString(key(slot, "icon"), iconKey)
    }

    /** Deja el slot vacío: sin app, texto ni icono. */
    fun reset(slot: Int) = prefs.edit {
        remove(key(slot))
        remove(key(slot, "label"))
        remove(key(slot, "icon"))
    }

    // "slot_N" se mantiene como clave de la app para conservar las asignaciones ya hechas.
    private fun key(slot: Int, field: String? = null) = if (field == null) "slot_$slot" else "slot_${slot}_$field"
}
