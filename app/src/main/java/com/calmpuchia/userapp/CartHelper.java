package com.calmpuchia.userapp;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartHelper {

    private static final String COLLECTION_NAME = "Cart";
    private FirebaseFirestore db;

    public CartHelper() {
        db = FirebaseFirestore.getInstance();
    }

    // Interface for callbacks
    public interface CartCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface CartDataCallback {
        void onSuccess(Map<String, Object> cartData);
        void onFailure(String error);
    }

    // Get cart data by cart_id
    public void getCartData(String cartId, CartDataCallback callback) {
        db.collection(COLLECTION_NAME)
                .document(cartId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onSuccess(documentSnapshot.getData());
                    } else {
                        callback.onFailure("Cart not found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Update cart items (remove specific item)
    public void removeItemFromCart(String cartId, String itemId, CartCallback callback) {
        db.collection(COLLECTION_NAME)
                .document(cartId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) documentSnapshot.get("items");
                        if (items != null) {
                            // Remove item with matching ID
                            items.removeIf(item -> itemId.equals(item.get("id")));

                            // Update cart
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("items", items);
                            updates.put("update_at", FieldValue.serverTimestamp());

                            updateCart(cartId, updates, callback);
                        } else {
                            callback.onFailure("No items found in cart");
                        }
                    } else {
                        callback.onFailure("Cart not found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Clear all items from cart
    public void clearAllItems(String cartId, CartCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("items", new java.util.ArrayList<>()); // Empty list
        updates.put("update_at", FieldValue.serverTimestamp());

        updateCart(cartId, updates, callback);
    }

    // Update item quantity
    public void updateItemQuantity(String cartId, String itemId, int newQuantity, CartCallback callback) {
        if (newQuantity <= 0) {
            removeItemFromCart(cartId, itemId, callback);
            return;
        }

        db.collection(COLLECTION_NAME)
                .document(cartId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) documentSnapshot.get("items");
                        if (items != null) {
                            // Find and update item quantity
                            for (Map<String, Object> item : items) {
                                if (itemId.equals(item.get("id"))) {
                                    item.put("quantity", newQuantity);
                                    break;
                                }
                            }

                            // Update cart
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("items", items);
                            updates.put("update_at", FieldValue.serverTimestamp());

                            updateCart(cartId, updates, callback);
                        } else {
                            callback.onFailure("No items found in cart");
                        }
                    } else {
                        callback.onFailure("Cart not found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Generic update method
    private void updateCart(String cartId, Map<String, Object> updates, CartCallback callback) {
        db.collection(COLLECTION_NAME)
                .document(cartId)
                .update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Get cart by user_id
    public void getCartByUserId(String userId, CartDataCallback callback) {
        db.collection(COLLECTION_NAME)
                .whereEqualTo("user_id", userId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Map<String, Object> cartData = document.getData();
                        if (cartData != null) {
                            cartData.put("cart_id", document.getId()); // Add cart_id to data
                        }
                        callback.onSuccess(cartData);
                    } else {
                        callback.onFailure("No cart found for this user");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}