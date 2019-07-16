package com.example.glypmse_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.glypmse_clone.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivationCodeVerificationActivity extends AppCompatActivity {

    private final int ACTIVATION_CODE=12345;
    private Intent activityIntent;
    ProgressBar registerProgressBar;
    EditText edtActivationCode;
    Button btnVerify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_activation_code_verification);
        registerProgressBar=findViewById(R.id.registerProgressBar);
        registerProgressBar.setVisibility(View.INVISIBLE);
        activityIntent=getIntent();
        edtActivationCode=findViewById(R.id.edtActivationCode);
        btnVerify=findViewById(R.id.btnActivationSubmit);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=edtActivationCode.getText().toString();
                int userEnteredCode=Integer.parseInt(s);
                if(userEnteredCode==ACTIVATION_CODE){
                    final AlertDialog.Builder dialog=new AlertDialog.Builder(ActivationCodeVerificationActivity.this);
                    dialog.setTitle("Register");
                    dialog.setMessage("Enter your name to register");
                    //LayoutInflater inflater=LayoutInflater.from(getParent());
                    //final View registerDialogLayout=inflater.inflate(R.layout.user_name_dialog,null);
                    final EditText edtName=new EditText(ActivationCodeVerificationActivity.this);
                    dialog.setView(edtName);
                    dialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            registerProgressBar.setVisibility(View.VISIBLE);
                            FirebaseDatabase db=FirebaseDatabase.getInstance();
                            DatabaseReference users=db.getReference("users");
                            final User user = new User(edtName.getText().toString(),activityIntent.getStringExtra("phoneNumber"),activityIntent.getStringExtra("country"),null);
                            users.child(user.getPhoneNumber()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    SharedPreferences.Editor editor= getSharedPreferences(getResources().getString(R.string.glympse_shared_preferences),MODE_PRIVATE).edit();
                                    editor.putBoolean("loggedInFlag",true);
                                    editor.putString("phoneNumber",user.getPhoneNumber());
                                    editor.commit();
                                    Intent i= new Intent(ActivationCodeVerificationActivity.this,MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    registerProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(ActivationCodeVerificationActivity.this,"Something went wrong! Make sure you are connected to the Internet",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                    dialog.show();
                }
                else{
                    Toast.makeText(getBaseContext(),"Activation Code not matched",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
