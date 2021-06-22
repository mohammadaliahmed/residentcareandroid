package com.siliconst.residentcare.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.siliconst.residentcare.Activities.ShowImage;
import com.siliconst.residentcare.Models.Reply;
import com.siliconst.residentcare.R;
import com.siliconst.residentcare.Utils.AppConfig;
import com.siliconst.residentcare.Utils.CommonUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.ViewHolder> {
    Context context;
    List<Reply> itemList;


    public RepliesAdapter(Context context, List<Reply> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(List<Reply> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.replies_item_layout, parent, false);
        RepliesAdapter.ViewHolder viewHolder = new RepliesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reply reply = itemList.get(position);
        holder.text.setText(reply.getReply());
        holder.name.setText(reply.getUser().getName());
        holder.date.setText(CommonUtils.getCorrectDateFromTimeStamp(reply.getCreatedAt()));
        if (reply.getAttachment() != null) {
            holder.downloadAttachment.setVisibility(View.VISIBLE);
            holder.fileName.setText(reply.getAttachment());
        } else {
            holder.downloadAttachment.setVisibility(View.GONE);
        }
        Glide.with(context).load(AppConfig.BASE_URL_Image + reply.getUser().getAvatar()).placeholder(R.drawable.avatar).into(holder.image);
        holder.fileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ShowImage.class);
                i.putExtra("url", reply.getAttachment());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, text;
        CircleImageView image;
        RelativeLayout downloadAttachment;
        TextView fileName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileName);
            downloadAttachment = itemView.findViewById(R.id.downloadAttachment);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            text = itemView.findViewById(R.id.text);
        }
    }
}
