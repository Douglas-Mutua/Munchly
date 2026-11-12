package com.example.munchly;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.munchly.data.MunchlyDbHelper;
import com.example.munchly.data.MunchlyRepository;
import com.example.munchly.model.Product;

import java.util.List;

public class HomeFragment extends Fragment {

    private LinearLayout allProductsContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        Button btnOrderNow = view.findViewById(R.id.btnOrderNow);
        ImageView btnOpenCart = view.findViewById(R.id.btnOpenCart);
        ImageView ivPromoSpaghetti = view.findViewById(R.id.ivPromoSpaghetti);
        allProductsContainer = view.findViewById(R.id.allProductsContainer);

        Product spaghetti = null;
        List<Product> products = MunchlyDbHelper.getInstance(requireContext()).getAllProducts();
        if (products != null) {
            for (Product p : products) {
                if (p.getName() != null && p.getName().toLowerCase().contains("spaghetti")) {
                    spaghetti = p;
                    break;
                }
            }
            if (spaghetti == null && !products.isEmpty()) {
                spaghetti = products.get(0);
            }
        }

        if (spaghetti != null) {
            if (ivPromoSpaghetti != null) {
                Glide.with(this).load(spaghetti.getImageUrl()).into(ivPromoSpaghetti);
            }
            final Product spagRef = spaghetti;
            btnOrderNow.setOnClickListener(v -> {
                MunchlyRepository.getInstance().addToCart(spagRef, 1);
                Toast.makeText(requireContext(), "Added " + spagRef.getName() + " to Cart", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireContext(), CartActivity.class));
            });
        }

        if (btnOpenCart != null) {
            btnOpenCart.setOnClickListener(v -> startActivity(new Intent(requireContext(), CartActivity.class)));
        }

        loadProducts();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProducts();
    }

    private void loadProducts() {
        if (allProductsContainer == null || getContext() == null) return;
        allProductsContainer.removeAllViews();

        List<Product> products = MunchlyDbHelper.getInstance(requireContext()).getAllProducts();
        if (products == null || products.isEmpty()) {
            products = MunchlyRepository.getInstance().getProducts();
        }

        for (Product product : products) {
            renderProductCard(product);
        }
    }

    private void renderProductCard(Product product) {
        CardView card = new CardView(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 20);
        card.setLayoutParams(cardParams);
        card.setRadius(20f);
        card.setCardBackgroundColor(0xFFFFFFFF);
        card.setCardElevation(4f);

        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(20, 20, 20, 20);
        row.setGravity(Gravity.CENTER_VERTICAL);

        CardView imageCard = new CardView(requireContext());
        imageCard.setLayoutParams(new LinearLayout.LayoutParams(130, 130));
        imageCard.setRadius(16f);
        imageCard.setCardElevation(0f);
        imageCard.setCardBackgroundColor(0xFFF2F2F2);

        ImageView ivProduct = new ImageView(requireContext());
        ivProduct.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        ivProduct.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(product.getImageUrl()).into(ivProduct);
        imageCard.addView(ivProduct);
        row.addView(imageCard);

        LinearLayout details = new LinearLayout(requireContext());
        details.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams detailsParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        detailsParams.setMargins(16, 0, 16, 0);
        details.setLayoutParams(detailsParams);

        TextView tvName = new TextView(requireContext());
        tvName.setText(product.getName());
        tvName.setTextSize(16f);
        tvName.setTypeface(null, Typeface.BOLD);
        tvName.setTextColor(0xFF222222);
        details.addView(tvName);

        TextView tvDesc = new TextView(requireContext());
        tvDesc.setText(product.getDescription());
        tvDesc.setTextSize(12f);
        tvDesc.setTextColor(0xFF666666);
        tvDesc.setPadding(0, 4, 0, 0);
        details.addView(tvDesc);

        TextView tvPrice = new TextView(requireContext());
        tvPrice.setText(String.format("KES %.2f", product.getPrice()));
        tvPrice.setTextSize(15f);
        tvPrice.setTypeface(null, Typeface.BOLD);
        tvPrice.setTextColor(0xFFFF6B00);
        tvPrice.setPadding(0, 6, 0, 0);
        details.addView(tvPrice);

        row.addView(details);

        Button btnAdd = new Button(requireContext());
        btnAdd.setText("+ Add");
        btnAdd.setTextSize(13f);
        btnAdd.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(210, 110);
        btnAdd.setLayoutParams(btnParams);
        btnAdd.setBackgroundTintList(ColorStateList.valueOf(0xFFFF6B00));
        btnAdd.setTextColor(0xFFFFFFFF);
        btnAdd.setOnClickListener(v -> {
            MunchlyRepository.getInstance().addToCart(product, 1);
            Toast.makeText(requireContext(), "Added " + product.getName() + " to Cart", Toast.LENGTH_SHORT).show();
        });
        row.addView(btnAdd);

        card.addView(row);
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ProductDetailsActivity.class);
            intent.putExtra(ProductDetailsActivity.EXTRA_PRODUCT, product);
            startActivity(intent);
        });

        allProductsContainer.addView(card);
    }
}
