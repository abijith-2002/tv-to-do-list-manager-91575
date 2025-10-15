package com.tv.todo.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tv.todo.data.db.AppDatabase
import com.tv.todo.data.repo.TaskRepository
import com.tv.todo.viewmodel.TaskViewModel

/**
 * PUBLIC_INTERFACE
 * ServiceLocator manages lightweight dependency provisioning.
 */
object ServiceLocator {
    private lateinit var appContext: Context

    lateinit var repository: TaskRepository
        private set

    fun init(context: Context) {
        appContext = context.applicationContext
        val db = AppDatabase.getInstance(appContext)
        repository = TaskRepository(db.taskDao())
    }

    // PUBLIC_INTERFACE
    fun provideTaskViewModelFactory(): ViewModelProvider.Factory {
        val repo = repository
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
                    return TaskViewModel(repo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
