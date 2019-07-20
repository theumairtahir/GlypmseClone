package com.example.glypmse_clone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {
    private Handler waitHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_splash_screen);
        waitHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                //The following code will execute after the 5 seconds.

                try {
                    Intent i;
                    SharedPreferences preferences=getSharedPreferences(getResources().getString(R.string.glympse_shared_preferences),MODE_PRIVATE);
                    if(preferences.getBoolean("loggedInFlag",false)){
                        i= new Intent(SplashScreenActivity.this,MainActivity.class);
                    }
                    else {
                        //Go to next page i.e, startMarker the next activity.
                        i = new Intent(SplashScreenActivity.this, RegisterActivity.class);
                    }
                    startActivity(i);
                    //Let's Finish Splash Activity since we don't want to show this when user press back button.
                    finish();
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        waitHandler.removeCallbacksAndMessages(null);
    }
}
