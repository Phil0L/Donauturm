package com.pl.donauturm.controller.ui.welcome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.pl.donauturm.R;
import com.pl.donauturm.controller.DonauturmActivity;
import com.pl.donauturm.databinding.FragmentWelcomeBinding;

public class WelcomeFragment extends Fragment implements DrawerLayout.DrawerListener {

    private FragmentWelcomeBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        binding.welcomeText.setText(getWelcomeText());
        Activity activity = getActivity();
        if (activity instanceof DonauturmActivity) {
            DonauturmActivity donauturmActivity = (DonauturmActivity) activity;
            donauturmActivity.drawerLayout.addDrawerListener(this);
            binding.button.setOnClickListener(v -> donauturmActivity.drawerLayout.openDrawer(donauturmActivity.binding.navView));
        }
        return binding.getRoot();
    }

    private String getWelcomeText() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        if (hour < 5) return getResources().getString(R.string.welcome_text_evening);
        if (hour < 12) return getResources().getString(R.string.welcome_text_morning);
        if (hour < 18) return getResources().getString(R.string.welcome_text_afternoon);
        return getResources().getString(R.string.welcome_text_evening);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Activity activity = getActivity();
        if (activity instanceof DonauturmActivity) {
            DonauturmActivity donauturmActivity = (DonauturmActivity) activity;
            donauturmActivity.drawerLayout.removeDrawerListener(this);
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}