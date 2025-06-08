package com.calmpuchia.userapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.calmpuchia.userapp.models.Voucher;
import com.calmpuchia.userapp.models.User;
import com.calmpuchia.userapp.services.PointService;
import java.util.ArrayList;
import java.util.List;

public class VoucherActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvCurrentPoints;
    private VoucherAdapter adapter;
    private FirebaseFirestore db;
    private PointService pointService;
    private String userId;
    private int currentUserPoints = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        initViews();
        initData();
        loadVouchers();
        loadCurrentPoints();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        tvCurrentPoints = findViewById(R.id.tv_current_points);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VoucherAdapter(this::redeemVoucher);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        db = FirebaseFirestore.getInstance();
        pointService = new PointService(this);
        userId = getIntent().getStringExtra("user_id");
    }

    private void loadVouchers() {
        progressBar.setVisibility(View.VISIBLE);

        // Tạo dữ liệu voucher mẫu (trong thực tế bạn sẽ lấy từ Firebase)
        List<Voucher> vouchers = createSampleVouchers();
        adapter.updateData(vouchers, currentUserPoints);
        progressBar.setVisibility(View.GONE);
    }

    private List<Voucher> createSampleVouchers() {
        List<Voucher> vouchers = new ArrayList<>();

        Voucher v1 = new Voucher();
        v1.setVoucher_id("v1");
        v1.setTitle("10% Off Orders");
        v1.setDescription("Applicable for orders from 200k");
        v1.setPoints_required(100);
        v1.setIs_store_only(false);
        v1.setIs_active(true);
        vouchers.add(v1);

        Voucher v2 = new Voucher();
        v2.setVoucher_id("v2");
        v2.setTitle("Miễn phí giao hàng");
        v2.setDescription("Miễn phí ship toàn quốc");
        v2.setPoints_required(50);
        v2.setIs_store_only(false);
        v2.setIs_active(true);
        vouchers.add(v2);

        Voucher v3 = new Voucher();
        v3.setVoucher_id("v3");
        v3.setTitle("Giảm 50k tại cửa hàng");
        v3.setDescription("Chỉ áp dụng khi mua tại cửa hàng");
        v3.setPoints_required(200);
        v3.setIs_store_only(true);
        v3.setIs_active(true);
        vouchers.add(v3);

        return vouchers;
    }

    private void loadCurrentPoints() {
        pointService.getUserById(userId, new PointService.UserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUserPoints = user.getLoyalty_points();
                tvCurrentPoints.setText(String.format("Your Points: %d", currentUserPoints));
                adapter.updateUserPoints(currentUserPoints);
            }

            @Override
            public void onFailure(String error) {
                tvCurrentPoints.setText("Your Points: --");
            }
        });
    }

    private void redeemVoucher(Voucher voucher) {
        if (currentUserPoints < voucher.getPoints_required()) {
            Toast.makeText(this, "Not enough points to redeem this voucher", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        pointService.redeemPoints(userId, voucher.getPoints_required(), voucher.getVoucher_id(),
                new PointService.PointCallback() {
                    @Override
                    public void onSuccess(int pointsRedeemed) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            currentUserPoints -= pointsRedeemed;
                            tvCurrentPoints.setText(String.format("Your Points: %d", currentUserPoints));
                            adapter.updateUserPoints(currentUserPoints);
                            Toast.makeText(VoucherActivity.this,
                                    "Redeem Voucher Successfully!", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(VoucherActivity.this,
                                    "Error while redeeming voucher: " + error, Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }

    // Adapter cho RecyclerView voucher
    private static class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {
        private List<Voucher> vouchers = new ArrayList<>();
        private int userPoints = 0;
        private VoucherClickListener listener;

        interface VoucherClickListener {
            void onRedeemClick(Voucher voucher);
        }

        VoucherAdapter(VoucherClickListener listener) {
            this.listener = listener;
        }

        public void updateData(List<Voucher> newVouchers, int userPoints) {
            this.vouchers = newVouchers;
            this.userPoints = userPoints;
            notifyDataSetChanged();
        }

        public void updateUserPoints(int userPoints) {
            this.userPoints = userPoints;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_voucher, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Voucher voucher = vouchers.get(position);
            holder.bind(voucher);
        }

        @Override
        public int getItemCount() {
            return vouchers.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDescription, tvPoints, tvStoreOnly;
            Button btnRedeem;

            ViewHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvVoucherTitle);
                tvDescription = itemView.findViewById(R.id.tvVoucherDescription);
                tvPoints = itemView.findViewById(R.id.tvPoints);
                tvStoreOnly = itemView.findViewById(R.id.tvVoucherForStoreOnly);
                btnRedeem = itemView.findViewById(R.id.btnRedeemVoucher);
            }

            void bind(Voucher voucher) {
                tvTitle.setText(voucher.getTitle());
                tvDescription.setText(voucher.getDescription());
                tvPoints.setText(voucher.getPoints_required() + " điểm");

                if (voucher.isIs_store_only()) {
                    tvStoreOnly.setVisibility(View.VISIBLE);
                    tvStoreOnly.setText("Chỉ áp dụng tại cửa hàng");
                } else {
                    tvStoreOnly.setVisibility(View.GONE);
                }

                boolean canRedeem = userPoints >= voucher.getPoints_required();
                btnRedeem.setEnabled(canRedeem);
                btnRedeem.setText(canRedeem ? "Đổi ngay" : "Không đủ điểm");

                btnRedeem.setOnClickListener(v -> {
                    if (listener != null && canRedeem) {
                        listener.onRedeemClick(voucher);
                    }
                });
            }
        }
    }
}