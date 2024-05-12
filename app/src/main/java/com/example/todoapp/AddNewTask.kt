package com.example.todoapp

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.example.todoapp.model.ToDoModel
import com.example.todoapp.utils.DatabaseHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

@Suppress("DEPRECATION")
class AddNewTask : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "AddNewTask"
        fun newInstance(): AddNewTask = AddNewTask()
    }

    private lateinit var mEditText: EditText
    private lateinit var mEditTextDescription: EditText
    private lateinit var mSpinnerPriority: Spinner
    private lateinit var mSaveButton: Button
    private lateinit var myDb: DatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_newtask, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        mEditText = view.findViewById(R.id.edittext)
        mEditTextDescription = view.findViewById(R.id.edittext_description)
        mSpinnerPriority = view.findViewById(R.id.spinner_priority)
        mSaveButton = view.findViewById(R.id.button_save)
        myDb = DatabaseHelper(requireActivity())

        // Setup spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.priority_levels,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mSpinnerPriority.adapter = adapter
        }

        var isUpdate = false
        var taskId = 0

        // Initialize fields if it's an update
        arguments?.let { bundle ->
            isUpdate = true
            taskId = bundle.getInt("id")
            mEditText.setText(bundle.getString("task"))
            mEditTextDescription.setText(bundle.getString("description"))
            val priorityIndex = resources.getStringArray(R.array.priority_levels).indexOf(bundle.getString("priority"))
            mSpinnerPriority.setSelection(priorityIndex)
            updateSaveButtonState()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        mEditText.addTextChangedListener(textWatcher)
        mEditTextDescription.addTextChangedListener(textWatcher)

        mSaveButton.setOnClickListener {
            val text = mEditText.text.toString()
            val description = mEditTextDescription.text.toString()
            val priority = when (mSpinnerPriority.selectedItem.toString()) {
                "High" -> 3
                "Medium" -> 2
                "Low" -> 1
                else -> 0
            }

            if (isUpdate) {
                myDb.updateTask(taskId, text, description, priority)
            } else {
                val item = ToDoModel().apply {
                    task = text
                    status = "0"  // Assuming status is a String. Change as necessary.
                    this.description = description
                    this.priority = priority
                }
                myDb.insertTask(item)
            }
            dismiss()
        }
    }

    private fun updateSaveButtonState() {
        mSaveButton.isEnabled = mEditText.text.isNotBlank() && mEditTextDescription.text.isNotBlank()
        mSaveButton.setBackgroundColor(if (mSaveButton.isEnabled) resources.getColor(R.color.Primary) else Color.GRAY)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (activity as? OnDialogCloseListener)?.onDialogClose(dialog)
    }
}
