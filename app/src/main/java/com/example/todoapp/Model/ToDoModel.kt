package com.example.todoapp.model

class ToDoModel {
    var task: String? = null
    var id: Int = 0
    var status: String? = null

    // Getter and setter for task
    fun getTask(): String? {
        return task
    }

    fun setTask(task: String) {
        this.task = task
    }

    // Getter and setter for id
    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    // Getter and setter for status
    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }
}
