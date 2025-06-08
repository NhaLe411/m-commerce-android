package com.calmpuchia.userapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.calmpuchia.userapp.models.PointTransaction;
import com.calmpuchia.userapp.models.User;
import com.calmpuchia.userapp.services.PointService;
import java.util.ArrayList;
import java.util.List;

// Activity hiển thị lịch sử tích điểm
public class PointHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvCurrentPoints;
    private PointHistoryAdapter adapter;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_history);

        initViews();
        initData();
        loadPointHistory();
        loadCurrentPoints();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        tvCurrentPoints = findViewById(R.id.tv_current_points);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PointHistoryAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        db = FirebaseFirestore.getInstance();
        userId = getIntent().getStringExtra("user_id");
    }

    private void loadPointHistory() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("point_transactions")
                .whereEqualTo("user_id", userId)
                .orderBy("transaction_date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<PointTransaction> transactions = new ArrayList<>();
                    queryDocumentSnapshots.forEach(doc -> {
                        PointTransaction transaction = doc.toObject(PointTransaction.class);
                        transactions.add(transaction);
                    });

                    adapter.updateData(transactions);
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load point history", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadCurrentPoints() {
        PointService pointService = new PointService(this);
        pointService.getUserById(userId, new PointService.UserCallback() {
            @Override
            public void onSuccess(User user) {
                tvCurrentPoints.setText(String.format("Current Points: %d", user.getLoyalty_points()));
            }

            @Override
            public void onFailure(String error) {
                tvCurrentPoints.setText("Current Points: --");
            }
        });
    }

    // Adapter cho RecyclerView lịch sử điểm
    private class PointHistoryAdapter extends RecyclerView.Adapter<PointHistoryAdapter.ViewHolder> {
        private List<PointTransaction> transactions = new ArrayList<>();

        public void updateData(List<PointTransaction> newTransactions) {
            this.transactions = newTransactions;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_point_transaction, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            PointTransaction transaction = transactions.get(position);
            holder.bind(transaction);
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvPoints, tvType, tvDate, tvDescription;

            ViewHolder(View itemView) {
                super(itemView);
                tvPoints = itemView.findViewById(R.id.tvPoints);
                tvType = itemView.findViewById(R.id.tvType);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvDescription = itemView.findViewById(R.id.tvDescription);
            }

            void bind(PointTransaction transaction) {
                if (transaction.getTransaction_type().equals("EARNED")) {
                    tvPoints.setText("+" + transaction.getPoints());
                    tvPoints.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    tvType.setText("Earn points");
                } else {
                    tvPoints.setText("-" + transaction.getPoints());
                    tvPoints.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    tvType.setText("Change voucher");
                }

                tvDate.setText(transaction.getTransaction_date());
                tvDescription.setText(transaction.getDescription());
            }
        }
    }
}