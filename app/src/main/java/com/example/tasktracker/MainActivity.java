package com.example.tasktracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String MY_TASKS_DATA = "MyTasksData";
    TextView displayName;
    Button btnNewTask;
    ListView lvTasks;
    Gson gson = new Gson();                  // GSON helps to convert our Object to String format (i.e, JSON format) & vice-versa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvTasks = findViewById(R.id.lvTasks);

        displayName = findViewById(R.id.tvDisplayName);

        SharedPreferences sp = getSharedPreferences(SplashScreenActivity.MY_APP_DATA, 0);
        String userName = sp.getString("Username", "user");
        displayName.setText(userName);

        btnNewTask = findViewById(R.id.btnNewTask);
        btnNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getSharedPreferences(MY_TASKS_DATA, 0);
        if(sharedPreferences.getBoolean("isTaskAdded", false))                                     // If a task has been added
        {
            String jsonTaskList = sharedPreferences.getString("taskList", "");           // Retrieving the stored task list in JSON format

            // Converting the task list back into its ArrayList form
            ArrayList<Task> tasks = gson.fromJson(jsonTaskList, new TypeToken<ArrayList<Task>>(){}.getType());

            if(tasks == null)
                tasks = new ArrayList<>();

            TaskAdaptor adaptor = new TaskAdaptor(MainActivity.this, R.layout.task_item_layout, tasks);

            lvTasks.setAdapter(adaptor);
        }
    }
}