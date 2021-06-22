package com.siliconst.residentcare.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.siliconst.residentcare.Adapters.DepartmentsListAdapter;
import com.siliconst.residentcare.Adapters.HomeTicketsAdapter;
import com.siliconst.residentcare.Models.Department;
import com.siliconst.residentcare.Models.Ticket;
import com.siliconst.residentcare.NetworkResponses.ApiResponse;
import com.siliconst.residentcare.R;
import com.siliconst.residentcare.Utils.AppConfig;
import com.siliconst.residentcare.Utils.CommonUtils;
import com.siliconst.residentcare.Utils.SharedPrefs;
import com.siliconst.residentcare.Utils.UserClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeeAllTickets extends AppCompatActivity {

    RecyclerView departmentRecycler;
    private List<Department> departmentList = new ArrayList<>();

    DepartmentsListAdapter departmentsListAdapter;
    RecyclerView recycler;
    HomeTicketsAdapter homeTicketsAdapter;
    private List<Ticket> ticketList = new ArrayList<>();
    ImageView back;
    ImageView createTicket;
    ImageView search;
    ImageView profile;
    Spinner sortSpinner;
    private String sortChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeall_tickets);
        createTicket = findViewById(R.id.createTicket);
        recycler = findViewById(R.id.recycler);
        back = findViewById(R.id.back);
        search = findViewById(R.id.search);
        profile = findViewById(R.id.profile);
        sortSpinner = findViewById(R.id.sortSpinner);
        departmentRecycler = findViewById(R.id.departmentRecycler);
        departmentsListAdapter = new DepartmentsListAdapter(this, departmentList, new DepartmentsListAdapter.DepartmentListAdapterCallbacks() {
            @Override
            public void onSelected(int id) {
                if (id == -1) {
                    homeTicketsAdapter.filter("");
                } else {
                    homeTicketsAdapter.filter("" + id);
                }
            }
        });
        departmentRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        departmentRecycler.setAdapter(departmentsListAdapter);
        createTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SeeAllTickets.this, CreateTicket.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SeeAllTickets.this, EditProfile.class));
            }
        });
        homeTicketsAdapter = new HomeTicketsAdapter(this, ticketList);
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recycler.setAdapter(homeTicketsAdapter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog();
            }
        });
        getDepartmentsFromSpinner();
        setupPrioritySpinner();
    }

    private void showSearchDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.search_dialog_full, null);

        dialog.setContentView(layout);
        ImageView close = layout.findViewById(R.id.close);
        EditText search = layout.findViewById(R.id.search);
        RecyclerView ticketsRecycler = layout.findViewById(R.id.ticketsRecycler);
        ticketsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        HomeTicketsAdapter homeTicketsAdapter = new HomeTicketsAdapter(this, ticketList);
        ticketsRecycler.setAdapter(homeTicketsAdapter);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                homeTicketsAdapter.filter(s.toString());
            }
        });


        dialog.show();

    }

    private void getMyTicketsFromServer() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUser().getId());


        Call<ApiResponse> call = getResponse.allTickets(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getTickets() != null) {
                        ticketList = response.body().getTickets();
                        homeTicketsAdapter.updateList(response.body().getTickets());
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

    private void setupPrioritySpinner() {
        List<String> list = new ArrayList<>();
        list.add("Date Added");
        list.add("Priority");
        list.add("Status");


//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, list);
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter dataAdapter=ArrayAdapter.createFromResource(this,R.array.sort_list,R.layout.color_spinner_item);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        sortSpinner.setAdapter(dataAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                gridViewAdapter.getFilter().filter(list.get(position));
                sortChosen = list.get(position);
                if (sortChosen.toLowerCase().contains("date")) {
                    Collections.sort(ticketList, new Comparator<Ticket>() {
                        @Override
                        public int compare(Ticket listData, Ticket t1) {
                            String ob1 = listData.getCreatedAt();
                            String ob2 = t1.getCreatedAt();
                            return ob2.compareTo(ob1);

                        }
                    });
                    homeTicketsAdapter.updateList(ticketList);
                } else if (sortChosen.toLowerCase().contains("priority")) {
                    Collections.sort(ticketList, new Comparator<Ticket>() {
                        @Override
                        public int compare(Ticket listData, Ticket t1) {
                            String ob1 = listData.getPriority();
                            String ob2 = t1.getPriority();
                            return ob2.compareTo(ob1);

                        }
                    });
                    homeTicketsAdapter.updateList(ticketList);
                } else if (sortChosen.toLowerCase().contains("status")) {
                    Collections.sort(ticketList, new Comparator<Ticket>() {
                        @Override
                        public int compare(Ticket listData, Ticket t1) {
                            String ob1 = listData.getStatus();
                            String ob2 = t1.getStatus();
                            return ob1.compareTo(ob2);

                        }
                    });
                    homeTicketsAdapter.updateList(ticketList);

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                                departmentList.add(new Department(-1, "All"));
                                departmentList.addAll(response.body().getDepartments());
                                departmentsListAdapter.setItemList(departmentList);
                                getMyTicketsFromServer();

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

}
