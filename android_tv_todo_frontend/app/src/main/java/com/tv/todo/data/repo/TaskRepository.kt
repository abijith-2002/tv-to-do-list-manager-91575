package com.tv.todo.data.repo

import androidx.lifecycle.LiveData
import com.tv.todo.data.dao.TaskDao
import com.tv.todo.data.entity.Task

/**
 * PUBLIC_INTERFACE
 * TaskRepository provides a clean API for data operations.
 */
class TaskRepository(private val dao: TaskDao) {

    // PUBLIC_INTERFACE
    fun observeTasks(): LiveData<List<Task>> = dao.observeAll()

    // PUBLIC_INTERFACE
    suspend fun getTask(id: Long): Task? = dao.getById(id)

    // PUBLIC_INTERFACE
    suspend fun addTask(title: String, description: String, dueDate: Long?): Long {
        val now = System.currentTimeMillis()
        return dao.insert(
            Task(
                title = title.trim(),
                description = description.trim(),
                dueDate = dueDate,
                isCompleted = false,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    // PUBLIC_INTERFACE
    suspend fun updateTask(task: Task): Int {
        return dao.update(task.copy(updatedAt = System.currentTimeMillis()))
    }

    // PUBLIC_INTERFACE
    suspend fun deleteTask(task: Task): Int = dao.delete(task)

    // PUBLIC_INTERFACE
    suspend fun toggleCompleted(task: Task): Int {
        return dao.update(task.copy(isCompleted = !task.isCompleted, updatedAt = System.currentTimeMillis()))
    }

    // PUBLIC_INTERFACE
    suspend fun seedIfEmpty() {
        val current = dao.observeAll().value
        if (current == null || current.isEmpty()) {
            val now = System.currentTimeMillis()
            val examples = listOf(
                Task(title = "Plan week", description = "Outline goals for the week", dueDate = now + 3 * 24 * 3600_000L),
                Task(title = "Grocery shopping", description = "Buy fruits, veggies, and milk", dueDate = now + 24 * 3600_000L),
                Task(title = "Workout", description = "30 min cardio + strength", dueDate = null),
                Task(title = "Read book", description = "Read 20 pages of a novel", dueDate = now + 5 * 24 * 3600_000L),
            )
            examples.forEach { dao.insert(it) }
        }
    }
}
