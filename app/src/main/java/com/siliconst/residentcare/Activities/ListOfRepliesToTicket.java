package com.siliconst.residentcare.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.gson.JsonObject;
import com.siliconst.residentcare.Adapters.RepliesAdapter;
import com.siliconst.residentcare.Models.Reply;
import com.siliconst.residentcare.Models.Ticket;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListOfRepliesToTicket extends AppCompatActivity {

    RecyclerView recycler;
    private List<Reply> repliesList = new ArrayList<>();
    private int ticketId;
    private Ticket ticketModel;
    TextView description, title, tokenNumber;
    RepliesAdapter adapter;
    ImageView sendMessage;
    EditText message;
    private String messageToSend;
    ImageView close;
    ImageView camera;
    private ArrayList<String> mSelected = new ArrayList<>();
    private String imageUrl;
    private String liveFileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_replies);


        ticketId = getIntent().getIntExtra("ticketId", 0);
        tokenNumber = findViewById(R.id.tokenNumber);
        sendMessage = findViewById(R.id.sendMessage);
        message = findViewById(R.id.message);
        title = findViewById(R.id.title);
        camera = findViewById(R.id.camera);
        description = findViewById(R.id.description);
        close = findViewById(R.id.close);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new RepliesAdapter(this, repliesList);
        recycler.setAdapter(adapter);
        getRepliesFromServer();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().length() == 0) {
                    message.setError("Cant send empty message");
                } else {
                    if (mSelected.size() > 0) {
                        uploadImage(imageUrl);
                    } else {
                        sendMessageNow();
                    }

                }
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options options = Options.init()
                        .setRequestCode(100)                                           //Request code for activity results
                        .setCount(1)                                                   //Number of images to restict selection count
                        .setExcludeVideos(true)                                       //Option to exclude videos
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                        ;                                       //Custom Path For media Storage

                Pix.start(ListOfRepliesToTicket.this, options);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            mSelected = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Glide.with(ListOfRepliesToTicket.this).load(mSelected.get(0)).into(camera);
            CompressImage compressImage = new CompressImage(this);
            imageUrl = compressImage.compressImage(mSelected.get(0));

        }


    }

    private void sendMessageNow() {
        messageToSend = message.getText().toString();
        message.setText("");
        Glide.with(this).load(R.drawable.ic_camera_black).into(camera);

        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("ticketId", ticketId);
        map.addProperty("reply", messageToSend);
        map.addProperty("userId", SharedPrefs.getUser().getId());
        if (liveFileUrl != null) {
            map.addProperty("liveUrl", liveFileUrl);
        }

        Call<ApiResponse> call = getResponse.sendReply(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getReplies() != null) {
                        repliesList = response.body().getReplies();
                        adapter.setItemList(repliesList);
                        recycler.scrollToPosition(repliesList.size() - 1);

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

    private void uploadImage(String path) {
        CommonUtils.showToast("Uploading Image");
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
                        sendMessageNow();

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

    private void getRepliesFromServer() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("ticketId", ticketId);


        Call<ApiResponse> call = getResponse.getReplies(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getReplies() != null) {
                        repliesList = response.body().getReplies();
                        adapter.setItemList(repliesList);
                        ticketModel = response.body().getTicket();
                        recycler.scrollToPosition(repliesList.size() - 1);
                        if (ticketModel != null) {
                            setupTicketUi();
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

    private void setupTicketUi() {


        tokenNumber.setText("Ticket #:   " + ticketModel.getTokenNo() + " | Date: " + CommonUtils.getCorrectDateFromTimeStamp(ticketModel.getCreatedAt()));
        title.setText(ticketModel.getSubject());
        description.setText(ticketModel.getDescription());
    }


}
