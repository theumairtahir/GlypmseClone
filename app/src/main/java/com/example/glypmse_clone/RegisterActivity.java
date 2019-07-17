package com.example.glypmse_clone;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;

public class RegisterActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ImageButton btnRegister;
    EditText edtPhoneNumber, edtCountryCode;
    Spinner spinCountries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_register);
        edtCountryCode=findViewById(R.id.edtCountryCode);
        edtPhoneNumber=findViewById(R.id.edtPhoneNumber);
        spinCountries=findViewById(R.id.spinner);
        //String [] countries=getResources().getStringArray(R.array.countries_names); //retrieving the countries in the spinner
        progressBar=findViewById(R.id.registerProgressBar);
        progressBar.setVisibility(View.INVISIBLE); //initially making the progress bar hidden
        btnRegister=findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(RegisterActivity.this,ActivationCodeVerificationActivity.class);
                if(edtCountryCode.getText().length()>0 && edtPhoneNumber.getText().length()>0) {
                    i.putExtra("country", (String) spinCountries.getSelectedItem());
                    i.putExtra("phoneNumber", edtCountryCode.getText() + "" + edtPhoneNumber.getText());
                    startActivity(i);
                }
                else{
                    Snackbar.make(view,"Please Make sure that no field is empty.",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}
