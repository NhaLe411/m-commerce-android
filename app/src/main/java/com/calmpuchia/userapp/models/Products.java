package com.calmpuchia.userapp.models;

import java.util.Date;

public class Products {
    private String product_id;
    private String name;
    private String description;
    private String brand;
    private String image_url;
    private double price;
    private double discount_price;
    private int stock;
    private long category_id;
    private String tags;
    private String updated_at;
    private String created_at;

    // Constructor không tham số - RẤT QUAN TRỌNG cho Firestore
    public Products() {}

    // Getters and Setters
    public String getProduct_id() { return product_id; }
    public void setProduct_id(String product_id) { this.product_id = product_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getDiscount_price() { return discount_price; }
    public void setDiscount_price(double discount_price) { this.discount_price = discount_price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public long getCategory_id() { return category_id; }
    public void setCategory_id(long category_id) { this.category_id = category_id; }

    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}