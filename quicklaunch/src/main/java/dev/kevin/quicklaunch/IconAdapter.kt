package dev.kevin.quicklaunch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kevin.quicklaunch.databinding.ItemIconBinding

/** Cuadrícula de iconos; la primera celda (null) es "Automático". */
class IconAdapter(private val onPick: (TileIcon?) -> Unit) : RecyclerView.Adapter<IconAdapter.Holder>() {

    class Holder(val binding: ItemIconBinding) : RecyclerView.ViewHolder(binding.root)

    private val items: List<TileIcon?> = listOf(null) + TileIcon.entries

    var selected: TileIcon? = null
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemIconBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val icon = items[position]
        with(holder.binding) {
            glyph.setImageResource(icon?.res ?: R.drawable.ic_icon_auto)
            root.isSelected = icon == selected
            root.contentDescription = root.context.getString(icon?.label ?: R.string.icon_auto)
            root.setOnClickListener { onPick(icon) }
        }
    }
}
