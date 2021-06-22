package com.siliconst.residentcare.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.siliconst.residentcare.Activities.Fragments.FAQsFragment;
import com.siliconst.residentcare.Activities.Fragments.HomeFragment;
import com.siliconst.residentcare.Activities.Fragments.NoticeBoardFragment;
import com.siliconst.residentcare.Models.NotificationModel;
import com.siliconst.residentcare.Models.User;
import com.siliconst.residentcare.NetworkResponses.ApiResponse;
import com.siliconst.residentcare.R;
import com.siliconst.residentcare.Utils.AppConfig;
import com.siliconst.residentcare.Utils.CommonUtils;
import com.siliconst.residentcare.Utils.Constants;
import com.siliconst.residentcare.Utils.SharedPrefs;

import com.siliconst.residentcare.Utils.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;

public class MainActivity extends AppCompatActivity {


    private Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String notification = getIntent().getStringExtra("notification");
        if (notification != null) {
            NotificationModel notificationModel = SharedPrefs.getNotification();
            if (notificationModel != null && notificationModel.getTitle() != null) {
                showNotificationAlert(notificationModel);
            }
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragment = new HomeFragment();
        loadFragment(fragment);

        if (SharedPrefs.getUser() != null) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (task.isComplete()) {
                        String token = task.getResult().getToken();
                        updateFcmKey(token);

                    }
                }
            });
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    toolbar.setTitle("Shop");

                    if (!Constants.HOME_FRAGMENT) {
                        fragment = new HomeFragment();
                        loadFragment(fragment);
                    }
                    Constants.HOME_FRAGMENT = true;

                    return true;
                case R.id.navigation_notice:
                    fragment = new NoticeBoardFragment();
                    loadFragment(fragment);
                    Constants.HOME_FRAGMENT = false;
                    return true;
                case R.id.navigation_post:

                    startActivity(new Intent(MainActivity.this, CreateTicket.class));
                    Constants.HOME_FRAGMENT = false;
                    return true;
                case R.id.navigation_faq:
                    fragment = new FAQsFragment();
                    loadFragment(fragment);
                    Constants.HOME_FRAGMENT = false;
                    return true;
                case R.id.navigation_profile:
                    startActivity(new Intent(MainActivity.this, EditProfile.class));
                    loadFragment(fragment);
                    Constants.HOME_FRAGMENT = false;
                    return true;

            }
            return false;
        }
    };

    private void showNotificationAlert(NotificationModel notificationModel) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_dialog_notification, null);

        dialog.setContentView(layout);

        Button close = layout.findViewById(R.id.close);
        TextView title = layout.findViewById(R.id.title);
        TextView message = layout.findViewById(R.id.message);
        title.setText(notificationModel.getTitle());
        message.setText(notificationModel.getMessage());
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefs.setNotification(null);
                dialog.dismiss();
            }
        });


        dialog.show();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateFcmKey(String token) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUser().getId());
        map.addProperty("fcmKey", token);

        Call<ApiResponse> call = getResponse.updateFcmKey(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    ApiResponse object = response.body();
                    if (object != null && object.getUser() != null) {
                        SharedPrefs.setadminPhone(response.body().getAdmin_phone());
                        User user = object.getUser();
                        if (user.getActive().equalsIgnoreCase("true")) {
                            SharedPrefs.setUser(user);
                        } else {
                            CommonUtils.showToast("Your account is not active");
                            SharedPrefs.logout();
                            Intent intent = new Intent(MainActivity.this, Splash.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }

                } else {
//                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView mBottomNavigationView = findViewById(R.id.navigation);
        if (mBottomNavigationView.getSelectedItemId() == R.id.navigation_home) {
            super.onBackPressed();
            finish();
        } else {
            mBottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

}