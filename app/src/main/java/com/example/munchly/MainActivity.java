package com.example.munchly;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.munchly.data.MunchlyRepository;

public class MainActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private ViewPager2 viewPager;
    private ImageView ivHome, ivCart, ivOrders, ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        MunchlyRepository.getInstance().init(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new MainPagerAdapter(this));

        ivHome = findViewById(R.id.navHomeIcon);
        ivCart = findViewById(R.id.navCartIcon);
        ivOrders = findViewById(R.id.navOrdersIcon);
        ivProfile = findViewById(R.id.navProfileIcon);

        View navHome = findViewById(R.id.navHome);
        View navCart = findViewById(R.id.navCart);
        View navOrders = findViewById(R.id.navOrders);
        View navProfile = findViewById(R.id.navProfile);

        if (navHome != null) {
            navHome.setOnClickListener(v -> viewPager.setCurrentItem(0, true));
        }
        if (navCart != null) {
            navCart.setOnClickListener(v -> viewPager.setCurrentItem(1, true));
        }
        if (navOrders != null) {
            navOrders.setOnClickListener(v -> viewPager.setCurrentItem(2, true));
        }
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> viewPager.setCurrentItem(3, true));
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateBottomNavTint(position);
            }
        });

        updateBottomNavTint(0);
    }

    private void updateBottomNavTint(int position) {
        if (ivHome != null) ivHome.setImageTintList(ColorStateList.valueOf(position == 0 ? 0xFFFF6B00 : 0xFF888888));
        if (ivCart != null) ivCart.setImageTintList(ColorStateList.valueOf(position == 1 ? 0xFFFF6B00 : 0xFF888888));
        if (ivOrders != null) ivOrders.setImageTintList(ColorStateList.valueOf(position == 2 ? 0xFFFF6B00 : 0xFF888888));
        if (ivProfile != null) ivProfile.setImageTintList(ColorStateList.valueOf(position == 3 ? 0xFFFF6B00 : 0xFF888888));
    }
}
