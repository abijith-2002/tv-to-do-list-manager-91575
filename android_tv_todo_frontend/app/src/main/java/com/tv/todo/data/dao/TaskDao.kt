package com.tv.todo.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tv.todo.data.entity.Task

/**
 * PUBLIC_INTERFACE
 * TaskDao defines CRUD operations for tasks.
 */
@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate IS NULL, dueDate ASC, updatedAt DESC")
    fun observeAll(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task): Int

    @Delete
    suspend fun delete(task: Task): Int

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()

    // PUBLIC_INTERFACE
    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun count(): Int
}
