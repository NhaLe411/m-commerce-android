package com.calmpuchia.userapp.models;

import java.util.List;

public class PointTransaction {
    private String transaction_id;
    private String user_id;
    private int points;
    private String transaction_type; // "EARNED" hoặc "REDEEMED"
    private String transaction_date;
    private String order_id;
    private String description;

    public PointTransaction() {}

    public PointTransaction(String transaction_id, String user_id, int points, String transaction_type,
                            String transaction_date, String order_id, String description) {
        this.transaction_id = transaction_id;
        this.user_id = user_id;
        this.points = points;
        this.transaction_type = transaction_type;
        this.transaction_date = transaction_date;
        this.order_id = order_id;
        this.description = description;
    }

    // Getters and Setters
    public String getTransaction_id() { return transaction_id; }
    public void setTransaction_id(String transaction_id) { this.transaction_id = transaction_id; }
    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public String getTransaction_type() { return transaction_type; }
    public void setTransaction_type(String transaction_type) { this.transaction_type = transaction_type; }
    public String getTransaction_date() { return transaction_date; }
    public void setTransaction_date(String transaction_date) { this.transaction_date = transaction_date; }
    public String getOrder_id() { return order_id; }
    public void setOrder_id(String order_id) { this.order_id = order_id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
