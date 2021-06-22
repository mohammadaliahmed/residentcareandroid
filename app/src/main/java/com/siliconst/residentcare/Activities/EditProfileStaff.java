package com.siliconst.residentcare.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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

import com.bumptech.glide.Glide;
import com.fxn.pix.Pix;
import com.google.gson.JsonObject;
import com.siliconst.residentcare.NetworkResponses.ApiResponse;
import com.siliconst.residentcare.R;
import com.siliconst.residentcare.Utils.AppConfig;
import com.siliconst.residentcare.Utils.CommonUtils;
import com.siliconst.residentcare.Utils.CompressImage;
import com.siliconst.residentcare.Utils.SharedPrefs;
import com.siliconst.residentcare.Utils.UserClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileStaff extends AppCompatActivity {

    EditText name, phone, houseNumber, email, username;
    Spinner blockSpinner;
    RadioButton male, female;
    Button update, logout;
    TextView characters;
    String gender;
    private String blockChosen;
    RelativeLayout wholeLayout;
    CircleImageView image;
    private ArrayList<String> mSelected = new ArrayList<>();
    private String imageUrl;
    private String liveUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_profile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("My Profile");


        logout = findViewById(R.id.logout);
        wholeLayout = findViewById(R.id.wholeLayout);
        characters = findViewById(R.id.characters);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        houseNumber = findViewById(R.id.houseNumber);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        blockSpinner = findViewById(R.id.blockSpinner);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        update = findViewById(R.id.update);
        image = findViewById(R.id.image);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showALert();

            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                Options options = Options.init()
//                        .setRequestCode(100)                                           //Request code for activity results
//                        .setCount(1)                                                   //Number of images to restict selection count
//                        .setExcludeVideos(true)                                       //Option to exclude videos
//                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
//                        ;                                       //Custom Path For media Storage
//
//                Pix.start(EditProfileStaff.this, options);

            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (name.getText().length() == 0) {
//                    name.setError("Enter name");
//                } else if (name.getText().length() < 5) {
//                    name.setError("Enter full name");
//                } else if (phone.getText().length() == 0) {
//                    phone.setError("Enter phone");
//                } else if (phone.getText().length() < 11) {
//                    phone.setError("Enter correct number");
//                } else if (email.getText().length() == 0) {
//                    email.setError("Enter email");
//                } else if (!email.getText().toString().contains("@")) {
//                    email.setError("Enter correct email");
//                } else if (username.getText().length() == 0) {
//                    username.setError("Enter username");
//                } else {
////                    if (mSelected.size() > 0) {
////                        uploadImage(imageUrl);
////                    } else {
//                    updateProfile();
////                    }
//
//                }
            }
        });
        setupUi();
        initChecks();
    }

    private void showALert() {

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Are you sure to logout?")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        SharedPrefs.logout();
                        Intent intent = new Intent(EditProfileStaff.this, Splash.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();


                    }
                }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.cancel();
            }
        })
                .show();


    }

    private void setupUi() {
        name.setText(SharedPrefs.getUser().getName());
        phone.setText(SharedPrefs.getUser().getPhone());
        email.setText(SharedPrefs.getUser().getEmail());
        username.setText(SharedPrefs.getUser().getUsername());
        houseNumber.setText(SharedPrefs.getUser().getHousenumber());
        Glide.with(this).load(AppConfig.BASE_URL_Image + SharedPrefs.getUser().getAvatar()).placeholder(R.drawable.ic_profile).into(image);

//        if (SharedPrefs.getUser().getGender().equalsIgnoreCase("Male")) {
//            gender = "Male";
//
//            female.setChecked(false);
//            male.setChecked(true);
//        } else if (SharedPrefs.getUser().getGender().equalsIgnoreCase("Female")) {
//            gender = "Female";
//            male.setChecked(false);
//            female.setChecked(true);
//        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            mSelected = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Glide.with(EditProfileStaff.this).load(mSelected.get(0)).into(image);
            CompressImage compressImage = new CompressImage(this);
            imageUrl = compressImage.compressImage(mSelected.get(0));

        }


    }

    private void uploadImage(String path) {
        CommonUtils.showToast("Uploading Image");
        wholeLayout.setVisibility(View.VISIBLE);
        File file = new File(path);

        UserClient service = AppConfig.getRetrofit().create(UserClient.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("photo", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        // finally, execute the request
        Call<ResponseBody> call = service.uploadFileToUploads(fileToUpload, filename);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {

                        String url = response.body().string();
                        liveUrl = url;
                        updateProfile();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                CommonUtils.showToast(t.getMessage());
            }
        });
    }

    private void updateProfile() {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUser().getId());
        map.addProperty("name", name.getText().toString());
        map.addProperty("phone", phone.getText().toString());
        map.addProperty("gender", gender);
        map.addProperty("housenumber", houseNumber.getText().toString());
        map.addProperty("block", blockChosen);
        if (liveUrl != null) {
            map.addProperty("liveUrl", liveUrl);
        }

        Call<ApiResponse> call = getResponse.updateProfile(map);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    if (response.body().getCode() == 200) {
                        CommonUtils.showToast("Profile Updated");
                        SharedPrefs.setUser(response.body().getUser());
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
        blockSpinner.setSelection(list.indexOf(SharedPrefs.getUser().getBlock()));

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
