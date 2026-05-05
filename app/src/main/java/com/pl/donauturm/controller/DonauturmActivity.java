package com.pl.donauturm.controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.navigation.NavigationView;
import com.pl.donauturm.R;
import com.pl.donauturm.controller.links.LinkManger;
import com.pl.donauturm.controller.ui.exception.ExceptionFragment;
import com.pl.donauturm.databinding.ActivityDonauturmBinding;
import com.pl.donauturm.util.ExceptionHandler;

public class DonauturmActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawerLayout;
    public ActivityDonauturmBinding binding;
    public ExceptionHandler exceptionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        exceptionHandler = new ExceptionHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
        if (exceptionHandler.hasLog()) showError();

        binding = ActivityDonauturmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_donauturm);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            new AppBarConfiguration.Builder(navController.getGraph())
                    .setOpenableLayout(binding.drawerLayout).build();
            NavigationUI.setupWithNavController(binding.navView, navController);
            if (binding.drawerLayout != null) {
                drawerLayout = binding.drawerLayout;
                drawerLayout.setStatusBarBackgroundColor(MaterialColors.getColor(this, R.attr.statusBarBackground, "Error loading status bar color"));
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawerLayout.addDrawerListener(toggle);
                binding.navView.addNavigationItemSelectedListener(this);
                toggle.setDrawerIndicatorEnabled(true);
                toggle.syncState();
            }else{
                getWindow().setStatusBarColor(MaterialColors.getColor(this, R.attr.statusBarBackground, "Error loading status bar color"));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("InternalInsetResource")
    @SuppressWarnings("unused")
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return !new LinkManger().check(item, this);
    }

    private void showError(){
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.nav_host_fragment_content_donauturm, ExceptionFragment.newInstance(exceptionHandler))
                .addToBackStack(ExceptionFragment.class.getSimpleName())
                .commit();

    }

}
