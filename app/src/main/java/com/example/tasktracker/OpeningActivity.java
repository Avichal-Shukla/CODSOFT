package com.example.tasktracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class OpeningActivity extends AppCompatActivity {

    TextInputLayout nameofUser;
    Button btnSaveUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        nameofUser = findViewById(R.id.tilUserName);

        btnSaveUserName = findViewById(R.id.btnSaveUserName);

        btnSaveUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validateName())
                {
                    String userName = Objects.requireNonNull(nameofUser.getEditText()).getText().toString().trim();

                    SharedPreferences sharedPreferences = getSharedPreferences(SplashScreenActivity.MY_APP_DATA, 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Username", userName);
                    editor.putBoolean("hasEnteredName", true);
                    editor.apply();

                    Intent intent = new Intent(OpeningActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    private Boolean validateName()
    {
        String value = Objects.requireNonNull(nameofUser.getEditText()).getText().toString();
        if(value.isEmpty())
        {
            nameofUser.setError("Username cannot be empty");
            nameofUser.requestFocus();                                 // start pointing to this particular field
            return false;
        }
        else
        {
            nameofUser.setError(null);                         // remove the error if it is corrected
            nameofUser.setErrorEnabled(false);                       // to remove extra space used by error msg as we are using material design
            return true;
        }
    }
}