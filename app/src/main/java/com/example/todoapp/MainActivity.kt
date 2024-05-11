package com.example.todoapp

import RecyclerViewTouchHelper
import android.annotation.SuppressLint
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.Adapter.ToDoAdapter
import com.example.todoapp.model.ToDoModel
import com.example.todoapp.utils.DatabaseHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), OnDialogCloseListener {
    private lateinit var mrecylerview: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var myDB: DatabaseHelper
    private lateinit var mList: MutableList<ToDoModel>
    private lateinit var adapter: ToDoAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mrecylerview = findViewById(R.id.recyletodo)
        fab = findViewById(R.id.fab)
        myDB = DatabaseHelper(this)
        mList = mutableListOf()
        adapter = ToDoAdapter(myDB,this)
        mrecylerview.adapter = adapter
        mrecylerview.layoutManager = LinearLayoutManager(this)
        loadTasks()
        mList.addAll(myDB.getAllTasks().reversed()) // Load tasks into the list
        adapter.notifyDataSetChanged()

        fab.setOnClickListener {
            AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG)
        }
        val itemTouchHelper = ItemTouchHelper(RecyclerViewTouchHelper(adapter))
        itemTouchHelper.attachToRecyclerView(mrecylerview)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDialogClose(dialogInterface: DialogInterface) {
        mList.clear()
        val tasks = myDB.getAllTasks().reversed()
        Log.d("MainActivity", "Fetched tasks: ${tasks.size}")
        mList.addAll(tasks)
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadTasks() {
        mList.clear()
        mList.addAll(myDB.getAllTasks())
        adapter.notifyDataSetChanged()
        adapter.setTasks(mList)
    }
}
