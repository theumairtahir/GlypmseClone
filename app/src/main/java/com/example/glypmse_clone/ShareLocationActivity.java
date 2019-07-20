package com.example.glypmse_clone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class ShareLocationActivity extends AppCompatActivity {

    SeekBar seekBarShareFor;
    TextView txtShareFor;
    EditText edtDestination;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Share");
        }
        txtShareFor=findViewById(R.id.txtShareFor);
        seekBarShareFor=findViewById(R.id.seekBarShareFor);
        seekBarShareFor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int shareFor=i*5;
                if(shareFor>=60){
                    int shareForHour=shareFor/60;
                    txtShareFor.setText(shareForHour+" hours");
                }
                else {
                    txtShareFor.setText(shareFor+" min");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        edtDestination=findViewById(R.id.edtAddDestination);
        edtDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(ShareLocationActivity.this,AddDestinationActivity.class);
                startActivityForResult(i,1121);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
