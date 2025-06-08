package com.calmpuchia.userapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.calmpuchia.userapp.R;
import com.calmpuchia.userapp.models.Voucher;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

    private Context context;
    private List<Voucher> voucherList;

    public VoucherAdapter(Context context, List<Voucher> voucherList) {
        this.context = context;
        this.voucherList = voucherList;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);
        holder.tvTitle.setText(voucher.getTitle());
        holder.tvDescription.setText(voucher.getDescription());
        holder.tvPoints.setText("Points: " + voucher.getVoucher_id());

    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public static class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvPoints;
        ImageView imgVoucher;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvVoucherTitle);
            tvDescription = itemView.findViewById(R.id.tvVoucherDescription);
            tvPoints = itemView.findViewById(R.id.tvPoints);
        }
    }
}