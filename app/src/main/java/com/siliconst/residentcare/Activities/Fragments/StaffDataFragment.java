package com.siliconst.residentcare.Activities.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.siliconst.residentcare.Activities.StaffManagement.StaffDashboard;
import com.siliconst.residentcare.Adapters.StaffTicketsAdapter;
import com.siliconst.residentcare.Models.Ticket;
import com.siliconst.residentcare.R;

public class StaffDataFragment extends Fragment {
    private View rootView;

    RecyclerView recycler;
    private StaffTicketsAdapter adapter;
    String status;

    public StaffDataFragment(String status) {
        this.status = status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.faq_data_fragment, container, false);
        recycler = rootView.findViewById(R.id.recycler);
        if (StaffDashboard.ticketsMap.get(status.toLowerCase()) != null && StaffDashboard.ticketsMap.get(status.toLowerCase()).size() > 0) {


            adapter = new StaffTicketsAdapter(getContext(), StaffDashboard.ticketsMap.get(status.toLowerCase()), new StaffTicketsAdapter.StaffTicketsAdapterCallbacks() {
                @Override
                public void onViewTask(Ticket ticket) {
                    showDialogDetails(ticket);
                }
            });
            adapter.updateList(StaffDashboard.ticketsMap.get(status.toLowerCase()));
            recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            recycler.setAdapter(adapter);
        }


        return rootView;

    }

    private void showDialogDetails(Ticket ticket) {
        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        TextView name = layout.findViewById(R.id.name);

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


}
