package dev.kevin.shortcutlauncher

import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import dev.kevin.dapcore.Apps
import dev.kevin.dapcore.PickApp
import dev.kevin.dapcore.enableDapEdgeToEdge
import dev.kevin.dapcore.fadeIn
import dev.kevin.dapcore.padForSystemBars
import dev.kevin.shortcutlauncher.databinding.ActivitySettingsBinding
import dev.kevin.dapcore.R as CoreR

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: LauncherPrefs
    private val roleManager by lazy { getSystemService(RoleManager::class.java) }

    private val pickApp = registerForActivityResult(PickApp()) { pkg ->
        if (pkg != null) {
            prefs.targetPackage = pkg
            render()
            binding.targetCard.fadeIn()
        }
    }

    private val requestHomeRole = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        render()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableDapEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.content.padForSystemBars()
        prefs = LauncherPrefs(this)

        binding.targetCard.setOnClickListener {
            pickApp.launch(
                PickApp.Request(
                    title = getString(R.string.pick_title),
                    exclude = setOf(packageName),
                    selected = prefs.targetPackage,
                ),
            )
        }
        binding.homeButton.setOnClickListener { changeDefaultHome() }
        binding.delaySlider.value = prefs.startupDelaySeconds.toFloat()
        binding.delaySlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                prefs.startupDelaySeconds = value.toInt()
                renderDelay()
            }
        }
        binding.testButton.setOnClickListener { launchTarget() }
    }

    override fun onResume() {
        super.onResume()
        // La app destino o el Home por defecto pueden haber cambiado fuera de aquí.
        render()
    }

    private fun render() {
        val pkg = prefs.targetPackage
        val app = pkg?.let { Apps.load(this, it) }
        with(binding) {
            when {
                pkg == null -> {
                    targetIcon.setImageResource(CoreR.drawable.dap_bg_placeholder)
                    targetName.setText(R.string.startup_not_set)
                    targetHint.setText(R.string.startup_tap_to_choose)
                }
                app == null -> {
                    targetIcon.setImageResource(CoreR.drawable.dap_bg_placeholder)
                    targetName.text = pkg
                    targetHint.setText(R.string.startup_missing)
                }
                else -> {
                    targetIcon.setImageDrawable(app.icon)
                    targetName.text = app.label
                    targetHint.setText(R.string.startup_change)
                }
            }
            testButton.isVisible = app != null

            val isHome = roleManager.isRoleHeld(RoleManager.ROLE_HOME)
            homeStatus.setText(if (isHome) R.string.home_active else R.string.home_inactive)
            homeButton.setText(if (isHome) R.string.home_change else R.string.home_set)
        }
        renderDelay()
    }

    private fun renderDelay() {
        val seconds = prefs.startupDelaySeconds
        binding.delayValue.text = if (seconds == 0) getString(R.string.delay_off) else getString(R.string.delay_value, seconds)
    }

    private fun changeDefaultHome() {
        if (!roleManager.isRoleHeld(RoleManager.ROLE_HOME) && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
            requestHomeRole.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME))
            return
        }
        try {
            startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
        }
    }

    private fun launchTarget() {
        val intent = prefs.targetPackage?.let { Apps.launchIntent(this, it) } ?: return
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
