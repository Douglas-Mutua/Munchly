package com.example.munchly;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Controller for the custom bottom navigation bar in layout_bottom_nav.xml.
 *
 * Responsibilities:
 *   - Wires click listeners on each tab.
 *   - Swaps the icon tint between active/inactive colors.
 *   - Shows/hides the "active dot" indicator (only Home has one in the layout).
 *   - Notifies a listener when a tab is selected.
 *
 * Usage (inside an Activity, after setContentView(...)):
 *
 *   BottomNavController nav = new BottomNavController(this);
 *   nav.addTab(R.id.navHome,    R.id.navHomeIcon,    R.id.navHomeDot);
 *   nav.addTab(R.id.navCart,    R.id.navCartIcon,    null);
 *   nav.addTab(R.id.navOrders,  R.id.navOrdersIcon,  null);
 *   nav.addTab(R.id.navProfile, R.id.navProfileIcon, null);
 *
 *   nav.setOnTabSelectedListener(tabId -> {
 *       // your navigation logic
 *   });
 *
 *   nav.select(R.id.navHome); // initial state
 */
public class BottomNavController {

    /** Called whenever the user selects a tab (and on initial programmatic select). */
    public interface OnTabSelectedListener {
        void onTabSelected(@IdRes int tabId);
    }

    private final Activity activity;
    private final int activeColor;
    private final int inactiveColor;

    private OnTabSelectedListener listener;
    private int currentTabId = View.NO_ID;

    public BottomNavController(@NonNull Activity activity) {
        this(activity, R.color.nav_icon_active, R.color.nav_icon_inactive);
    }

    public BottomNavController(@NonNull Activity activity,
                               @ColorRes int activeColorRes,
                               @ColorRes int inactiveColorRes) {
        this.activity      = activity;
        this.activeColor   = ContextCompat.getColor(activity, activeColorRes);
        this.inactiveColor = ContextCompat.getColor(activity, inactiveColorRes);
    }

    /** Sets the callback invoked when a tab is selected. */
    public void setOnTabSelectedListener(@Nullable OnTabSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * Registers a bottom-nav tab.
     *
     * @param tabId  id of the clickable LinearLayout container (e.g. navHome)
     * @param iconId id of the AppCompatImageView inside it (e.g. navHomeIcon)
     * @param dotId  id of the optional active-dot View, or null if none
     */
    public void addTab(@IdRes final int tabId,
                       @IdRes final int iconId,
                       @Nullable final Integer dotId) {
        View tab = activity.findViewById(tabId);
        if (tab == null) {
            throw new IllegalArgumentException(
                "BottomNavController: no view found for tabId 0x"
                    + Integer.toHexString(tabId));
        }

        tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(tabId);
            }
        });

        // Apply current state in case select() ran before this tab was added
        applyState(iconId, dotId, tabId == currentTabId);
    }

    /**
     * Programmatically selects a tab, updating tints and dot visibility,
     * and firing the listener.
     */
    public void select(@IdRes int tabId) {
        if (tabId == currentTabId) {
            // Still fire listener the first time (initial select) or if caller
            // explicitly reselects — adjust if you'd rather suppress duplicates.
            if (listener != null) listener.onTabSelected(tabId);
            return;
        }
        currentTabId = tabId;

        // Iterate through the four known tabs.
        applyState(R.id.navHomeIcon,    null,               tabId == R.id.navHome);
        applyState(R.id.navCartIcon,    null,               tabId == R.id.navCart);
        applyState(R.id.navOrdersIcon,  null,               tabId == R.id.navOrders);
        applyState(R.id.navProfileIcon, null,               tabId == R.id.navProfile);

        if (listener != null) listener.onTabSelected(tabId);
    }

    /** Returns the currently selected tab id, or View.NO_ID if none. */
    @IdRes
    public int getCurrentTabId() {
        return currentTabId;
    }

    private void applyState(@IdRes int iconId,
                            @Nullable Integer dotId,
                            boolean active) {
        ImageView icon = activity.findViewById(iconId);
        if (icon != null) {
            icon.setImageTintList(
                ColorStateList.valueOf(active ? activeColor : inactiveColor));
        }
        if (dotId != null) {
            View dot = activity.findViewById(dotId);
            if (dot != null) {
                dot.setVisibility(active ? View.VISIBLE : View.GONE);
            }
        }
    }
}