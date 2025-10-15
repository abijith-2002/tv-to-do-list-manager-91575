package com.tv.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tv.todo.databinding.ActivityMainBinding
import com.tv.todo.ui.screens.EditTaskFragment
import com.tv.todo.ui.screens.TaskListFragment

/**
 * PUBLIC_INTERFACE
 * MainActivity for Android TV To-Do app.
 * - Hosts TaskListFragment and EditTaskFragment
 * - Handles navigation events from fragments
 */
class MainActivity : AppCompatActivity(), TaskListFragment.Listener, EditTaskFragment.Listener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_TvTodo_NoActionBar)
        super.onCreate(savedInstanceState)
        // Fallback to direct setContentView to avoid any inflate overload ambiguity
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.bind(findViewById(android.R.id.content))

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, TaskListFragment.newInstance())
                .commitNow()
        }
    }

    // PUBLIC_INTERFACE
    override fun onAddNewTask() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, EditTaskFragment.newInstance(null))
            .addToBackStack("edit")
            .commit()
    }

    // PUBLIC_INTERFACE
    override fun onEditTask(taskId: Long) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, EditTaskFragment.newInstance(taskId))
            .addToBackStack("edit")
            .commit()
    }

    // PUBLIC_INTERFACE
    override fun onDoneEditing() {
        supportFragmentManager.popBackStack()
    }
}
