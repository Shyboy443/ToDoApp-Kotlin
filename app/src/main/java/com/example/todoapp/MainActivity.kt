package com.example.todoapp

import RecyclerViewTouchHelper
import android.annotation.SuppressLint
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var myDB: DatabaseHelper
    private lateinit var mList: MutableList<ToDoModel>
    private lateinit var adapter: ToDoAdapter
    private lateinit var unfinishedTaskCountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyletodo)
        fab = findViewById(R.id.fab)
        unfinishedTaskCountTextView = findViewById(R.id.unfinishedTaskCountTextView)
        myDB = DatabaseHelper(this)

        mList = mutableListOf()
        adapter = ToDoAdapter(myDB, this)
        adapter.listener = this

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        updateUnfinishedTaskCount()
        loadTasks()

        fab.setOnClickListener {
            AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG)
        }

        val itemTouchHelper = ItemTouchHelper(RecyclerViewTouchHelper(adapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onDialogClose(dialogInterface: DialogInterface) {
        mList.clear()
        mList.addAll(myDB.getAllTasks().reversed())
        adapter.setTasks(mList)
        updateUnfinishedTaskCount()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadTasks() {
        mList.clear()
        mList.addAll(myDB.getAllTasks())
        adapter.notifyDataSetChanged()
        adapter.setTasks(mList)
    }

    override fun onCheckedChanged(item: ToDoModel, isChecked: Boolean) {
        updateUnfinishedTaskCount()
    }

     fun updateUnfinishedTaskCount() {
        val unfinishedTaskCount = myDB.getUnfinishedTaskCount()
        unfinishedTaskCountTextView.text = "Unfinished Tasks: $unfinishedTaskCount"
    }
}
