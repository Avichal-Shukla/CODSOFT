package com.example.tasktracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.google.gson.Gson;
import java.util.List;

public class TaskAdaptor extends ArrayAdapter<Task> {

    Context context;
    List<Task> tasks;
    int resource;
    LinearLayout listItem;
    Gson gson = new Gson();

    public TaskAdaptor(@NonNull Context context, int resource, @NonNull List<Task> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        this.tasks = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Task task = tasks.get(position);                                  // Retrieving the Task object at the specified position within the tasks list

        View view = LayoutInflater.from(context).inflate(resource, null);                    // To show our layout

        // Now bringing views from our custom layout using 'view'
        TextView titleOfTask = view.findViewById(R.id.tvTaskItemTitle);
        TextView statusOfTask = view.findViewById(R.id.tvTaskItemStatus);

        titleOfTask.setText(task.title);
        statusOfTask.setText(task.completionStatus);

        // Making this attribute as 'final' so it can be accessed inside the onClick method
        final String title = task.title;

        listItem = view.findViewById(R.id.llAdaptorLayout);

        // This will execute when user simply clicks on task item once
        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TaskDetailsActivity.class);
                intent.putExtra("taskID", position);                        // Sending ID of the task for which details have to be shown
                context.startActivity(intent);
            }
        });

        // This will execute when user presses task item for a long time
        listItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                // Vibrating the device when long pressed on the task item
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE));
                }
                else {
                    vibrator.vibrate(60);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Task: "+title);

                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, EditTaskActivity.class);
                        intent.putExtra("taskID", position);                        // Sending ID of the task which is to be edited
                        context.startActivity(intent);
                    }
                });

                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        tasks.remove(task);
                        String updatedTaskList = gson.toJson(tasks);

                        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.MY_TASKS_DATA, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("taskList", updatedTaskList);
                        editor.apply();

                        notifyDataSetChanged();
                    }
                });

                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) { dialogInterface.dismiss();}
                });

                // Showing the dialog box only after setting up functionality for all the buttons
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;                                       // Returning true to indicate that the long click event has happened
            }
        });

        return view;
    }
}