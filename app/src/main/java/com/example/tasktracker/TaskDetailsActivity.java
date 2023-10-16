package com.example.tasktracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class TaskDetailsActivity extends AppCompatActivity {

    TextView taskTitle, taskDesc, taskPriority, taskDate, taskStatus;
    Button btnBack;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        taskTitle = findViewById(R.id.tvTaskTitle);
        taskDesc = findViewById(R.id.tvTaskDescription);
        taskPriority = findViewById(R.id.tvTaskPriority);
        taskDate = findViewById(R.id.tvTaskDate);
        taskStatus = findViewById(R.id.tvTaskStatus);

        btnBack = findViewById(R.id.btnBack);

        Intent intent = getIntent();
        int taskID = intent.getIntExtra("taskID", -1);                  // Fetching ID of the task which has been clicked

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.MY_TASKS_DATA, 0);

        String jsonTaskList = sharedPreferences.getString("taskList", "");

        ArrayList<Task> tasks = gson.fromJson(jsonTaskList, new TypeToken<ArrayList<Task>>(){}.getType());

        Task task = tasks.get(taskID);                                   // Fetching that task from taskList whose details have to be shown

        String titleOfTask = task.title;
        String descOfTask = task.description;
        String priorityOfTask = task.priority;
        String dateOfTask = task.dueDate;
        String statusOfTask = task.completionStatus;

        taskTitle.setText(titleOfTask);
        taskDesc.setText(descOfTask);
        taskPriority.setText(priorityOfTask);
        taskDate.setText(dateOfTask);
        taskStatus.setText(statusOfTask);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(TaskDetailsActivity.this, MainActivity.class);
                startActivity(intent1);
                finish();
            }
        });
    }
}