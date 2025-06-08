package com.calmpuchia.userapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import com.calmpuchia.userapp.models.Order;
import com.calmpuchia.userapp.models.User;
import com.calmpuchia.userapp.services.PointService;

public class OrderSuccessActivity extends AppCompatActivity {

    private TextView tvOrderId, tvTotalAmount, tvPointsEarned, tvCurrentPoints, tvBonusInfo;
    private Button btnViewPoints, btnContinueShopping, btnViewVouchers;
    private ProgressBar progressBar;

    private PointService pointService;
    private String userId;
    private String orderId;
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        initViews();
        initServices();
        getIntentData();
        processOrder();
    }

    private void initViews() {
        tvOrderId = findViewById(R.id.tv_order_id);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvPointsEarned = findViewById(R.id.tv_points_earned);
        tvCurrentPoints = findViewById(R.id.tv_current_points);
        tvBonusInfo = findViewById(R.id.tv_bonus_info);
        btnViewPoints = findViewById(R.id.btn_view_points);
        btnContinueShopping = findViewById(R.id.btn_continue_shopping);
        btnViewVouchers = findViewById(R.id.btn_view_vouchers);
        progressBar = findViewById(R.id.progress_bar);

        // Set click listeners
        btnViewPoints.setOnClickListener(v -> openPointHistory());
        btnContinueShopping.setOnClickListener(v -> continueShopping());
        btnViewVouchers.setOnClickListener(v -> openVouchers());
    }

    private void initServices() {
        pointService = new PointService(this);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");
        orderId = intent.getStringExtra("order_id");

        // Giả sử bạn có thông tin order từ intent hoặc database
        // Ở đây tôi tạo một order mẫu
        currentOrder = createSampleOrder();
    }

    private Order createSampleOrder() {
        // Tạo order mẫu - trong thực tế bạn sẽ lấy từ database
        Order order = new Order();
        order.setOrder_id(orderId);
        order.setUser_id(userId);
        order.setTotal_amount(250000); // 250k VND
        order.setStatus("SUCCESS");
        order.setIs_store_purchase(true); // Mua tại cửa hàng
        order.setStore_location("Store Location");
        return order;
    }

    private void processOrder() {
        showLoading(true);

        // Hiển thị thông tin đơn hàng
        displayOrderInfo();

        // Tích điểm cho đơn hàng
        pointService.processOrderPoints(currentOrder, new PointService.PointCallback() {
            @Override
            public void onSuccess(int pointsEarned) {
                runOnUiThread(() -> {
                    showLoading(false);
                    displayPointsEarned(pointsEarned);
                    loadCurrentUserPoints();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(OrderSuccessActivity.this,
                            "Points processing failed: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void displayOrderInfo() {
        tvOrderId.setText("Order Id: " + currentOrder.getOrder_id());
        tvTotalAmount.setText(String.format("Total amount: %,.0f VND", currentOrder.getTotal_amount()));

        // Hiển thị thông tin bonus nếu mua tại cửa hàng
        if (currentOrder.isIs_store_purchase()) {
            tvBonusInfo.setVisibility(View.VISIBLE);
            tvBonusInfo.setText("🎉 Get +50 loyalty points when shopping in-store!");
        } else {
            tvBonusInfo.setVisibility(View.GONE);
        }
    }

    private void displayPointsEarned(int pointsEarned) {
        tvPointsEarned.setVisibility(View.VISIBLE);
        tvPointsEarned.setText(String.format("You've earned: +%d points", pointsEarned));

        // Animation hiệu ứng
        tvPointsEarned.setAlpha(0f);
        tvPointsEarned.animate()
                .alpha(1f)
                .setDuration(1000)
                .start();
    }

    private void loadCurrentUserPoints() {
        pointService.getUserById(userId, new PointService.UserCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    tvCurrentPoints.setVisibility(View.VISIBLE);
                    tvCurrentPoints.setText(String.format("Total points available: %d points",
                            user.getLoyalty_points()));
                });
            }

            @Override
            public void onFailure(String error) {
                // Không hiển thị lỗi này vì không quan trọng lắm
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnViewPoints.setEnabled(!show);
        btnContinueShopping.setEnabled(!show);
        btnViewVouchers.setEnabled(!show);
    }

    private void openPointHistory() {
        Intent intent = new Intent(this, PointHistoryActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    private void continueShopping() {
        // Quay về màn hình chính hoặc danh sách sản phẩm
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void openVouchers() {
        Intent intent = new Intent(this, VoucherActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // Không cho phép back, chỉ có thể dùng các nút
        continueShopping();
    }
}