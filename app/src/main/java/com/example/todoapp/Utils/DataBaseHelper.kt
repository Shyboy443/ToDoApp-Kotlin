package com.example.todoapp.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.todoapp.model.ToDoModel

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private lateinit var db: SQLiteDatabase
        private const val DATABASE_NAME = "TODO_DATABASE"
        private const val TABLE_NAME = "TODO"
        private const val COL_1 = "ID"
        private const val COL_2 = "TASK"
        private const val COL_3 = "STATUS"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME ($COL_1 INTEGER PRIMARY KEY AUTOINCREMENT, $COL_2 TEXT, $COL_3 INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertTask(model: ToDoModel) {
        val values = ContentValues()
        values.put(COL_2, model.task)
        values.put(COL_3, model.status)
        db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        Log.d("DatabaseHelper", "Inserted new task with ID: $COL_1, Task: ${model.task}, Status: ${model.status}")
    }

    fun updateTask(id: Int, task: String) {
        db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_2, task)
        db.update(TABLE_NAME, values, "$COL_1 = ?", arrayOf(id.toString()))
    }

    fun updateStatus(id: Int, status: String) {
        val db = this.writableDatabase // Use a local database object
        val values = ContentValues()
        values.put(COL_3, status) // Directly use integer for status
        db.update(TABLE_NAME, values, "$COL_1 = ?", arrayOf(id.toString()))
        db.close() // Close database connection to avoid leaks
    }


    fun deleteTask(id: Int) {
        db = this.writableDatabase
        db.delete(TABLE_NAME, "ID = ?", arrayOf(id.toString()))
    }

    fun getAllTasks(): List<ToDoModel> {
        db = this.readableDatabase
        val modelList = mutableListOf<ToDoModel>()
        var cursor: Cursor? = null

        try {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val task = ToDoModel()
                    val idIndex = cursor.getColumnIndex(COL_1)
                    if (idIndex != -1) {
                        task.id = cursor.getInt(idIndex)
                    }
                    val taskIndex = cursor.getColumnIndex(COL_2)
                    if (taskIndex != -1) {
                        task.task = cursor.getString(taskIndex)
                    }
                    val statusIndex = cursor.getColumnIndex(COL_3)
                    if (statusIndex != -1) {
                        task.status = cursor.getInt(statusIndex).toString()
                    }
                    modelList.add(task)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }
        return modelList
    }

    fun getUnfinishedTaskCount(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME WHERE $COL_3 = 0", null)
        var count = 0
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
            cursor.close()
        }
        return count
    }


}
