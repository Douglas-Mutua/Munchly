package com.example.munchly;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.munchly.data.MunchlyRepository;
import com.example.munchly.model.User;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvUserPhone, tvUserRole, tvUserId;
    private ImageView ivProfilePic;
    private View btnLogout;                 // the clickable logout icon
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUserName   = findViewById(R.id.tvUserName);
        tvUserEmail  = findViewById(R.id.tvUserEmail);
        tvUserPhone  = findViewById(R.id.tvUserPhone);
        tvUserRole   = findViewById(R.id.tvUserRole);
        tvUserId     = findViewById(R.id.tvUserId);
        ivProfilePic = findViewById(R.id.ivProfilePic);

        // Use the id that exists in activity_profile.xml
        btnLogout = findViewById(R.id.btnLogoutIcon);

        User user = MunchlyRepository.getInstance().getCurrentUser();
        if (user != null) {
            tvUserName.setText(safeStr(user.getFullName(), "John Doe"));
            tvUserEmail.setText(safeStr(user.getEmail(), "john@example.com"));
            tvUserPhone.setText(safeStr(user.getPhone(), "254712345678"));
            tvUserRole.setText(safeStr(user.getRole(), "customer"));
            tvUserId.setText(safeStr(user.getUserId(), "u1"));
        } else {
            tvUserName.setText("Guest");
            tvUserEmail.setText("guest@munchly.com");
            tvUserPhone.setText("N/A");
            tvUserRole.setText("guest");
            tvUserId.setText("N/A");
        }

        if (ivProfilePic != null) {
            Glide.with(this)
                    .load("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=300&q=80")
                    .circleCrop()
                    .into(ivProfilePic);
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            Glide.with(this)
                                    .load(selectedImageUri)
                                    .circleCrop()
                                    .into(ivProfilePic);
                            Toast.makeText(this,
                                    "Profile picture updated successfully!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        if (ivProfilePic != null) {
            ivProfilePic.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> performLogout());
        }

        setupBottomNav();
    }

    private String safeStr(String val, String fallback) {
        return (val == null || val.trim().isEmpty()) ? fallback : val;
    }

    private void performLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out of Munchly?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupBottomNav() {
        ImageView ivHome = findViewById(R.id.navHomeIcon);
        ImageView ivCart = findViewById(R.id.navCartIcon);
        ImageView ivOrders = findViewById(R.id.navOrdersIcon);
        ImageView ivProfile = findViewById(R.id.navProfileIcon);

        if (ivHome != null) ivHome.setImageTintList(ColorStateList.valueOf(0xFF888888));
        if (ivCart != null) ivCart.setImageTintList(ColorStateList.valueOf(0xFF888888));
        if (ivOrders != null) ivOrders.setImageTintList(ColorStateList.valueOf(0xFF888888));
        if (ivProfile != null) ivProfile.setImageTintList(ColorStateList.valueOf(0xFFFF6B00));

        View navHome    = findViewById(R.id.navHome);
        View navCart    = findViewById(R.id.navCart);
        View navOrders  = findViewById(R.id.navOrders);
        View navProfile = findViewById(R.id.navProfile);

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
                startActivity(new Intent(this, OrderHistoryActivity.class));
                finish();
            });
        }
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                // already on profile
            });
        }
    }
}
