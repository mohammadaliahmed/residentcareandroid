package com.siliconst.residentcare.Activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.google.gson.JsonObject;
import com.siliconst.residentcare.Models.Ticket;
import com.siliconst.residentcare.NetworkResponses.ApiResponse;
import com.siliconst.residentcare.R;
import com.siliconst.residentcare.Utils.AppConfig;
import com.siliconst.residentcare.Utils.CommonUtils;
import com.siliconst.residentcare.Utils.SharedPrefs;
import com.siliconst.residentcare.Utils.UserClient;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadPostService extends Service {
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    int uploadCount = 0;
    private String liveFileUrl;
    private String sessionId;
    private Ticket tikcet;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent resultIntent = null;
        uploadCount = 0;
        resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Processing ticket..")
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        int importance = NotificationManager.IMPORTANCE_NONE;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Resident Care", importance);
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setSound(null, null);
//            notificationChannel.enableVibration(true);
//            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
//        mNotificationManager.notify(num /* Request Code */, mBuilder.build());
        Notification notification = mBuilder.build();
        startForeground(101, notification);
        if (CreateTicket.imageUrl == null) {
            submitTicketToServer();
        } else {
            uploadFile(CreateTicket.imageUrl);
        }

        return Service.START_STICKY;

    }

    private void uploadFile(String abc) {
        // create upload service client
        File file = new File(abc);

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
                stopService(new Intent(getApplicationContext(), UploadPostService.class));

            }
        });

    }

    private void submitTicketToServer() {

        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUser().getId());
        map.addProperty("department_id", CreateTicket.departmentChosenId);
        map.addProperty("priority", CreateTicket.priorityChosen);
        map.addProperty("title", CreateTicket.title.getText().toString());
        map.addProperty("description", CreateTicket.description.getText().toString());
        if (liveFileUrl != null) {
            map.addProperty("liveUrl", liveFileUrl);
        }


        Call<ApiResponse> call = getResponse.createTicket(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.body() != null) {
                    if (response.body().getTicket() != null) {
                        tikcet = response.body().getTicket();

//                        callAuthApi();
                    }
                }
                stopService(new Intent(getApplicationContext(), UploadPostService.class));



//                stopService(new Intent(getApplicationContext(), UploadPostService.class));


            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                stopService(new Intent(getApplicationContext(), UploadPostService.class));


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

                        callcustomerSMSApi();

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

    private void callcustomerSMSApi() {
        UserClient getResponse = AppConfig.getRetrofit2().create(UserClient.class);
        String phons = SharedPrefs.getUser().getPhone();
        if (phons.startsWith("03")) {
            phons.replace("03", "923");
        }
        String msg = "Your ticket has been submitted. Token: " + tikcet.getTokenNo() + " Date: " + tikcet.getCreatedAt();
        Call<ResponseBody> call = getResponse.sendSMS(sessionId, phons, msg, "4B Group PK");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    calladminSMSApi();


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());

            }
        });
    }

    private void calladminSMSApi() {
        UserClient getResponse = AppConfig.getRetrofit2().create(UserClient.class);
        String phons = SharedPrefs.getadminPhone();
        if (phons.startsWith("03")) {
            phons.replace("03", "923");
        }
        String msg = "New ticket has been created by " + SharedPrefs.getUser().getName() + " House: " + SharedPrefs.getUser().getHousenumber()
                + SharedPrefs.getUser().getBlock() + ". Token: " + tikcet.getTokenNo() + " Date: " + tikcet.getCreatedAt();

        Call<ResponseBody> call = getResponse.sendSMS(sessionId, phons, msg, "4B Group PK");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    stopService(new Intent(getApplicationContext(), UploadPostService.class));


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());

            }
        });
    }


    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("uploaded");
        // You can also include some extra data.
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        super.onTaskRemoved(rootIntent);
    }

}
