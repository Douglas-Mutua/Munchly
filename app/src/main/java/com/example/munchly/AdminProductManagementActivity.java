package com.example.munchly;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.munchly.data.MunchlyDbHelper;
import com.example.munchly.data.MunchlyRepository;
import com.example.munchly.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminProductManagementActivity extends AppCompatActivity {

    private EditText etProductName, etProductCategory, etProductPrice, etStockQuantity, etImageUrl, etProductDescription;
    private Button btnSaveProduct, btnPickImage;
    private ImageView btnBack, ivSelectedImagePreview;
    private LinearLayout adminProductListContainer;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String editingProductId = null;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_management);

        etProductName = findViewById(R.id.etProductName);
        etProductCategory = findViewById(R.id.etProductCategory);
        etProductPrice = findViewById(R.id.etProductPrice);
        etStockQuantity = findViewById(R.id.etStockQuantity);
        etImageUrl = findViewById(R.id.etImageUrl);
        etProductDescription = findViewById(R.id.etProductDescription);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnBack = findViewById(R.id.btnBack);
        ivSelectedImagePreview = findViewById(R.id.ivSelectedImagePreview);
        adminProductListContainer = findViewById(R.id.adminProductListContainer);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            etImageUrl.setText(selectedImageUri.toString());
                            if (ivSelectedImagePreview != null) {
                                ivSelectedImagePreview.setVisibility(View.VISIBLE);
                                Glide.with(this).load(selectedImageUri).into(ivSelectedImagePreview);
                            }
                            Toast.makeText(this, "Photo loaded from gallery!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        if (btnPickImage != null) {
            btnPickImage.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            });
        }

        btnSaveProduct.setOnClickListener(v -> addOrUpdateProduct());

        loadExistingProducts();
        setupAdminNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExistingProducts();
    }

    private void loadExistingProducts() {
        if (adminProductListContainer == null) return;
        adminProductListContainer.removeAllViews();

        List<Product> products = MunchlyDbHelper.getInstance(this).getAllProducts();
        if (products == null || products.isEmpty()) {
            products = MunchlyRepository.getInstance().getProducts();
        }

        if (products == null || products.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No products available.");
            emptyView.setTextSize(14f);
            emptyView.setTextColor(0xFF888888);
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setPadding(0, 24, 0, 24);
            adminProductListContainer.addView(emptyView);
            return;
        }

        for (Product product : products) {
            if (product == null) continue;

            CardView card = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, 20);
            card.setLayoutParams(cardParams);
            card.setRadius(20f);
            card.setCardBackgroundColor(0xFFFFFFFF);
            card.setCardElevation(4f);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(20, 20, 20, 20);
            row.setGravity(Gravity.CENTER_VERTICAL);

            // Thumbnail
            CardView imageCard = new CardView(this);
            imageCard.setLayoutParams(new LinearLayout.LayoutParams(130, 130));
            imageCard.setRadius(16f);
            imageCard.setCardElevation(0f);
            imageCard.setCardBackgroundColor(0xFFF2F2F2);

            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(this).load(product.getImageUrl()).into(iv);
            imageCard.addView(iv);
            row.addView(imageCard);

            // Info
            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            );
            infoParams.setMargins(16, 0, 16, 0);
            info.setLayoutParams(infoParams);

            TextView tvName = new TextView(this);
            tvName.setText(product.getName());
            tvName.setTextSize(16f);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextColor(0xFF222222);
            info.addView(tvName);

            TextView tvSub = new TextView(this);
            tvSub.setText("KES " + String.format("%.2f", product.getPrice()) + " | Stock: " + product.getStockQuantity());
            tvSub.setTextSize(13f);
            tvSub.setTextColor(0xFF666666);
            tvSub.setPadding(0, 4, 0, 0);
            info.addView(tvSub);

            TextView tvCat = new TextView(this);
            tvCat.setText("Category: " + product.getCategory());
            tvCat.setTextSize(11f);
            tvCat.setTextColor(0xFFFF6B00);
            tvCat.setPadding(0, 2, 0, 0);
            info.addView(tvCat);

            row.addView(info);

            // Action Buttons (Edit/Restock & Delete)
            LinearLayout actionContainer = new LinearLayout(this);
            actionContainer.setOrientation(LinearLayout.VERTICAL);
            actionContainer.setGravity(Gravity.CENTER);

            Button btnEdit = new Button(this);
            btnEdit.setText("Edit/Restock");
            btnEdit.setTextSize(11f);
            btnEdit.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(210, 90);
            editParams.setMargins(0, 0, 0, 8);
            btnEdit.setLayoutParams(editParams);
            btnEdit.setBackgroundTintList(ColorStateList.valueOf(0xFF2196F3));
            btnEdit.setTextColor(0xFFFFFFFF);
            btnEdit.setOnClickListener(v -> populateFormForEdit(product));
            actionContainer.addView(btnEdit);

            Button btnDelete = new Button(this);
            btnDelete.setText("Delete");
            btnDelete.setTextSize(11f);
            btnDelete.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams delParams = new LinearLayout.LayoutParams(210, 90);
            btnDelete.setLayoutParams(delParams);
            btnDelete.setBackgroundTintList(ColorStateList.valueOf(0xFFE53935));
            btnDelete.setTextColor(0xFFFFFFFF);
            btnDelete.setOnClickListener(v -> {
                String id = product.getProductId();
                MunchlyRepository.getInstance().removeProduct(id);
                MunchlyDbHelper.getInstance(this).deleteProduct(id);

                Runnable deleteFromDb = () -> db.collection("products").document(id).delete();

                if (mAuth.getCurrentUser() == null) {
                    mAuth.signInAnonymously().addOnSuccessListener(authResult -> deleteFromDb.run());
                } else {
                    deleteFromDb.run();
                }

                Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                loadExistingProducts();
            });
            actionContainer.addView(btnDelete);

            row.addView(actionContainer);
            card.addView(row);

            card.setClickable(true);
            card.setFocusable(true);
            card.setOnClickListener(v -> populateFormForEdit(product));

            adminProductListContainer.addView(card);
        }
    }

    private void populateFormForEdit(Product product) {
        editingProductId = product.getProductId();
        etProductName.setText(product.getName());
        etProductCategory.setText(product.getCategory());
        etProductPrice.setText(String.valueOf(product.getPrice()));
        etStockQuantity.setText(String.valueOf(product.getStockQuantity()));
        etImageUrl.setText(product.getImageUrl());
        etProductDescription.setText(product.getDescription());

        if (ivSelectedImagePreview != null && product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            ivSelectedImagePreview.setVisibility(View.VISIBLE);
            Glide.with(this).load(product.getImageUrl()).into(ivSelectedImagePreview);
        }

        btnSaveProduct.setText("Update / Restock Product");
        Toast.makeText(this, "Loaded " + product.getName() + " for editing & restocking above.", Toast.LENGTH_SHORT).show();

        ScrollView scrollView = (ScrollView) findViewById(R.id.btnBack).getParent().getParent().getParent();
        if (scrollView != null) {
            scrollView.smoothScrollTo(0, 0);
        }
    }

    private void addOrUpdateProduct() {
        String name = etProductName.getText().toString().trim();
        String category = etProductCategory.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();
        String stockStr = etStockQuantity.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Please fill in product name, price, and stock", Toast.LENGTH_SHORT).show();
            return;
        }

        if (category.isEmpty()) category = "Fast Food";
        if (imageUrl.isEmpty()) imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=600&q=80";
        if (description.isEmpty()) description = "Delicious " + name + " freshly prepared for you.";

        double price = Double.parseDouble(priceStr);
        int stock = Integer.parseInt(stockStr);
        String productId;

        if (editingProductId != null) {
            productId = editingProductId;
        } else {
            productId = "MNCH-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        }

        Product product = new Product(productId, name, description, price, stock, category, imageUrl);

        if (editingProductId != null) {
            MunchlyRepository.getInstance().updateProduct(product);
            MunchlyDbHelper.getInstance(this).addProduct(product);
            Toast.makeText(this, "Product updated & restocked successfully!", Toast.LENGTH_SHORT).show();
        } else {
            MunchlyRepository.getInstance().addProduct(product);
            MunchlyDbHelper.getInstance(this).addProduct(product);
            Toast.makeText(this, "Product added successfully to store!", Toast.LENGTH_SHORT).show();
        }

        // Save to Firebase Firestore
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("productId", productId);
        productMap.put("name", name);
        productMap.put("description", description);
        productMap.put("price", price);
        productMap.put("stockQuantity", stock);
        productMap.put("category", category);
        productMap.put("imageUrl", imageUrl);

        Runnable saveToDb = () -> db.collection("products").document(productId).set(productMap);

        if (mAuth.getCurrentUser() == null) {
            mAuth.signInAnonymously()
                    .addOnSuccessListener(authResult -> saveToDb.run())
                    .addOnFailureListener(e -> {
                        // Handled locally
                    });
        } else {
            saveToDb.run();
        }

        clearForm();
        loadExistingProducts();
    }

    private void clearForm() {
        editingProductId = null;
        etProductName.setText("");
        etProductCategory.setText("");
        etProductPrice.setText("");
        etStockQuantity.setText("");
        etImageUrl.setText("");
        etProductDescription.setText("");
        if (ivSelectedImagePreview != null) {
            ivSelectedImagePreview.setVisibility(View.GONE);
        }
        btnSaveProduct.setText(R.string.btn_save_product);
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
        if (ivProd != null) ivProd.setImageTintList(ColorStateList.valueOf(0xFFFF6B00));
        if (ivOrd != null) ivOrd.setImageTintList(ColorStateList.valueOf(0xFF888888));

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
                // Already on products
            });
        }
        if (navOrders != null) {
            navOrders.setOnClickListener(v -> {
                startActivity(new Intent(this, AdminOrdersActivity.class));
                finish();
            });
        }
        if (navLogout != null) {
            navLogout.setOnClickListener(v -> confirmAdminLogout());
        }
    }
}
