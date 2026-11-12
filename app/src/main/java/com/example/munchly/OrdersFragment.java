package com.example.munchly;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.munchly.data.MunchlyRepository;
import com.example.munchly.model.Order;
import com.example.munchly.model.Payment;

public class OrdersFragment extends Fragment {

    private LinearLayout ordersContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_order_history, container, false);

        ordersContainer = view.findViewById(R.id.ordersContainer);
        ImageView btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setVisibility(View.GONE);

        loadOrders();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        if (ordersContainer == null || getContext() == null) return;
        ordersContainer.removeAllViews();

        if (MunchlyRepository.getInstance().getOrders().isEmpty()) {
            TextView emptyView = new TextView(requireContext());
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

            CardView card = new CardView(requireContext());
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

            LinearLayout content = new LinearLayout(requireContext());
            content.setOrientation(LinearLayout.VERTICAL);
            content.setPadding(20, 20, 20, 20);

            TextView tvId = new TextView(requireContext());
            tvId.setText("Order: " + order.getOrderId());
            tvId.setTextSize(15f);
            tvId.setTypeface(null, Typeface.BOLD);
            tvId.setTextColor(0xFF333333);
            content.addView(tvId);

            TextView tvDetails = new TextView(requireContext());
            tvDetails.setText("Amount: KES " + String.format("%.2f", order.getTotalAmount())
                    + "\nStatus: " + order.getOrderStatus()
                    + "\nPhone: " + order.getPhone()
                    + "\nM-Pesa Receipt: " + receipt);
            tvDetails.setTextSize(14f);
            tvDetails.setTextColor(0xFF666666);
            tvDetails.setPadding(0, 8, 0, 0);
            content.addView(tvDetails);

            card.addView(content);

            final String oid = order.getOrderId();
            card.setOnClickListener(v -> {
                Intent i = new Intent(requireContext(), OrderDetailsActivity.class);
                i.putExtra(OrderDetailsActivity.EXTRA_ORDER_ID, oid);
                startActivity(i);
            });

            ordersContainer.addView(card);
        }
    }
}
