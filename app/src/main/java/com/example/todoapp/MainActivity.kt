package com.example.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.utils.DatabaseHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var mrecylerview: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var myDB: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mrecylerview = findViewById(R.id.recyletodo)
        fab = findViewById(R.id.fab)
        myDB = DatabaseHelper(this)

        fab.setOnClickListener {

        }
    }
}
