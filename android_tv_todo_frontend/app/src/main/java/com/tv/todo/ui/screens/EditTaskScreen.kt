package com.tv.todo.ui.screens

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.tv.todo.R
import com.tv.todo.data.entity.Task
import com.tv.todo.databinding.FragmentEditTaskBinding
import com.tv.todo.di.ServiceLocator
import com.tv.todo.ui.theme.ThemeUtils
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

    private fun validateTitle(): Boolean {
        val text = binding.inputTitle.text?.toString()?.trim().orEmpty()
        return if (text.isEmpty()) {
            binding.inputTitle.error = getString(R.string.error_title_required)
            false
        } else {
            binding.inputTitle.error = null
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ThemeUtils.applyFocusOutline(binding.btnSave)
        ThemeUtils.applyFocusOutline(binding.btnDelete)

        // Accessibility labels
        binding.btnSave.contentDescription = getString(R.string.cd_save_task)
        binding.btnDelete.contentDescription = getString(R.string.cd_delete_task)

        binding.inputTitle.doAfterTextChanged { validateTitle() }
        binding.inputDescription.doAfterTextChanged { }

        binding.btnSave.setOnClickListener {
            if (!validateTitle()) {
                Snackbar.make(binding.editRoot, getString(R.string.error_title_required), Snackbar.LENGTH_SHORT).show()
                binding.inputTitle.requestFocus()
                return@setOnClickListener
            }
            val title = binding.inputTitle.text?.toString().orEmpty()
            val desc = binding.inputDescription.text?.toString().orEmpty()
            val due = null // Date picker can be added in future
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (loadedTask == null) {
                        viewModel.addTask(title, desc, due)
                    } else {
                        val t = loadedTask!!.copy(title = title, description = desc)
                        viewModel.updateTask(t)
                    }
                    launch(Dispatchers.Main) {
                        Toast.makeText(requireContext(), getString(R.string.saved_success), Toast.LENGTH_SHORT).show()
                        listener?.onDoneEditing()
                    }
                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        Snackbar.make(binding.editRoot, getString(R.string.saved_failed), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            val t = loadedTask ?: return@setOnClickListener
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.delete) { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            viewModel.deleteTask(t)
                            launch(Dispatchers.Main) {
                                Toast.makeText(requireContext(), getString(R.string.deleted_success), Toast.LENGTH_SHORT).show()
                                listener?.onDoneEditing()
                            }
                        } catch (e: Exception) {
                            launch(Dispatchers.Main) {
                                Snackbar.make(binding.editRoot, getString(R.string.deleted_failed), Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }

        if (taskId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val task = viewModel.getTask(taskId!!)
                loadedTask = task
                CoroutineScope(Dispatchers.Main).launch {
                    if (task != null) {
                        binding.inputTitle.setText(task.title)
                        binding.inputDescription.setText(task.description)
                        binding.lblHeader.text = getString(R.string.edit_task)
                        binding.btnDelete.visibility = View.VISIBLE
                        binding.lblDueDate.text = getString(R.string.due_date_label, task.dueDate?.let {
                            DateFormat.getDateFormat(requireContext()).format(it)
                        } ?: getString(R.string.none))
                    }
                }
            }
        } else {
            binding.lblHeader.text = getString(R.string.add_task_header)
            binding.btnDelete.visibility = View.GONE
            binding.lblDueDate.text = getString(R.string.due_date_label, getString(R.string.none))
        }

        // Initial focus
        binding.editRoot.post { binding.inputTitle.requestFocus() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TASK_ID = "task_id"

        // PUBLIC_INTERFACE
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
