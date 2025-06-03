package com.calmpuchia.userapp;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.calmpuchia.userapp.adapter.ProductAdapter;
import com.calmpuchia.userapp.models.Products;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private ArrayList<Products> productList;
    private ProgressBar progressBar;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // 1. Nhận category_id
        String categoryId = getIntent().getStringExtra("category_id");
        if (categoryId == null || categoryId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy danh mục!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu category_id không hợp lệ
            return;
        }

        // 2. Setup View
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 3. Kết nối Firestore
        db = FirebaseFirestore.getInstance();

        // 4. Tải sản phẩm theo category
        loadProductsByCategory(categoryId);
    }

    private void loadProductsByCategory(String categoryId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .whereEqualTo("category_id", categoryId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(this, "Không có sản phẩm nào!", Toast.LENGTH_SHORT).show();
                        } else {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Products product = doc.toObject(Products.class);
                                productList.add(product);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Lỗi tải danh mục", task.getException());
                    }
                });
    }
}