package com.siliconst.residentcare.Activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.gson.JsonObject;
import com.siliconst.residentcare.Models.Department;
import com.siliconst.residentcare.NetworkResponses.ApiResponse;
import com.siliconst.residentcare.R;
import com.siliconst.residentcare.Utils.AppConfig;
import com.siliconst.residentcare.Utils.ApplicationClass;
import com.siliconst.residentcare.Utils.CommonUtils;
import com.siliconst.residentcare.Utils.CompressImage;
import com.siliconst.residentcare.Utils.CompressImageNew;
import com.siliconst.residentcare.Utils.SharedPrefs;
import com.siliconst.residentcare.Utils.UserClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTicket extends AppCompatActivity {

    ImageView pickImage;
    private ArrayList<String> mSelected = new ArrayList<>();
    RelativeLayout wholeLayout;
    Button submit;
    public static EditText description, title;
    Spinner departmentSpinner, prioritySpinner;
    public static Integer departmentChosenId = 0;
    private List<Department> departmentList = new ArrayList<>();
    private String liveFileUrl;
    public static String imageUrl;
    public static String priorityChosen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);
        getSupportActionBar().setElevation(0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Create Ticket");

        wholeLayout = findViewById(R.id.wholeLayout);
        description = findViewById(R.id.description);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        title = findViewById(R.id.title);
        submit = findViewById(R.id.submit);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        pickImage = findViewById(R.id.pickImage);

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options options = Options.init()
                        .setRequestCode(100)                                           //Request code for activity results
                        .setCount(1)                                                   //Number of images to restict selection count
                        .setExcludeVideos(true)                                       //Option to exclude videos
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                        ;                                       //Custom Path For media Storage

                Pix.start(CreateTicket.this, options);
            }
        });

        getDepartmentsFromSpinner();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                uploadImage(imageUrl);
                if (title.getText().length() < 10) {
                    title.setError("Enter detailed title");
                } else if (description.getText().length() < 15) {
                    description.setError("Please explain problem");
                } else if (departmentChosenId == 0) {
                    CommonUtils.showToast("Please select department");
                } else if (priorityChosen.equalsIgnoreCase("Choose priority")) {
                    CommonUtils.showToast("Please choose priority");
                } else {
                    showAlert();
                }
            }
        });
        setupPrioritySpinner();
    }

    private void showAlert() {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setContentText("Create ticket?")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        startServiceNow();
                        showDoneAlert();
//                        if (mSelected.size() > 0) {
//                            startServiceNow();
//                            uploadImage(imageUrl);
//                        } else {
//                            submitTicketToServer();
//                        }

                    }
                }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.cancel();
            }
        })
                .show();
    }


    private void startServiceNow() {
        stopServiceNow();
        boolean isClipboardServiceRunning = isMyServiceRunning(UploadPostService.class);

        if (!isClipboardServiceRunning) {
            Intent svc = new Intent(CreateTicket.this, UploadPostService.class);

            startService(svc);
        } else {
//            CommonUtils.showToast("Service running");

        }

    }

    private void stopServiceNow() {
        boolean isClipboardServiceRunning = isMyServiceRunning(UploadPostService.class);

        if (isClipboardServiceRunning) {
            stopService(new Intent(getApplicationContext(), UploadPostService.class));

        } else {
//            CommonUtils.showToast("Service not running running");
        }


    }

    private void submitTicketToServer() {
        wholeLayout.setVisibility(View.VISIBLE);

        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUser().getId());
        map.addProperty("department_id", departmentChosenId);
        map.addProperty("priority", priorityChosen);
        map.addProperty("title", title.getText().toString());
        map.addProperty("description", description.getText().toString());
        if (liveFileUrl != null) {
            map.addProperty("liveUrl", liveFileUrl);
        }


        Call<ApiResponse> call = getResponse.createTicket(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                wholeLayout.setVisibility(View.GONE);

                if (response.code() == 200) {
                    showDoneAlert();

                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                wholeLayout.setVisibility(View.GONE);


            }
        });
    }

    private void showDoneAlert() {
        new SweetAlertDialog(CreateTicket.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success")
                .setContentText("Ticket Submitted")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        finish();
                    }
                })
                .show();
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
        Call<ResponseBody> call = service.uploadFile(fileToUpload, filename);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {

                        String url = response.body().string();
                        liveFileUrl = url;
                        submitTicketToServer();

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

    private void getDepartmentsFromSpinner() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);


        Call<ApiResponse> call = getResponse.getDepartments(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 200) {
                            if (response.body().getDepartments() != null) {
                                departmentList = response.body().getDepartments();
                                setupSpinner();
                            }
                        } else {
                            CommonUtils.showToast(response.body().getMessage());
                        }
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });

    }

    private void setupSpinner() {
        List<String> list = new ArrayList<>();
        list.add("Choose Department");
        for (Department department : departmentList) {
            list.add(department.getName());
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(dataAdapter);
        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                gridViewAdapter.getFilter().filter(list.get(position));
                if (position > 0) {
                    departmentChosenId = departmentList.get(position - 1).getId();

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupPrioritySpinner() {
        List<String> list = new ArrayList<>();
        list.add("Choose priority");
        list.add("low");
        list.add("medium");
        list.add("high");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(dataAdapter);
        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                gridViewAdapter.getFilter().filter(list.get(position));
                priorityChosen = list.get(position);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            mSelected = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Glide.with(CreateTicket.this).load(mSelected.get(0)).into(pickImage);
            CompressImageNew compressImage = new CompressImageNew(this);
//            CompressImage compressImage = new CompressImage(this);
            imageUrl = compressImage.compressImage(mSelected.get(0));

        }


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

    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) ApplicationClass.getInstance().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
