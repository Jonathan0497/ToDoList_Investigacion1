package udb.edu.sv

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val _tasks = MutableLiveData<MutableList<Task>>().apply { value = mutableListOf() }
    val tasks: LiveData<MutableList<Task>> = _tasks
    private val sharedPreferences = application.getSharedPreferences("tasks", Context.MODE_PRIVATE)

    init {
        loadTasks()
    }

    fun addTask(task: Task) {
        val updatedTasks = _tasks.value ?: mutableListOf()
        updatedTasks.add(task)
        _tasks.value = updatedTasks
        saveTasks(updatedTasks)
    }

    fun updateTask(task: Task) {
        val updatedTasks = _tasks.value?.map {
            if (it.id == task.id) task else it
        }?.toMutableList()
        _tasks.value = updatedTasks
        saveTasks(updatedTasks)
    }

    fun deleteTask(task: Task) {
        val updatedTasks = _tasks.value?.filter { it.id != task.id }?.toMutableList()
        _tasks.value = updatedTasks
        saveTasks(updatedTasks)
    }

    private fun saveTasks(tasks: List<Task>?) {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(tasks)
        editor.putString("tasks", json)
        editor.apply()
    }

    private fun loadTasks() {
        val json = sharedPreferences.getString("tasks", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            val tasks: MutableList<Task> = Gson().fromJson(json, type)
            _tasks.value = tasks
        }
    }
}
