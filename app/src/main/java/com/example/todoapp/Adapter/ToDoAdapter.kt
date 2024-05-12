package com.example.todoapp.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.AddNewTask
import com.example.todoapp.MainActivity
import com.example.todoapp.R
import com.example.todoapp.model.ToDoModel
import com.example.todoapp.utils.DatabaseHelper

@Suppress("DEPRECATION")
class ToDoAdapter(private val myDB: DatabaseHelper, private val activity: MainActivity) :
    RecyclerView.Adapter<ToDoAdapter.MyViewHolder>() {

    var listener: OnCheckedChangeListener? = null

    interface OnCheckedChangeListener {
        fun onCheckedChanged(item: ToDoModel, isChecked: Boolean)
    }

    private var mList: List<ToDoModel> = listOf()

    @Suppress("DEPRECATION")
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mCheckBox: CheckBox = itemView.findViewById(R.id.matchbox)
        private val mDescription: TextView = itemView.findViewById(R.id.text_description)
        val mPriority: TextView = itemView.findViewById(R.id.text_priority)
        init {
            itemView.setOnClickListener {

                mDescription.visibility = if (mDescription.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }

            // Handle checkbox changes with a separate listener
            mCheckBox.setOnCheckedChangeListener { _, isChecked ->
                val item = mList[adapterPosition]
                val newStatus = if (isChecked) "1" else "0"
                myDB.updateStatus(item.id, newStatus)
                listener?.onCheckedChanged(item, isChecked)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.task_layout, parent, false)
        return MyViewHolder(v)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = mList[position]
        holder.mCheckBox.text = item.task
        holder.mCheckBox.isChecked = item.status == "1"
        holder.mPriority.text = "Priority: ${item.priority}"

        // Prevent checkbox state from triggering during recycling
        holder.mCheckBox.setOnCheckedChangeListener(null)
        holder.mCheckBox.isChecked = item.status == "1"
        holder.mCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val newStatus = if (isChecked) "1" else "0"
            myDB.updateStatus(item.id, newStatus)
            listener?.onCheckedChanged(item, isChecked)
        }

        // Setting priority text and color based on priority value
        when (item.priority) {
            3 -> {
                holder.mPriority.text = "High"
                holder.mPriority.setTextColor(activity.resources.getColor(R.color.priority_high))
            }
            2 -> {
                holder.mPriority.text = "Medium"
                holder.mPriority.setTextColor(activity.resources.getColor(R.color.priority_medium))
            }
            1 -> {
                holder.mPriority.text = "Low"
                holder.mPriority.setTextColor(activity.resources.getColor(R.color.priority_low))
            }
            else -> {
                holder.mPriority.text = "Undefined"
                holder.mPriority.setTextColor(activity.resources.getColor(android.R.color.black)) // Default color
            }
        }

        // Set click listener to show popup window with description
        holder.itemView.setOnClickListener {
            item.description?.let { it1 -> showDescriptionPopup(it1, holder.itemView.context) }
        }
    }


    @SuppressLint("InflateParams")
    private fun showDescriptionPopup(description: String, context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_description, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val descriptionTextView: TextView = popupView.findViewById(R.id.popup_description_text)
        descriptionTextView.text = description

        val closeButton: Button = popupView.findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            popupWindow.dismiss() // Close the popup window when the button is clicked
        }

        // Set the popup window animation style (optional)
        popupWindow.animationStyle = R.style.PopupAnimation

        // Create an overlay view to cover the background content
        val overlayView = View(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundColor(Color.parseColor("#80000000")) // Semi-transparent black color
            isClickable = true // Intercept clicks
        }

        // Add the overlay view to the root view of the activity
        (activity.window.decorView as ViewGroup).addView(overlayView)

        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        // Dismiss the popup window and remove the overlay view when it's closed
        popupWindow.setOnDismissListener {
            (activity.window.decorView as ViewGroup).removeView(overlayView)
        }
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTasks(list: List<ToDoModel>) {
        mList = list.sortedByDescending { it.priority }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteTask(position: Int) {
        val item = mList[position]
        myDB.deleteTask(item.id)
        mList = mList.filterIndexed { index, _ -> index != position }
        notifyDataSetChanged()
        // Update unfinished task count
        activity.updateUnfinishedTaskCount()
    }

    fun editItem(position: Int) {
        val item = mList[position]
        val bundle = Bundle().apply {
            putInt("id", item.id)
            putString("task", item.task)
            putString("description", item.description)
            putInt("priority", item.priority)
        }
        val task = AddNewTask()
        task.arguments = bundle
        task.show(activity.supportFragmentManager, task.tag)
    }

    fun getContext(): Context {
        return activity
    }

}
