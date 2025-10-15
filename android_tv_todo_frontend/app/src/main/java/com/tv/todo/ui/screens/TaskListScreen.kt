package com.tv.todo.ui.screens

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tv.todo.R
import com.tv.todo.data.entity.Task
import com.tv.todo.databinding.FragmentTaskListBinding
import com.tv.todo.di.ServiceLocator
import com.tv.todo.ui.components.TaskCardAdapter
import com.tv.todo.ui.theme.ThemeUtils
import com.tv.todo.viewmodel.TaskViewModel

/**
 * PUBLIC_INTERFACE
 * TaskListFragment shows the main TV grid of tasks with a sidebar for filters.
 */
class TaskListFragment : Fragment() {

    interface Listener {
        fun onAddNewTask()
        fun onEditTask(taskId: Long)
    }

    private var listener: Listener? = null
    private lateinit var viewModel: TaskViewModel
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private enum class Filter { ALL, OPEN, DONE }
    private var currentFilter: Filter = Filter.ALL
    private var fullList: List<Task> = emptyList()
    private lateinit var adapter: TaskCardAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? Listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, ServiceLocator.provideTaskViewModelFactory())
            .get(TaskViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun applyFilter() {
        val filtered = when (currentFilter) {
            Filter.ALL -> fullList
            Filter.OPEN -> fullList.filter { !it.isCompleted }
            Filter.DONE -> fullList.filter { it.isCompleted }
        }
        adapter.submitList(filtered)
        binding.emptyState.isVisible = filtered.isEmpty()
        // Set initial focus after list updates
        Handler(Looper.getMainLooper()).post {
            if (filtered.isNotEmpty()) {
                binding.recycler.requestFocus()
            } else {
                binding.fabAdd.requestFocus()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val spanCount = resources.getInteger(R.integer.tv_grid_span).takeIf { it > 0 } ?: 3
        adapter = TaskCardAdapter(emptyList(),
            onClick = { task ->
                listener?.onEditTask(task.id)
            },
            onToggle = { task ->
                viewModel.toggleCompleted(task)
                Snackbar.make(binding.listRoot, if (task.isCompleted) getString(R.string.marked_open) else getString(R.string.marked_done), Snackbar.LENGTH_SHORT).show()
            }
        )
        binding.recycler.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.recycler.adapter = adapter

        // Apply focus outline for main actions
        ThemeUtils.applyFocusOutline(binding.fabAdd)

        // Configure filter buttons if present in activity sidebar include
        // The fragment layout owns filter buttons ids via activity include; safe-guard lookups
        val rootActivity = requireActivity()
        val btnAll = rootActivity.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnFilterAll)
        val btnOpen = rootActivity.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnFilterOpen)
        val btnDone = rootActivity.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnFilterDone)

        listOf(btnAll, btnOpen, btnDone).forEach { btn ->
            btn?.isCheckable = true
            btn?.isFocusable = true
            btn?.isFocusableInTouchMode = true
            ThemeUtils.applyFocusOutline(btn!!)
        }

        fun updateFilterSelection() {
            btnAll?.isChecked = currentFilter == Filter.ALL
            btnOpen?.isChecked = currentFilter == Filter.OPEN
            btnDone?.isChecked = currentFilter == Filter.DONE
        }

        btnAll?.setOnClickListener {
            currentFilter = Filter.ALL
            updateFilterSelection()
            applyFilter()
            Toast.makeText(requireContext(), getString(R.string.filter_all), Toast.LENGTH_SHORT).show()
        }
        btnOpen?.setOnClickListener {
            currentFilter = Filter.OPEN
            updateFilterSelection()
            applyFilter()
            Toast.makeText(requireContext(), getString(R.string.filter_open), Toast.LENGTH_SHORT).show()
        }
        btnDone?.setOnClickListener {
            currentFilter = Filter.DONE
            updateFilterSelection()
            applyFilter()
            Toast.makeText(requireContext(), getString(R.string.filter_done), Toast.LENGTH_SHORT).show()
        }
        updateFilterSelection()

        binding.fabAdd.setOnClickListener {
            listener?.onAddNewTask()
            Snackbar.make(binding.listRoot, getString(R.string.add_task_started), Snackbar.LENGTH_SHORT).show()
        }

        viewModel.tasks.observe(viewLifecycleOwner) { list ->
            fullList = list
            applyFilter()
        }

        // Initial focus after first layout
        binding.listRoot.post { binding.fabAdd.requestFocus() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = TaskListFragment()
    }
}
