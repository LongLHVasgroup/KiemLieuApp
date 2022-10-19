package com.example.vasclientv2.admin.resetpass;

import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.vasclientv2.R;
import com.example.vasclientv2.adapter.CheckingScrapAdapter;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.kiemlieu.rejected.RejectedKLViewModel;
import com.example.vasclientv2.model.common.AcctionMessage;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.common.TempTable;
import com.example.vasclientv2.model.entities.CheckingScrapModel;
import com.example.vasclientv2.model.entities.UserModel;
import com.example.vasclientv2.ui.WareHouse;

import java.util.ArrayList;

import retrofit2.Call;

public class ResetPassUserActivity extends AppCompatActivity implements UserAdapter.OnItemClickListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<UserModel> list = new ArrayList<>();

    private ResetPassUserViewModel viewModel;
    private ProgressBar progressBar;
    private Call<ListResponeMessage<UserModel>> callApiGetAllUser;
    private Call<AcctionMessage> callResetUser;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass_user);

        setupControlls();

        setupEvents();

        viewModel =
                new ViewModelProvider(this).get(ResetPassUserViewModel.class);

        viewModel.getListUser().observe(this, new Observer<ArrayList<UserModel>>() {
            @Override
            public void onChanged(ArrayList<UserModel> userModels) {
                list = userModels;
                userAdapter.setListUser(userModels);
                userAdapter.notifyDataSetChanged();
            }
        });
        viewModel.getIsSuccessReset().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null) {
                    if (aBoolean) {
                        Toast.makeText(ResetPassUserActivity.this, "Đặt lại mật khẩu thành công", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(ResetPassUserActivity.this, "Đặt lại mật khẩu không thành công, vui lòng thử lại", Toast.LENGTH_LONG).show();
                }

            }
        });

        // get data List User

        viewModel.getData(callApiGetAllUser);

        // check get data is Success

        viewModel.getIsSuccess().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null) {
//                    if (aBoolean){
                    progressBar.setVisibility(View.GONE);
//                    }
                }
            }
        });

    }

    private void setupEvents() {

        // Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupControlls() {

        //Api
        apiInterface = ApiService.getClient().create(ApiInterface.class);
        callApiGetAllUser = apiInterface.GetListUser(WareHouse.key, WareHouse.token, WareHouse.UserId);

        //Tool Bar
        toolbar = findViewById(R.id.add_info_kl_toolbar);
        recyclerView = findViewById(R.id.rcv_list_user);
        recyclerView.setHasFixedSize(true);

        //ProgressBar
        progressBar = findViewById(R.id.loading);
        // RecyclerView
        layoutManager = new LinearLayoutManager(this);
        userAdapter = new UserAdapter(list, this);
        userAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(userAdapter);

    }

    @Override
    public void onItemClick(int position) {

        /**
         * Nguy hiểm không được bấm bậy
         *
         */
        Log.e("ListUser", list.toString());

        AlertDialog alertDialog = new AlertDialog.Builder(ResetPassUserActivity.this)
                .setTitle("Xác nhận Reset mật khẩu")
                .setMessage("Mật khẩu của " + list.get(position).getFullName() + " sẽ đươc reset")
                .setPositiveButton("OK", (dialog, which) -> {
                    approvedReset(position);
                }).setNegativeButton("Hủy", (dialog, which) -> {

                }).show();
    }

    private void approvedReset(int position) {
        TempTable tempTable = new TempTable();
        tempTable.setUserID(list.get(position).getUserId());
        callResetUser = apiInterface.ResetPassword(WareHouse.key, WareHouse.token, WareHouse.UserId, tempTable);
        viewModel.resetUser(callResetUser);
    }


}