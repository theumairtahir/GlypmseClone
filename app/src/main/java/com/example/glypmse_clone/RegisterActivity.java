package com.example.glypmse_clone;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

public class RegisterActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ImageButton btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_register);
        progressBar=findViewById(R.id.registerProgressBar);
        progressBar.setVisibility(View.INVISIBLE); //initially making the progress bar hidden
        btnRegister=findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
    }
}
