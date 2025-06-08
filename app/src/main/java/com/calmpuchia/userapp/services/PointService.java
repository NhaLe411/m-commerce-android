package com.calmpuchia.userapp.services;

import android.content.Context;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.calmpuchia.userapp.models.User;
import com.calmpuchia.userapp.models.Order;
import com.calmpuchia.userapp.models.PointTransaction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PointService {
    private static final String TAG = "PointService";
    private static final String USERS_COLLECTION = "users";
    private static final String POINT_TRANSACTIONS_COLLECTION = "point_transactions";
    private static final String ORDERS_COLLECTION = "orders";

    // Tỷ lệ tích điểm: 1000 VND = 1 điểm
    private static final double POINT_RATE = 1000.0;
    // Bonus điểm khi mua tại cửa hàng
    private static final int STORE_BONUS_POINTS = 50;

    private FirebaseFirestore db;
    private Context context;

    public PointService(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    // Interface để callback kết quả
    public interface PointCallback {
        void onSuccess(int pointsEarned);

        void onFailure(String error);
    }

    public interface UserCallback {
        void onSuccess(User user);

        void onFailure(String error);
    }

    /**
     * Tính toán và cộng điểm cho đơn hàng thành công
     */
    public void processOrderPoints(Order order, PointCallback callback) {
        if (order == null || !order.getStatus().equals("SUCCESS")) {
            callback.onFailure("Invalid or unsuccessful order");
            return;
        }

        // Tính điểm cơ bản
        int basePoints = calculateBasePoints(order.getTotal_amount());

        // Cộng bonus nếu mua tại cửa hàng
        int bonusPoints = order.isIs_store_purchase() ? STORE_BONUS_POINTS : 0;

        int totalPoints = basePoints + bonusPoints;

        // Cập nhật điểm cho user
        updateUserPoints(order.getUser_id(), totalPoints, order.getOrder_id(), callback);
    }

    /**
     * Tính điểm cơ bản dựa trên tổng tiền
     */
    private int calculateBasePoints(double totalAmount) {
        return (int) (totalAmount / POINT_RATE);
    }

    /**
     * Cập nhật điểm tích lũy cho user
     */
    private void updateUserPoints(String userId, int pointsToAdd, String orderId, PointCallback callback) {
        db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            // Cộng điểm mới
                            int newPoints = user.getLoyalty_points() + pointsToAdd;

                            // Cập nhật Firebase
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("loyalty_points", newPoints);

                            db.collection(USERS_COLLECTION)
                                    .document(userId)
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        // Lưu lịch sử giao dịch điểm
                                        savePointTransaction(userId, pointsToAdd, "EARNED", orderId);
                                        callback.onSuccess(pointsToAdd);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Point update error:" + e.getMessage());
                                        callback.onFailure("Unable to update points");
                                    });
                        }
                    } else {
                        callback.onFailure("No user information found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error obtaining user information: " + e.getMessage());
                    callback.onFailure("Unable to obtain user information");
                });
    }

    /**
     * Lưu lịch sử giao dịch điểm
     */
    private void savePointTransaction(String userId, int points, String type, String orderId) {
        String transactionId = db.collection(POINT_TRANSACTIONS_COLLECTION).document().getId();
        String currentDate = getCurrentDate();

        PointTransaction transaction = new PointTransaction(
                transactionId,
                userId,
                points,
                type,
                currentDate,
                orderId,
                type.equals("EARNED") ? "Loyalty points from orders" : "Claim voucher"
        );

        db.collection(POINT_TRANSACTIONS_COLLECTION)
                .document(transactionId)
                .set(transaction)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Saved point trading history"))
                .addOnFailureListener(e -> Log.e(TAG, "History saving error: " + e.getMessage()));
    }

    /**
     * Lấy thông tin user theo ID
     */
    public void getUserById(String userId, UserCallback callback) {
        db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            callback.onSuccess(user);
                        } else {
                            callback.onFailure("Unable to parse user data");
                        }
                    } else {
                        callback.onFailure("No user information found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error obtaining user information: " + e.getMessage());
                    callback.onFailure("Database connection error");
                });
    }

    /**
     * Trừ điểm khi đổi voucher
     */
    public void redeemPoints(String userId, int pointsToRedeem, String voucherId, PointCallback callback) {
        db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            if (user.getLoyalty_points() >= pointsToRedeem) {
                                int newPoints = user.getLoyalty_points() - pointsToRedeem;

                                Map<String, Object> updates = new HashMap<>();
                                updates.put("loyalty_points", newPoints);

                                db.collection(USERS_COLLECTION)
                                        .document(userId)
                                        .update(updates)
                                        .addOnSuccessListener(aVoid -> {
                                            savePointTransaction(userId, pointsToRedeem, "REDEEMED", voucherId);
                                            callback.onSuccess(pointsToRedeem);
                                        })
                                        .addOnFailureListener(e -> callback.onFailure("Unable to update points"));
                            } else {
                                callback.onFailure("Not enough points to redeem the voucher");
                            }
                        }
                    } else {
                        callback.onFailure("No user information found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure("Database connection error"));
    }

    /**
     * Lấy ngày hiện tại theo định dạng dd/MM/yyyy
     */
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Kiểm tra xem có thể tích điểm cho đơn hàng này không
     */
    public boolean canEarnPoints(Order order) {
        return order != null &&
                order.getStatus().equals("SUCCESS") &&
                order.getTotal_amount() > 0;
    }
}