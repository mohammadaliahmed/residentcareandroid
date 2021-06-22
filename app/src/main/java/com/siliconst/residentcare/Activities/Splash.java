package com.siliconst.residentcare.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import com.siliconst.residentcare.Activities.StaffManagement.StaffDashboard;
import com.siliconst.residentcare.Activities.UserManagement.LoginActivity;
import com.siliconst.residentcare.R;
import com.siliconst.residentcare.Utils.CommonUtils;
import com.siliconst.residentcare.Utils.SharedPrefs;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (SharedPrefs.getUser() != null) {
                    if (SharedPrefs.getUser().getActive().equalsIgnoreCase("true")) {
                        if (SharedPrefs.getUser().getRole().equalsIgnoreCase("staff")) {
                            Intent i = new Intent(Splash.this, StaffDashboard.class);
                            startActivity(i);
                        } else if (SharedPrefs.getUser().getRole().equalsIgnoreCase("client")) {
                            Intent i = new Intent(Splash.this, MainActivity.class);
                            startActivity(i);
                        }

                    } else {
                        CommonUtils.showToast("You account is not active");
                    }
                } else {
                    Intent i = new Intent(Splash.this, LoginActivity.class);
                    startActivity(i);
                }

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
