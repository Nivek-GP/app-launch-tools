package dev.kevin.dapcore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.kevin.dapcore.databinding.DapItemAppBinding

internal class AppListAdapter(
    private val selected: String?,
    private val onPick: (AppInfo) -> Unit,
) : ListAdapter<AppInfo, AppListAdapter.Holder>(Diff) {

    class Holder(val binding: DapItemAppBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(DapItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val app = getItem(position)
        with(holder.binding) {
            icon.setImageDrawable(app.icon)
            label.text = app.label
            pkg.text = app.packageName
            check.isVisible = app.packageName == selected
            root.setOnClickListener { onPick(app) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem.packageName == newItem.packageName

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem.packageName == newItem.packageName && oldItem.label == newItem.label
    }
}
