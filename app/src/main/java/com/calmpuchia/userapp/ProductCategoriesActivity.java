package com.calmpuchia.userapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProductCategoriesActivity extends AppCompatActivity {

    private CardView CateA, CateB, CateC, CateD, CateE, CateF, CateG, CateH, CateI, CateJ, CateK, CateL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_categories);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ các CardView từ XML
        TextView tvCateA = findViewById(R.id.tvCateA);
        TextView tvCateB = findViewById(R.id.tvCateB);
        TextView tvCateC = findViewById(R.id.tvCateC);
        TextView tvCateD = findViewById(R.id.tvCateD);
        TextView tvCateE = findViewById(R.id.tvCateE);
        TextView tvCateF = findViewById(R.id.tvCateF);
        TextView tvCateG = findViewById(R.id.tvCateG);
        TextView tvCateH = findViewById(R.id.tvCateH);
        TextView tvCateI = findViewById(R.id.tvCateI);
        TextView tvCateJ = findViewById(R.id.tvCateJ);
        TextView tvCateK = findViewById(R.id.tvCateK);
        TextView tvCateL = findViewById(R.id.tvCateL);



        // Gán sự kiện click
        tvCateA.setOnClickListener(v -> openProductList("1490000000000000000"));
        tvCateB.setOnClickListener(v -> openProductList("2140000000000000000"));
        tvCateC.setOnClickListener(v -> openProductList("1660000000000000000"));
        tvCateD.setOnClickListener(v -> openProductList("1780000000000000000"));
        tvCateE.setOnClickListener(v -> openProductList("1600000000000000000"));
        tvCateF.setOnClickListener(v -> openProductList("2150000000000000000"));
        tvCateG.setOnClickListener(v -> openProductList("1940000000000000000"));
        tvCateH.setOnClickListener(v -> openProductList("2000000000000000000"));
        tvCateI.setOnClickListener(v -> openProductList("2200000000000000000"));
        tvCateJ.setOnClickListener(v -> openProductList("2080000000000000000"));
        tvCateK.setOnClickListener(v -> openProductList("2190000000000000000"));
        tvCateL.setOnClickListener(v -> openProductList("2130000000000000000"));
    }

    private void openProductList(String categoryId) {
        if (categoryId == null || categoryId.trim().isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy danh mục!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(ProductCategoriesActivity.this, ProductListActivity.class);
        intent.putExtra("category_id", categoryId);
        startActivity(intent);
    }

    private void loadCategories() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("categories").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String categoryName = document.getString("name");
                            String categoryId = document.getString("category_id");

                            // Tạo một TextView động cho danh mục
                            TextView categoryView = new TextView(this);
                            categoryView.setText(categoryName);
                            categoryView.setTextSize(16);
                            categoryView.setPadding(20, 12, 20, 12);
                            categoryView.setOnClickListener(v -> openProductList(categoryId));

                            // Thêm vào layout chứa danh mục
                            ((LinearLayout) findViewById(R.id.categoryLayout)).addView(categoryView);
                        }
                    } else {
                        Log.e("Firestore", "Lỗi tải danh mục", task.getException());
                    }
                });
    }

}