# TV To-Do Android App

## Overview
TV To-Do is an Android TV application that lets users create, view, edit, and manage tasks using a remote control. It uses a local SQLite database via Room, follows a simple Repository + ViewModel architecture, and applies an Ocean Professional dark theme with strong focus states and D‑pad friendly components. The app launches directly into a grid list of tasks with a left sidebar for quick filters.

The application is fully self-contained and does not require any external services or network connectivity.

## Features
- Local persistence with Room
  - Tasks table fields: id, title, description, dueDate (nullable), isCompleted, createdAt, updatedAt
  - Auto-seeding a small sample list on first run
- Complete CRUD
  - Add a new task from the Task List screen
  - Edit existing task fields (title, description)
  - Delete a task with confirmation
  - Toggle completion state directly from task cards
- Filters and layout for TV
  - Sidebar filter buttons for All, Open, Done
  - TV-friendly grid layout with configurable span count (values/integers.xml)
  - Empty state messaging when filters yield no results
- D‑pad and remote navigation
  - All actionable controls are focusable and provide clear focus outlines and elevation/scale on focus
  - Logical focus order within each task card to avoid traps
  - Initial focus set on primary actions where appropriate
- Theming and visual design
  - Ocean Professional dark theme: primary #F97316, secondary/success #10B981, error #EF4444, background #000000, surface #1F2937, text #FFFFFF
  - Rounded corners, bold button style, high-contrast text and surfaces
- Accessibility
  - Content descriptions on key controls and status badges
  - Validation messaging for required fields
  - Snackbar/Toast feedback for actions

## Getting Started
### Prerequisites
- Java 17 (JDK)
- Android SDK and build tools installed
- Android TV emulator or physical Android TV device

### Build
From the container workspace root, navigate to the Android project folder:
- cd tv-to-do-list-manager-91575/android_tv_todo_frontend
- Build debug APK: ./gradlew assembleDebug

This will produce a debug build under app/build/outputs/apk/debug/.

### Run
- Install and run on an Android TV emulator or device via Android Studio or adb.
- The app is TV-launcher enabled; it includes LEANBACK and LAUNCHER categories and will appear in TV app lists.

### Preview
- On launch, you will see the Task List grid in the main area and a sidebar of filters on the left.
- Initial sample tasks are seeded on first run for quick testing.

## Usage
### Remote/D‑pad controls mapping
- D‑pad Up/Down/Left/Right: Move focus between sidebar buttons, Add button, and task cards.
- D‑pad Center/OK: Activate the focused control:
  - On a task card: opens the Edit screen.
  - On “Toggle” in a card: switches task between Open and Done.
  - On “Add Task”: opens the Add/Edit screen in Add mode.
  - On filter buttons: applies the selected filter.
- Back: Returns to the previous screen; closes Edit and returns to the Task List.

### Adding a task
1. From Task List, focus “Add Task” and press OK.
2. Enter a title (required) and optional description.
3. Select Save to persist the task and return to the list.

### Editing a task
1. Focus a task card and press OK.
2. Modify the title and/or description.
3. Save to persist changes. Delete is available for existing tasks.

### Deleting a task
- From the Edit screen, select Delete, confirm the dialog, and you will return to the list.

### Filtering tasks
- In the sidebar, choose All, Open, or Done. The grid will update immediately, and an empty state is shown if no tasks match.

## Architecture
### Layers and components
- Data (Room)
  - Entity: com.tv.todo.data.entity.Task
  - DAO: com.tv.todo.data.dao.TaskDao
  - Database: com.tv.todo.data.db.AppDatabase
- Repository
  - com.tv.todo.data.repo.TaskRepository provides a clean API for CRUD and toggling completion.
- ViewModel
  - com.tv.todo.viewmodel.TaskViewModel exposes LiveData<List<Task>> and async operations using viewModelScope.
- UI
  - Activity: com.tv.todo.MainActivity hosts fragments and handles navigation.
  - Fragments: TaskListFragment (grid + filters) and EditTaskFragment (add/edit).
  - Recycler: TaskCardAdapter renders task cards and supports click/toggle actions.
  - Theme utils: com.tv.todo.ui.theme.ThemeUtils centralizes focus outline and styling helpers.

### Navigation
- MainActivity starts TaskListFragment.
- TaskListFragment triggers navigation to EditTaskFragment for add/edit.
- Back returns to TaskListFragment.

### Packages
- com.tv.todo (App, MainActivity)
- com.tv.todo.data (entity, dao, db, repo)
- com.tv.todo.viewmodel
- com.tv.todo.ui (theme, components, screens)
- com.tv.todo.di (ServiceLocator)

## Theming
The app implements the Ocean Professional theme across styles and runtime helpers:
- Colors
  - Primary: #F97316
  - Secondary/Success: #10B981
  - Error: #EF4444
  - Background: #000000
  - Surface: #1F2937
  - Text: #FFFFFF
- Theme implementation
  - XML theme: res/values/themes.xml (Theme.TvTodo and Theme_TvTodo_NoActionBar) with bold Material buttons (Widget.TvTodo.Button).
  - Runtime: ThemeUtils.applyFocusOutline adds focus states (stroke, elevation, scale) for TV focus clarity.
- Focus states
  - Focused elements gain a primary-colored outline and slight elevation/scale to make selection highly visible on TV screens.

## Accessibility considerations
- Content descriptions are provided for interactive elements such as Add, Save, Delete, Toggle, and filter buttons.
- Task cards set descriptive content descriptions including current status (Open/Done).
- Required fields (title) are validated with clear error messages and Snackbar feedback.
- Focus order within each card ensures that users can navigate to action buttons without traps.

## Known limitations and future improvements
- Due date selection is not implemented; the field is stored but no date picker UI is provided in the Edit screen.
- There is no search or sort customization beyond the provided filters and default Room query ordering.
- Animations and transitions are minimal; could be enhanced for better TV polish.
- Multi-select and bulk actions are not implemented.
- Backup excludes the database file in the provided backup rules; cloud sync is not included.

## Build configuration notes
- Minimum SDK: 30, Target/Compile SDK: 35.
- Tooling: Kotlin Android, Room with KSP, Leanback library for TV focus patterns, Material Components, RecyclerView, CardView, ConstraintLayout.
- Database filename: tv_todo.db (Room, destructive migration fallback enabled for this initial version).

## License
This project is provided as-is for demonstration purposes within the workspace. No external services are required; all data is stored locally on device.
