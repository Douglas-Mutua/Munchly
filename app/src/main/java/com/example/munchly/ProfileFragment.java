package com.example.munchly;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.munchly.data.MunchlyRepository;
import com.example.munchly.model.User;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvUserPhone, tvUserRole, tvUserId;
    private ImageView ivProfilePic;
    private View btnLogout;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        tvUserName   = view.findViewById(R.id.tvUserName);
        tvUserEmail  = view.findViewById(R.id.tvUserEmail);
        tvUserPhone  = view.findViewById(R.id.tvUserPhone);
        tvUserRole   = view.findViewById(R.id.tvUserRole);
        tvUserId     = view.findViewById(R.id.tvUserId);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        btnLogout    = view.findViewById(R.id.btnLogoutIcon);

        User user = MunchlyRepository.getInstance().getCurrentUser();
        if (user != null) {
            tvUserName.setText(safeStr(user.getFullName(), "John Doe"));
            tvUserEmail.setText(safeStr(user.getEmail(), "john@example.com"));
            tvUserPhone.setText(safeStr(user.getPhone(), "254712345678"));
            tvUserRole.setText(safeStr(user.getRole(), "Customer"));
            tvUserId.setText(safeStr(user.getUserId(), "u1"));
        } else {
            tvUserName.setText("Guest");
            tvUserEmail.setText("guest@munchly.com");
            tvUserPhone.setText("N/A");
            tvUserRole.setText("Guest");
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
                    if (result.getResultCode() == -1 && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null && ivProfilePic != null) {
                            Glide.with(this)
                                    .load(selectedImageUri)
                                    .circleCrop()
                                    .into(ivProfilePic);
                            Toast.makeText(requireContext(),
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

        return view;
    }

    private String safeStr(String val, String fallback) {
        return (val == null || val.trim().isEmpty()) ? fallback : val;
    }

    private void performLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out of Munchly?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
