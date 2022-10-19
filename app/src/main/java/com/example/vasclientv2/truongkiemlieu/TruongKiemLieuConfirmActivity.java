package com.example.vasclientv2.truongkiemlieu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vasclientv2.R;
import com.example.vasclientv2.adapter.HistoryAdapter;
import com.example.vasclientv2.adapter.ScaleTicketPODetailPageAdapter;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.kiemlieu.kiemlieu.AddInfoKLActivity;
import com.example.vasclientv2.kiemlieu.kiemlieu.AddInfoKLViewModel;
import com.example.vasclientv2.kiemlieu.kiemlieu.FcmResponse;
import com.example.vasclientv2.kiemlieu.kiemlieu.TempProductForTicket;
import com.example.vasclientv2.message.DataMessage;
import com.example.vasclientv2.message.MesageModel;
import com.example.vasclientv2.model.common.AcctionMessage;
import com.example.vasclientv2.model.common.SingleResponeMessage;
import com.example.vasclientv2.model.common.TempTable;
import com.example.vasclientv2.model.entities.CheckingScrapDTO;
import com.example.vasclientv2.model.entities.HistoryModel;
import com.example.vasclientv2.model.entities.ProductModel;
import com.example.vasclientv2.model.entities.ScaleTicketModel;
import com.example.vasclientv2.model.entities.ScaleTicketPODetailModel;
import com.example.vasclientv2.model.entities.WarehouseModel;
import com.example.vasclientv2.ui.WareHouse;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TruongKiemLieuConfirmActivity extends AppCompatActivity {

    private static final String TAG = "TKiemLieuConfirm";

    private Toolbar toolbar;
    private TextView tv_MaPhieu, tv_GioVao, edtPlateNumber, edtTypeText, tv_KhoNhap;
    private TextView edt_TruKG, edt_TruPhanTram, soHieuCont1, soHieuCont2, edt_barge_number, edt_KhoNhap;
    private EditText edt_Note;
    private RadioButton rd_SaLan, rd_2Cont, rd_1Cont, rd_XeThuong, rd_20feet, rd_40feet;
    private RadioGroup rg_radionButton, rd_Container;
    private Button btnReject, btnSave;
    //    private View viewDisableLayout;
    // show loading khi lưu phiếu
    private ConstraintLayout disableView;
    private RecyclerView recyclerView;
    private AlertDialog dialogSuccessSave, dialogConfirmSave, dialogConfirmReject;
    // Data get from Intent
    private ScaleTicketPODetailPageAdapter scaleTicketPODetailPageAdapter;
    private HistoryAdapter historyAdapter;
    private ArrayList<ScaleTicketPODetailModel> items;
    private ArrayList<ProductModel> products;
    private ScaleTicketModel scaleTicket;
    private String rfid, maPhieu, gioVao, bsx, loaiXe, userIdKL;
    private WarehouseModel warehouse;

    // Token của kiểm liệu viên dùng để gửi thông báo khi bị reject
    private String receiver_token;

    //API
    private ApiInterface apiInterface;
    private Call<FcmResponse> callSendNoti;
    private Call<AcctionMessage> LuuKiemLieu;

    private ArrayList<TempProductForTicket> tempProductForTickets = new ArrayList<TempProductForTicket>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tkl_activity_confirm);

        //Tool Bar
        toolbar = findViewById(R.id.add_tkl_confirm_toolbar);
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
//        viewDisableLayout = findViewById(R.id.viewDisableLayout);
        disableView = findViewById(R.id.disableView);

        // Dialog Lưu thành công
        dialogSuccessSave = new AlertDialog.Builder(this).setNegativeButton("OK", (dialog, which) -> {
            finish();
        }).setCancelable(false).create();


        // Dialog xác nhận lưu phiếu kiểm liệu
        dialogConfirmSave = new AlertDialog.Builder(this)
                .setTitle("Xác nhận lưu")
                .setMessage("Phiếu kiểm liệu sẽ được lưu")
                .setPositiveButton("Lưu", (dialog, which) -> {
                    luuKiemLieu();
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                })
                .create();


        // Dialog xác nhận từ chối phiếu kiểm liệu
        dialogConfirmReject = new AlertDialog.Builder(this)
                .setTitle("Xác nhận từ chối")
                .setMessage("Phiếu kiểm liệu sẽ được gửi về lại cho kiểm liệu viên")
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    rejectKiemLieu();
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
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
        btnReject = findViewById(R.id.btnReject);
        btnSave = findViewById(R.id.btnSave);

        getDataFromIntent();


        showValueReceived();

        /**RecyclerView PO Detail*/
//        if (itemsHistory.size() > 0) {
//            tv_StatusThongBao.setVisibility(View.VISIBLE);
//        } else {
//            tv_StatusThongBao.setVisibility(View.GONE);
//        }
        recyclerView.setHasFixedSize(true);

        scaleTicketPODetailPageAdapter = new ScaleTicketPODetailPageAdapter(TruongKiemLieuConfirmActivity.this, products, tempProductForTickets, items, true, new ScaleTicketPODetailPageAdapter.OnItemClickListener() {
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
        ArrayList<ScaleTicketPODetailModel> POLine = new ArrayList<>();
//        for (int i = 0; i < POLine.size(); i++) {
//            items.add(POLine.get(i));
//        }
        scaleTicketPODetailPageAdapter.notifyDataSetChanged();
//
////        /**RecyclerView Status ThongBao*/
//        listStatusThongBao.setHasFixedSize(true);
//        historyAdapter = new HistoryAdapter(itemsHistory) {
//        };
//        listStatusThongBao.setAdapter(historyAdapter);
//        listStatusThongBao.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        listStatusThongBao.addItemDecoration(new DividerItemDecoration(listStatusThongBao.getContext(), 0));
//        List<HistoryModel> History = new ArrayList<>();
//        for (int i = 0; i < History.size(); i++) {
//            itemsHistory.add(History.get(i));
//        }
//        historyAdapter.notifyDataSetChanged();
//        //old
////        Data data = new Data();
////        data.RFID = RFID;
////        data.ScaleTicketPODetailList = items;
////        data.ScaleTicket = scaleTicket;
////        data.NumberCong = "";
////        data.container1Code = "";
////        data.container2Code = "";
////        data.TruKg = 0;
////        data.TruPhanTram = 0;
////        data.Note = note;
//

        // Show alert dialog xác nhận
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogConfirmSave.show();
            }
        });

        btnReject.setOnClickListener(v -> {
            dialogConfirmReject.show();
        });

        // Get token của kL để gửi thông báo khi bị reject
        getTokenKL2SendNoti(userIdKL);

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
        warehouse = (WarehouseModel) getIntent().getSerializableExtra("WAREHOUSE");


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

    /**
     * Xóa dữ liệu trong intent khi destroy activity
     */
    private void removeIntenData() {
        getIntent().removeExtra("RFID");
        getIntent().removeExtra("SCALE_TICKET");
        getIntent().removeExtra("SCALE_TICKET_CODE");
        getIntent().removeExtra("VEHICLE_NUMBER");
        getIntent().removeExtra("TYPE_TEXT");
        getIntent().removeExtra("IN_HOUR");
        getIntent().removeExtra("SCALE_TICKET_PODETAIL_LIST");
        getIntent().removeExtra("HISTORY");
        getIntent().removeExtra("DUYET_PKL");
        getIntent().removeExtra("PRODUCTS");
        getIntent().removeExtra("USER_ID_3");
        getIntent().removeExtra("WAREHOUSE");
    }

    private void rejectKiemLieu() {

//        viewDisableLayout.setVisibility(View.VISIBLE);
        showLoading(true);

        // Call api save data
        apiInterface = ApiService.getClient().create(ApiInterface.class);
        LuuKiemLieu = apiInterface.LuuKiemLieu(WareHouse.key, WareHouse.token, prepareData(), WareHouse.UserId, 2);
        try {
            LuuKiemLieu.enqueue(new Callback<AcctionMessage>() {

                @Override
                public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
                    if (response.body().getIsSuccess()) {
//                            Toast.makeText(v.getContext(), "Đã Lưu", Toast.LENGTH_LONG).show();
//                        viewDisableLayout.setVisibility(View.GONE);
                        showLoading(false);
                        sendNotify();
                        showDialogApproved("Từ chối thành công", response.body().getErr().getMsgString().toString());
                    } else {
//                        viewDisableLayout.setVisibility(View.GONE);
                        showLoading(false);
                        Toast.makeText(getApplicationContext(), response.body().getErr().getMsgString().toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AcctionMessage> call, Throwable t) {
//                    viewDisableLayout.setVisibility(View.GONE);
                    showLoading(false);
                    showDialogApproved("Lỗi", "Không lưu được, vui lòng thử lại!!!");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e + "");
//            viewDisableLayout.setVisibility(View.GONE);
            showLoading(false);
            showDialogApproved("Lỗi", "Không lưu được, vui lòng thử lại!!!");
        }
    }

    private void luuKiemLieu() {
//        viewDisableLayout.setVisibility(View.VISIBLE);
        showLoading(true);
        // Call api save data

        apiInterface = ApiService.getClient().create(ApiInterface.class);
        LuuKiemLieu = apiInterface.LuuKiemLieu(WareHouse.key, WareHouse.token, prepareData(), WareHouse.UserId, 4);

        try {
            LuuKiemLieu.enqueue(new Callback<AcctionMessage>() {

                @Override
                public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
                    if (response.body().getIsSuccess()) {
                        Toast.makeText(getApplicationContext(), "Đã Lưu", Toast.LENGTH_LONG).show();
//                        viewDisableLayout.setVisibility(View.GONE);
                        showLoading(false);
                        showDialogApproved("Lưu thành công", response.body().getErr().getMsgString().toString());
                    } else {
//                        viewDisableLayout.setVisibility(View.GONE);
                        showLoading(false);
                        Toast.makeText(getApplicationContext(), "Lưu không thành công, Vui lòng thử lại", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AcctionMessage> call, Throwable t) {
//                    viewDisableLayout.setVisibility(View.GONE);
                    showLoading(false);
                    showDialogApproved("Lối", "Không lưu được, vui lòng thử lại!!!");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e + "");
//            viewDisableLayout.setVisibility(View.GONE);
            showLoading(false);
            showDialogApproved("Lối", "Không lưu được, vui lòng thử lại!!!");
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
        String dateTemp = temp.toString();
        //long utcMillisecond = Long.parseLong(InHour.substring(InHour.indexOf("(") + 1, InHour.indexOf(")")));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        tv_GioVao.setText(DateFormat.format("HH:mm - dd/MM/yyyy", temp));
        edtPlateNumber.setText(bsx);
        edtTypeText.setText(loaiXe);
        edt_Note.setText(scaleTicket.getDescription());
        edt_TruKG.setText(scaleTicket.getKgReduced().intValue() + "");
        edt_TruPhanTram.setText(scaleTicket.getPercentReduced() + "");
        if (warehouse != null){
            edt_KhoNhap.setText(warehouse.getWareHouseName());
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

    private void showDialogApproved(String title, String message) {
        dialogSuccessSave.setTitle(title);
        dialogSuccessSave.setMessage(message);
        dialogSuccessSave.show();
    }

    private CheckingScrapDTO prepareData() {
        CheckingScrapDTO checkingScrapDTO = new CheckingScrapDTO();
        checkingScrapDTO.setNumberCong("0"); // Số lượng cont
        checkingScrapDTO.setContainer1Code("");
        checkingScrapDTO.setContainer2Code("");

        if (rd_SaLan.isChecked()) {// checked
            scaleTicket.setVehicleTypeCode("S");
            scaleTicket.setBargeNumber(edt_barge_number.getText().toString());
            //scaleTicket.setTrailersNumber("");
            scaleTicket.setIs20Feet(false);
            scaleTicket.setIs40Feet(false);
            scaleTicket.setSoHieuCont1("");
            scaleTicket.setSoHieuCont2("");

            checkingScrapDTO.setIs20Feet(false);
            checkingScrapDTO.setVehicleCode("S");
        } else if (rd_2Cont.isChecked()) { //checked
            scaleTicket.setVehicleTypeCode("C");
            scaleTicket.setBargeNumber("");
//            scaleTicket.setTrailersNumber("");
            scaleTicket.setIs20Feet(true);
            scaleTicket.setIs40Feet(false);
            scaleTicket.setSoHieuCont1(soHieuCont1.getText().toString());
            scaleTicket.setSoHieuCont2(soHieuCont2.getText().toString());
            scaleTicket.setContainerCount("2");

            //

            checkingScrapDTO.setIs20Feet(true);
            checkingScrapDTO.setNumberCong("2"); // Số lượng cont
            checkingScrapDTO.setVehicleCode("C");
            checkingScrapDTO.setContainer1Code(soHieuCont1.getText().toString());
            checkingScrapDTO.setContainer2Code(soHieuCont2.getText().toString());

        } else if (rd_1Cont.isChecked()) {
            scaleTicket.setVehicleTypeCode("C");
            scaleTicket.setBargeNumber("");
//            scaleTicket.setTrailersNumber("");
            if (rd_20feet.isChecked()) {
                scaleTicket.setIs20Feet(true);
                scaleTicket.setIs40Feet(false);
                checkingScrapDTO.setIs20Feet(true);
            } else {
                scaleTicket.setIs20Feet(false);
                scaleTicket.setIs40Feet(true);
                checkingScrapDTO.setIs20Feet(false);

            }
            scaleTicket.setSoHieuCont1(soHieuCont1.getText().toString());
            scaleTicket.setSoHieuCont2("");
            scaleTicket.setContainerCount("1");

            checkingScrapDTO.setNumberCong("1"); // Số lượng cont
            checkingScrapDTO.setVehicleCode("C");
            checkingScrapDTO.setContainer1Code(soHieuCont1.getText().toString());
        } else {
            scaleTicket.setVehicleTypeCode("T");
            scaleTicket.setBargeNumber("");
            scaleTicket.setTrailersNumber("");
            scaleTicket.setIs20Feet(false);
            scaleTicket.setIs40Feet(false);
            scaleTicket.setSoHieuCont1("");
            scaleTicket.setSoHieuCont2("");

            checkingScrapDTO.setIs20Feet(false);
            checkingScrapDTO.setVehicleCode("T");
        }

        checkingScrapDTO.setPhanBoData(items);
        checkingScrapDTO.setScaleTicket(scaleTicket);
        checkingScrapDTO.setRFID(rfid);
        try {
            checkingScrapDTO.setTruKg(Integer.parseInt(edt_TruKG.getText().toString()));
        } catch (Exception e) {
            checkingScrapDTO.setTruKg(0);
        }

        try {
            checkingScrapDTO.setTruPhanTram(BigDecimal.valueOf(Float.parseFloat(edt_TruPhanTram.getText().toString())));
        } catch (Exception e) {
            checkingScrapDTO.setTruPhanTram(BigDecimal.valueOf(0));
        }

//        checkingScrapDTO.setTruKg(Integer.parseInt(edt_TruKG.getText().toString()));
//        checkingScrapDTO.setTruPhanTram(BigDecimal.valueOf(Float.parseFloat(edt_TruPhanTram.getText().toString())));
        checkingScrapDTO.setNote(edt_Note.getText().toString().replaceAll("\n", ", "));
        checkingScrapDTO.setEdit(true);
        checkingScrapDTO.setDuyetPKL(false);// trường này gửi xuống cho vui thôi

        return checkingScrapDTO;
    }

    /**
     * Gửi notification cho kiểm liệu
     */
    private void sendNotify() {
        DataMessage dataMessage = new DataMessage();
        dataMessage.setTitle("Có phiếu kiểm liệu bị từ chối");
        dataMessage.setMessage("Phiếu kiểm liệu xe " + bsx + " bị từ chối");
        dataMessage.setVerhicle_number(bsx);
        dataMessage.setReciver_token("");
        dataMessage.setSender_id(WareHouse.UserId);
        dataMessage.setRfid(rfid);

        MesageModel mesageModel = new MesageModel();
        mesageModel.setTo(receiver_token);
        mesageModel.setData(dataMessage);

        apiInterface = ApiService.getClientFcm().create(ApiInterface.class);
        callSendNoti = apiInterface.sendNotifycation(WareHouse.contentType, WareHouse.serverKey, mesageModel);
        // Send notification to Truong kiem lieu
        try {
            callSendNoti.enqueue(new Callback<FcmResponse>() {
                @Override
                public void onResponse(Call<FcmResponse> call, Response<FcmResponse> response) {
                    Log.d("TAG", response.toString());
                }

                @Override
                public void onFailure(Call<FcmResponse> call, Throwable t) {
                    Log.d("TAG", t + "");
                }
            });
        } catch (Exception e) {
            Log.d("TAG", e + "");
        }
    }

    private void getTokenKL2SendNoti(String userId) {
        if (userId != null) {
            apiInterface = ApiService.getClient().create(ApiInterface.class);
            Call<SingleResponeMessage<TempTable>> call = apiInterface.GetTokenForNoti(WareHouse.key, WareHouse.token, userId, WareHouse.groupUser);
            call.enqueue(new Callback<SingleResponeMessage<TempTable>>() {
                @Override
                public void onResponse(Call<SingleResponeMessage<TempTable>> call, Response<SingleResponeMessage<TempTable>> response) {
                    try {
                        if (response.body().getIsSuccess()) {
                            receiver_token = response.body().getItem().getTokenKL();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e + "");
                    }
                }

                @Override
                public void onFailure(Call<SingleResponeMessage<TempTable>> call, Throwable t) {

                }
            });
        }
    }

    private void showLoading(Boolean isShow) {
        if (isShow)
            disableView.setVisibility(View.VISIBLE);
        else
            disableView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        removeIntenData();
    }
}