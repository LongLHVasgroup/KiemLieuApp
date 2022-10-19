package com.example.vasclientv2.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasclientv2.R;
import com.example.vasclientv2.model.entities.CheckingScrapModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CheckingScrapAdapter extends RecyclerView.Adapter<CheckingScrapAdapter.CheckingScrapViewHolder> {
    private ArrayList<CheckingScrapModel> list;
    private OnItemClickListener mlistener;

    public static class CheckingScrapViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtStt,txtPlate,txtPort, txtInHour, txtStatus;
        OnItemClickListener listener;
        public CheckingScrapViewHolder(@NonNull View itemView,OnItemClickListener listener) {
            super(itemView);
            txtStt=itemView.findViewById(R.id.txtStt);
            txtPlate =itemView.findViewById(R.id.txtPlate);
            txtPort = itemView.findViewById(R.id.txtPort);
            txtInHour = itemView.findViewById(R.id.txtInHour);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            this.listener=listener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition());
        }
    }
    public CheckingScrapAdapter(ArrayList<CheckingScrapModel> list,OnItemClickListener mlistener)
    {
        this.mlistener=mlistener;
        this.list=list;
    }

    public void setList(ArrayList<CheckingScrapModel> list)
    {
        this.list=list;
    }
    @NonNull
    @Override
    //gắn customview vào
    public CheckingScrapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tkl_item_scale,parent,false);
        CheckingScrapViewHolder evh = new CheckingScrapViewHolder(view,mlistener);
        return evh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    //set từng giá trị lên từng item custom
    public void onBindViewHolder(@NonNull CheckingScrapViewHolder holder, int position) {
        CheckingScrapModel currentItem = list.get(position);
//        holder.txtStt.setText(currentItem.getStt().toString());
        holder.txtStt.setText(position+1+"");
        holder.txtPlate.setText(currentItem.getVehicleNumber());
        holder.txtPort.setText(currentItem.getGateName());
//        2021-01-26T13:24:56.987 ==>    2021/01/26 13:24
        String time = currentItem.getInHourGuard().replaceAll("T"," ").substring(0,16).replaceAll("-","/");
        holder.txtInHour.setText(time);
        holder.txtStatus.setText(vehicleStatus(currentItem));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener{
        void  onItemClick(int position);
    }

    private String vehicleStatus(CheckingScrapModel model){
        switch (model.getStep()){
            case 1:
                return "Chờ cân L1";
            case 2:
                return "Đã cân L1 -> Chờ kiểm liệu";
            case 3:
                return "Kiểm liệu chờ duyệt";
            case 4:
                return "Đã kiểm liệu -> Chờ cân L2";
            case 5:
                return "Đã cân L2";
        }
        return model.getStep()+"";
    }
}
