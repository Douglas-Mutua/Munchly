package com.example.munchly;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.munchly.data.MunchlyRepository;
import com.example.munchly.model.CartItem;

public class CartFragment extends Fragment {

    private LinearLayout cartItemsContainer;
    private TextView tvSubtotal, tvDelivery, tvCartTotal, btnClearCart;
    private EditText etPromoCode;
    private Button btnApplyPromo, btnProceedCheckout;
    private double discount = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart, container, false);

        cartItemsContainer = view.findViewById(R.id.cartItemsContainer);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvDelivery = view.findViewById(R.id.tvDelivery);
        tvCartTotal = view.findViewById(R.id.tvCartTotal);
        btnClearCart = view.findViewById(R.id.btnClearCart);
        etPromoCode = view.findViewById(R.id.etPromoCode);
        btnApplyPromo = view.findViewById(R.id.btnApplyPromo);
        btnProceedCheckout = view.findViewById(R.id.btnProceedCheckout);
        ImageView btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setVisibility(View.GONE);

        btnClearCart.setOnClickListener(v -> {
            MunchlyRepository.getInstance().clearCart();
            loadCart();
            Toast.makeText(requireContext(), "Cart cleared", Toast.LENGTH_SHORT).show();
        });

        btnApplyPromo.setOnClickListener(v -> {
            String code = etPromoCode.getText().toString().trim().toUpperCase();
            if (code.equals("MUNCHLY10")) {
                discount = 150.00;
                Toast.makeText(requireContext(), "Promo code applied! KES 150.00 off", Toast.LENGTH_SHORT).show();
                loadCart();
            } else if (code.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a promo code", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Invalid promo code", Toast.LENGTH_SHORT).show();
            }
        });

        btnProceedCheckout.setOnClickListener(v -> {
            if (MunchlyRepository.getInstance().getCartItems().isEmpty()) {
                Toast.makeText(requireContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(requireContext(), CheckoutActivity.class));
        });

        loadCart();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCart();
    }

    private void loadCart() {
        if (cartItemsContainer == null || getContext() == null) return;
        cartItemsContainer.removeAllViews();
        double subtotal = MunchlyRepository.getInstance().getCartTotal();
        double delivery = subtotal > 0 ? 150.00 : 0.00;
        double total = Math.max(0, subtotal + delivery - discount);

        tvSubtotal.setText(String.format("KES %.2f", subtotal));
        tvDelivery.setText(String.format("KES %.2f", delivery));
        tvCartTotal.setText(String.format("KES %.2f", total));

        if (MunchlyRepository.getInstance().getCartItems().isEmpty()) {
            TextView emptyView = new TextView(requireContext());
            emptyView.setText("Your cart is empty 🛒");
            emptyView.setTextSize(16f);
            emptyView.setTextColor(0xFF888888);
            emptyView.setPadding(0, 32, 0, 32);
            cartItemsContainer.addView(emptyView);
            return;
        }

        for (CartItem item : MunchlyRepository.getInstance().getCartItems()) {
            LinearLayout itemRow = new LinearLayout(requireContext());
            itemRow.setOrientation(LinearLayout.HORIZONTAL);
            itemRow.setPadding(0, 16, 0, 16);
            itemRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView info = new TextView(requireContext());
            info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            info.setText(item.getProduct().getName() + "\nKES " + String.format("%.2f", item.getProduct().getPrice()) + " x " + item.getQuantity() + " = KES " + String.format("%.2f", item.getSubtotal()));
            info.setTextSize(14f);
            info.setTextColor(0xFF333333);
            itemRow.addView(info);

            Button btnPlus = new Button(requireContext());
            btnPlus.setText("+");
            btnPlus.setTextSize(12f);
            btnPlus.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            btnPlus.setOnClickListener(v -> {
                item.setQuantity(item.getQuantity() + 1);
                loadCart();
            });
            itemRow.addView(btnPlus);

            Button btnMinus = new Button(requireContext());
            btnMinus.setText("-");
            btnMinus.setTextSize(12f);
            btnMinus.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            btnMinus.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    MunchlyRepository.getInstance().removeFromCart(item.getProduct().getProductId());
                }
                loadCart();
            });
            itemRow.addView(btnMinus);

            cartItemsContainer.addView(itemRow);
        }
    }
}
