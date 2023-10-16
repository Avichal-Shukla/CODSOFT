package com.example.tasktracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class NewTaskActivity extends AppCompatActivity {

    TextInputLayout taskTitle, taskDescription, taskPriority, taskDueDate;
    Button btnAddTask;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        taskTitle = findViewById(R.id.tilTitle);
        taskDescription = findViewById(R.id.tilDescription);
        taskPriority = findViewById(R.id.tilPriority);
        taskDueDate = findViewById(R.id.tilDate);

        btnAddTask = findViewById(R.id.btnAddTask);

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateTitle() && validateDescription() && validatePriority() && validateDate())
                {
                    // Fetching the details
                    String title = Objects.requireNonNull(taskTitle.getEditText()).getText().toString().trim();
                    String description = Objects.requireNonNull(taskDescription.getEditText()).getText().toString().trim();
                    String priority = Objects.requireNonNull(taskPriority.getEditText()).getText().toString().trim();
                    String dueDate = Objects.requireNonNull(taskDueDate.getEditText()).getText().toString().trim();


                    // Retrieve the existing task list from SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.MY_TASKS_DATA, 0);
                    String jsonTaskList = sharedPreferences.getString("taskList", "");

                    // Converting the task list back into its ArrayList form if some tasks are already added else creating a new list
                    ArrayList<Task> tasks;
                    if (!jsonTaskList.isEmpty())
                        tasks = gson.fromJson(jsonTaskList, new TypeToken<ArrayList<Task>>(){}.getType());
                    else
                        tasks = new ArrayList<>();


                    Task task = new Task(title, description, priority, dueDate, "Active");                 // Creating a task object using the task details acquired

                    tasks.add(task);              // Adding Task object in ArrayList

                    if(tasks.size() > 1)                                    // Means if there are at least two tasks in the task List
                    {
                        // Sorting the tasks based on priority
                        Collections.sort(tasks, new Comparator<Task>() {
                            @Override
                            public int compare(Task task1, Task task2) {
                                if (task1.priority.equalsIgnoreCase("High") && task2.priority.equalsIgnoreCase("Low"))
                                    return -1;                                    // It means task1 has "High" priority and it must come first
                                else if (task1.priority.equalsIgnoreCase("Low") && task2.priority.equalsIgnoreCase("High"))
                                    return 1;                                                   // It means task1 has "Low" priority and it must come last
                                else
                                    return 0;                                                         // It means both tasks have same priority
                            }
                        });
                    }

                    String jsonFormOfTaskList = gson.toJson(tasks);              // Converting List object into String for storing it in SharedPreference

                    // Saving the task details
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("taskList", jsonFormOfTaskList);
                    editor.putBoolean("isTaskAdded", true);
                    editor.apply();

                    Toast.makeText(NewTaskActivity.this, "Tasks are arranged according to their priority", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(NewTaskActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    private Boolean validateTitle()
    {
        String value = Objects.requireNonNull(taskTitle.getEditText()).getText().toString();
        if(value.isEmpty())
        {
            taskTitle.setError("Title cannot be empty");
            taskTitle.requestFocus();
            return false;
        }
        else
        {
            taskTitle.setError(null);
            taskTitle.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateDescription()
    {
        String value = Objects.requireNonNull(taskDescription.getEditText()).getText().toString();
        if(value.isEmpty())
        {
            taskDescription.setError("Description cannot be empty");
            taskDescription.requestFocus();
            return false;
        }
        else
        {
            taskDescription.setError(null);
            taskDescription.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validatePriority()
    {
        String value = Objects.requireNonNull(taskPriority.getEditText()).getText().toString();
        if(value.isEmpty())
        {
            taskPriority.setError("Priority cannot be empty");
            taskPriority.requestFocus();
            return false;
        }
        else if (!(value.equalsIgnoreCase("High") || value.equalsIgnoreCase("Low")))
        {
            taskPriority.setError("Priority can only be either High or Low");
            taskPriority.requestFocus();
            return false;
        }
        else
        {
            taskPriority.setError(null);
            taskPriority.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateDate()
    {
        String value = Objects.requireNonNull(taskDueDate.getEditText()).getText().toString();

        // Defining the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);               // This makes date parsing strict, so it doesn't accept invalid dates

        if(value.isEmpty())
        {
            taskDueDate.setError("Due date cannot be empty");
            taskDueDate.requestFocus();
            return false;
        }
        else
        {
            try
            {
                // Parsing the entered date using the specified format
                Date enteredDate = dateFormat.parse(value);

                // Ensuring the parsed date is not null and matches the expected format
                if (enteredDate != null) {
                    taskDueDate.setError(null);
                    taskDueDate.setErrorEnabled(false);
                    return true;
                }
                else {
                    taskDueDate.setError("Invalid date format");
                    taskDueDate.requestFocus();
                    return false;
                }
            }
            catch (ParseException e) {
                // If parsing fails, handling the exception
                taskDueDate.setError("Invalid date format");
                taskDueDate.requestFocus();
                return false;
            }
        }
    }
}