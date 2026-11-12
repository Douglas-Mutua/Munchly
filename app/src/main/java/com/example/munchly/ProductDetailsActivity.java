package com.example.munchly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.munchly.data.MunchlyRepository;
import com.example.munchly.model.Product;

public class ProductDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT = "extra_product";
    private int quantity = 1;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        product = (Product) getIntent().getSerializableExtra(EXTRA_PRODUCT);
        if (product == null) {
            Toast.makeText(this, "Product details not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView ivDetailImage = findViewById(R.id.ivDetailImage);
        TextView tvDetailCategory = findViewById(R.id.tvDetailCategory);
        TextView tvDetailStock = findViewById(R.id.tvDetailStock);
        TextView tvDetailName = findViewById(R.id.tvDetailName);
        TextView tvDetailPrice = findViewById(R.id.tvDetailPrice);
        TextView tvDetailDescription = findViewById(R.id.tvDetailDescription);
        TextView tvQuantity = findViewById(R.id.tvQuantity);
        Button btnMinus = findViewById(R.id.btnMinus);
        Button btnPlus = findViewById(R.id.btnPlus);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        Glide.with(this).load(product.getImageUrl()).into(ivDetailImage);
        tvDetailCategory.setText(product.getCategory());
        tvDetailStock.setText("In Stock: " + product.getStockQuantity());
        tvDetailName.setText(product.getName());
        tvDetailPrice.setText(String.format("KES %.2f", product.getPrice()));
        tvDetailDescription.setText(product.getDescription());

        btnPlus.setOnClickListener(v -> {
            if (quantity < product.getStockQuantity()) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "Maximum available stock reached", Toast.LENGTH_SHORT).show();
            }
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            MunchlyRepository.getInstance().addToCart(product, quantity);
            Toast.makeText(this, "Added " + quantity + "x " + product.getName() + " to Cart", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
