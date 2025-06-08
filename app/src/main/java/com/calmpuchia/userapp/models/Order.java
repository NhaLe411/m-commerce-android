package com.calmpuchia.userapp.models;

import java.util.List;

public class Order {
    private String order_id;
    private String user_id;
    private double total_amount;
    private String order_date;
    private String status;
    private List<OrderItem> items;
    private String store_location;
    private boolean is_store_purchase;

    // Constructor không tham số - RẤT QUAN TRỌNG cho Firestore
    public Order() {
    }

    public Order(String order_id, String user_id, double total_amount, String order_date,
                 String status, List<OrderItem> items, String store_location, boolean is_store_purchase) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.total_amount = total_amount;
        this.order_date = order_date;
        this.status = status;
        this.items = items;
        this.store_location = store_location;
        this.is_store_purchase = is_store_purchase;
    }

    // Getters and Setters
    public String getOrder_id() { return order_id; }
    public void setOrder_id(String order_id) { this.order_id = order_id; }
    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }
    public double getTotal_amount() { return total_amount; }
    public void setTotal_amount(double total_amount) { this.total_amount = total_amount; }
    public String getOrder_date() { return order_date; }
    public void setOrder_date(String order_date) { this.order_date = order_date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public String getStore_location() { return store_location; }
    public void setStore_location(String store_location) { this.store_location = store_location; }
    public boolean isIs_store_purchase() { return is_store_purchase; }
    public void setIs_store_purchase(boolean is_store_purchase) { this.is_store_purchase = is_store_purchase; }
}