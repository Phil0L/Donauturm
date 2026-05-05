package com.pl.donauturm.dtspotify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pl.donauturm.commons.util.ThemeManager;
import com.pl.donauturm.commons.util.log.ExceptionHandler;
import com.pl.donauturm.commons.util.log.Logging;
import com.pl.donauturm.dtspotify.commons.PageChangeCallbackHandler;
import com.pl.donauturm.dtspotify.commons.Timer;
import com.pl.donauturm.dtspotify.databinding.ActivityDtspotifyBinding;
import com.pl.donauturm.dtspotify.ui.main.PlayerFragment;
import com.pl.donauturm.dtspotify.ui.main.PlayerStateManager;
import com.pl.donauturm.dtspotify.ui.main.PlayersPagerAdapter;
import com.pl.donauturm.dtspotify.ui.settings.SettingsDialog;
import com.pl.donauturm.spotify.RepeatingPlaybackRefresh;
import com.pl.donauturm.spotify.SpotifyAuthHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DTSpotify extends AppCompatActivity implements PlayersPagerAdapter.FragmentCreated,
        TabLayout.OnTabSelectedListener, View.OnLongClickListener, SettingsDialog.OnSettingsChanged,
        PageChangeCallbackHandler.OnPageChangeCallbackInterface, Timer.OnTimer, ThemeManager.OnThemSwitch {

    private ActivityDtspotifyBinding binding;
    private static final String CLIENT_ID1 = "13a80ef7d1b14ad6a7d1839b910c15c4";
    private static final String CLIENT_ID2 = "fa1a2a79bbc14d97aeeffe5f6ec03607";
    private static final String SECRET_1 = "ede4d8242cf1461e868296cff140a192"; // TODO: <- remove this
    private static final String SECRET_2 = "b5cbbe7a5d774411b892677db727fda6"; // TODO: <- remove this
    private static final String REDIRECT_URI1 = "https://spotify_donauturm_pl.com/auth1";
    private static final String REDIRECT_URI2 = "https://spotify_donauturm_pl.com/auth2";
    private static final String USERNAME1 = "spotify1@donauturm.eu";
    private static final String USERNAME2 = "spotify2@donauturm.eu";
    public static boolean inDayNightReload;

    private final SpotifyAuthHelper.AuthData authData1 = new SpotifyAuthHelper.AuthData(CLIENT_ID1, SECRET_1, REDIRECT_URI1, USERNAME1);
    private final SpotifyAuthHelper.AuthData authData2 = new SpotifyAuthHelper.AuthData(CLIENT_ID2, SECRET_2, REDIRECT_URI2, USERNAME2);
    private PlayerStateManager playerStateManager;
    private RepeatingPlaybackRefresh<String> playbackRefresh1;
    private RepeatingPlaybackRefresh<String> playbackRefresh2;
    private TabLayoutMediator tabLayoutMediator;
    private PlayersPagerAdapter playersPagerAdapter;
    private ActivityLogger activityLogger;
    private ThemeManager themeManager;
    private Timer lyricsTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // LOGGING
        activityLogger = new ActivityLogger();
        activityLogger.activityStart();

        // VIEWS
        binding = ActivityDtspotifyBinding.inflate(getLayoutInflater());
        binding.tabs.addOnTabSelectedListener(this);
        binding.viewPager.registerOnPageChangeCallback(new PageChangeCallbackHandler(this));
        setContentView(binding.getRoot());

        // TABS
        playersPagerAdapter = new PlayersPagerAdapter(this, this, new SpotifyAuthHelper.AuthData[]{authData1, authData2});
        playersPagerAdapter.notifyFragmentCreated(this);
        tabLayoutMediator = new TabLayoutMediator(binding.tabs, binding.viewPager, ((tab, position) -> tab.setText(playersPagerAdapter.getPageTitle(position))));
        playerStateManager = new PlayerStateManager(binding.tabs, binding.snackbar.getRoot());
        binding.viewPager.setAdapter(playersPagerAdapter);
        binding.viewPager.setOffscreenPageLimit(5);
        tabLayoutMediator.attach();

        // LYRICS TIMER
        lyricsTimer = Timer.set(30).seconds();
        lyricsTimer.start();
        lyricsTimer.onRing(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleExceptions();
        defineDarkMode();
        defineSwipeControls();
        insetContentUnderStatusBar();
    }

    @Override
    public void onFragmentCreated(Fragment newFragment, int position, Fragment[] activeFragments) {
        playerStateManager.provideFragment(((PlayerFragment) newFragment), position);
        LinearLayout tabStrip = (LinearLayout) binding.tabs.getChildAt(0);
        tabStrip.getChildAt(position).setOnLongClickListener(this);
    }

    @Override
    protected void onDestroy() {
        activityLogger.activityEnd();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!playerStateManager.getCurrentFragment().onBackPressed())
            super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        // on any touch event
        if (playerStateManager.getCurrentFragment() != null)
            playerStateManager.getCurrentFragment().hideLyricsScreen();
        lyricsTimer.restart();
        // detect a touch event from a dialog and consume event now
        if ((ev.getEdgeFlags() & 0b0010000) > 0)
            return true;

        // on activity touch event
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onTimer() {
        if (playerStateManager.getCurrentFragment().getConnectionState().getValue() != PlayerFragment.ConnectionState.CONNECTED)
            return;
        playerStateManager.getCurrentFragment().onLyricsTimerExpired();
    }


    public boolean isLyricsTimerExpired() {
        return lyricsTimer.isExpired();
    }

    public PlayerFragment getCurrentPlayer() {
        return playerStateManager.getCurrentFragment();
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                return playerStateManager.getCurrentFragment().onVolumePressed(event.getKeyCode(), event.getAction()) || super.dispatchKeyEvent(event);
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    public boolean dispatchKeyEvent(@NonNull KeyEvent event, int oldVolume) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                return playerStateManager.getCurrentFragment().onVolumePressed(oldVolume, event.getKeyCode(), event.getAction()) || super.dispatchKeyEvent(event);
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    private void handleExceptions() {
        Logging.provideContext(this);
        ExceptionHandler exceptionHandler = new ExceptionHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
        if (exceptionHandler.hasLogUE()) {
            activityLogger.exception(exceptionHandler.getLogUE());
            exceptionHandler.resetLogUE();
        }
        if (exceptionHandler.hasLogSE()) {
            activityLogger.exception(exceptionHandler.getLogSE());
            exceptionHandler.resetLogSE();
        }
        exceptionHandler.addUECallback((t, a, e) -> activityLogger.exception(ExceptionHandler.uncaughtExceptionToString(t, a, e)));
        exceptionHandler.addSECallback((t, a, s) -> activityLogger.exception(ExceptionHandler.systemErrorToString(t, a, s)));
    }

    private void defineSwipeControls() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean swipeEnabled = defaultSharedPreferences.getBoolean(SettingsDialog.KEY_SWIPE, false);
        binding.viewPager.setUserInputEnabled(swipeEnabled);
    }

    private void defineSwipeControls(boolean enabled) {
        binding.viewPager.setUserInputEnabled(enabled);
    }

    private void defineDarkMode() {
        themeManager = ThemeManager.getInstance();
        themeManager.addThemeChangeListener(this);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkModeEnabled = defaultSharedPreferences.getBoolean(SettingsDialog.KEY_DARKMODE, false);
        darkModeChanged(darkModeEnabled);
        new Handler(Looper.getMainLooper()).post(() -> reloadDarkMode(darkModeEnabled));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        themeManager.instant(this, nightModeFlags == Configuration.UI_MODE_NIGHT_NO ? ThemeManager.Theme.LIGHT : ThemeManager.Theme.DARK);
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int index = playersPagerAdapter.getIndexOfTab(tab);
        playerStateManager.setSelected(index);
        playerStateManager.updateTabColors();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        playerStateManager.updateTabColors();
    }

    private void insetContentUnderStatusBar() {
        ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) binding.coordinator.getLayoutParams();
        newLayoutParams.topMargin = getStatusBarHeight();
        binding.coordinator.setLayoutParams(newLayoutParams);
    }

    @SuppressLint({"DiscouragedApi", "InternalInsetResource"})
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @SuppressLint({"DiscouragedApi", "InternalInsetResource"})
    public int getNavigationBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onLongClick(View v) {
        SettingsDialog settingsDialog = SettingsDialog.newInstance(this);
        settingsDialog.show(getSupportFragmentManager(), getClass().getSimpleName());
        return true;
    }

    @Override
    public void swipeChanged(boolean swipe) {
        defineSwipeControls(swipe);
    }

    @Override
    public void darkModeChanged(boolean darkMode) {
        if (darkMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public void reloadDarkMode(boolean darkMode) {
        if (darkMode) {
            inDayNightReload = true;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            inDayNightReload = false;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            inDayNightReload = true;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            inDayNightReload = false;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    @Override
    public void shareClicked() {
        ApplicationInfo app = getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        // Append file and send Intent
        File originalApk = new File(filePath);

        try {
            //Make new directory in new location
            File tempFile = new File(getExternalCacheDir() + "/ExtractedApk");
            //If directory doesn't exists create new
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;
            //Get application's name and convert to lowercase
            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes).replace(" ", "").toLowerCase() + ".apk");
            //If file doesn't exists create new
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            //Copy file to new location
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            //Open share dialog
            Intent intent = new Intent(Intent.ACTION_SEND);

            // MIME of .apk is "application/vnd.android.package-archive".
            // but Bluetooth does not accept this. You could use "*/*" instead.
            intent.setType("application/vnd.android.package-archive");
            intent.putExtra(
                    Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                            this, BuildConfig.APPLICATION_ID + ".provider", new File(tempFile.getPath())
                    )
            );
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(Intent.createChooser(intent, "Share app via"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onThemeSwitch(ThemeManager.Theme theme) {
        binding.rootConstraint.setBackgroundColor(ThemeManager.ThemeColor.getColor(this, android.R.attr.colorBackground));
        binding.appbarmcv.setCardBackgroundColor(ThemeManager.ThemeColor.getColor(this, androidx.appcompat.R.attr.actionBarItemBackground));
        playerStateManager.updateTabColors();
        getWindow().setStatusBarColor(ThemeManager.ThemeColor.getColor(this, androidx.appcompat.R.attr.actionBarItemBackground));
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(theme == ThemeManager.Theme.LIGHT);
    }

    private static class ActivityLogger extends Logging<String> {

        protected ActivityLogger() {
            super("ApplicationLogger");
        }

        private void exception(String savedData) {
            log(savedData);
        }

        private void activityStart() {
            log("Activity created");
        }

        private void activityEnd() {
            log("Activity destroyed");
        }
    }
}