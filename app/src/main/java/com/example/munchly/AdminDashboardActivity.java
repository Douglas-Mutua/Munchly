package com.example.munchly;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.munchly.data.MunchlyRepository;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvAdminProductCount, tvAdminOrderCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        CardView cardManageProducts = findViewById(R.id.cardManageProducts);
        CardView cardViewOrders = findViewById(R.id.cardViewOrders);
        tvAdminProductCount = findViewById(R.id.tvAdminProductCount);
        tvAdminOrderCount = findViewById(R.id.tvAdminOrderCount);

        updateStats();

        cardViewOrders.setOnClickListener(v ->
                startActivity(new Intent(this, AdminOrdersActivity.class)));

        cardManageProducts.setOnClickListener(v ->
                startActivity(new Intent(this, AdminProductManagementActivity.class)));

        setupAdminNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }

    private void updateStats() {
        if (tvAdminProductCount != null) {
            int prodCount = MunchlyRepository.getInstance().getProducts().size();
            tvAdminProductCount.setText(String.valueOf(prodCount));
        }
        if (tvAdminOrderCount != null) {
            int orderCount = MunchlyRepository.getInstance().getOrders().size();
            tvAdminOrderCount.setText(String.valueOf(orderCount));
        }
    }

    private void performAdminLogout() {
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

        if (ivDash != null) ivDash.setImageTintList(ColorStateList.valueOf(0xFFFF6B00));
        if (ivProd != null) ivProd.setImageTintList(ColorStateList.valueOf(0xFF888888));
        if (ivOrd != null) ivOrd.setImageTintList(ColorStateList.valueOf(0xFF888888));

        View navDashboard = findViewById(R.id.navAdminDashboard);
        View navProducts = findViewById(R.id.navAdminProducts);
        View navOrders = findViewById(R.id.navAdminOrders);
        View navLogout = findViewById(R.id.navAdminLogout);

        if (navDashboard != null) {
            navDashboard.setOnClickListener(v -> {
                // Already on dashboard
            });
        }
        if (navProducts != null) {
            navProducts.setOnClickListener(v ->
                    startActivity(new Intent(this, AdminProductManagementActivity.class)));
        }
        if (navOrders != null) {
            navOrders.setOnClickListener(v ->
                    startActivity(new Intent(this, AdminOrdersActivity.class)));
        }
        if (navLogout != null) {
            navLogout.setOnClickListener(v -> performAdminLogout());
        }
    }
}
