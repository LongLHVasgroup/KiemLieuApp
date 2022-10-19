package com.example.vasclientv2.truongkiemlieu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vasclientv2.R;
import com.example.vasclientv2.adapter.HistoryAdapter;
import com.example.vasclientv2.adapter.ScaleTicketPODetailPageAdapter;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.kiemlieu.kiemlieu.FcmResponse;
import com.example.vasclientv2.kiemlieu.kiemlieu.TempProductForTicket;
import com.example.vasclientv2.model.common.AcctionMessage;
import com.example.vasclientv2.model.entities.CheckingScrapDTO;
import com.example.vasclientv2.model.entities.ProductModel;
import com.example.vasclientv2.model.entities.ScaleTicketModel;
import com.example.vasclientv2.model.entities.ScaleTicketPODetailModel;
import com.example.vasclientv2.model.entities.WarehouseModel;
import com.example.vasclientv2.ui.WareHouse;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DestroyPhieuKLActivity extends AppCompatActivity {

    private static final String TAG = "DestroyPhieuKLActivity";

    private Toolbar toolbar;
    private TextView tv_MaPhieu, tv_GioVao, edtPlateNumber, edtTypeText, tv_KhoNhap;
    private TextView edt_TruKG, edt_TruPhanTram, soHieuCont1, soHieuCont2, edt_barge_number, edt_Note, edt_KhoNhap;
    private RadioButton rd_SaLan, rd_2Cont, rd_1Cont, rd_XeThuong, rd_20feet, rd_40feet;
    private RadioGroup rg_radionButton, rd_Container;
    private Button btnDestroy;
    // show loading khi lưu phiếu
    private ConstraintLayout disableView;
    private RecyclerView recyclerView;
    private AlertDialog dialogSuccessSave, dialogConfirmDestroy, dialogFailDestroy;
    // Data get from Intent
    private ScaleTicketPODetailPageAdapter scaleTicketPODetailPageAdapter;
//    private HistoryAdapter historyAdapter;
    private ArrayList<ScaleTicketPODetailModel> items;
    private ArrayList<ProductModel> products;
    private ScaleTicketModel scaleTicket;
    private String rfid, maPhieu, gioVao, bsx, loaiXe, userIdKL, scaleTicketMobileId;

    private WarehouseModel selectedWarehouse;

    // Token của kiểm liệu viên dùng để gửi thông báo khi bị reject
//    private String receiver_token;

    //API
    private ApiInterface apiInterface;
//    private Call<FcmResponse> callSendNoti;
    private Call<AcctionMessage> DestroyKiemLieu;

    private ArrayList<TempProductForTicket> tempProductForTickets = new ArrayList<TempProductForTicket>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destroy_phieu_kl_activity);


        toolbar = findViewById(R.id.add_tkl_destroy_toolbar);
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

        disableView = findViewById(R.id.disableView);

        // Dialog Hủy kiểm liệu thành công
        dialogSuccessSave = new AlertDialog.Builder(this).setNegativeButton("OK", (dialog, which) -> {
            finish();
        }).setCancelable(false).create();


        // Dialog thông báo hủy phiếu không thành công
        dialogFailDestroy = new AlertDialog.Builder(this).setNegativeButton("OK", (dialog, which) -> {
        }).setCancelable(false).create();

        // Dialog xác nhận Hủy phiếu kiểm liệu
        dialogConfirmDestroy = new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy phiếu")
                .setMessage("Phiếu kiểm liệu sẽ được hủy")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
//                    luuKiemLieu();
                    destroyKl();
                })
                .setNegativeButton("Không", (dialog, which) -> {
                })
                .create();


        tv_MaPhieu = findViewById(R.id.tv_MaPhieu);
        tv_GioVao = findViewById(R.id.tv_GioVao);
        edtPlateNumber = findViewById(R.id.edtPlateNumber);
        edtTypeText = findViewById(R.id.edtTypeText);
        edt_TruKG = findViewById(R.id.edt_TruKG);
        edt_TruPhanTram = findViewById(R.id.edt_TruPhanTram);
        edt_Note = findViewById(R.id.edt_Note);
        recyclerView = findViewById(R.id.ListVatTu);
        soHieuCont1 = findViewById(R.id.so_hieu_cont_1);
        soHieuCont2 = findViewById(R.id.so_hieu_cont_2);
        edt_barge_number = findViewById(R.id.barge_number);
        tv_KhoNhap = findViewById(R.id.tv_KhoNhap);
        edt_KhoNhap = findViewById(R.id.edt_KhoNhap);

        /**RadioButton*/
        rd_SaLan = findViewById(R.id.rd_SaLan);
        rd_1Cont = findViewById(R.id.rd_1Cont);
        rd_2Cont = findViewById(R.id.rd_2Cont);
        rd_XeThuong = findViewById(R.id.rd_XeThuong);
        rg_radionButton = findViewById(R.id.rg_radionButton);
        rd_Container = findViewById(R.id.rd_Container);
        rd_20feet = findViewById(R.id.rd_20feet);
        rd_40feet = findViewById(R.id.rd_40feet);

        rg_radionButton.setOnCheckedChangeListener((group, checkedId) -> {
            int id = group.getCheckedRadioButtonId();
            switch (id) {
                case R.id.rd_SaLan:
                    soHieuCont1.setVisibility(View.GONE);
                    soHieuCont2.setVisibility(View.GONE);
                    rd_Container.setVisibility(View.GONE);
                    edt_barge_number.setVisibility(View.VISIBLE);
                    break;
                case R.id.rd_XeThuong:
                    soHieuCont1.setVisibility(View.GONE);
                    soHieuCont2.setVisibility(View.GONE);
                    rd_Container.setVisibility(View.GONE);
                    edt_barge_number.setVisibility(View.GONE);
                    break;
                case R.id.rd_1Cont:
                    soHieuCont2.setVisibility(View.GONE);
                    soHieuCont1.setVisibility(View.VISIBLE);
                    rd_Container.setVisibility(View.VISIBLE);
                    edt_barge_number.setVisibility(View.GONE);
                    break;
                case R.id.rd_2Cont:
                    soHieuCont1.setVisibility(View.VISIBLE);
                    soHieuCont2.setVisibility(View.VISIBLE);
                    rd_Container.setVisibility(View.GONE);
                    edt_barge_number.setVisibility(View.GONE);
                    break;
                default:

                    break;
            }
        });
        /**Button*/
        btnDestroy = findViewById(R.id.btnDestroy);


        getDataFromIntent();


        showValueReceived();


        scaleTicketPODetailPageAdapter = new ScaleTicketPODetailPageAdapter(DestroyPhieuKLActivity.this, products, tempProductForTickets, items, true, new ScaleTicketPODetailPageAdapter.OnItemClickListener() {
            @Override
            public void OnItemRemove(int position) {

            }

            @Override
            public void OnItemSelectPercent(int position, int value) {

            }

            @Override
            public void OnItemSelectVatTu(int position, String tenVatTu) {

            }

            @Override
            public void OnItemCheckedDiffName(int position, Boolean isChecked) {

            }

            @Override
            public void OnItemChangeProductName(int position, String productName) {

            }
        });
        recyclerView.setAdapter(scaleTicketPODetailPageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL));

        btnDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogConfirmDestroy.show();
            }
        });


    }


    /**
     * Lấy dữ liệu từ intent
     */
    private void getDataFromIntent() {
        products = (ArrayList<ProductModel>) getIntent().getSerializableExtra("PRODUCTS");
        items = (ArrayList<ScaleTicketPODetailModel>) getIntent().getSerializableExtra("SCALE_TICKET_PODETAIL_LIST");
        scaleTicket = (ScaleTicketModel) getIntent().getSerializableExtra("SCALE_TICKET");
//        isDaDuyetPKL = getIntent().getBooleanExtra("DUYET_PKL", false);
        rfid = getIntent().getStringExtra("RFID");
        maPhieu = getIntent().getStringExtra("SCALE_TICKET_CODE");
        gioVao = getIntent().getStringExtra("IN_HOUR");
        bsx = getIntent().getStringExtra("VEHICLE_NUMBER");
        loaiXe = getIntent().getStringExtra("TYPE_TEXT");
        userIdKL = getIntent().getStringExtra("USER_ID_3");
        scaleTicketMobileId = getIntent().getStringExtra("SCALE_TICKET_MOBILE_ID");
        selectedWarehouse = (WarehouseModel) getIntent().getSerializableExtra("WAREHOUSE");


        for (ScaleTicketPODetailModel scaleTicketPODetailModel : items) {
            tempProductForTickets.add(new TempProductForTicket(scaleTicketPODetailModel.getProductCode()
                    , scaleTicketPODetailModel.getProductName()
                    , isDiffProductName(scaleTicketPODetailModel.getProductCode(), scaleTicketPODetailModel.getProductName())));
        }
    }

    private Boolean isDiffProductName(String productCodeOfItem, String productNameOfItem) {
        for (ProductModel productModel : products) {
            if (productCodeOfItem.equals(productModel.getProductCode()) && !productNameOfItem.equals(productModel.getProductName())) {
                return true;
            }
        }
        return false;
    }


    // gửi thông báo hủy phiếu kiểm liệu
    private void destroyKl() {
        showLoading(true);
        // Call api save data

        apiInterface = ApiService.getClient().create(ApiInterface.class);
        DestroyKiemLieu = apiInterface.DestroyPhieuKL(WareHouse.key, WareHouse.token, scaleTicketMobileId);
        try {
            DestroyKiemLieu.enqueue(new Callback<AcctionMessage>() {

                @Override
                public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
                    if (response.body().getIsSuccess()) {
//                        Toast.makeText(getApplicationContext(), "Hủy phiếu thành công", Toast.LENGTH_LONG).show();
                        showLoading(false);
                        showDialogResult("Hủy phiếu thành công", response.body().getErr().getMsgString().toString());
                    } else {
                        showLoading(false);
//                        Toast.makeText(getApplicationContext(), response.body().getErr().getMsgString().toString(), Toast.LENGTH_LONG).show();
                        showDialogFail("Hủy phiếu không thành công", response.body().getErr().getMsgString().toString());
                    }
                }

                @Override
                public void onFailure(Call<AcctionMessage> call, Throwable t) {
                    showLoading(false);
                    showDialogFail("Lỗi", "Không lưu được, vui lòng thử lại!!!");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e + "");
            showLoading(false);
            showDialogResult("Lỗi", "Không lưu được, vui lòng thử lại!!!");
        }

    }

    private void showValueReceived() {
        tv_MaPhieu.setText(maPhieu);
        String date = gioVao.replaceAll("T", " ");
        Date temp = new Date();
        try {
            temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS")
                    .parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tv_GioVao.setText(DateFormat.format("HH:mm - dd/MM/yyyy", temp));
        edtPlateNumber.setText(bsx);
        edtTypeText.setText(loaiXe);
        edt_Note.setText(scaleTicket.getDescription());
        edt_TruKG.setText(scaleTicket.getKgReduced().intValue() + "");
        edt_TruPhanTram.setText(scaleTicket.getPercentReduced() + "");

        if(selectedWarehouse != null){
            edt_KhoNhap.setText(selectedWarehouse.getWareHouseName());
        }
        // Set mục xe
        try {
            if (scaleTicket.getVehicleTypeCode().equals("S")) {
                // Sà lang
                rd_SaLan.setChecked(true);
                edt_barge_number.setText(scaleTicket.getBargeNumber().toString());

            } else if (scaleTicket.getVehicleTypeCode().equals("T")) {
                // Xe thường
                rd_XeThuong.setChecked(true);

            } else if (scaleTicket.getVehicleTypeCode().equals("C")) {
                // Xe container
                if (scaleTicket.getContainerCount().equals("1")) {
                    // Xe 1 container
                    rd_1Cont.setChecked(true);
                    soHieuCont1.setText(scaleTicket.getSoHieuCont1());
                    if (scaleTicket.getIs20Feet()) {
                        rd_20feet.setChecked(true);
                    } else if (scaleTicket.getIs40Feet()) {
                        rd_40feet.setChecked(true);
                    }
                } else {
                    // Xe 2 container
                    rd_2Cont.setChecked(true);
                    soHieuCont1.setText(scaleTicket.getSoHieuCont1());
                    soHieuCont2.setText(scaleTicket.getSoHieuCont2());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e + "");
        }

    }

    private void showDialogResult(String title, String message) {
        dialogSuccessSave.setTitle(title);
        dialogSuccessSave.setMessage(message);
        dialogSuccessSave.show();
    }

    private void showDialogFail(String title, String message) {
        dialogFailDestroy.setTitle(title);
        dialogFailDestroy.setMessage(message);
        dialogFailDestroy.show();
    }


    private void showLoading(Boolean isShow) {
        if (isShow)
            disableView.setVisibility(View.VISIBLE);
        else
            disableView.setVisibility(View.GONE);
    }

}