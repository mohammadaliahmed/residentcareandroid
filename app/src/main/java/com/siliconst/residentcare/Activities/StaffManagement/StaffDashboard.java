package com.siliconst.residentcare.Activities.StaffManagement;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import com.siliconst.residentcare.Activities.EditProfileStaff;
import com.siliconst.residentcare.Activities.Fragments.TickersFragmentPagerAdapter;
import com.siliconst.residentcare.Activities.Splash;
import com.siliconst.residentcare.Adapters.StaffTicketsAdapter;
import com.siliconst.residentcare.Models.Ticket;
import com.siliconst.residentcare.Models.User;
import com.siliconst.residentcare.NetworkResponses.ApiResponse;
import com.siliconst.residentcare.R;
import com.siliconst.residentcare.Utils.AppConfig;
import com.siliconst.residentcare.Utils.CommonUtils;
import com.siliconst.residentcare.Utils.SharedPrefs;
import com.siliconst.residentcare.Utils.UserClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffDashboard extends AppCompatActivity {

    TextView name, email, phone, designation;
    CircleImageView image;
    RecyclerView recycler;
    StaffTicketsAdapter adapter;
    public List<Ticket> ticketList = new ArrayList<>();
    Spinner statusSpinner;
    private String statusChosen;
    CardView profile;
    ViewPager viewPager;
    TabLayout tabLayout;
    public static HashMap<String, List<Ticket>> ticketsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        image = findViewById(R.id.image);

        viewPager = findViewById(R.id.viewpager);


        // Give the TabLayout the ViewPager
        tabLayout = findViewById(R.id.sliding_tabs);


        recycler = findViewById(R.id.recycler);
        name = findViewById(R.id.name);
        designation = findViewById(R.id.designation);
        profile = findViewById(R.id.profile);

        name.setText("WELCOME BACK, " + SharedPrefs.getUser().getName());
        phone.setText(SharedPrefs.getUser().getPhone());
        email.setText(SharedPrefs.getUser().getEmail());
        designation.setText(SharedPrefs.getUser().getDesignation());
        Glide.with(this).load(AppConfig.BASE_URL_Image + SharedPrefs.getUser().getAvatar()).placeholder(R.drawable.ic_profile).into(image);

//        recycler = findViewById(R.id.recycler);
//        adapter = new StaffTicketsAdapter(this, ticketList, new StaffTicketsAdapter.StaffTicketsAdapterCallbacks() {
//            @Override
//            public void onViewTask(Ticket ticket) {
//                showDialogDetails(ticket);
//            }
//        });
//        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
//        recycler.setAdapter(adapter);


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StaffDashboard.this, EditProfileStaff.class));
            }
        });


        getdataFromServer();
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

    private void showDialogDetails(Ticket ticket) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.alert_dialog_ticket_details, null);
        dialog.setContentView(layout);
        TextView houseNumber = layout.findViewById(R.id.houseNumber);
        TextView title = layout.findViewById(R.id.title);
        TextView description = layout.findViewById(R.id.description);
        TextView block = layout.findViewById(R.id.block);
        TextView phone = layout.findViewById(R.id.phone);
        TextView status = layout.findViewById(R.id.status);
        Button close = layout.findViewById(R.id.close);
        Button call = layout.findViewById(R.id.call);
        Button name = layout.findViewById(R.id.name);

        houseNumber.setText(ticket.getUser().getHousenumber());
        name.setText(ticket.getUser().getName());
        block.setText(ticket.getUser().getBlock());
        phone.setText(ticket.getUser().getPhone());
        status.setText("Ticket Status: " + ticket.getStatus());

        if (ticket.getStatus().equalsIgnoreCase("closed")) {
            status.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        } else if (ticket.getStatus().equalsIgnoreCase("pending")) {
            status.setBackgroundColor(getResources().getColor(R.color.colorOriginalBlue));
        } else if (ticket.getStatus().equalsIgnoreCase("processing")) {
            status.setBackgroundColor(getResources().getColor(R.color.colorPurple));
        } else {
            status.setBackgroundColor(getResources().getColor(R.color.colorRed));
        }


        description.setText(ticket.getDescription());
        title.setText(ticket.getSubject());
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ticket.getUser().getPhone()));
                startActivity(i);

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }


    private void getdataFromServer() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUser().getId());


        Call<ApiResponse> call = getResponse.assignedTickets(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getTickets() != null) {
                        ticketList = response.body().getTickets();
                        for (Ticket model : ticketList) {
                            if (ticketsMap.containsKey(model.getId())) {
                                List<Ticket> list = ticketsMap.get(model.getStatus());
                                list.add(model);
                                ticketsMap.put(model.getStatus(), list);
                            } else {
                                List<Ticket> list = new ArrayList<>();
                                list.add(model);
                                ticketsMap.put(model.getStatus(), list);

                            }
                        }


                        setupTabs();
//                        adapter.updateList(response.body().getTickets());
//                        setupPrioritySpinner();
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

    private void setupTabs() {
        List<String> list = new ArrayList<>();

        list.add("Open");
        list.add("Pending");
        list.add("Processing");
        list.add("Closed");
        list.add("Resolved");

        TickersFragmentPagerAdapter adapter = new TickersFragmentPagerAdapter(this, getSupportFragmentManager(), list);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

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
                            Intent intent = new Intent(StaffDashboard.this, Splash.class);
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
}
