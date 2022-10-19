package com.example.vasclientv2.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasclientv2.R;
import com.example.vasclientv2.kiemlieu.kiemlieu.TempProductForTicket;
import com.example.vasclientv2.model.entities.ProductModel;
import com.example.vasclientv2.model.entities.ScaleTicketPODetailModel;

import java.util.ArrayList;
import java.util.List;

public class ScaleTicketPODetailPageAdapter extends RecyclerView.Adapter<ScaleTicketPODetailPageAdapter.ViewHolder> {

    private List<ScaleTicketPODetailModel> items;
    private List<ProductModel> products;
    private List<TempProductForTicket> tempProductForTickets;
    private OnItemClickListener onItemClickListener;
    private Context context;
    private Boolean isTKiemLieu;
    private int percent;

    public ScaleTicketPODetailPageAdapter(Context context, List<ProductModel> products, List<TempProductForTicket> tempProductForTickets, List<ScaleTicketPODetailModel> items, Boolean isTKiemLieu, OnItemClickListener onItemClickListener) {
        this.items = items;
        this.products = products;
        this.context = context;
        this.isTKiemLieu = isTKiemLieu;
        this.onItemClickListener = onItemClickListener;
        this.tempProductForTickets = tempProductForTickets;
    }

    @NonNull
    @Override
    public ScaleTicketPODetailPageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kiemlieu, parent, false);
        return new ViewHolder(view);
    }

    // ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        Spinner sp_vattu;
        Spinner Sp_Percent;
        ImageButton btnRemove;
        EditText diffName;
        CheckBox isDiffName;

        ViewHolder(View itemView) {
            super(itemView);
            sp_vattu = itemView.findViewById(R.id.sp_vattu);
            Sp_Percent = itemView.findViewById(R.id.Sp_Percent);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            diffName = itemView.findViewById(R.id.txtTenPheLieuKhac);
            isDiffName = itemView.findViewById(R.id.isDifPheLieuName);

            if (isTKiemLieu) {
                sp_vattu.setEnabled(false);
                Sp_Percent.setEnabled(false);
                btnRemove.setEnabled(false);
                diffName.setEnabled(false);
                isDiffName.setEnabled(false);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ScaleTicketPODetailPageAdapter.ViewHolder holder, int position) {
        ScaleTicketPODetailModel item = items.get(position);
        // Vat tu
        final List<ProductModel> vatTuArray = new ArrayList<>();
        vatTuArray.addAll(products);
        ArrayAdapter vatTuAdapter = new ArrayAdapter(context, R.layout.spinner_vattu, vatTuArray);
        holder.sp_vattu.setAdapter(vatTuAdapter);
        //set show spinner
        for (int j = 0; j < products.size(); j++) {

            if (products.get(j).getProductCode().equals(item.getProductCode())) {
                holder.sp_vattu.setSelection(j);
                break;
            }
        }
        //set show isDifference Name
        holder.isDiffName.setChecked(tempProductForTickets.get(position).getDiff());
        // nếu là trưởng kiểm liệu thì không cho phép edit
        if (!isTKiemLieu) {
            holder.diffName.setEnabled(tempProductForTickets.get(position).getDiff());
        }

        if (tempProductForTickets.get(position).getDiff()) {
            holder.diffName.setText(tempProductForTickets.get(position).getProductName());
        }
        //
        holder.sp_vattu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onItemClickListener.OnItemSelectVatTu(holder.getAdapterPosition(), holder.sp_vattu.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        holder.diffName.addTextChangedListener(new TextWatcher() {
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
                onItemClickListener.OnItemChangeProductName(position, holder.diffName.getText().toString());
            }
        });


        //spiner percent
        double d = item.getTyLeTrongLuong();
        String s = String.valueOf(d);
        String[] str = s.split("\\.");
        int tl = Integer.parseInt(str[0]);

        int a = 0;
        final List<Integer> Percent = new ArrayList<>();
        Percent.add(0);

        // Spinner 0->100
        for (int i = 0; i <= 99; i++) {
            a = a + 1;
            Percent.add(a);
        }

        ArrayAdapter percentAdapter = new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, Percent);
        holder.Sp_Percent.setAdapter(percentAdapter);
        holder.Sp_Percent.setSelection(tl);
        holder.Sp_Percent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                percent = (int) holder.Sp_Percent.getItemAtPosition(position);
                onItemClickListener.OnItemSelectPercent(holder.getAdapterPosition(), percent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemRemove(holder.getAdapterPosition());
            }
        });

//        /**
//         * Kiểm tra theo product code
//         * nếu khác productName thì đó là vật tư có tên khác
//         * check vào checkedBox
//         */
//        for (ProductModel productModel : products) {
//            if (productModel.getProductCode().equals(item.getProductCode()) && !productModel.getProductName().equals(item.getProductName())) {
//                if (!productModel.getProductName().equals(item.getProductName())) {
//                    holder.isDiffName.setChecked(true);
//                    holder.diffName.setText(item.getProductName());
//                }
//            }
//        }
        holder.isDiffName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                holder.diffName.setEnabled(isChecked);
                if (isChecked){
                    holder.diffName.setText(holder.sp_vattu.getSelectedItem().toString());
                }else {
                    holder.diffName.setText("");
                }

                onItemClickListener.OnItemCheckedDiffName(holder.getAdapterPosition(), isChecked);
            }
        });

        Log.d("aaaaaaaaaaaa",tempProductForTickets.toString());

    }

    @Override
    public int getItemCount() {
        if (items != null)
            return items.size();
        return 0;
    }


    // interface OnItemClickListener
    public interface OnItemClickListener {
        void OnItemRemove(int position);

        void OnItemSelectPercent(int position, int value);

        void OnItemSelectVatTu(int position, String tenVatTu);

        void OnItemCheckedDiffName(int position, Boolean isChecked);

        void OnItemChangeProductName(int position, String productName);

    }
}
