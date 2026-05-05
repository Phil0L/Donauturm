package com.pl.donauturm.dtspotify.ui.main;

import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.pl.donauturm.commons.util.ThemeManager;
import com.pl.donauturm.dtspotify.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class PlayerStateManager {

    private final SortedMap<Integer, PlayerFragment> fragments = new TreeMap<>();
    private final TabLayout mTabs;
    private final ConstraintLayout mSnackbar;
    private final long startMillis;
    private int selected = 0;

    public PlayerStateManager(TabLayout tabs, ConstraintLayout snackbar) {
        this.mTabs = tabs;
        this.mSnackbar = snackbar;
        this.startMillis = System.currentTimeMillis();
    }

    public void provideFragment(PlayerFragment fragment, int position){
        PlayerFragment oldPlayerFragment = fragments.put(position, fragment);
        if (oldPlayerFragment != null)
            oldPlayerFragment.onDestroyView();
        fragment.getConnectionState().observe(fragment, connection -> onFragmentStateChanged(fragment, connection, position));
    }

    public PlayerFragment getFragment(int index){
        return fragments.getOrDefault(index, null);
    }

    public PlayerFragment getCurrentFragment(){
        return fragments.getOrDefault(selected, new ArrayList<>(fragments.values()).stream().findAny().orElse(null));
    }

    public void updateTabColors(){
        for (Map.Entry<Integer, PlayerFragment> indexedFragment : fragments.entrySet()) {
            setTabTextColor(indexedFragment.getKey(), indexedFragment.getValue().getConnectionState().getValue());
        }
    }

    private void onFragmentStateChanged(PlayerFragment fragment, PlayerFragment.ConnectionState state, int position){
        setTabTextColor(position, state);
    }

    @Deprecated
    private void showConnectionStatusOfAll() {
        int connected = 0;
        int active = 0;
        for (PlayerFragment fragment : fragments.values()) {
            if (fragment.getConnectionState().getValue().isLoggedIn())
                connected++;
            if (fragment.getConnectionState().getValue() == PlayerFragment.ConnectionState.CONNECTED)
                active++;
            fragment.getConnectionState().observe(fragment, this::showConnectionStatus);
        }
        if (connected == 0)
            return;
        String message = "";
        if (connected == fragments.size())
            message += "All accounts connected. ";
        else
            message += connected + " accounts connected. ";
        if (active == fragments.size())
            message += "All accounts active.";
        else
            message += active + " accounts active.";
        showToast(message);
    }

    private void showConnectionStatus(PlayerFragment.ConnectionState state){
        showToast("Player is now" + state.name().toLowerCase());
    }

    private void showToast(String text) {
        showToast(text, mSnackbar);
    }

    private void showToast(String text, ConstraintLayout layout) {
        TextView snackText = layout.findViewById(com.pl.donauturm.spotify.R.id.snackbar).findViewById(com.pl.donauturm.spotify.R.id.text);
        snackText.setText(text);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.clear(com.pl.donauturm.spotify.R.id.snackbar, ConstraintSet.TOP);
        constraintSet.connect(com.pl.donauturm.spotify.R.id.snackbar, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        Transition transition = new ChangeBounds();
        transition.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        transition.setDuration(1200);
        TransitionManager.beginDelayedTransition(layout, transition);
        constraintSet.applyTo(layout);
        layout.postDelayed(() -> hideToast(layout), 1000 * 3);
    }

    private void hideToast(ConstraintLayout layout) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.clear(com.pl.donauturm.spotify.R.id.snackbar, ConstraintSet.BOTTOM);
        constraintSet.connect(com.pl.donauturm.spotify.R.id.snackbar, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        Transition transition = new ChangeBounds();
        transition.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        transition.setDuration(1200);
        TransitionManager.beginDelayedTransition(layout, transition);
        constraintSet.applyTo(layout);
    }

    private void setTabTextColor(int position, PlayerFragment.ConnectionState state){
        if (state == PlayerFragment.ConnectionState.LOGGED_OUT)
            setTabTextColor(position, ThemeManager.ThemeColor.getColor(mTabs.getContext(), ThemeManager.getInstance().getCurrentTheme(), R.color.pink_600, R.color.pink_400));
        if (state == PlayerFragment.ConnectionState.UNKNOWN)
            setTabTextColor(position, ThemeManager.ThemeColor.getColor(mTabs.getContext(), ThemeManager.getInstance().getCurrentTheme(), R.color.gray_700, R.color.gray_200));
        if (state == PlayerFragment.ConnectionState.LOGGED_IN)
            setTabTextColor(position, ThemeManager.ThemeColor.getColor(mTabs.getContext(), ThemeManager.getInstance().getCurrentTheme(), R.color.gray_700, R.color.gray_200));
        if (state == PlayerFragment.ConnectionState.DISCONNECTED)
            setTabTextColor(position, ThemeManager.ThemeColor.getColor(mTabs.getContext(), ThemeManager.getInstance().getCurrentTheme(), R.color.gray_700, R.color.gray_200));
        if (state == PlayerFragment.ConnectionState.CONNECTED)
            setTabTextColor(position, ThemeManager.ThemeColor.getColor(mTabs.getContext(), ThemeManager.getInstance().getCurrentTheme(), R.color.teal_700, R.color.teal_200));
    }

    private void setTabTextColor(int position, @ColorInt int color){
        TabLayout.Tab tab = mTabs.getTabAt(position);
        if (tab == null) return;
        TabLayout.TabView tabView = tab.view;
        View viewText = tabView.getChildAt(1);
        MaterialTextView textView = (MaterialTextView) viewText;
        textView.setTextColor(color);
    }

    private float time(){
        return (System.currentTimeMillis() - startMillis) / 1000f;
    }

    public void setSelected(int index) {
        this.selected = index;
    }
}
