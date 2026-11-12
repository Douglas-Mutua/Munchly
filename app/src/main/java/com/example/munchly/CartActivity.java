package com.example.munchly;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.munchly.data.MunchlyRepository;
import com.example.munchly.model.CartItem;

public class CartActivity extends AppCompatActivity {

    private LinearLayout cartItemsContainer;
    private TextView tvSubtotal, tvDelivery, tvCartTotal, btnClearCart;
    private EditText etPromoCode;
    private Button btnApplyPromo, btnProceedCheckout;
    private ImageView btnBack;
    private double discount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartItemsContainer = findViewById(R.id.cartItemsContainer);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDelivery = findViewById(R.id.tvDelivery);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        btnClearCart = findViewById(R.id.btnClearCart);
        etPromoCode = findViewById(R.id.etPromoCode);
        btnApplyPromo = findViewById(R.id.btnApplyPromo);
        btnProceedCheckout = findViewById(R.id.btnProceedCheckout);
        btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        btnClearCart.setOnClickListener(v -> {
            MunchlyRepository.getInstance().clearCart();
            loadCart();
            Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show();
        });

        btnApplyPromo.setOnClickListener(v -> {
            String code = etPromoCode.getText().toString().trim().toUpperCase();
            if (code.equals("MUNCHLY10")) {
                discount = 150.00;
                Toast.makeText(this, "Promo code applied! KES 150.00 off", Toast.LENGTH_SHORT).show();
                loadCart();
            } else if (code.isEmpty()) {
                Toast.makeText(this, "Enter a promo code", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Invalid promo code", Toast.LENGTH_SHORT).show();
            }
        });

        btnProceedCheckout.setOnClickListener(v -> {
            if (MunchlyRepository.getInstance().getCartItems().isEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, CheckoutActivity.class));
        });

        loadCart();
        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCart();
    }

    private void loadCart() {
        cartItemsContainer.removeAllViews();
        double subtotal = MunchlyRepository.getInstance().getCartTotal();
        double delivery = subtotal > 0 ? 150.00 : 0.00; // KES 150 delivery
        double total = Math.max(0, subtotal + delivery - discount);

        tvSubtotal.setText(String.format("KES %.2f", subtotal));
        tvDelivery.setText(String.format("KES %.2f", delivery));
        tvCartTotal.setText(String.format("KES %.2f", total));

        if (MunchlyRepository.getInstance().getCartItems().isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("Your cart is empty 🛒");
            emptyView.setTextSize(16f);
            emptyView.setTextColor(0xFF888888);
            emptyView.setPadding(0, 32, 0, 32);
            cartItemsContainer.addView(emptyView);
            return;
        }

        for (CartItem item : MunchlyRepository.getInstance().getCartItems()) {
            LinearLayout itemRow = new LinearLayout(this);
            itemRow.setOrientation(LinearLayout.HORIZONTAL);
            itemRow.setPadding(0, 16, 0, 16);
            itemRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView info = new TextView(this);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            info.setText(item.getProduct().getName() + "\nKES " + String.format("%.2f", item.getProduct().getPrice()) + " x " + item.getQuantity() + " = KES " + String.format("%.2f", item.getSubtotal()));
            info.setTextSize(14f);
            info.setTextColor(0xFF333333);
            itemRow.addView(info);

            Button btnPlus = new Button(this);
            btnPlus.setText("+");
            btnPlus.setTextSize(12f);
            btnPlus.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            btnPlus.setOnClickListener(v -> {
                item.setQuantity(item.getQuantity() + 1);
                loadCart();
            });
            itemRow.addView(btnPlus);

            Button btnMinus = new Button(this);
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

    private void setupBottomNav() {
        ImageView ivHome = findViewById(R.id.navHomeIcon);
        ImageView ivCart = findViewById(R.id.navCartIcon);
        ImageView ivOrders = findViewById(R.id.navOrdersIcon);
        ImageView ivProfile = findViewById(R.id.navProfileIcon);

        if (ivHome != null) ivHome.setImageTintList(ColorStateList.valueOf(0xFF888888));
        if (ivCart != null) ivCart.setImageTintList(ColorStateList.valueOf(0xFFFF6B00));
        if (ivOrders != null) ivOrders.setImageTintList(ColorStateList.valueOf(0xFF888888));
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
                // Already on cart
            });
        }
        if (navOrders != null) {
            navOrders.setOnClickListener(v -> {
                startActivity(new Intent(this, OrderHistoryActivity.class));
                finish();
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