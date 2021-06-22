package com.siliconst.residentcare.Activities.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.siliconst.residentcare.Models.Department;
import com.siliconst.residentcare.Models.FaqsModel;
import com.siliconst.residentcare.NetworkResponses.ApiResponse;
import com.siliconst.residentcare.R;
import com.siliconst.residentcare.Utils.AppConfig;
import com.siliconst.residentcare.Utils.CommonUtils;
import com.siliconst.residentcare.Utils.UserClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FAQsFragment extends Fragment {
    private View rootView;
    private List<Department> departmentList = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout tabLayout;
    public static HashMap<Integer, List<FaqsModel>> faqMap = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.faq_fragment, container, false);


        getDataFromServer();

        viewPager = rootView.findViewById(R.id.viewpager);


        // Give the TabLayout the ViewPager
        tabLayout = rootView.findViewById(R.id.sliding_tabs);
        return rootView;

    }

    private void getDataFromServer() {

        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);


        Call<ApiResponse> call = getResponse.appFaqs(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 200) {
                            if (response.body().getFaqs() != null) {
                                faqMap = new HashMap<>();
                                departmentList.clear();
                                for (FaqsModel model : response.body().getFaqs()) {
                                    if (faqMap.containsKey(model.getDepartment_id())) {
                                        List<FaqsModel> list = faqMap.get(model.getDepartment_id());
                                        list.add(model);
                                        faqMap.put(model.getDepartment_id(), list);
                                    } else {
                                        List<FaqsModel> list = new ArrayList<>();
                                        list.add(model);
                                        faqMap.put(model.getDepartment_id(), list);
                                        departmentList.add(new Department(model.getDepartment_id(), model.getDepartment_name()));

                                    }
                                }


                                setupTabs();

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

    private void setupTabs() {
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getContext(), getChildFragmentManager(), departmentList);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

}
