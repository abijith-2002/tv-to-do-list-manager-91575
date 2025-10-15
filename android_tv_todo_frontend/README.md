# TV To-Do Android App

Android TV To-Do list manager with local SQLite (Room), Ocean Professional dark theme, and D-pad friendly UI.

Features:
- Local Room database with Tasks table: id, title, description, dueDate, isCompleted, createdAt, updatedAt
- Repository + ViewModel architecture
- TV-friendly grid list, focus outlines, Add/Edit screens
- Ocean Professional theme (primary #F97316, secondary/success #10B981, error #EF4444, background #000000, surface #1F2937, text #FFFFFF)

Build:
- From `android_tv_todo_frontend` run `./gradlew assembleDebug`
- Launch on TV emulator or device.

```text
Packages:
- com.tv.todo (App, MainActivity)
- com.tv.todo.data (entity, dao, db, repo)
- com.tv.todo.viewmodel
- com.tv.todo.ui (theme, components, screens)
- com.tv.todo.di (ServiceLocator)
```

No external services; uses local SQLite only.
