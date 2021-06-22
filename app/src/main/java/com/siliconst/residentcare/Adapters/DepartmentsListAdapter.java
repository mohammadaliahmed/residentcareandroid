package com.siliconst.residentcare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.siliconst.residentcare.Models.Department;
import com.siliconst.residentcare.R;

import java.util.List;

public class DepartmentsListAdapter extends RecyclerView.Adapter<DepartmentsListAdapter.ViewHolder> {
    Context context;
    List<Department> itemList;
    int selected = 0;
    DepartmentListAdapterCallbacks callbacks;

    public DepartmentsListAdapter(Context context, List<Department> itemList, DepartmentListAdapterCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.callbacks = callbacks;
    }


    public void setItemList(List<Department> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.department_item_layout, parent, false);
        DepartmentsListAdapter.ViewHolder viewHolder = new DepartmentsListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Department department = itemList.get(position);
        holder.departmentName.setText(department.getName());


        if (selected == position) {
            holder.departmentName.setBackground(context.getDrawable(R.drawable.blue_curved_bg));
            holder.departmentName.setTextColor(context.getResources().getColor(R.color.colorWhite));
        } else {
            holder.departmentName.setBackground(context.getDrawable(R.drawable.white_curved_bg));
            holder.departmentName.setTextColor(context.getResources().getColor(R.color.colorBlue));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onSelected(department.getId());
                selected=position;
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView departmentName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            departmentName = itemView.findViewById(R.id.departmentName);

        }
    }

    public interface DepartmentListAdapterCallbacks {
        public void onSelected(int id);
    }
}
