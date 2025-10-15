package com.tv.todo.ui.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tv.todo.data.entity.Task
import com.tv.todo.databinding.ItemTaskCardBinding
import com.tv.todo.ui.theme.ThemeUtils

/**
 * PUBLIC_INTERFACE
 * TaskCardAdapter shows tasks in a TV friendly grid/list.
 */
class TaskCardAdapter(
    private var items: List<Task>,
    private val onClick: (Task) -> Unit,
    private val onToggle: (Task) -> Unit
) : RecyclerView.Adapter<TaskCardAdapter.VH>() {

    fun submitList(newItems: List<Task>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemTaskCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Task) {
            binding.title.text = item.title
            binding.description.text = item.description
            val isDone = item.isCompleted
            binding.badge.text = if (isDone) "Done" else "Open"
            binding.badge.setBackgroundColor(if (isDone) 0xFF16A34A.toInt() else 0xFFF59E0B.toInt())

            // Accessibility
            binding.root.contentDescription = "${item.title}. Status: ${if (isDone) "Done" else "Open"}"
            binding.toggleComplete.contentDescription = if (isDone) "Mark as Open" else "Mark as Done"

            binding.root.setOnClickListener { onClick(item) }
            binding.toggleComplete.setOnClickListener { onToggle(item) }

            // TV Focus polish
            ThemeUtils.applyFocusOutline(binding.root)
            ThemeUtils.applyFocusOutline(binding.toggleComplete)

            // Avoid focus trap: define focus order within card
            binding.root.nextFocusDownId = binding.toggleComplete.id
            binding.toggleComplete.nextFocusUpId = binding.root.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemTaskCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
