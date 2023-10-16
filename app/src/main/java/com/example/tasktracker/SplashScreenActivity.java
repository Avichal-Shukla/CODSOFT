package com.example.tasktracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

public class SplashScreenActivity extends AppCompatActivity {

    public static final String MY_APP_DATA = "MyAppData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        // This thread will help to hold up the splash screen for few seconds
        Thread thread = new Thread()
        {
            public void run()
            {
                try {
                    sleep(2000);
                }
                catch (Exception e) {
                    e.printStackTrace();                 // parent of all exceptions
                }
                finally {

                    SharedPreferences sharedPreferences = getSharedPreferences(MY_APP_DATA, 0);
                    boolean hasEnteredName = sharedPreferences.getBoolean("hasEnteredName", false);

                    if(hasEnteredName)
                    {
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Intent intent = new Intent(SplashScreenActivity.this, OpeningActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };
        thread.start();
    }
}