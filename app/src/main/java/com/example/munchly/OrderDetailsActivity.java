package com.example.munchly;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.munchly.data.MunchlyRepository;
import com.example.munchly.model.Order;
import com.example.munchly.model.Payment;

public class OrderDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "extra_order_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        String orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);
        LinearLayout container = findViewById(R.id.orderDetailsContainer);
        if (container == null || orderId == null) { finish(); return; }

        Order order = null;
        for (Order o : MunchlyRepository.getInstance().getOrders()) {
            if (o != null && orderId.equals(o.getOrderId())) { order = o; break; }
        }
        if (order == null) { finish(); return; }

        addRow(container, "Order ID", safe(order.getOrderId()));
        addRow(container, "Status", safe(order.getOrderStatus()));
        addRow(container, "Customer", safe(order.getCustomerName()));
        addRow(container, "Phone", safe(order.getPhone()));
        addRow(container, "Delivery Address", safe(order.getDeliveryAddress()));
        addRow(container, "Total Amount", "KES " + String.format("%.2f", order.getTotalAmount()));

        for (Payment p : MunchlyRepository.getInstance().getPayments()) {
            if (p != null && orderId.equals(p.getOrderId())) {
                addRow(container, "Payment Status", safe(p.getPaymentStatus()));
                addRow(container, "M-Pesa Receipt", safe(p.getMpesaReceiptNumber()));
                addRow(container, "Phone (M-Pesa)", safe(p.getPhoneNumber()));
                addRow(container, "Amount Paid", "KES " + String.format("%.2f", p.getAmount()));
                break;
            }
        }
    }

    private void addRow(LinearLayout parent, String label, String value) {
        TextView tv = new TextView(this);
        tv.setText(label + ": " + value);
        tv.setTextSize(15f);
        tv.setPadding(0, 12, 0, 12);
        parent.addView(tv);
    }

    private String safe(String v) { return v == null || v.trim().isEmpty() ? "N/A" : v; }
}