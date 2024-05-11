package com.example.todoapp

import RecyclerViewTouchHelper
import android.annotation.SuppressLint
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.Adapter.ToDoAdapter
import com.example.todoapp.model.ToDoModel
import com.example.todoapp.utils.DatabaseHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), OnDialogCloseListener,
    ToDoAdapter.OnCheckedChangeListener {
    private lateinit var mrecylerview: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var myDB: DatabaseHelper
    private lateinit var mList: MutableList<ToDoModel>
    private lateinit var adapter: ToDoAdapter

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views and database helper
        mrecylerview = findViewById(R.id.recyletodo)
        fab = findViewById(R.id.fab)
        myDB = DatabaseHelper(this)

        // Initialize list and adapter
        mList = mutableListOf()
        adapter = ToDoAdapter(myDB,this)
        adapter.listener = this
        // Set up RecyclerView
        mrecylerview.layoutManager = LinearLayoutManager(this)
        mrecylerview.adapter = adapter

        // Update unfinished task count and display it
        updateUnfinishedTaskCount()

        // Load tasks from database
        loadTasks()

        // Set up FloatingActionButton
        fab.setOnClickListener {
            AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG)
        }

        // Attach ItemTouchHelper for swipe-to-delete functionality
        val itemTouchHelper = ItemTouchHelper(RecyclerViewTouchHelper(adapter))
        itemTouchHelper.attachToRecyclerView(mrecylerview)
    }

    override fun onDialogClose(dialogInterface: DialogInterface) {
        mList.clear()
        mList.addAll(myDB.getAllTasks().reversed())
        adapter.setTasks(mList)
        updateUnfinishedTaskCount() // Update count after adding a new task
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadTasks() {
        // Load tasks from database and update adapter
        mList.clear()
        mList.addAll(myDB.getAllTasks())
        adapter.notifyDataSetChanged()
        adapter.setTasks(mList)
    }

    override fun onCheckedChanged(item: ToDoModel, isChecked: Boolean) {
        updateUnfinishedTaskCount()
    }

    private fun updateUnfinishedTaskCount() {
        val unfinishedTaskCount = myDB.getUnfinishedTaskCount()
        val unfinishedTaskCountTextView: TextView = findViewById(R.id.unfinishedTaskCountTextView)
        unfinishedTaskCountTextView.text = "Unfinished Tasks: $unfinishedTaskCount"
    }


}
