package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.databinding.ActivityTaskDetailBinding
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var detailTaskViewModel: DetailTaskViewModel
    private lateinit var binding: ActivityTaskDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO 11 : Show detail task and implement delete action

        val factory = ViewModelFactory.getInstance(this)
        detailTaskViewModel = ViewModelProvider(this, factory)[DetailTaskViewModel::class.java]

        val extras: Bundle = intent.extras!!
        val id = extras.getInt(TASK_ID)

        detailTaskViewModel.setTaskId(id)
        detailTaskViewModel.task.observe(this) { task ->
            task?.let {
                binding.detailEdDescription.setText(task.description)
                binding.detailEdTitle.setText(task.title)
                binding.detailEdDueDate.setText(DateConverter.convertMillisToString(task.dueDateMillis))
            }
        }

        binding.btnDeleteTask.setOnClickListener {
            detailTaskViewModel.deleteTask()
            finish()
        }
    }
}