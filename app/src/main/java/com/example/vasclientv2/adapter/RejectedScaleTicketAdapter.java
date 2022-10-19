package com.example.vasclientv2.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasclientv2.R;
import com.example.vasclientv2.model.entities.CheckingScrapModel;
import com.example.vasclientv2.model.entities.KLCheckingScrap;

import java.util.ArrayList;

public class RejectedScaleTicketAdapter extends RecyclerView.Adapter<RejectedScaleTicketAdapter.RejectedScaleTickitViewHolder> {
    private ArrayList<CheckingScrapModel> list;
    private OnItemClickListener onItemClickListener;

    public static class RejectedScaleTickitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtMaPhieuCan;
        public TextView txtBienSoXe;
        private OnItemClickListener onItemClickListener;

        public RejectedScaleTickitViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            txtMaPhieuCan = itemView.findViewById(R.id.txtMaPhieuCan);
            txtBienSoXe = itemView.findViewById(R.id.txtBienSoXe);
            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public RejectedScaleTicketAdapter(ArrayList<CheckingScrapModel> list, OnItemClickListener onItemClickListener) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    public void setList(ArrayList<CheckingScrapModel> list){
        this.list = list;
    }
    public ArrayList<CheckingScrapModel> getList(){
        return list;
    }
    @NonNull
    @Override
    public RejectedScaleTickitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kl_rejected, parent, false);
        RejectedScaleTickitViewHolder evh = new RejectedScaleTickitViewHolder(v, onItemClickListener);
        return evh;
    }
    @Override
    public void onBindViewHolder(@NonNull RejectedScaleTickitViewHolder holder, int position) {
        holder.txtMaPhieuCan.setText(list.get(position).getScaleTicketCode());
        holder.txtBienSoXe.setText(list.get(position).getVehicleNumber());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
