package com.calmpuchia.userapp.models;

import java.util.List;

public class Voucher {
    private String voucher_id;
    private String title;
    private String description;
    private int points_required;
    private String voucher_code;
    private String expiry_date;
    private boolean is_active;
    private boolean is_store_only;

    public Voucher() {}

    public Voucher(String voucher_id, String title, String description, int points_required,
                   String voucher_code, String expiry_date, boolean is_active, boolean is_store_only) {
        this.voucher_id = voucher_id;
        this.title = title;
        this.description = description;
        this.points_required = points_required;
        this.voucher_code = voucher_code;
        this.expiry_date = expiry_date;
        this.is_active = is_active;
        this.is_store_only = is_store_only;
    }

    // Getters and Setters
    public String getVoucher_id() { return voucher_id; }
    public void setVoucher_id(String voucher_id) { this.voucher_id = voucher_id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getPoints_required() { return points_required; }
    public void setPoints_required(int points_required) { this.points_required = points_required; }
    public String getVoucher_code() { return voucher_code; }
    public void setVoucher_code(String voucher_code) { this.voucher_code = voucher_code; }
    public String getExpiry_date() { return expiry_date; }
    public void setExpiry_date(String expiry_date) { this.expiry_date = expiry_date; }
    public boolean isIs_active() { return is_active; }
    public void setIs_active(boolean is_active) { this.is_active = is_active; }
    public boolean isIs_store_only() { return is_store_only; }
    public void setIs_store_only(boolean is_store_only) { this.is_store_only = is_store_only; }
}
