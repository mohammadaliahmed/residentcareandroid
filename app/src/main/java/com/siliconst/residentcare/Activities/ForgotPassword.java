package com.siliconst.residentcare.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.siliconst.residentcare.NetworkResponses.ApiResponse;
import com.siliconst.residentcare.R;
import com.siliconst.residentcare.Utils.AppConfig;
import com.siliconst.residentcare.Utils.CommonUtils;
import com.siliconst.residentcare.Utils.KeyboardUtils;
import com.siliconst.residentcare.Utils.SharedPrefs;
import com.siliconst.residentcare.Utils.UserClient;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    RelativeLayout wholeLayout;
    EditText phone;
    Button send, confirm;
    LinearLayout enterCodeLayout, enterPhoneLayout;
    EditText code, password, confirmPassword;
    RelativeLayout sadadas;
    TextView textt;
    private String sessionId;
    private String userCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forogt_password);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Reset Password");

        phone = findViewById(R.id.phone);
        confirm = findViewById(R.id.confirm);
        textt = findViewById(R.id.textt);
        send = findViewById(R.id.send);
        wholeLayout = findViewById(R.id.wholeLayout);
        sadadas = findViewById(R.id.sadadas);


        enterCodeLayout = findViewById(R.id.enterCodeLayout);
        enterPhoneLayout = findViewById(R.id.enterPhoneLayout);
        code = findViewById(R.id.code);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code.getText().length() < 6) {
                    code.setError("Enter correct code");
                } else if (password.getText().length() < 5) {
                    password.setError("Enter password");
                } else if (confirmPassword.getText().length() < 5) {
                    confirmPassword.setError("Enter confirm password");
                } else if (!confirmPassword.getText().toString().equals(password.getText().toString())) {
                    CommonUtils.showToast("Password dot not match");
                } else {
                    resetPasswordNow();
                }
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().length() < 5) {
                    phone.setError("Enter phone");
                } else {
                    resetPasswordAPI();
                }

            }
        });
    }

    private void callAuthApi() {
        UserClient getResponse = AppConfig.getRetrofit2().create(UserClient.class);
        Call<ResponseBody> call = getResponse.loginSMS("923453480541", "yahoo123456");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String res = response.body().string();
                        res = res.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><corpsms><command>Auth_request</command><data>", "");
                        sessionId = res.replace("</data><response>OK</response></corpsms>", "");
                        callSMSApi();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());

            }
        });

    }

    private void callSMSApi() {
        UserClient getResponse = AppConfig.getRetrofit2().create(UserClient.class);
        String phons = phone.getText().toString();
        if (phons.startsWith("03")) {
            phons.replace("03", "923");
        }
        Call<ResponseBody> call = getResponse.sendSMS(sessionId, phons, "Your reset code is: " + userCode, "4B Group PK");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());

            }
        });
    }

    private void resetPasswordNow() {
        KeyboardUtils.forceCloseKeyboard(sadadas);

        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("phone", phone.getText().toString());
        map.addProperty("code", code.getText().toString());
        map.addProperty("password", password.getText().toString());
        Call<ApiResponse> call = getResponse.resetpasswordNow(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    if (response.body().getCode() == 200) {
                        CommonUtils.showToast("Password reset successfully");
                        SharedPrefs.setUser(response.body().getUser());
                        startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                        finish();
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

    private void resetPasswordAPI() {
        KeyboardUtils.forceCloseKeyboard(sadadas);
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("phone", phone.getText().toString());

        Call<ApiResponse> call = getResponse.resetpassword(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    userCode = response.body().getUsercode();
                    CommonUtils.showToast("Code sent. Please check sms");
                    textt.setText("Code sent on: " + phone.getText().toString());
                    callAuthApi();
                    enterPhoneLayout.setVisibility(View.GONE);
                    enterCodeLayout.setVisibility(View.VISIBLE);


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
