package dev.kevin.dapcore

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

/** Abre [AppPickerActivity] y devuelve el paquete elegido, o null si se cancela. */
class PickApp : ActivityResultContract<PickApp.Request, String?>() {

    data class Request(
        val title: String,
        val exclude: Set<String> = emptySet(),
        val selected: String? = null,
    )

    override fun createIntent(context: Context, input: Request): Intent =
        Intent(context, AppPickerActivity::class.java)
            .putExtra(AppPickerActivity.EXTRA_TITLE, input.title)
            .putExtra(AppPickerActivity.EXTRA_EXCLUDE, input.exclude.toTypedArray())
            .putExtra(AppPickerActivity.EXTRA_SELECTED, input.selected)

    override fun parseResult(resultCode: Int, intent: Intent?): String? =
        intent?.getStringExtra(AppPickerActivity.EXTRA_PACKAGE)?.takeIf { resultCode == Activity.RESULT_OK }
}
