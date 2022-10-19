package com.example.vasclientv2.admin.resetpass;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasclientv2.R;
import com.example.vasclientv2.model.entities.UserModel;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<UserModel> listUser;
    private OnItemClickListener listener;

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        UserViewHolder userViewHolder = new UserViewHolder(v,listener);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        UserModel model = listUser.get(position);
        holder.txtFullName.setText(model.getFullName());
        holder.txtRole.setText(model.getRoldCode());
        holder.txtGroup.setText(model.getGroupUser());
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtFullName, txtRole, txtGroup;
        private OnItemClickListener onItemClickListener;

        public UserViewHolder(@NonNull View itemView, OnItemClickListener itemClickListener) {
            super(itemView);
            this.onItemClickListener = itemClickListener;
            txtFullName = itemView.findViewById(R.id.txtFullName);
            txtRole = itemView.findViewById(R.id.txt_role);
            txtGroup = itemView.findViewById(R.id.txt_group);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public UserAdapter(ArrayList<UserModel> listUser, OnItemClickListener itemClickListener) {
        this.listUser = listUser;
        this.listener = itemClickListener;
    }

    public void setListUser(ArrayList<UserModel> listUser) {
        this.listUser = listUser;
    }
}
