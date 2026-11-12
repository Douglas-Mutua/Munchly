package com.example.munchly;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.munchly.data.MunchlyRepository;
import com.example.munchly.model.Order;
import com.example.munchly.model.Payment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AdminOrdersActivity extends AppCompatActivity {

    private LinearLayout adminOrdersContainer;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        adminOrdersContainer = findViewById(R.id.adminOrdersContainer);
        btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        loadCustomerOrdersAndPayments();
        setupAdminNav();
    }

    private void loadCustomerOrdersAndPayments() {
        if (adminOrdersContainer == null) return;
        adminOrdersContainer.removeAllViews();

        List<Order> orders = MunchlyRepository.getInstance().getOrders();
        List<Payment> payments = MunchlyRepository.getInstance().getPayments();

        if (orders == null || orders.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No customer orders placed yet.");
            emptyView.setTextSize(16f);
            emptyView.setTextColor(0xFF888888);
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setPadding(0, 64, 0, 0);
            adminOrdersContainer.addView(emptyView);
            return;
        }

        for (Order order : orders) {
            if (order == null) continue;

            CardView card = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, 16);
            card.setLayoutParams(cardParams);
            card.setRadius(16f);
            card.setCardElevation(4f);
            card.setCardBackgroundColor(0xFFFFFFFF);

            LinearLayout contentLayout = new LinearLayout(this);
            contentLayout.setOrientation(LinearLayout.VERTICAL);
            contentLayout.setPadding(20, 20, 20, 20);

            // --- Order ID ---
            TextView tvOrderHeader = new TextView(this);
            tvOrderHeader.setText("Order ID: " + safe(order.getOrderId(), "N/A"));
            tvOrderHeader.setTypeface(null, Typeface.BOLD);
            tvOrderHeader.setTextColor(0xFF333333);
            tvOrderHeader.setTextSize(16f);
            contentLayout.addView(tvOrderHeader);

            // --- Customer ---
            TextView tvCustomer = new TextView(this);
            tvCustomer.setText("Customer: " + safe(order.getCustomerName(), "Unknown")
                    + " (" + safe(order.getPhone(), "N/A") + ")");
            tvCustomer.setTextColor(0xFF555555);
            tvCustomer.setTextSize(14f);
            contentLayout.addView(tvCustomer);

            // --- Address ---
            TextView tvAddress = new TextView(this);
            tvAddress.setText("Delivery Address: " + safe(order.getDeliveryAddress(), "N/A"));
            tvAddress.setTextColor(0xFF555555);
            tvAddress.setTextSize(14f);
            contentLayout.addView(tvAddress);

            // --- Total ---
            TextView tvTotal = new TextView(this);
            tvTotal.setText("Total Amount: KES " + String.format("%.2f", order.getTotalAmount()));
            tvTotal.setTypeface(null, Typeface.BOLD);
            tvTotal.setTextColor(0xFFFF6B00);
            tvTotal.setTextSize(14f);
            contentLayout.addView(tvTotal);

            // --- Status ---
            String orderStatus = safe(order.getOrderStatus(), "Pending");
            TextView tvStatus = new TextView(this);
            tvStatus.setText("Status: " + orderStatus);
            tvStatus.setTypeface(null, Typeface.BOLD);
            tvStatus.setTextColor("Paid".equalsIgnoreCase(orderStatus) || "Delivered".equalsIgnoreCase(orderStatus) ? 0xFF4CAF50 : 0xFFFF9800);
            tvStatus.setTextSize(14f);
            contentLayout.addView(tvStatus);

            // --- M-Pesa Receipt ---
            String paymentRef = "M-Pesa Receipt: None / Pending";
            if (payments != null && order.getOrderId() != null) {
                for (Payment p : payments) {
                    if (p == null) continue;
                    if (order.getOrderId().equals(p.getOrderId())) {
                        String receipt = safe(p.getMpesaReceiptNumber(), "N/A");
                        paymentRef = "M-Pesa Receipt: " + receipt;
                        break;
                    }
                }
            }

            TextView tvPaymentRef = new TextView(this);
            tvPaymentRef.setText(paymentRef);
            tvPaymentRef.setTextColor(0xFF666666);
            tvPaymentRef.setTextSize(13f);
            tvPaymentRef.setPadding(0, 4, 0, 8);
            contentLayout.addView(tvPaymentRef);

            // --- Admin Control Action Buttons ---
            LinearLayout actionLayout = new LinearLayout(this);
            actionLayout.setOrientation(LinearLayout.HORIZONTAL);
            actionLayout.setPadding(0, 8, 0, 0);

            Button btnMarkPaid = new Button(this);
            btnMarkPaid.setText("Mark Paid");
            btnMarkPaid.setTextSize(12f);
            btnMarkPaid.setBackgroundTintList(ColorStateList.valueOf(0xFF4CAF50));
            btnMarkPaid.setTextColor(0xFFFFFFFF);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, 110, 1f);
            btnParams.setMargins(0, 0, 4, 0);
            btnMarkPaid.setLayoutParams(btnParams);
            btnMarkPaid.setOnClickListener(v -> {
                order.setOrderStatus("Paid");
                Toast.makeText(this, "Order marked as Paid", Toast.LENGTH_SHORT).show();
                loadCustomerOrdersAndPayments();
            });
            actionLayout.addView(btnMarkPaid);

            Button btnMarkDelivered = new Button(this);
            btnMarkDelivered.setText("Delivered");
            btnMarkDelivered.setTextSize(12f);
            btnMarkDelivered.setBackgroundTintList(ColorStateList.valueOf(0xFF2196F3));
            btnMarkDelivered.setTextColor(0xFFFFFFFF);
            btnParams = new LinearLayout.LayoutParams(0, 110, 1f);
            btnParams.setMargins(4, 0, 4, 0);
            btnMarkDelivered.setLayoutParams(btnParams);
            btnMarkDelivered.setOnClickListener(v -> {
                order.setOrderStatus("Delivered");
                Toast.makeText(this, "Order marked as Delivered", Toast.LENGTH_SHORT).show();
                loadCustomerOrdersAndPayments();
            });
            actionLayout.addView(btnMarkDelivered);

            Button btnDelete = new Button(this);
            btnDelete.setText("Delete");
            btnDelete.setTextSize(12f);
            btnDelete.setBackgroundTintList(ColorStateList.valueOf(0xFFE53935));
            btnDelete.setTextColor(0xFFFFFFFF);
            btnParams = new LinearLayout.LayoutParams(0, 110, 1f);
            btnParams.setMargins(4, 0, 0, 0);
            btnDelete.setLayoutParams(btnParams);
            btnDelete.setOnClickListener(v -> {
                orders.remove(order);
                Toast.makeText(this, "Order deleted", Toast.LENGTH_SHORT).show();
                loadCustomerOrdersAndPayments();
            });
            actionLayout.addView(btnDelete);

            contentLayout.addView(actionLayout);

            card.addView(contentLayout);
            adminOrdersContainer.addView(card);
        }
    }

    private void confirmAdminLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Admin Logout")
                .setMessage("Are you sure you want to log out of the Admin Portal?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    MunchlyRepository.getInstance().setCurrentUser(null);
                    Intent intent = new Intent(this, AdminLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupAdminNav() {
        ImageView ivDash = findViewById(R.id.navAdminDashboardIcon);
        ImageView ivProd = findViewById(R.id.navAdminProductsIcon);
        ImageView ivOrd = findViewById(R.id.navAdminOrdersIcon);

        if (ivDash != null) ivDash.setImageTintList(ColorStateList.valueOf(0xFF888888));
        if (ivProd != null) ivProd.setImageTintList(ColorStateList.valueOf(0xFF888888));
        if (ivOrd != null) ivOrd.setImageTintList(ColorStateList.valueOf(0xFFFF6B00));

        View navDashboard = findViewById(R.id.navAdminDashboard);
        View navProducts = findViewById(R.id.navAdminProducts);
        View navOrders = findViewById(R.id.navAdminOrders);
        View navLogout = findViewById(R.id.navAdminLogout);

        if (navDashboard != null) {
            navDashboard.setOnClickListener(v -> {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                finish();
            });
        }
        if (navProducts != null) {
            navProducts.setOnClickListener(v -> {
                startActivity(new Intent(this, AdminProductManagementActivity.class));
                finish();
            });
        }
        if (navOrders != null) {
            navOrders.setOnClickListener(v -> {
                // Already on orders
            });
        }
        if (navLogout != null) {
            navLogout.setOnClickListener(v -> confirmAdminLogout());
        }
    }

    /** Returns fallback if value is null or blank. */
    private String safe(String value, String fallback) {
        return (value == null || value.trim().isEmpty()) ? fallback : value;
    }
}
