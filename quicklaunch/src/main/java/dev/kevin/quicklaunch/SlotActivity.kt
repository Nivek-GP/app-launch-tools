package dev.kevin.quicklaunch

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import dev.kevin.dapcore.Apps
import dev.kevin.dapcore.PickApp
import dev.kevin.dapcore.enableDapEdgeToEdge
import dev.kevin.dapcore.fadeIn
import dev.kevin.dapcore.padForSystemBars
import dev.kevin.quicklaunch.databinding.ActivitySlotBinding
import dev.kevin.dapcore.R as CoreR

/** Edición de un tile: app, texto, icono y visibilidad. Los cambios se guardan al momento. */
class SlotActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySlotBinding
    private lateinit var prefs: TilePrefs
    private lateinit var icons: IconAdapter
    private var slot = 0

    private val pickApp = registerForActivityResult(PickApp()) { pkg ->
        if (pkg != null) {
            prefs.setApp(slot, pkg)
            render()
            binding.appCard.fadeIn()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        slot = intent.getIntExtra(EXTRA_SLOT, 0)
        if (slot !in TileSlots.all) {
            finish()
            return
        }
        enableDapEdgeToEdge()
        binding = ActivitySlotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.content.padForSystemBars()
        prefs = TilePrefs(this)

        binding.title.text = getString(R.string.slot_title, slot)
        binding.appCard.setOnClickListener { pick() }

        binding.labelInput.setText(prefs.label(slot))
        binding.labelInput.doAfterTextChanged {
            prefs.setLabel(slot, it?.toString())
            renderPreview()
        }
        binding.labelInput.setOnEditorActionListener { view, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                getSystemService(InputMethodManager::class.java).hideSoftInputFromWindow(view.windowToken, 0)
                view.clearFocus()
            }
            false
        }

        icons = IconAdapter { icon ->
            prefs.setIconKey(slot, icon?.name)
            render()
        }
        binding.iconGrid.layoutManager = GridLayoutManager(this, ICON_COLUMNS)
        binding.iconGrid.adapter = icons

        binding.visibleSwitch.isChecked = TileSlots.isVisible(this, slot)
        binding.visibleSwitch.setOnCheckedChangeListener { _, checked ->
            TileSlots.setVisible(this, slot, checked)
            renderPreview()
        }

        binding.resetButton.setOnClickListener {
            prefs.reset(slot)
            binding.labelInput.text = null
            render()
        }

        render()
        if (savedInstanceState == null && intent.getBooleanExtra(EXTRA_PICK_APP, false) && prefs.app(slot) == null) {
            pick()
        }
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    override fun onPause() {
        super.onPause()
        // Un solo refresco del tile al salir, en vez de uno por cada tecla.
        TileSlots.refresh(this, slot)
    }

    private fun pick() {
        pickApp.launch(
            PickApp.Request(
                title = getString(R.string.pick_title, slot),
                exclude = setOf(packageName),
                selected = prefs.app(slot),
            ),
        )
    }

    private fun render() {
        val pkg = prefs.app(slot)
        val app = pkg?.let { Apps.load(this, it) }
        with(binding) {
            when {
                pkg == null -> {
                    appIcon.setImageResource(CoreR.drawable.dap_bg_placeholder)
                    appName.setText(R.string.app_not_set)
                    appHint.setText(R.string.app_tap_choose)
                }
                app == null -> {
                    appIcon.setImageResource(CoreR.drawable.dap_bg_placeholder)
                    appName.text = pkg
                    appHint.setText(CoreR.string.dap_app_missing)
                }
                else -> {
                    appIcon.setImageDrawable(app.icon)
                    appName.text = app.label
                    appHint.setText(R.string.app_tap_change)
                }
            }
            labelInput.hint = app?.label ?: getString(R.string.tile_set_up)

            val chosen = TileIcon.fromKey(prefs.iconKey(slot))
            icons.selected = chosen
            iconCaption.isVisible = chosen == null
            iconCaption.text = getString(R.string.icon_auto_caption, getString(TileIcon.suggest(this@SlotActivity, pkg).label))

            resetButton.isVisible = pkg != null || prefs.label(slot) != null || chosen != null
        }
        renderPreview()
    }

    private fun renderPreview() {
        val content = TileContent.of(this, prefs, slot)
        with(binding) {
            previewIcon.setImageResource(content.icon.res)
            previewLabel.text = content.label
            previewSubtitle.text = content.subtitle
            previewSubtitle.isVisible = content.subtitle != null
            preview.alpha = if (visibleSwitch.isChecked) 1f else HIDDEN_ALPHA
        }
    }

    companion object {
        const val EXTRA_SLOT = "dev.kevin.quicklaunch.extra.SLOT"

        /** Abre el selector de apps al entrar si el slot está vacío (toque en un tile sin app). */
        const val EXTRA_PICK_APP = "dev.kevin.quicklaunch.extra.PICK_APP"

        private const val ICON_COLUMNS = 5
        private const val HIDDEN_ALPHA = 0.4f
    }
}
