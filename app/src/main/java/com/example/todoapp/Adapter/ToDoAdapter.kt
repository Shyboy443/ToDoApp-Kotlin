package com.example.todoapp.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.AddNewTask
import com.example.todoapp.MainActivity
import com.example.todoapp.R
import com.example.todoapp.model.ToDoModel
import com.example.todoapp.utils.DatabaseHelper


class ToDoAdapter(private val myDB: DatabaseHelper, private val activity: MainActivity) :
    RecyclerView.Adapter<ToDoAdapter.MyViewHolder>() {

    var listener: OnCheckedChangeListener? = null

    interface OnCheckedChangeListener {
        fun onCheckedChanged(item: ToDoModel, isChecked: Boolean)
    }

    private var mList: List<ToDoModel> = listOf()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mCheckBox: CheckBox = itemView.findViewById(R.id.matchbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.task_layout, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = mList[position]
        holder.mCheckBox.text = item.task
        holder.mCheckBox.isChecked = item.status == "1" // Check if status is "1"

        // Set checkbox listener to update database and notify dataset change
        holder.mCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val newStatus = if (isChecked) "1" else "0"
            myDB.updateStatus(item.id, newStatus)
            listener?.onCheckedChanged(item, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTasks(list: List<ToDoModel>) {
        mList = list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteTask(position: Int) {
        val item = mList[position]
        myDB.deleteTask(item.id)
        mList = mList.filterIndexed { index, _ -> index != position } // Remove item from mList
        notifyDataSetChanged()
    }

    fun editItem(position: Int) {
        val item = mList[position]
        val bundle = Bundle().apply {
            putInt("id", item.id)
            putString("task", item.task)
        }
        val task = AddNewTask()
        task.arguments = bundle
        task.show(activity.supportFragmentManager, task.tag)
    }

    fun getContext(): Context {
        return activity
    }
}

