package com.siliconst.residentcare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.siliconst.residentcare.Models.Notice;
import com.siliconst.residentcare.R;

import java.util.List;

public class NoticesAdapter extends RecyclerView.Adapter<NoticesAdapter.ViewHolder> {
    Context context;
    List<Notice> itemList;


    public NoticesAdapter(Context context, List<Notice> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(List<Notice> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notice_item_layout, parent, false);
        NoticesAdapter.ViewHolder viewHolder = new NoticesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notice notice = itemList.get(position);
        holder.title.setText(notice.getTitle());
        holder.description.setText(notice.getDescription());

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }
    }
}
