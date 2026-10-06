package dev.kevin.quicklaunch

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import dev.kevin.dapcore.Apps
import dev.kevin.dapcore.enableDapEdgeToEdge
import dev.kevin.dapcore.padForSystemBars
import dev.kevin.quicklaunch.databinding.ActivityConfigBinding
import dev.kevin.quicklaunch.databinding.ItemSlotBinding

/** Lista de los 4 tiles; tocar uno abre su edición en [SlotActivity]. */
class ConfigActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfigBinding
    private lateinit var prefs: TilePrefs
    private val rows = mutableMapOf<Int, ItemSlotBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableDapEdgeToEdge()
        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.content.padForSystemBars()
        prefs = TilePrefs(this)

        for (slot in TileSlots.all) {
            val row = ItemSlotBinding.inflate(layoutInflater, binding.slots, true)
            row.root.setOnClickListener { openSlot(slot) }
            rows[slot] = row
        }

        if (savedInstanceState == null) handleTarget(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleTarget(intent)
    }

    override fun onResume() {
        super.onResume()
        // Volvemos de editar un slot, o una app asignada pudo desinstalarse.
        render()
    }

    /** Pulsación larga en un tile: SystemUI indica cuál con EXTRA_COMPONENT_NAME; vamos directo a él. */
    private fun handleTarget(intent: Intent) {
        val slot = TileSlots.slotFor(intent.getParcelableExtra(Intent.EXTRA_COMPONENT_NAME, ComponentName::class.java))
            ?: return
        openSlot(slot)
    }

    private fun openSlot(slot: Int) {
        startActivity(Intent(this, SlotActivity::class.java).putExtra(SlotActivity.EXTRA_SLOT, slot))
    }

    private fun render() {
        for ((slot, row) in rows) {
            val content = TileContent.of(this, prefs, slot)
            val visible = TileSlots.isVisible(this, slot)
            with(row) {
                glyph.setImageResource(content.icon.res)
                val app = content.packageName?.let { Apps.load(this@ConfigActivity, it) }
                appIcon.isVisible = app != null
                app?.let { appIcon.setImageDrawable(it.icon) }

                val details = mutableListOf(getString(R.string.slot_title, slot))
                if (content.packageName == null) {
                    name.setText(R.string.slot_empty)
                    details += getString(R.string.slot_not_set)
                } else {
                    name.text = content.label
                    content.appLabel?.takeIf { it != content.label }?.let { details += it }
                    content.subtitle?.let { details += it }
                }
                if (!visible) details += getString(R.string.slot_hidden)
                hint.text = details.joinToString(" · ")
                root.alpha = if (visible) 1f else HIDDEN_ALPHA
            }
        }
    }

    private companion object {
        const val HIDDEN_ALPHA = 0.55f
    }
}
