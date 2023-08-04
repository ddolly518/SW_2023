package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

public class WriteAdapter extends RecyclerView.Adapter<WriteAdapter.WriteViewHolder> {
    private ArrayList<WriteInfo> arrayList;
    private Context context;

    public WriteAdapter(ArrayList<WriteInfo> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public WriteAdapter.WriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary, parent, false);
        WriteViewHolder holder = new WriteViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WriteAdapter.WriteViewHolder holder, int position) {
        holder.textDiaryContent.setText(arrayList.get(position).getContent());
        holder.textViewDate.setText(arrayList.get(position).getDate());
        holder.textEmotion.setText(arrayList.get(position).getEmotion());

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class WriteViewHolder extends RecyclerView.ViewHolder {
        TextView textDiaryContent;
        TextView textViewDate;
        TextView textViewId;
        TextView textEmotion;
        public WriteViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textDiaryContent = itemView.findViewById(R.id.textDiaryContent);
            this.textViewDate = itemView.findViewById(R.id.textViewDate);
            this.textViewId = itemView.findViewById(R.id.textViewId);
            this.textEmotion = itemView.findViewById(R.id.textEmotion);
        }
    }
}
