package com.tv.todo.ui.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.tv.todo.databinding.FragmentTaskListBinding
import com.tv.todo.di.ServiceLocator
import com.tv.todo.ui.components.TaskCardAdapter
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val spanCount = 3
        val adapter = TaskCardAdapter(emptyList(),
            onClick = { task -> listener?.onEditTask(task.id) },
            onToggle = { task -> viewModel.toggleCompleted(task) }
        )
        binding.recycler.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.recycler.adapter = adapter

        binding.fabAdd.setOnClickListener {
            listener?.onAddNewTask()
        }

        viewModel.tasks.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = TaskListFragment()
    }
}
