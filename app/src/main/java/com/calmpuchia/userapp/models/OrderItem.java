package com.calmpuchia.userapp.models;

import java.util.List;

public class OrderItem {
    private String item_id;
    private String item_name;
    private int quantity;
    private double price;
    private double subtotal;

    public OrderItem() {}

    public OrderItem(String item_id, String item_name, int quantity, double price, double subtotal) {
        this.item_id = item_id;
        this.item_name = item_name;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }

    // Getters and Setters
    public String getItem_id() { return item_id; }
    public void setItem_id(String item_id) { this.item_id = item_id; }
    public String getItem_name() { return item_name; }
    public void setItem_name(String item_name) { this.item_name = item_name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
