package dev.kevin.dapcore

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kevin.dapcore.databinding.DapActivityPickerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Hoja con la lista de apps instalables; se usa vía [PickApp]. */
class AppPickerActivity : AppCompatActivity() {

    private lateinit var binding: DapActivityPickerBinding
    private lateinit var adapter: AppListAdapter
    private var allApps: List<AppInfo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableDapEdgeToEdge()
        binding = DapActivityPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyBackdrop()

        binding.title.text = intent.getStringExtra(EXTRA_TITLE) ?: getString(R.string.dap_picker_title)
        binding.sheet.padForSystemBars(top = false)
        // Tocar fuera de la hoja la cierra.
        binding.root.setOnClickListener { finish() }

        adapter = AppListAdapter(intent.getStringExtra(EXTRA_SELECTED)) { app ->
            setResult(RESULT_OK, Intent().putExtra(EXTRA_PACKAGE, app.packageName))
            finish()
        }
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = adapter
        binding.search.doAfterTextChanged { applyFilter(it?.toString().orEmpty()) }

        val exclude = intent.getStringArrayExtra(EXTRA_EXCLUDE)?.toSet().orEmpty()
        lifecycleScope.launch {
            allApps = withContext(Dispatchers.IO) { Apps.loadLaunchable(this@AppPickerActivity, exclude) }
            binding.progress.isVisible = false
            applyFilter(binding.search.text.toString())
            binding.list.fadeIn()
        }
    }

    private fun applyFilter(query: String) {
        val q = query.trim()
        val filtered = if (q.isEmpty()) {
            allApps
        } else {
            allApps.filter { it.label.contains(q, ignoreCase = true) || it.packageName.contains(q, ignoreCase = true) }
        }
        adapter.submitList(filtered)
        binding.empty.isVisible = filtered.isEmpty() && !binding.progress.isVisible
    }

    /** Blur detrás de la hoja si el dispositivo lo soporta; si no, un scrim más opaco. */
    private fun applyBackdrop() {
        val blur = windowManager.isCrossWindowBlurEnabled
        if (blur) {
            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            window.attributes = window.attributes.apply { blurBehindRadius = dp(24) }
        }
        binding.root.setBackgroundResource(if (blur) R.color.dap_scrim_light else R.color.dap_scrim_strong)
    }

    companion object {
        const val EXTRA_TITLE = "dev.kevin.dapcore.extra.TITLE"
        const val EXTRA_EXCLUDE = "dev.kevin.dapcore.extra.EXCLUDE"
        const val EXTRA_SELECTED = "dev.kevin.dapcore.extra.SELECTED"
        const val EXTRA_PACKAGE = "dev.kevin.dapcore.extra.PACKAGE"
    }
}
