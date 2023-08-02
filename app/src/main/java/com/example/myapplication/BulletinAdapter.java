package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BulletinAdapter extends RecyclerView.Adapter<BulletinAdapter.BulletinViewHolder> {
    private ArrayList<BulletinInfo> arrayList;
    private Context context;

    public BulletinAdapter(ArrayList<BulletinInfo> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public BulletinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        BulletinViewHolder holder = new BulletinViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BulletinViewHolder holder, int position) {
        BulletinInfo bulletinInfo = arrayList.get(position);
        holder.textViewPostContent.setText(bulletinInfo.getContent());
        holder.textViewDate.setText(bulletinInfo.getDate());
        holder.textViewNickname.setText(bulletinInfo.getName());
        holder.btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentLikeCount = bulletinInfo.getLikeCount();

                if (holder.btn_like.isSelected()) {
                    currentLikeCount--;
                    holder.btn_like.setImageResource(R.drawable.baseline_favorite_border_24);
                    holder.btn_like.setSelected(false);
                } else {
                    currentLikeCount++;
                    holder.btn_like.setImageResource(R.drawable.baseline_favorite_24);
                    holder.btn_like.setSelected(true);
                }
                bulletinInfo.setLikeCount(currentLikeCount);
                holder.likeCount.setText(String.valueOf(currentLikeCount));
            }
        });
        holder.btn_like.setSelected(bulletinInfo.isLiked());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class BulletinViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPostContent;
        TextView textViewDate;
        TextView textViewNickname;
        TextView likeCount;
        TextView comment;
        ImageButton btn_like;
        ImageButton btn_comment;
        public BulletinViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewPostContent = itemView.findViewById(R.id.textViewPostContent);
            this.textViewDate = itemView.findViewById(R.id.textViewDate);
            this.textViewNickname = itemView.findViewById(R.id.textViewNickname);
            this.likeCount = itemView.findViewById(R.id.likeCount);
            this.comment = itemView.findViewById(R.id.comment);
            this.btn_comment = itemView.findViewById(R.id.btn_comment);
            this.btn_like = itemView.findViewById(R.id.btn_like);
        }
    }
}
