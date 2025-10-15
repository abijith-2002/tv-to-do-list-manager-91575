package com.tv.todo

import android.app.Application
import com.tv.todo.di.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Application entrypoint initializing the ServiceLocator and seeding sample data.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)

        // Seed sample data on first run
        CoroutineScope(Dispatchers.IO).launch {
            ServiceLocator.repository.seedIfEmpty()
        }
    }
}
