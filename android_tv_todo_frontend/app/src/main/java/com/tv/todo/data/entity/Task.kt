package com.tv.todo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * PUBLIC_INTERFACE
 * Task entity for Room representing a to-do task.
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String,
    val dueDate: Long?, // epoch millis
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
