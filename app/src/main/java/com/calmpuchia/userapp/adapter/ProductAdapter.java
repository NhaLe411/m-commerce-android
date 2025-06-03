package com.calmpuchia.userapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.calmpuchia.userapp.R;
import com.calmpuchia.userapp.models.Products;
import com.squareup.picasso.Picasso;
import android.graphics.Paint;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Products> productList;

    public ProductAdapter(Context context, List<Products> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Products product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productName.setText("" + product.getProduct_id());
        holder.productName.setText("" + product.getCreated_at());
        holder.productName.setText("" + product.getUpdated_at());
        holder.productName.setText("" + product.getBrand());
        holder.productName.setText("" + product.getDescription());;
        holder.productPrice.setText("" + product.getPrice());
        holder.productPrice.setText("" + product.getDiscount_price());
        holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.productName.setText("" + product.getStock());
        holder.productPrice.setText("" + product.getCategory_id());
        holder.productTags.setText(""+ product.getTags());



        // Kiểm tra và tải ảnh từ Firestore bằng Picasso
        if (product.getImage_url() != null && !product.getImage_url().isEmpty()) {
            Picasso.get().load(product.getImage_url()).into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.placeholder_image); // Ảnh mặc định
        }
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView originalPrice;
        TextView productName, productPrice, productTags;
        ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.discountPrice);
            productPrice= itemView.findViewById(R.id.originalPrice);
            productImage = itemView.findViewById(R.id.productImage);
            productTags = itemView.findViewById(R.id.productTags);
        }
    }
}