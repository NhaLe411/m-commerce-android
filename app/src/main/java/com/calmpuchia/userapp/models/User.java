package com.calmpuchia.userapp.models;

public class User {
    private String user_id;
    private String created_at;
    private String dob;
    private String email;
    private String gender;
    private String last_login;
    private int loyalty_points;
    private String name;
    private String phone;

    // Constructor mặc định (bắt buộc cho Firebase)
    public User() {}

    // Constructor đầy đủ
    public User(String user_id, String created_at, String dob, String email,
                String gender, String last_login, int loyalty_points, String name, String phone) {
        this.user_id = user_id;
        this.created_at = created_at;
        this.dob = dob;
        this.email = email;
        this.gender = gender;
        this.last_login = last_login;
        this.loyalty_points = loyalty_points;
        this.name = name;
        this.phone = phone;
    }

    // Getters
    public String getUser_id() { return user_id; }
    public String getCreated_at() { return created_at; }
    public String getDob() { return dob; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
    public String getLast_login() { return last_login; }
    public int getLoyalty_points() { return loyalty_points; }
    public String getName() { return name; }
    public String getPhone() { return phone; }

    // Setters
    public void setUser_id(String user_id) { this.user_id = user_id; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
    public void setDob(String dob) { this.dob = dob; }
    public void setEmail(String email) { this.email = email; }
    public void setGender(String gender) { this.gender = gender; }
    public void setLast_login(String last_login) { this.last_login = last_login; }
    public void setLoyalty_points(int loyalty_points) { this.loyalty_points = loyalty_points; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }

    // Phương thức thêm điểm
    public void addPoints(int points) {
        this.loyalty_points += points;
    }

    // Phương thức trừ điểm (dùng voucher)
    public boolean deductPoints(int points) {
        if (this.loyalty_points >= points) {
            this.loyalty_points -= points;
            return true;
        }
        return false;
    }
}
