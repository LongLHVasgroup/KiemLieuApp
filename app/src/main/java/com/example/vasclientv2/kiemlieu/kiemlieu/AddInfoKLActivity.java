package com.example.vasclientv2.kiemlieu.kiemlieu;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.vasclientv2.R;
import com.example.vasclientv2.adapter.ScaleTicketPODetailPageAdapter;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.message.DataMessage;
import com.example.vasclientv2.message.MesageModel;
import com.example.vasclientv2.model.common.AcctionMessage;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.common.SingleResponeMessage;
import com.example.vasclientv2.model.common.TempTable;
import com.example.vasclientv2.model.entities.BargeModel;
import com.example.vasclientv2.model.entities.CheckingScrapDTO;
import com.example.vasclientv2.model.entities.HistoryModel;
import com.example.vasclientv2.model.entities.ProductModel;
import com.example.vasclientv2.model.entities.ScaleTicketModel;
import com.example.vasclientv2.model.entities.ScaleTicketPODetailModel;
import com.example.vasclientv2.model.entities.WarehouseModel;
import com.example.vasclientv2.ui.TransferData;
import com.example.vasclientv2.ui.WareHouse;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddInfoKLActivity extends AppCompatActivity {

    private static final String TAG = "AddInfoKLActivity";
    private Toolbar toolbar;
    private TextView tv_MaPhieu, tv_GioVao, edtPlateNumber, edtTypeText;
    private EditText edt_TruKG, edt_TruPhanTram, edt_Note, soHieuCont1, soHieuCont2;
    private AutoCompleteTextView edt_barge_number;
    private AutoCompleteTextView edt_warehouse;
    private RadioButton rd_SaLan, rd_2Cont, rd_1Cont, rd_XeThuong, rd_20feet, rd_40feet;
    private RadioGroup rg_radionButton, rd_Container;
    private Button btnAddNew, btnSaveTemp, btnSave, btnSaveAsTKL, btnSwapWarehouse;
    //    private View viewDisableLayout;
    private ConstraintLayout disableView;
    private RecyclerView recyclerView, listStatusThongBao;
    private AlertDialog dialogConfirmAddItem, dialogConfirmSave, dialogConfirmSaveAsTKL, dialogConfirmSwapWarehouse;
    private AlertDialog dialogSuccessSave, dialogFailSave;
    private ScaleTicketPODetailPageAdapter scaleTicketPODetailPageAdapter;

    private List<String> listBarge = new ArrayList<>();
    //    private List<String> listWarehouseName = new ArrayList<>();
//    private List<String> listWarehouseCode = new ArrayList<>();
    private List<WarehouseModel> listWarehouse = new ArrayList<>();
    private ArrayAdapter<String> listBargeAdapter;
    private ArrayAdapter<WarehouseModel> listWarehouseAdapter;


    private WarehouseModel selectedWarehouse;

    //    private HistoryAdapter historyAdapter;


    // value get from intent
    private ArrayList<ScaleTicketPODetailModel> items;
    private ArrayList<ProductModel> products;
    private ScaleTicketModel scaleTicket;
    private String rfid;
    private String maPhieuCan, gioVao, bsx, loaiXe;
    private boolean isDaDuyetPKL;


    //token người nhận thông báo-> trưởng kiểm liệu
    private String receiver_token;

    //API
    private ApiInterface apiInterface;
    private Call<FcmResponse> callSendNoti;

    private Call<AcctionMessage> LuuKiemLieu;
    private Call<AcctionMessage> SwapWareHouse; // Lê Hoàng Long
    private ArrayList<HistoryModel> itemsHistory;
    private AddInfoKLViewModel addInfoKLViewModel;

    // Dùng lưu giá trị PoNumber khi nhận được qua API
//    private String poNumber;
    // Dùng lưu giá trị PoLine nhận được từ api-> từ đó tự tăng Poline lên theo bước nhảy là 10, VD: 00000->00010
    private int defaultPOLine;

    // Tạo một array tạm đê lưu mảng  line itemp, dùng mảng này để biết tên vật tư có thay đổi so với mặc định hay không, để gửi mã vật tư đúng với tên vật tư được KL đổi tên
    private ArrayList<TempProductForTicket> tempProductForTickets = new ArrayList<TempProductForTicket>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiemlieu_themthongtinkl);

        //Tool Bar
        toolbar = findViewById(R.id.add_info_kl_toolbar);
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

        // Khai báo các đối tượng trên màn hình
        createControll();


        // Dialog Lưu thành công
        dialogSuccessSave = new AlertDialog.Builder(this).setNegativeButton("OK", (dialog, which) -> {
            finish();
        }).create();

        // Dialog Lưu Lỗi
        dialogFailSave = new AlertDialog.Builder(this).setNegativeButton("OK", (dialog, which) -> {
        }).create();

        // Dialog xác nhận lưu và gửi thông báo cho trưởng kiểm liệu
        dialogConfirmSave = new AlertDialog.Builder(this)
                .setTitle("Xác nhận lưu")
                .setMessage("Phiếu kiểm liệu sẽ được lưu và gửi thông báo cho trưởng kiểm liệu")
                .setPositiveButton("Lưu", (dialog, which) -> {
                    processSave();
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                })
                .create();

        dialogConfirmSaveAsTKL = new AlertDialog.Builder(this)
                .setTitle("Xác nhận lưu")
                .setMessage("Phiếu kiểm liệu sẽ được lưu")
                .setPositiveButton("Lưu", (dialog, which) -> {
                    processSaveAsTKL();
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                })
                .create();

        //Le Hoang Long dialog xác nhận chuyển kho
        dialogConfirmSwapWarehouse = new AlertDialog.Builder(this)
                .setTitle("Xác nhận đổi kho")
                .setMessage("Xe sẽ được đẩy ra hệ thống và cân lại")
                .setPositiveButton("Chuyển kho", (dialog,which) ->{
                    processSwapWareHouse();

                    Log.e("VehicleRegisterMobileModelID","AVD");
                    Log.i("Trang thai chuyen kho","Chuyen kho thanh cong");
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                })
                .create();

        dialogConfirmAddItem = new AlertDialog.Builder(this).setTitle("Xác nhân thêm vật tư").setPositiveButton("Xác nhận", (dialog, which) -> {
            addTempItem();
        }).setNegativeButton("Hủy", (dialog, which) -> {
        }).create();

        addInfoKLViewModel = new ViewModelProvider(this).get(AddInfoKLViewModel.class);
        addInfoKLViewModel.getTruKG().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!s.isEmpty()) {
                    edt_TruKG.setText(s);
                }
            }
        });
        addInfoKLViewModel.getTruPhanTram().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!s.isEmpty()) {
                    edt_TruPhanTram.setText(s);
                }
            }
        });
        addInfoKLViewModel.getEnableTruKg().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null) {
                    edt_TruKG.setEnabled(aBoolean);
                    if (aBoolean) {
                        edt_TruKG.setTextColor(getResources().getColor(R.color.black));
                    } else {
                        edt_TruKG.setTextColor(getResources().getColor(R.color.disable_text));
                    }
                }
            }
        });

        addInfoKLViewModel.getEnableTruPercent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null) {
                    edt_TruPhanTram.setEnabled(aBoolean);
                    if (aBoolean) {
                        edt_TruPhanTram.setTextColor(getResources().getColor(R.color.black));
                    } else {
                        edt_TruPhanTram.setTextColor(getResources().getColor(R.color.disable_text));
                    }
                }
            }
        });


        rg_radionButton.setOnCheckedChangeListener((group, checkedId) -> {
            int id = group.getCheckedRadioButtonId();
            switch (id) {
                case R.id.rd_SaLan:
                    getListBarge();
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
//                    Toast.makeText(AddInfoKLActivity.this, "Xe 1 container", Toast.LENGTH_SHORT).show();
                    soHieuCont2.setVisibility(View.GONE);
                    soHieuCont1.setVisibility(View.VISIBLE);
                    rd_Container.setVisibility(View.VISIBLE);
                    edt_barge_number.setVisibility(View.GONE);
                    break;
                case R.id.rd_2Cont:
//                    Toast.makeText(AddInfoKLActivity.this, "Xe 2 container", Toast.LENGTH_SHORT).show();
                    soHieuCont1.setVisibility(View.VISIBLE);
                    soHieuCont2.setVisibility(View.VISIBLE);
                    rd_Container.setVisibility(View.GONE);
                    edt_barge_number.setVisibility(View.GONE);
                    break;
                default:

                    break;
            }
        });


        edt_barge_number.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = edt_barge_number.getText().toString();

                if (listBarge.size() > 0) {
                    // Trường hợp người dùng nhập tiếp dựa trên 3 ký tự đầu thì chỉ cần lọc lại kết quả của lần trước
                    // update lại adapter
                    ArrayList<String> strings = new ArrayList<>();
                    for (String s1 : listBarge) {
                        String s2 = s1.replaceAll("\\.", "");
                        if (s2.contains(value)) {
                            strings.add(s1);
                        }
                    }
                    listBargeAdapter = new ArrayAdapter<String>(getApplication(), android.R.layout.simple_dropdown_item_1line, strings);
                    edt_barge_number.setAdapter(listBargeAdapter);
                    listBargeAdapter.notifyDataSetChanged();
                }
            }
        });


        edt_warehouse.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = edt_warehouse.getText().toString();
                if (value.length() >= 3) {
                    // Nếu nhập từ 3 ký tự
                    getListWarehouse(value);
                }
            }
        });

        edt_warehouse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedWarehouse = (WarehouseModel) adapterView.getAdapter().getItem(i);
            }
        });

        getDataIntent();

//        getPONumber(items);

        showValueReceived();

        /**RecyclerView PO Detail*/
//        if (itemsHistory.size() > 0) {
//            tv_StatusThongBao.setVisibility(View.VISIBLE);
//        } else {
//            tv_StatusThongBao.setVisibility(View.GONE);
//        }
        recyclerView.setHasFixedSize(true);

        scaleTicketPODetailPageAdapter = new ScaleTicketPODetailPageAdapter(AddInfoKLActivity.this, products, tempProductForTickets, items, false, new ScaleTicketPODetailPageAdapter.OnItemClickListener() {
            @Override
            public void OnItemRemove(int position) {
                if (!isDaDuyetPKL) {
                    showDialogConfirmRemoveItem(position);
                }
            }

            @Override
            public void OnItemSelectPercent(int position, int value) {
                items.get(position).setTyLeTrongLuong((double) value);
                int sum = 0;
                int remain = 0;

                for (int i = 0; i <= position; i++) {
                    sum += items.get(i).getTyLeTrongLuong();
                }
                if (sum <= 100) {
                    remain = 100 - sum;
                } else {
                    remain = 0;
                    //items.get(position).TyLeTrongLuong -= sum - 100;
                    double temp = items.get(position).getTyLeTrongLuong();
                    temp -= sum - 100;
                    items.get(position).setTyLeTrongLuong(temp);
                    scaleTicketPODetailPageAdapter.notifyItemChanged(position);
                }
                if (position < items.size() - 1) {
                    int size = items.size() - 1 - position;
                    int ratio = remain / size;
                    int recheck = 0;
                    for (int i = position + 1; i < items.size(); i++) {
                        items.get(i).setTyLeTrongLuong((double) ratio);//TyLeTrongLuong = (double) ratio; oldcode
                        recheck += ratio;
                    }
                    if (remain > recheck) {
                        double temp = items.get(items.size() - 1).getTyLeTrongLuong();
                        temp += remain - recheck;
                        //items.get(items.size() - 1).TyLeTrongLuong += remain - recheck;
                        items.get(items.size() - 1).setTyLeTrongLuong(temp);
                    }
                    scaleTicketPODetailPageAdapter.notifyItemRangeChanged(position + 1, size);
                } else if (remain > 0) {
                    double temp = items.get(position).getTyLeTrongLuong();
                    temp += remain;
                    //items.get(position).TyLeTrongLuong += remain;
                    items.get(position).setTyLeTrongLuong(temp);
                    scaleTicketPODetailPageAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public void OnItemSelectVatTu(int position, String tenVatTu) {
                items.get(position).setProductName(tenVatTu);
//                tempProductForTickets.get(position).setProductName(tenVatTu);
                try {
                    for (int i = 0; i < products.size(); i++) {
                        if (products.get(i).getProductName().equals(tenVatTu)) {
                            items.get(position).setProductCode(products.get(i).getProductCode());
                            tempProductForTickets.get(position).setProductCode(products.get(i).getProductCode());
                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e + "");
                }
//                scaleTicketPODetailPageAdapter.notifyItemChanged(position);
            }

            @Override
            public void OnItemCheckedDiffName(int position, Boolean isChecked) {
                // trường hợp là cùng product code nhưng kiểm liệu update với tên vật tư khác

                tempProductForTickets.get(position).setDiff(isChecked);
//                scaleTicketPODetailPageAdapter.notifyItemChanged(position);
            }

            @Override
            public void OnItemChangeProductName(int position, String productName) {

                // mỗi lần thay đổi giá trị của edittext diffName thì sẽ lưu vào array tạm
                try {
                    tempProductForTickets.get(position).setProductName(productName);
                } catch (Exception e) {
                    Log.e(TAG, e + "");
                }

            }
        });
        recyclerView.setAdapter(scaleTicketPODetailPageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL));
//        ArrayList<ScaleTicketPODetailModel> POLine = new ArrayList<>();
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidValue()) {
                    dialogConfirmSave.show();
                } else {
                    Toast.makeText(getApplication(), "Vui lòng nhập đầy đủ thông tin!!!", Toast.LENGTH_LONG).show();
                }

            }
        });

        //Lê Hoàng Long => Thêm nút chuyển đổi kho:
        //Diễn giải: Khi bắm vào thì sẽ đổi dòng CheckInSrap trong databse với Step = 4 và gọi api để tạo thêm dòng đăng ký mới
        //Khi đó cân lần 2 y như cân lần 1 và đăng ký được khởi tạo lại như đầu
        btnSwapWarehouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SwapWareHouse","Click nut chuyen kho");
                dialogConfirmSwapWarehouse.show();
            }
        });



        btnSaveAsTKL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidValue()) {
                    dialogConfirmSaveAsTKL.show();
                } else {
                    Toast.makeText(getApplication(), "Vui lòng nhập đầy đủ thông tin!!!", Toast.LENGTH_LONG).show();
                }

            }
        });

        // Button Lưu tạm
        btnSaveTemp.setOnClickListener(v -> {
            Log.d(TAG, tempProductForTickets.toString());
//            viewDisableLayout.setVisibility(View.VISIBLE);
            showLoading(true);
            // Call api save data
            apiInterface = ApiService.getClient().create(ApiInterface.class);
            LuuKiemLieu = apiInterface.LuuKiemLieu(WareHouse.key, WareHouse.token, prepareData(false), WareHouse.UserId, 2);

            try {
                LuuKiemLieu.enqueue(new Callback<AcctionMessage>() {

                    @Override
                    public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
                        if (response.body().getIsSuccess()) {
//                            Toast.makeText(v.getContext(), "Đã Lưu", Toast.LENGTH_LONG).show();
                            // Lưu kho nếu có
                            CheckAndSaveKhoNhap();

                            showLoading(false);
//                            viewDisableLayout.setVisibility(View.GONE);

                            showDialogSaved("Lưu tạm thành công", "Phiếu kiểm liệu đã đươc lưu tạm");
                        } else {
                            showLoading(false);
//                            viewDisableLayout.setVisibility(View.GONE);
                            Toast.makeText(v.getContext(), response.body().getErr().getMsgString().toString(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AcctionMessage> call, Throwable t) {
//                        viewDisableLayout.setVisibility(View.GONE);
                        showLoading(false);
                        showDialogFailed("Lỗi", "Không lưu được, vui lòng thử lại!!!");
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, e + "");
//                viewDisableLayout.setVisibility(View.GONE);
                showLoading(false);
                showDialogFailed("Lỗi", "Không lưu được, vui lòng thử lại!!!");
            }


        });

        // add new PO line
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmAddItem.show();
            }
        });


        // Gửi api get token trưởng kiểm liệu để gửi thông báo khi lưu Phiếu kiểm liệu
        getTokenTKL2SendNoti();

        // Get Cấu hình trừ tạp chất
        Boolean getBoth = getReferenceTypeTru();
        addEventTruTapChat(getBoth);

    }

    private void showDialogConfirmRemoveItem(int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Xác nhận xóa vật tư").setMessage("Bạn có muốn xóa vật tư đã chọn?").setPositiveButton("Xóa", (dialog, which) -> {
            items.remove(position);
            // Xóa array tạm
            tempProductForTickets.remove(position);

            scaleTicketPODetailPageAdapter.notifyDataSetChanged();
        }).setNegativeButton("Hủy", (dialog, which) -> {

        }).show();
    }

    /**
     * Kiểm tra giá trị các field đã nhập đầy đủ hay chưa
     */
    private Boolean isValidValue() {
        // Kiểm tra field trừ tạp chất
        if (edt_TruKG.getText().toString().equals("") && edt_TruPhanTram.getText().toString().equals("")) {
            edt_TruKG.setError("Chưa nhập trừ tạp chất");
            edt_TruKG.requestFocus();
            return false;
        } else if (rd_2Cont.isChecked()) {
            //Lê Hoàng Long
            //Bỏ required nhập số cont
            //if (soHieuCont1.getText().toString().equals("")) {
                //soHieuCont1.setError("Không được để trống");
               // soHieuCont1.requestFocus();
              //  return false;
           // }
           // if (soHieuCont2.getText().toString().equals("")) {
           //     soHieuCont2.setError("Không được để trống");
           //     soHieuCont2.requestFocus();
           //     return false;
          //  }
        } else if (rd_1Cont.isChecked()) {
           // if (soHieuCont1.getText().toString().equals("")) {
            //    soHieuCont1.setError("Không được để trống");
           //     soHieuCont1.requestFocus();
         //       return false;
        //    }
         //   if (!rd_20feet.isChecked() && !rd_40feet.isChecked()) {
                return false;
         //   }
        } else if (rd_SaLan.isChecked()) {
            if (edt_barge_number.getText().toString().equals("")) {
                edt_barge_number.setError("Không được để trống");
                edt_barge_number.requestFocus();
                return false;
            }
        }

        if (selectedWarehouse == null) {
            edt_warehouse.setError("Chưa chọn kho nhập");
            edt_warehouse.requestFocus();
            return false;
        }

        if (items.size() < 1) {
            return false;
        } else if (items.size() > 1) {
            for (int k = 0; k < items.size(); k++) {
                if (items.get(k).getTyLeTrongLuong() == 0) {
                    Toast.makeText(this, "Không được để tỉ lệ vật tư = 0", Toast.LENGTH_LONG).show();
                    return false;
                }
                for (int h = k + 1; h < items.size(); h++) {
                    if (items.get(k).getProductCode().equals(items.get(h).getProductCode())) {
                        Toast.makeText(this, "Bạn nhập trùng Phế liệu", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
            }
        }
        return true;
    }

//    private void getPONumber(ArrayList<ScaleTicketPODetailModel> items) {
//        try {
//            if (items.get(0).getPoNumber() != null) {
//                poNumber = items.get(0).getPoNumber().toString();
//            } else poNumber = "";
//        } catch (Exception e) {
//            poNumber = "";
//        }
//    }

    private void processSave() {
//        viewDisableLayout.setVisibility(View.VISIBLE);
        showLoading(true);

        /**
         * Xe công : C
         * xe thường: T
         * xà lang: Không có gửi "" => sửa lại thành gửi S + bargeNumber
         */

        // Call api save data
        apiInterface = ApiService.getClient().create(ApiInterface.class);
        LuuKiemLieu = apiInterface.LuuKiemLieu(WareHouse.key, WareHouse.token, prepareData(true), WareHouse.UserId, 3);

        try {
            LuuKiemLieu.enqueue(new Callback<AcctionMessage>() {

                @Override
                public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
                    try {
                        if (response.body().getIsSuccess()) {

//                            viewDisableLayout.setVisibility(View.GONE);
                            // Lưu Kho nhập
                            CheckAndSaveKhoNhap();
                            showLoading(false);
                            // Gửi thông báo cho trưởng kiểm liệu
                            sendNotify();
                            // Show dialog lưu thành công
                            showDialogSaved("Lưu thành công", response.body().getErr().getMsgString().toString());
                        } else {
                            showLoading(false);
//                            viewDisableLayout.setVisibility(View.GONE);
                            showDialogFailed("Lưu không thành công", response.body().getErr().getMsgString().toString());
                        }
                    } catch (Exception e) {
                        showLoading(false);
//                        viewDisableLayout.setVisibility(View.GONE);
                        Toast.makeText(getApplication(), "Lưu không thành công", Toast.LENGTH_LONG).show();
                        Log.e(TAG, e + "");
                    }

                }

                @Override
                public void onFailure(Call<AcctionMessage> call, Throwable t) {
                    try {
                        showLoading(false);
//                        viewDisableLayout.setVisibility(View.GONE);
                        showDialogFailed("Lỗi", "Không lưu được, vui lòng thử lại!!!");
                    } catch (Exception e) {
                        Toast.makeText(getApplication(), "Không lưu được, vui lòng thử lại!!!" + t, Toast.LENGTH_LONG).show();
                    }

                }
            });
        } catch (Exception e) {
            Log.e(TAG, e + "");
            showLoading(false);
//            viewDisableLayout.setVisibility(View.GONE);
            showDialogFailed("Lỗi", "Không lưu được, vui lòng thử lại!!!");
        }
    }

    /**
     * Nếu cấu hình kiểm liệu có quyền duyệt không cần qua tkl thì lưu theo cách này
     */
    private void processSaveAsTKL() {
        showLoading(true);
//        viewDisableLayout.setVisibility(View.VISIBLE);

        /**
         * Xe công : C
         * xe thường: T
         * xà lang: Không có gửi ""
         */

        // Call api save data
        apiInterface = ApiService.getClient().create(ApiInterface.class);
        LuuKiemLieu = apiInterface.LuuKiemLieu(WareHouse.key, WareHouse.token, prepareData(true), WareHouse.UserId, 4);

        try {
            LuuKiemLieu.enqueue(new Callback<AcctionMessage>() {

                @Override
                public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
                    if (response.body().getIsSuccess()) {
                        // Lưu kho nhập
                        CheckAndSaveKhoNhap();
                        showLoading(false);
//                        viewDisableLayout.setVisibility(View.GONE);
                        // Show dialog lưu thành công
                        showDialogSaved("Lưu thành công", response.body().getErr().getMsgString().toString());
                    } else {
                        showLoading(false);
//                        viewDisableLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<AcctionMessage> call, Throwable t) {
                    showLoading(false);
//                    viewDisableLayout.setVisibility(View.GONE);
                    showDialogFailed("Lỗi", "Không lưu được, vui lòng thử lại!!!");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e + "");
            showLoading(false);
//            viewDisableLayout.setVisibility(View.GONE);
            showDialogFailed("Lỗi", "Không lưu được, vui lòng thử lại!!!");
        }
    }

    /**
     * Lê Hoàng Long:Nếu kiểm liệu không muốn đổ phế liệu thì đổi bãi
     */
    private void processSwapWareHouse(){
        showLoading(true);

        // Call api đổi bãi
        apiInterface = ApiService.getClient().create(ApiInterface.class);
        SwapWareHouse = apiInterface.SwapWareHouse(WareHouse.key, WareHouse.token, prepareData(true).getScaleTicket().getScaleTicketId(), WareHouse.UserId, 3);

        try {
            SwapWareHouse.enqueue(new Callback<AcctionMessage>() {
                @Override
                public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
                    try {
                        if (response.body().getIsSuccess()) {
                            // Show dialog lưu thành công
                            showDialogSaved("Đổi thành công", response.body().getErr().getMsgString().toString());
                        } else {
                            showLoading(false);
                            showDialogFailed("Đổi kho không thành công", response.body().getErr().getMsgString().toString());
                        }
                    } catch (Exception e) {
                        showLoading(false);
                        Toast.makeText(getApplication(), "Đổi kho không thành công, vui lòng thử lại!!!", Toast.LENGTH_LONG).show();
                        Log.e(TAG, e + "");
                    }

                }

                @Override
                public void onFailure(Call<AcctionMessage> call, Throwable t) {
                    try {
                        showLoading(false);
                        showDialogFailed("Lỗi", "Đổi kho không thành công, vui lòng thử lại!!!");
                    } catch (Exception e) {
                        Toast.makeText(getApplication(), "Đổi kho không thành công, vui lòng thử lại!!!" + t, Toast.LENGTH_LONG).show();
                    }

                }
            });
        }catch (Exception e) {
            Log.e(TAG, e + "");
            showLoading(false);
            showDialogFailed("Lỗi", "Đổi kho không thành công, vui lòng thử lại!!!");
        }
    }

    void notifyItemRemoved(int position) {
        scaleTicketPODetailPageAdapter.notifyItemRemoved(position);
        scaleTicketPODetailPageAdapter.notifyItemRangeChanged(position, items.size());
    }

    private void addTempItem() {

        ScaleTicketPODetailModel tempItem = new ScaleTicketPODetailModel();
        tempItem.setScaleTicketPODetailId("00000000-0000-0000-0000-000000000000");
        tempItem.setScaleTicketId("00000000-0000-0000-0000-000000000000");

        tempItem.setPoNumber("");
        tempItem.setPoLine("");
        tempItem.setIsNoPO(true);
        tempItem.setProductCode(products.get(0).getProductCode());
        tempItem.setProductName(products.get(0).getProductName());
        tempItem.setPoQty(0);
        tempItem.setQty1(0);
        tempItem.setQty2(0);
        tempItem.setUnit("KG");
        tempItem.setSoLuongDaNhap(0);
        tempItem.setTyLeTrongLuong(50.0);
        tempItem.setTapChat(0);
        tempItem.setUnit1("KG");
        tempItem.setIsSendToSAPCompleted(false);
        items.add(tempItem);
        scaleTicketPODetailPageAdapter.notifyDataSetChanged();
        ///temp
        tempProductForTickets.add(new TempProductForTicket(products.get(0).getProductCode(), products.get(0).getProductName(), false));
    }

//    private void updatePOlineBeforeSend() {
//        for (int i = 0; i < items.size(); i++) {
//            if (items.get(i).getPoLine().equals("")) {
//                defaultPOLine += 10;
//                if (defaultPOLine < 100) {
//                    items.get(i).setPoLine("000" + defaultPOLine);
//                } else {
//                    items.get(i).setPoLine("00" + defaultPOLine);
//                }
//            }
//        }
//    }

    private void showValueReceived() {
        tv_MaPhieu.setText(maPhieuCan);
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
        if (selectedWarehouse != null) {
            edt_warehouse.setText(selectedWarehouse.getWareHouseName());
            Log.e(TAG, selectedWarehouse.getWareHouseName());
        }

        addInfoKLViewModel.setTruKG(String.valueOf(scaleTicket.getKgReduced().intValue()));
        addInfoKLViewModel.setTruPhanTram(String.valueOf(scaleTicket.getPercentReduced()));
//        RelativeLayout tv_StatusThongBao = findViewById(R.id.tv_StatusThongBao);
        if (isDaDuyetPKL) {
            btnSave.setEnabled(false);
            btnSaveTemp.setEnabled(false);
            edt_Note.setEnabled(false);
            rd_SaLan.setEnabled(false);
            rd_1Cont.setEnabled(false);
            rd_2Cont.setEnabled(false);
            edt_barge_number.setEnabled(false);
            edt_warehouse.setEnabled(false);
            rd_XeThuong.setEnabled(false);
            edt_TruKG.setEnabled(false);
            edt_TruPhanTram.setEnabled(false);
            btnAddNew.setEnabled(false);
            btnAddNew.setBackground(getResources().getDrawable(R.drawable.buttonshape8));
            btnSave.setBackground(getResources().getDrawable(R.drawable.buttonshape8));
            btnSaveTemp.setBackground(getResources().getDrawable(R.drawable.buttonshape8));
        } else {
            btnSave.setEnabled(true);
            btnSaveTemp.setEnabled(true);
        }

        // Set mục xe
        try {
            if (scaleTicket.getVehicleTypeCode().equals("S")) {
                // Sà lang
                rd_SaLan.setChecked(true);
                edt_barge_number.setText(scaleTicket.getBargeNumber());

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

    private void showDialogSaved(String title, String message) {
        dialogSuccessSave.setTitle(title);
        dialogSuccessSave.setMessage(message);
        dialogSuccessSave.show();
    }

    private void showDialogFailed(String title, String message) {
        dialogFailSave.setTitle(title);
        dialogFailSave.setMessage(message);
        dialogFailSave.show();
    }

    private CheckingScrapDTO prepareData(Boolean isFinal) {
        CheckingScrapDTO checkingScrapDTO = new CheckingScrapDTO();
        checkingScrapDTO.setNumberCong("0"); // Số lượng cont
        checkingScrapDTO.setContainer1Code("");
        checkingScrapDTO.setContainer2Code("");

        if (rd_SaLan.isChecked()) {// checked
            scaleTicket.setVehicleTypeCode("S");
            scaleTicket.setBargeNumber("");
            //scaleTicket.setTrailersNumber("");
            scaleTicket.setIs20Feet(false);
            scaleTicket.setIs40Feet(false);
            scaleTicket.setSoHieuCont1("");
            scaleTicket.setSoHieuCont2("");
            scaleTicket.setBargeNumber(edt_barge_number.getText().toString());

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
            scaleTicket.setBargeNumber("");

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
            scaleTicket.setBargeNumber("");

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
            scaleTicket.setBargeNumber("");

            checkingScrapDTO.setIs20Feet(false);
            checkingScrapDTO.setVehicleCode("T");
        }

        if (isFinal) {
            // Nếu là lưu duyệt thì mới cần update lại POLine, lưu tạm thì lưu sao cũng được
//            updatePOlineBeforeSend();

            // Thêm tên KL viên vào phần ghi chú của phiếu cân
            checkingScrapDTO.setNote(edt_Note.getText().toString().replaceAll("\n", ", "));
        } else {
            checkingScrapDTO.setNote(edt_Note.getText().toString().replaceAll("\n", ", "));
        }


        // set lại tên vật tư theo tên đã được KL chỉnh sửa
        for (int i = 0; i < items.size(); i++) {
            if (tempProductForTickets.get(i).getDiff()) {
                items.get(i).setProductName(tempProductForTickets.get(i).getProductName());
            }
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


        checkingScrapDTO.setEdit(true);
        checkingScrapDTO.setDuyetPKL(false);

        return checkingScrapDTO;
    }

    private String getNameKL() {
        TransferData transferData = TransferData.getInstance(getApplicationContext());
        return transferData.getData("FULL_NAME", "NONE").toUpperCase();
    }

    private void sendNotify() {
        // Send notification to Truong kiem lieu
        if (receiver_token != null) {
            try {
                DataMessage dataMessage = new DataMessage();
                dataMessage.setTitle("Có phiếu kiểm liệu mới");
                dataMessage.setMessage("Có phiếu kiểm liệu mới cho xe " + bsx);
                dataMessage.setVerhicle_number(bsx);
                dataMessage.setReciver_token("");
                dataMessage.setSender_id(WareHouse.UserId);
                dataMessage.setRfid(rfid);

                MesageModel mesageModel = new MesageModel();
                mesageModel.setTo(receiver_token);
                mesageModel.setData(dataMessage);
                apiInterface = ApiService.getClientFcm().create(ApiInterface.class);
                callSendNoti = apiInterface.sendNotifycation(WareHouse.contentType, WareHouse.serverKey, mesageModel);
                callSendNoti.enqueue(new Callback<FcmResponse>() {

                    @Override
                    public void onResponse(Call<FcmResponse> call, Response<FcmResponse> response) {
                        Log.d(TAG, response.toString());
                    }

                    @Override
                    public void onFailure(Call<FcmResponse> call, Throwable t) {
                        Log.d(TAG, t + "");
                    }
                });
            } catch (Exception e) {
                Log.d(TAG, e + "");

            }
        }

    }

    private void getTokenTKL2SendNoti() {
        apiInterface = ApiService.getClient().create(ApiInterface.class);
        Call<SingleResponeMessage<TempTable>> call = apiInterface.GetTokenForNoti(WareHouse.key, WareHouse.token, WareHouse.UserId, WareHouse.groupUser);
        call.enqueue(new Callback<SingleResponeMessage<TempTable>>() {


            @Override
            public void onResponse(Call<SingleResponeMessage<TempTable>> call, Response<SingleResponeMessage<TempTable>> response) {
                if (response.body().getIsSuccess()) {
                    try {
                        receiver_token = response.body().getItem().getTokenTKL();
                    } catch (Exception e) {
                        Log.e(TAG, e + "");
                    }
                }
            }

            @Override
            public void onFailure(Call<SingleResponeMessage<TempTable>> call, Throwable t) {

            }
        });
    }


    private void CheckAndSaveKhoNhap() {
        if (selectedWarehouse == null) return;
        try {
            apiInterface = ApiService.getClient().create(ApiInterface.class);
            Call<AcctionMessage> call = apiInterface.UpdateKhoNhapPhieuCan(WareHouse.key, WareHouse.token, scaleTicket.getScaleTicketId(), new WarehouseModel("00000000-0000-0000-0000-000000000000", selectedWarehouse.getWareHouseCode(), selectedWarehouse.getWareHouseName()));
            call.enqueue(new Callback<AcctionMessage>() {
                @Override
                public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {

                }

                @Override
                public void onFailure(Call<AcctionMessage> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }

    private void createControll() {
//        viewDisableLayout = findViewById(R.id.viewDisableLayout);
        // hiện màn hình mờ khi loading lưu
        disableView = findViewById(R.id.disableView);
        tv_MaPhieu = findViewById(R.id.tv_MaPhieu);
        tv_GioVao = findViewById(R.id.tv_GioVao);
        edtPlateNumber = findViewById(R.id.edtPlateNumber);
        edtTypeText = findViewById(R.id.edtTypeText);
        edt_TruKG = findViewById(R.id.edt_TruKG);
        edt_TruPhanTram = findViewById(R.id.edt_TruPhanTram);
        edt_Note = findViewById(R.id.edt_Note);
        recyclerView = findViewById(R.id.ListVatTu);
        listStatusThongBao = findViewById(R.id.listStatusThongBao);
        soHieuCont1 = findViewById(R.id.so_hieu_cont_1);
        soHieuCont2 = findViewById(R.id.so_hieu_cont_2);
        edt_barge_number = findViewById(R.id.barge_number);
        edt_warehouse = findViewById(R.id.warehouse);

        /**RadioButton*/
        rd_SaLan = findViewById(R.id.rd_SaLan);
        rd_1Cont = findViewById(R.id.rd_1Cont);
        rd_2Cont = findViewById(R.id.rd_2Cont);
//        rd_SaLan = findViewById(R.id.rd_SaLan);
        rd_XeThuong = findViewById(R.id.rd_XeThuong);
        rg_radionButton = findViewById(R.id.rg_radionButton);
        rd_Container = findViewById(R.id.rd_Container);
        rd_20feet = findViewById(R.id.rd_20feet);
        rd_40feet = findViewById(R.id.rd_40feet);

        /**Button*/
        btnAddNew = findViewById(R.id.btnAddNew);
        btnSaveTemp = findViewById(R.id.btnSaveTemp);
        btnSave = findViewById(R.id.btnSave);
        btnSaveAsTKL = findViewById(R.id.btnSaveAsTKL);
        /**LeLong*/
        btnSwapWarehouse = findViewById(R.id.btnSwapWarehouse);

        /**
         * Nếu kiểm liệu có quyền duyệt thì không cần phải qua trưởng kiểm liệu
         * check setting trong sharedReference
         */
        if (getReferenceTypeConfirm()) {
            btnSaveAsTKL.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
        } else {
            btnSaveAsTKL.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
        }

        // Adapter list select BSX
        listBargeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, listBarge);
        edt_barge_number.setAdapter(listBargeAdapter);

        // Kho nhập
        // Adapter list select BSX
        listWarehouseAdapter = new ArrayAdapter<WarehouseModel>(this, android.R.layout.simple_dropdown_item_1line, listWarehouse);
        edt_warehouse.setAdapter(listWarehouseAdapter);
    }

    /**
     * Bắt sự kiện thay đổi value của EditText trừ ký và EditText trừ %
     * Truyền vào cấu hình của hệ thống là trừ cả theo ký và % hay trừ theo ký hoặc %
     *
     * @param getBoth
     */
    private void addEventTruTapChat(Boolean getBoth) {
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                addInfoKLViewModel.checkEnableTruField(getBoth, edt_TruKG.getText().toString(), edt_TruPhanTram.getText().toString());
            }
        };
        edt_TruKG.addTextChangedListener(afterTextChangedListener);
        edt_TruPhanTram.addTextChangedListener(afterTextChangedListener);
    }

    /**
     * lấy dữ liệu Setting chọn trừ tạp chất theo dạng là trừ cả 2 hay là dạng chỉ lấy 1 trong 2
     * Trừ theo ký và %: true
     * Trừ theo ký hoặc %: false
     *
     * @return
     */
    private Boolean getReferenceTypeTru() {
        TransferData transferData = TransferData.getInstance(this);
        return transferData.getBoolean("IS_GET_BOTH", false);

    }

    /**
     * lấy dữ liệu Setting có cho phép kiểm liệu duyệt phiếu hay không
     * Cho phép kiểm liệu duyệt phiếu: true
     * Không cho phép kiểm liệu duyệt phiếu: false
     *
     * @return
     */
    private Boolean getReferenceTypeConfirm() {
        TransferData transferData = TransferData.getInstance(this);
        return transferData.getBoolean("ALLOW_KL_CONFIRM", false);

    }

    private int getDefaultPOLine() {
        if (items.size() > 0) {
            try {
                return Integer.parseInt(items.get(items.size() - 1).getPoLine());
            } catch (Exception e) {
                Log.e(TAG, e + "");
            }
        }
        return 0;
    }

    private void getDataIntent() {
        products = (ArrayList<ProductModel>) getIntent().getSerializableExtra("PRODUCTS");
        items = (ArrayList<ScaleTicketPODetailModel>) getIntent().getSerializableExtra("SCALE_TICKET_PODETAIL_LIST");
        itemsHistory = (ArrayList<HistoryModel>) getIntent().getSerializableExtra("HISTORY");
        scaleTicket = (ScaleTicketModel) getIntent().getSerializableExtra("SCALE_TICKET");
        isDaDuyetPKL = getIntent().getBooleanExtra("DUYET_PKL", false);
        rfid = getIntent().getStringExtra("RFID");
        maPhieuCan = getIntent().getStringExtra("SCALE_TICKET_CODE");
        gioVao = getIntent().getStringExtra("IN_HOUR");
        bsx = getIntent().getStringExtra("VEHICLE_NUMBER");
        loaiXe = getIntent().getStringExtra("TYPE_TEXT");
        selectedWarehouse = (WarehouseModel) getIntent().getSerializableExtra("WAREHOUSE");
        //
        defaultPOLine = getDefaultPOLine();
        //
        for (ScaleTicketPODetailModel scaleTicketPODetailModel : items) {
            tempProductForTickets.add(new TempProductForTicket(scaleTicketPODetailModel.getProductCode()
                    , scaleTicketPODetailModel.getProductName()
                    , isDiffProductName(scaleTicketPODetailModel.getProductCode()
                    , scaleTicketPODetailModel.getProductName())));
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

    private void removeDataIntent() {
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
        getIntent().removeExtra("WAREHOUSE");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Nếu remove thì khi giao diện được render lại bị mất data
//        removeDataIntent();
    }

    private void getListBarge() {
        // Call api save data
        apiInterface = ApiService.getClient().create(ApiInterface.class);
        Call<ListResponeMessage<BargeModel>> callListBarge = apiInterface.GetListBarge(WareHouse.key, WareHouse.token);

        try {
            callListBarge.enqueue(new Callback<ListResponeMessage<BargeModel>>() {
                @Override
                public void onResponse(Call<ListResponeMessage<BargeModel>> call, Response<ListResponeMessage<BargeModel>> response) {
                    if (response.body().getIsSuccess()) {
                        List<BargeModel> listBargeModel = response.body().getData();
                        for (BargeModel model : listBargeModel) {
                            listBarge.add(model.getBargeNumber());
                        }
                        // set adapter

                        listBargeAdapter = new ArrayAdapter<String>(getApplication(), android.R.layout.simple_dropdown_item_1line, listBarge);
                        edt_barge_number.setAdapter(listBargeAdapter);
                        listBargeAdapter.notifyDataSetChanged();
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<ListResponeMessage<BargeModel>> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            Log.e(TAG, e + "");
            showLoading(false);
//            viewDisableLayout.setVisibility(View.GONE);
            showDialogFailed("Lỗi", "Không lưu được, vui lòng thử lại!!!");
        }
    }

    private void getListWarehouse(String text) {
        // Call api save data
        apiInterface = ApiService.getClient().create(ApiInterface.class);
        Call<ListResponeMessage<WarehouseModel>> callListBarge = apiInterface.GetListWarehouse(WareHouse.key, WareHouse.token, text);

        try {
            callListBarge.enqueue(new Callback<ListResponeMessage<WarehouseModel>>() {
                @Override
                public void onResponse(Call<ListResponeMessage<WarehouseModel>> call, Response<ListResponeMessage<WarehouseModel>> response) {
                    if (response.body().getIsSuccess()) {
                        listWarehouse = response.body().getData();

//                        listWarehouseName = new ArrayList();
//                        listWarehouseCode = new ArrayList();
//                        for (WarehouseModel model : listWarehouse) {
//                            listWarehouseName.add(model.getWareHouseName());
//                            listWarehouseCode.add(model.getWareHouseCode());
//                        }
                        // set adapter

                        listWarehouseAdapter = new ArrayAdapter<WarehouseModel>(getApplication(), android.R.layout.simple_dropdown_item_1line, listWarehouse);
                        edt_warehouse.setAdapter(listWarehouseAdapter);
                        listWarehouseAdapter.notifyDataSetChanged();
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<ListResponeMessage<WarehouseModel>> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            Log.e(TAG, e + "");
//            showLoading(false);
//            viewDisableLayout.setVisibility(View.GONE);
            showDialogFailed("Lỗi", "Không lấy được danh sách kho");
        }
    }

    private void showLoading(Boolean isShow) {
        if (isShow)
            disableView.setVisibility(View.VISIBLE);
        else
            disableView.setVisibility(View.GONE);
    }
}