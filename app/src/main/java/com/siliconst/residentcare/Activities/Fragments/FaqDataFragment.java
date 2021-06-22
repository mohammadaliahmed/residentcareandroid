package com.siliconst.residentcare.Activities.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.siliconst.residentcare.Adapters.FaqsAdapter;
import com.siliconst.residentcare.Models.FaqsModel;
import com.siliconst.residentcare.R;

import java.util.ArrayList;
import java.util.List;

public class FaqDataFragment extends Fragment {
    private View rootView;

    RecyclerView recycler;
    private List<FaqsModel> itemList = new ArrayList<>();
    private FaqsAdapter adapter;
    Integer departmentId;

    public FaqDataFragment(Integer departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.faq_data_fragment, container, false);
        recycler = rootView.findViewById(R.id.recycler);
        adapter = new FaqsAdapter(getContext(), FAQsFragment.faqMap.get(departmentId));
        recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recycler.setAdapter(adapter);


        return rootView;

    }


}
