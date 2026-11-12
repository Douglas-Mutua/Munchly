package com.example.munchly;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.munchly.data.MunchlyRepository;
import com.example.munchly.model.Order;
import com.example.munchly.model.Payment;

public class OrderHistoryActivity extends AppCompatActivity {

    private LinearLayout ordersContainer;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        ordersContainer = findViewById(R.id.ordersContainer);
        btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        loadOrders();
        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        if (ordersContainer == null) return;
        ordersContainer.removeAllViews();

        if (MunchlyRepository.getInstance().getOrders().isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No orders placed yet 📦");
            emptyView.setTextSize(16f);
            emptyView.setTextColor(0xFF888888);
            emptyView.setPadding(0, 32, 0, 32);
            ordersContainer.addView(emptyView);
            return;
        }

        for (Order order : MunchlyRepository.getInstance().getOrders()) {
            if (order == null) continue;

            String receipt = "N/A (Awaiting M-Pesa Callback)";
            for (Payment p : MunchlyRepository.getInstance().getPayments()) {
                if (p != null && order.getOrderId() != null
                        && order.getOrderId().equals(p.getOrderId())) {
                    receipt = p.getMpesaReceiptNumber() != null
                            ? p.getMpesaReceiptNumber() : "N/A";
                    break;
                }
            }

            CardView card = new CardView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 16);
            card.setLayoutParams(lp);
            card.setRadius(16f);
            card.setCardBackgroundColor(0xFFFFFFFF);
            card.setCardElevation(2f);
            card.setClickable(true);
            card.setFocusable(true);

            LinearLayout content = new LinearLayout(this);
            content.setOrientation(LinearLayout.VERTICAL);
            content.setPadding(20, 20, 20, 20);

            TextView tvId = new TextView(this);
            tvId.setText("Order: " + order.getOrderId());
            tvId.setTextSize(15f);
            tvId.setTypeface(null, Typeface.BOLD);
            tvId.setTextColor(0xFF333333);
            content.addView(tvId);

            TextView tvDetails = new TextView(this);
            tvDetails.setText("Amount: KES " + String.format("%.2f", order.getTotalAmount())
                    + "\nStatus: " + order.getOrderStatus()
                    + "\nPhone: " + order.getPhone()
                    + "\nM-Pesa Receipt: " + receipt);
            tvDetails.setTextSize(14f);
            tvDetails.setTextColor(0xFF666666);
            tvDetails.setPadding(0, 8, 0, 0);
            content.addView(tvDetails);

            card.addView(content);

            // Tap to open details
            final String oid = order.getOrderId();
            card.setOnClickListener(v -> {
                Intent i = new Intent(this, OrderDetailsActivity.class);
                i.putExtra(OrderDetailsActivity.EXTRA_ORDER_ID, oid);
                startActivity(i);
            });

            ordersContainer.addView(card);
        }
    }

    private void setupBottomNav() {
        ImageView ivHome = findViewById(R.id.navHomeIcon);
        ImageView ivCart = findViewById(R.id.navCartIcon);
        ImageView ivOrders = findViewById(R.id.navOrdersIcon);
        ImageView ivProfile = findViewById(R.id.navProfileIcon);

        if (ivHome != null) ivHome.setImageTintList(ColorStateList.valueOf(0xFF888888));
        if (ivCart != null) ivCart.setImageTintList(ColorStateList.valueOf(0xFF888888));
        if (ivOrders != null) ivOrders.setImageTintList(ColorStateList.valueOf(0xFFFF6B00));
        if (ivProfile != null) ivProfile.setImageTintList(ColorStateList.valueOf(0xFF888888));

        View navHome = findViewById(R.id.navHome);
        View navProfile = findViewById(R.id.navProfile);
        View navCart = findViewById(R.id.navCart);
        View navOrders = findViewById(R.id.navOrders);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        }
        if (navCart != null) {
            navCart.setOnClickListener(v -> {
                startActivity(new Intent(this, CartActivity.class));
                finish();
            });
        }
        if (navOrders != null) {
            navOrders.setOnClickListener(v -> {
                // Already on orders
            });
        }
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            });
        }
    }
}