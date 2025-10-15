package com.tv.todo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tv.todo.data.entity.Task
import com.tv.todo.data.repo.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * PUBLIC_INTERFACE
 * TaskViewModel exposes tasks and CRUD operations.
 */
class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val tasks: LiveData<List<Task>> = repository.observeTasks()

    // PUBLIC_INTERFACE
    suspend fun getTask(id: Long): Task? = withContext(Dispatchers.IO) {
        repository.getTask(id)
    }

    // PUBLIC_INTERFACE
    fun toggleCompleted(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleCompleted(task)
        }
    }

    // PUBLIC_INTERFACE
    fun addTask(title: String, description: String, dueDate: Long?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(title, description, dueDate)
        }
    }

    // PUBLIC_INTERFACE
    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task)
        }
    }

    // PUBLIC_INTERFACE
    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(task)
        }
    }
}
