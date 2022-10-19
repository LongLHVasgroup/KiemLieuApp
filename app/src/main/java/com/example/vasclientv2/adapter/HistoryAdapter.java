package com.example.vasclientv2.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasclientv2.R;
import com.example.vasclientv2.model.entities.HistoryModel;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryModel> items;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView statusContent;

        ViewHolder(View itemView) {
            super(itemView);
            statusContent = itemView.findViewById(R.id.statusContent);
        }
    }
    public HistoryAdapter(List<HistoryModel> items) {
        this.items = items;

    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statusthongbao, parent, false);
        return new HistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        HistoryModel item = items.get(position);
        holder.statusContent.setText(item.getHistoryNote());
    }

    @Override
    public int getItemCount() {
        if (items != null)
            return items.size();
        return 0;
    }
}
