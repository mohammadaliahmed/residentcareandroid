package com.siliconst.residentcare.Activities.UserManagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.siliconst.residentcare.Activities.ForgotPassword;
import com.siliconst.residentcare.Activities.MainActivity;
import com.siliconst.residentcare.Activities.StaffManagement.StaffDashboard;
import com.siliconst.residentcare.NetworkResponses.ApiResponse;
import com.siliconst.residentcare.R;
import com.siliconst.residentcare.Utils.AppConfig;
import com.siliconst.residentcare.Utils.CommonUtils;
import com.siliconst.residentcare.Utils.KeyboardUtils;
import com.siliconst.residentcare.Utils.SharedPrefs;
import com.siliconst.residentcare.Utils.UserClient;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextView register;
    Button login;
    EditText phone, password;

    TextView characters;
    RelativeLayout wholeLayout;
    TextView forgotPassword;
    RelativeLayout sdasdas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register = findViewById(R.id.register);
        forgotPassword = findViewById(R.id.forgotPassword);

        characters = findViewById(R.id.characters);
        sdasdas = findViewById(R.id.sdasdas);
        wholeLayout = findViewById(R.id.wholeLayout);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().length() < 11) {
                    phone.setError("Enter correct phone number");
                } else if (password.getText().length() < 6) {
                    password.setError("Enter 6 or more characters");
                } else {
                    loginNow();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
            }
        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                characters.setText(phone.getText().length() + "/11");
            }
        });


        String termsAndConditions = "Dont have an account?";
        String privacyPolicy = "Register now";

        register.setText(
                String.format(
                        "Dont have an account? Register now",
                        privacyPolicy)
        );
        register.setMovementMethod(LinkMovementMethod.getInstance());

//        Pattern termsAndConditionsMatcher = Pattern.compile(termsAndConditions);
//        Linkify.addLinks(register, termsAndConditionsMatcher, "terms:");

        Pattern privacyPolicyMatcher = Pattern.compile(privacyPolicy);
        Linkify.addLinks(register, privacyPolicyMatcher, "privacy:");

    }

    private void loginNow() {
        KeyboardUtils.forceCloseKeyboard(sdasdas);
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("password", password.getText().toString());
        map.addProperty("phone", phone.getText().toString());

        Call<ApiResponse> call = getResponse.login(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    if (response.body().getCode() == 200) {
                        if (response.body().getUser().getActive().equalsIgnoreCase("true")) {
                            Intent i = null;

                            if (response.body().getUser().getRole().equalsIgnoreCase("staff")) {
                               i= new Intent(LoginActivity.this, StaffDashboard.class);
                            } else if (response.body().getUser().getRole().equalsIgnoreCase("client")) {
                                i=new Intent(LoginActivity.this, MainActivity.class);
                            }

                            CommonUtils.showToast("Successfully Logged in");
                            SharedPrefs.setUser(response.body().getUser());
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            CommonUtils.showToast("Your account is not active");
                        }
                    } else {

                        CommonUtils.showToast(response.body().getMessage());
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                wholeLayout.setVisibility(View.GONE);
            }
        });

    }
}
