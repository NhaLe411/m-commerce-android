package com.calmpuchia.userapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    // Cart item class
    public static class CartItem {
        public String id;
        public String name;
        public double price;
        public int quantity;
        public int imageRes;

        public CartItem(String id, String name, double price, int quantity, int imageRes) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.imageRes = imageRes;
        }

        public double getTotalPrice() {
            return price * quantity;
        }
    }

    private List<CartItem> cartItems;
    private LinearLayout cartContainer;
    private LinearLayout emptyCartLayout;
    private LinearLayout checkoutSection;
    private TextView totalText;
    private NumberFormat currencyFormat;
    private CartHelper cartHelper;
    private String cartId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initCart();
        setupListeners();
        loadCartFromFirebase();
    }

    private void initViews() {
        cartContainer = findViewById(R.id.cartContainer);
        emptyCartLayout = findViewById(R.id.emptyCartLayout);
        checkoutSection = findViewById(R.id.checkoutSection);
        totalText = findViewById(R.id.totalText);

        // Currency format for Vietnamese Dong
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    private void initCart() {
        cartItems = new ArrayList<>();
        cartHelper = new CartHelper();

        // Get user_id from Intent or SharedPreferences
        userId = getIntent().getStringExtra("user_id");
        cartId = getIntent().getStringExtra("cart_id");

        // If you don't have cart_id, you can get it by user_id later
    }

    private void setupListeners() {
        // Back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Clear all button
        TextView btnClearAll = findViewById(R.id.btnClearAll);
        btnClearAll.setOnClickListener(v -> showClearAllDialog());

        // Checkout button
        findViewById(R.id.btn_checkout).setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            } else {
                // Handle checkout process
                Toast.makeText(this, "Proceeding to checkout...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCartDisplay() {
        if (cartItems.isEmpty()) {
            showEmptyCart();
        } else {
            showCartItems();
            updateTotal();
        }
    }

    private void showEmptyCart() {
        emptyCartLayout.setVisibility(View.VISIBLE);
        checkoutSection.setVisibility(View.GONE);

        // Hide all product items
        LinearLayout productItem = findViewById(R.id.productItem1);
        if (productItem != null) {
            productItem.setVisibility(View.GONE);
        }
    }

    private void showCartItems() {
        emptyCartLayout.setVisibility(View.GONE);
        checkoutSection.setVisibility(View.VISIBLE);

        // Show product items and update their data
        updateProductItems();
    }

    private void updateProductItems() {
        // Update the existing product item with cart data
        LinearLayout productItem = findViewById(R.id.productItem1);
        if (productItem != null && !cartItems.isEmpty()) {
            productItem.setVisibility(View.VISIBLE);

            CartItem item = cartItems.get(0); // First item

            // Update quantity display
            TextView quantityText = findViewById(R.id.quantity_text);
            quantityText.setText(String.valueOf(item.quantity));

            // Setup quantity buttons
            ImageButton btnMinus = findViewById(R.id.btnMinus);
            ImageButton btnPlus = findViewById(R.id.btnPlus);
            ImageButton btnDelete = findViewById(R.id.btnDelete1);

            btnMinus.setOnClickListener(v -> changeQuantity(0, -1));
            btnPlus.setOnClickListener(v -> changeQuantity(0, 1));
            btnDelete.setOnClickListener(v -> showDeleteDialog(0));
        }
    }

    private void changeQuantity(int itemIndex, int change) {
        if (itemIndex >= 0 && itemIndex < cartItems.size()) {
            CartItem item = cartItems.get(itemIndex);
            int newQuantity = item.quantity + change;

            if (newQuantity <= 0) {
                showDeleteDialog(itemIndex);
            } else {
                // Update Firebase
                cartHelper.updateItemQuantity(cartId, item.id, newQuantity, new CartHelper.CartCallback() {
                    @Override
                    public void onSuccess() {
                        item.quantity = newQuantity;
                        updateCartDisplay();
                        Toast.makeText(CartActivity.this, "Updated quantity to " + newQuantity, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(CartActivity.this, "Error updating quantity: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void showDeleteDialog(int itemIndex) {
        if (itemIndex >= 0 && itemIndex < cartItems.size()) {
            CartItem item = cartItems.get(itemIndex);

            new AlertDialog.Builder(this)
                    .setTitle("Remove Item")
                    .setMessage("Are you sure you want to remove " + item.name + " from your cart?")
                    .setPositiveButton("Remove", (dialog, which) -> {
                        removeItem(itemIndex);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void showClearAllDialog() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is already empty", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Clear Cart")
                .setMessage("Are you sure you want to remove all items from your cart?")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    clearAllItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeItem(int itemIndex) {
        if (itemIndex >= 0 && itemIndex < cartItems.size()) {
            CartItem item = cartItems.get(itemIndex);

            cartHelper.removeItemFromCart(cartId, item.id, new CartHelper.CartCallback() {
                @Override
                public void onSuccess() {
                    cartItems.remove(itemIndex);
                    updateCartDisplay();
                    Toast.makeText(CartActivity.this, item.name + " removed from cart", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(CartActivity.this, "Error removing item: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void clearAllItems() {
        int itemCount = cartItems.size();

        cartHelper.clearAllItems(cartId, new CartHelper.CartCallback() {
            @Override
            public void onSuccess() {
                cartItems.clear();
                updateCartDisplay();
                Toast.makeText(CartActivity.this, itemCount + " items removed from cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(CartActivity.this, "Error clearing cart: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }

        String formattedTotal = "₫" + currencyFormat.format(total);
        totalText.setText("Total: " + formattedTotal);
    }

    private void updateCartCollection() {
        // This method is no longer needed as we update Firebase directly in other methods
        System.out.println("Cart updated. Items count: " + cartItems.size());
    }

    // Load cart data from Firebase
    private void loadCartFromFirebase() {
        if (cartId != null) {
            // Load by cart_id
            cartHelper.getCartData(cartId, new CartHelper.CartDataCallback() {
                @Override
                public void onSuccess(Map<String, Object> cartData) {
                    parseCartData(cartData);
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(CartActivity.this, "Error loading cart: " + error, Toast.LENGTH_SHORT).show();
                    updateCartDisplay();
                }
            });
        } else if (userId != null) {
            // Load by user_id
            cartHelper.getCartByUserId(userId, new CartHelper.CartDataCallback() {
                @Override
                public void onSuccess(Map<String, Object> cartData) {
                    cartId = (String) cartData.get("cart_id");
                    parseCartData(cartData);
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(CartActivity.this, "Error loading cart: " + error, Toast.LENGTH_SHORT).show();
                    updateCartDisplay();
                }
            });
        } else {
            // No cart info available
            updateCartDisplay();
        }
    }

    private void parseCartData(Map<String, Object> cartData) {
        cartItems.clear();

        List<Map<String, Object>> items = (List<Map<String, Object>>) cartData.get("items");
        if (items != null) {
            for (Map<String, Object> itemMap : items) {
                String id = (String) itemMap.get("id");
                String name = (String) itemMap.get("name");
                Double price = (Double) itemMap.get("price");
                Long quantity = (Long) itemMap.get("quantity");

                if (id != null && name != null && price != null && quantity != null) {
                    cartItems.add(new CartItem(id, name, price, quantity.intValue(), R.mipmap.ic_productA));
                }
            }
        }

        updateCartDisplay();
    }

    // Optional: Save cart to SharedPreferences
    private void saveCartToPreferences() {
        // Not needed with Firebase implementation
    }

    // Optional: Load cart from SharedPreferences
    private void loadCartFromPreferences() {
        // Not needed with Firebase implementation
    }

    // Method to add item to cart (can be called from other activities)
    public void addToCart(CartItem item) {
        // Check if item already exists
        for (CartItem existingItem : cartItems) {
            if (existingItem.id.equals(item.id)) {
                existingItem.quantity += item.quantity;
                updateCartDisplay();
                return;
            }
        }

        // Add new item
        cartItems.add(item);
        updateCartDisplay();
    }

    // Method to get cart items count
    public int getCartItemsCount() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.quantity;
        }
        return total;
    }

    // Method to get cart total value
    public double getCartTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }
}