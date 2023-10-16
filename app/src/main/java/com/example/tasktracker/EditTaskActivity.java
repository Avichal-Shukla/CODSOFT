package com.example.tasktracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class EditTaskActivity extends AppCompatActivity {

    EditText taskTitle, taskDescription, taskPriority, taskDueDate;
    String status;
    Button btnSaveTask;
    RadioGroup radioGroup;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        taskTitle = findViewById(R.id.etTitle);
        taskDescription = findViewById(R.id.etDescription);
        taskPriority = findViewById(R.id.etPriority);
        taskDueDate = findViewById(R.id.etDate);

        btnSaveTask = findViewById(R.id.btnSaveTask);

        // Retrieving the already saved tasks list
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.MY_TASKS_DATA, 0);
        String jsonFormOfTaskList = sharedPreferences.getString("taskList", "");

        ArrayList<Task> tasks = gson.fromJson(jsonFormOfTaskList, new TypeToken<ArrayList<Task>>(){}.getType());

        Intent intent = getIntent();
        int taskID = intent.getIntExtra("taskID", -1);                  // Fetching ID of the task which is to be edited

        Task task = tasks.get(taskID);                                   // Fetching that task from taskList which has to be edited

        String titleToEdit = task.title;
        String descToEdit = task.description;
        String priorityToEdit = task.priority;
        String dateToEdit = task.dueDate;

        // First entering all the previously entered task details by default so that user can make changes
        taskTitle.setText(titleToEdit);
        taskDescription.setText(descToEdit);
        taskPriority.setText(priorityToEdit);
        taskDueDate.setText(dateToEdit);

        radioGroup = findViewById(R.id.rgForRBtn);

        status = task.completionStatus;                     // Fetching the current completion status of the task

        // Updating the checked status of the RadioGroup
        if (status.equals("Active"))
            radioGroup.check(R.id.rbActive);
        else if (status.equals("Completed"))
            radioGroup.check(R.id.rbCompleted);

        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validateTitle() && validateDescription() && validatePriority() && validateDate())
                {
                    String editedTitle = taskTitle.getText().toString().trim();
                    String editedDesc = taskDescription.getText().toString().trim();
                    String editedPriority = taskPriority.getText().toString().trim();
                    String editedDueDate = taskDueDate.getText().toString().trim();

                    // Updating the status based on the checked RadioButton
                    int checkedRBtnId = radioGroup.getCheckedRadioButtonId();

                    if (checkedRBtnId == R.id.rbActive)
                        status = "Active";
                    else if (checkedRBtnId == R.id.rbCompleted)
                        status = "Completed";

                    Task editedTask = new Task(editedTitle, editedDesc, editedPriority, editedDueDate, status);

                    // Update the specific task in the task list
                    tasks.set(taskID, editedTask);

                    if(tasks.size() > 1)                            // Means if there are at least two tasks in the task List
                    {
                        // Sorting the tasks based on priority
                        Collections.sort(tasks, new Comparator<Task>() {
                            @Override
                            public int compare(Task task1, Task task2) {
                                if (task1.priority.equalsIgnoreCase("High") && task2.priority.equalsIgnoreCase("Low"))
                                    return -1;
                                else if (task1.priority.equalsIgnoreCase("Low") && task2.priority.equalsIgnoreCase("High"))
                                    return 1;
                                else
                                    return 0;
                            }
                        });
                    }

                    String updatedTaskListJSON = gson.toJson(tasks);

                    SharedPreferences sharedPreferences1 = getSharedPreferences(MainActivity.MY_TASKS_DATA, 0);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    editor.putString("taskList", updatedTaskListJSON);
                    editor.apply();

                    Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent1);
                    finish();
                }
            }
        });
    }

    private Boolean validateTitle()
    {
        String value = taskTitle.getText().toString().trim();
        if(value.isEmpty())
        {
            taskTitle.setError("Title cannot be empty");
            taskTitle.requestFocus();
            return false;
        }
        else
        {
            taskTitle.setError(null);
            return true;
        }
    }
    private Boolean validateDescription()
    {
        String value = taskDescription.getText().toString().trim();
        if(value.isEmpty())
        {
            taskDescription.setError("Description cannot be empty");
            taskDescription.requestFocus();
            return false;
        }
        else
        {
            taskDescription.setError(null);
            return true;
        }
    }
    private Boolean validatePriority()
    {
        String value = taskPriority.getText().toString().trim();
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
            return true;
        }
    }
    private Boolean validateDate()
    {
        String value = taskDueDate.getText().toString().trim();

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