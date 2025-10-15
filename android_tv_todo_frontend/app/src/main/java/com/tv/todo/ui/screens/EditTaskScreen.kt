package com.tv.todo.ui.screens

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tv.todo.data.entity.Task
import com.tv.todo.databinding.FragmentEditTaskBinding
import com.tv.todo.di.ServiceLocator
import com.tv.todo.viewmodel.TaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * PUBLIC_INTERFACE
 * EditTaskFragment allows creating a new task or editing an existing task.
 */
class EditTaskFragment : Fragment() {

    interface Listener {
        fun onDoneEditing()
    }

    private var listener: Listener? = null
    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TaskViewModel
    private var taskId: Long? = null
    private var loadedTask: Task? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? Listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskId = arguments?.getLong(ARG_TASK_ID)
        viewModel = ViewModelProvider(this, ServiceLocator.provideTaskViewModelFactory())
            .get(TaskViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.inputTitle.doAfterTextChanged { /* live changes already stored on save */ }
        binding.inputDescription.doAfterTextChanged { }

        binding.btnSave.setOnClickListener {
            val title = binding.inputTitle.text?.toString().orEmpty()
            val desc = binding.inputDescription.text?.toString().orEmpty()
            val due = null // Simplified: date picker can be added later
            CoroutineScope(Dispatchers.IO).launch {
                if (loadedTask == null) {
                    viewModel.addTask(title, desc, due)
                } else {
                    val t = loadedTask!!.copy(title = title, description = desc)
                    viewModel.updateTask(t)
                }
            }
            listener?.onDoneEditing()
        }

        binding.btnDelete.setOnClickListener {
            loadedTask?.let { t ->
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.deleteTask(t)
                }
            }
            listener?.onDoneEditing()
        }

        if (taskId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val task = viewModel.getTask(taskId!!)
                loadedTask = task
                CoroutineScope(Dispatchers.Main).launch {
                    if (task != null) {
                        binding.inputTitle.setText(task.title)
                        binding.inputDescription.setText(task.description)
                        binding.lblHeader.text = "Edit Task"
                        binding.btnDelete.visibility = View.VISIBLE
                        binding.lblDueDate.text = "Due: " + (task.dueDate?.let {
                            DateFormat.getDateFormat(requireContext()).format(it)
                        } ?: "None")
                    }
                }
            }
        } else {
            binding.lblHeader.text = "Add Task"
            binding.btnDelete.visibility = View.GONE
            binding.lblDueDate.text = "Due: None"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TASK_ID = "task_id"

        fun newInstance(taskId: Long?): EditTaskFragment {
            val f = EditTaskFragment()
            if (taskId != null) {
                val b = Bundle()
                b.putLong(ARG_TASK_ID, taskId)
                f.arguments = b
            }
            return f
        }
    }
}
