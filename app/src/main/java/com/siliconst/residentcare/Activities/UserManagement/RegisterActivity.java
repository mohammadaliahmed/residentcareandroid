package com.siliconst.residentcare.Activities.UserManagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.siliconst.residentcare.Activities.MainActivity;
import com.siliconst.residentcare.NetworkResponses.ApiResponse;
import com.siliconst.residentcare.R;
import com.siliconst.residentcare.Utils.AppConfig;
import com.siliconst.residentcare.Utils.CommonUtils;
import com.siliconst.residentcare.Utils.KeyboardUtils;
import com.siliconst.residentcare.Utils.SharedPrefs;
import com.siliconst.residentcare.Utils.UserClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText name, phone, houseNumber, email, password, username;
    Spinner blockSpinner;
    RadioButton male, female;
    Button register;
    TextView characters;
    String gender;
    private String blockChosen;
    RelativeLayout wholeLayout, asdasdas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        wholeLayout = findViewById(R.id.wholeLayout);
        characters = findViewById(R.id.characters);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        houseNumber = findViewById(R.id.houseNumber);
        email = findViewById(R.id.email);
        asdasdas = findViewById(R.id.asdasdas);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        blockSpinner = findViewById(R.id.blockSpinner);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        register = findViewById(R.id.register);

        initChecks();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() == 0) {
                    name.setError("Enter name");
                } else if (name.getText().length() < 5) {
                    name.setError("Enter full name");
                } else if (phone.getText().length() == 0) {
                    phone.setError("Enter phone");
                } else if (phone.getText().length() < 11) {
                    phone.setError("Enter correct number");
                } else if (gender == null) {
                    CommonUtils.showToast("Please select gender");
                } else if (houseNumber.getText().length() == 0) {
                    houseNumber.setError("Enter house number");
                } else if (blockChosen.equalsIgnoreCase("Choose block")) {
                    CommonUtils.showToast("Please choose block");
                } else if (email.getText().length() == 0) {
                    email.setError("Enter email");
                } else if (!email.getText().toString().contains("@")) {
                    email.setError("Enter correct email");
                } else if (username.getText().length() == 0) {
                    username.setError("Enter username");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter name");
                } else if (password.getText().length() < 6) {
                    password.setError("Enter 6 or more characters");
                } else {
                    singupNow();
                }

            }
        });


    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    private void singupNow() {
        KeyboardUtils.forceCloseKeyboard(asdasdas);
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("name", name.getText().toString());
        map.addProperty("phone", phone.getText().toString());
        map.addProperty("email", email.getText().toString());
        map.addProperty("password", password.getText().toString());
        map.addProperty("username", username.getText().toString());
        map.addProperty("gender", gender);
        map.addProperty("housenumber", houseNumber.getText().toString());
        map.addProperty("block", blockChosen);

        Call<ApiResponse> call = getResponse.register(map);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    if (response.body().getCode() == 200) {
                        CommonUtils.showToast("Successfully registered");
                        SharedPrefs.setUser(response.body().getUser());
                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        if (response.body().getMessage().contains("Username")) {
                            username.setError("Username taken");
                            username.requestFocus();
                        } else if (response.body().getMessage().contains("Phone")) {
                            phone.setError("Phone number exists");
                            phone.requestFocus();
                        } else if (response.body().getMessage().contains("Email")) {
                            email.setError("Email exists");
                            email.requestFocus();
                        }
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

    private void initChecks() {

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

        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        gender = "Male";
                    }
                }
            }
        });
        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        gender = "Female";
                    }
                }
            }
        });

        setupSpinner();

    }

    private void setupSpinner() {
        List<String> list = new ArrayList<>();
        list.add("Select Block");
        list.add("Block A");
        list.add("Block B");
        list.add("Block C");
        list.add("Block D");
        list.add("Block E");
        list.add("Block F");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blockSpinner.setAdapter(dataAdapter);
        blockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                gridViewAdapter.getFilter().filter(list.get(position));

                blockChosen = list.get(position);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
