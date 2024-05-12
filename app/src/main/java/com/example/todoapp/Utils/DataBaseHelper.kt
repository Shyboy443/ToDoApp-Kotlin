package com.example.todoapp.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.todoapp.model.ToDoModel

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "TODO_DATABASE"
        private const val TABLE_NAME = "TODO"
        private const val COL_1 = "ID"
        private const val COL_2 = "TASK"
        private const val COL_3 = "STATUS"
        private const val COL_4 = "DESCRIPTION"
        private const val COL_5 = "PRIORITY"
        private const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
        CREATE TABLE $TABLE_NAME (
            $COL_1 INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_2 TEXT,
            $COL_3 TEXT,
            $COL_4 TEXT,
            $COL_5 INTEGER
        )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertTask(model: ToDoModel): List<ToDoModel> {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_2, model.task)
            put(COL_3, model.status)
            put(COL_4, model.description)
            put(COL_5, model.priority)
        }

        try {
            db.beginTransaction()  // Start transaction
            db.insert(TABLE_NAME, null, values)
            db.setTransactionSuccessful() // Mark transaction as successful
        } finally {
            db.endTransaction() // End transaction
            db.close() // Close database connection
        }

        return getAllTasks() // Fetch and return the sorted list of all tasks
    }


    fun updateTask(id: Int, task: String, description: String, priority: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_2, task)
            put(COL_4, description)
            put(COL_5, priority)
        }
        db.update(TABLE_NAME, values, "$COL_1 = ?", arrayOf(id.toString()))
        db.close()
    }

    fun updateStatus(id: Int, status: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_3, status)
        }
        db.update(TABLE_NAME, values, "$COL_1 = ?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteTask(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COL_1 = ?", arrayOf(id.toString()))
        db.close()
    }

    fun getAllTasks(): List<ToDoModel> {
        val db = this.readableDatabase
        val modelList = mutableListOf<ToDoModel>()
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "$COL_5 DESC")
        cursor.use {
            while (it.moveToNext()) {
                val task = ToDoModel().apply {
                    id = it.getInt(it.getColumnIndexOrThrow(COL_1))
                    task = it.getString(it.getColumnIndexOrThrow(COL_2))
                    status = it.getString(it.getColumnIndexOrThrow(COL_3))
                    description = it.getString(it.getColumnIndexOrThrow(COL_4))
                    priority = it.getInt(it.getColumnIndexOrThrow(COL_5))
                }
                modelList.add(task)
            }
        }
        db.close()
        return modelList
    }

    fun getUnfinishedTaskCount(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME WHERE $COL_3 = 0", null)
        var count = 0
        cursor.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }
        db.close()
        return count
    }
}
