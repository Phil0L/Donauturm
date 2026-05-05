package com.pl.donauturm.dtspotify.ui.settings;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.pl.donauturm.commons.util.ThemeManager;
import com.pl.donauturm.commons.util.log.Logging;
import com.pl.donauturm.dtspotify.R;
import com.pl.donauturm.dtspotify.commons.PopupDialog;
import com.pl.donauturm.dtspotify.databinding.DialogSettingsBinding;

import org.jetbrains.annotations.Contract;

import java.util.List;

public class SettingsDialog extends DialogFragment implements Logging.OnLogUpdate,
        PopupDialog.OnDispatchTouchEvent, ThemeManager.OnThemSwitch {

    public static final String KEY_SWIPE = "spotify_swipe";
    public static final String KEY_SHARE = "spotify_share";
    public static final String KEY_DARKMODE = "spotify_dark";
    private final SettingsLogger logger;
    private final PreferencesFragment preferences;
    private final Activity activity;
    private OnDialogCreated creatorCallback;
    private DialogSettingsBinding binding;

    public SettingsDialog() {
        this(null);
    }

    public SettingsDialog(Activity activity) {
        this.activity = activity;
        this.preferences = new PreferencesFragment();
        this.logger = new SettingsLogger();
    }

    @NonNull
    @Contract("_ -> new")
    public static SettingsDialog newInstance(Activity activity) {
        return new SettingsDialog(activity);
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // VIEW
        binding = DialogSettingsBinding.inflate(getLayoutInflater());
        binding.logging.setMovementMethod(new ScrollingMovementMethod());
        binding.logging.setHorizontallyScrolling(true);
        binding.logging.setText(String.join("\n", Logging.getLogs()) + "\n");
        binding.copyButton.setOnClickListener(v -> onCopyLogsClicked());
        binding.clearButton.setOnClickListener(v -> onClearLogsClicked());
        preferences.setCallback(listener);

        // DIALOG
        PopupDialog popupDialog = new PopupDialog(getActivity());
        popupDialog.setView(binding.dialogRoot);
        popupDialog.onDispatchTouchEvent(this);
        if (popupDialog.getWindow() != null)
            popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return popupDialog;

    }

    @Override
    public void onStart() {
        super.onStart();
        if (creatorCallback != null)
            creatorCallback.onDialogCreated();

        Logging.addListener(this);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, preferences)
                .commit();
    }

    public void onCreated(OnDialogCreated creatorCallback){
        this.creatorCallback = creatorCallback;
    }

    private void onCopyLogsClicked() {
        ClipboardManager clipboard = getSystemService(requireContext(), ClipboardManager.class);
        ClipData clip = ClipData.newPlainText("DT Spotify app logs", String.join("\n", Logging.getLogs()));
        if (clipboard != null && clip != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Copied logs to clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    private void onClearLogsClicked() {
        Logging.clearLogs();
        logger.clearedLogs();
    }


    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        requireActivity().dispatchTouchEvent(ev);
        return false;
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        if (getDialog() != null && getDialog().isShowing() && !isRemoving())
            return;
        super.show(manager, tag);
    }

    @SuppressLint("SetTextI18n")
    @Override
    @AnyThread
    public void onLogUpdate(List<String> logs) {
        if (activity != null)
            activity.runOnUiThread(() -> binding.logging.setText(String.join("\n", logs) + "\n"));
        else binding.logging.setText(String.join("\n", logs) + "\n");
    }

    @Override
    public void onThemeSwitch(ThemeManager.Theme theme) {
        // reload dialog
        SettingsDialog settingsDialog = SettingsDialog.newInstance(requireActivity());
        settingsDialog.show(requireActivity().getSupportFragmentManager(), getClass().getSimpleName());
        dismiss();
    }

    public <A> A requireActivity(@NonNull Class<A> aClass) {
        return aClass.cast(requireActivity());
    }

    private final OnSettingsChanged listener = new OnSettingsChanged() {
        @Override
        public void swipeChanged(boolean swipe) {
            logger.swipe(swipe);
        }

        @Override
        public void darkModeChanged(boolean darkMode) {
            logger.darkMode(darkMode);
            SettingsDialog.this.onThemeSwitch(darkMode ? ThemeManager.Theme.DARK : ThemeManager.Theme.LIGHT);
        }

        @Override
        public void shareClicked() {
            logger.share();
        }
    };

    public static class PreferencesFragment extends PreferenceFragmentCompat {

        private OnSettingsChanged callback;

        public void setCallback(OnSettingsChanged callback) {
            this.callback = callback;
        }

        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            SwitchPreference swipePreference = findPreference(KEY_SWIPE);
            if (swipePreference != null) {
                swipePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    if (callback != null)
                        callback.swipeChanged((Boolean) newValue);
                    FragmentActivity activity = getActivity();
                    if (activity instanceof OnSettingsChanged)
                        ((OnSettingsChanged) activity).swipeChanged((Boolean) newValue);
                    return true;
                });
            }

            SwitchPreference darkModePreference = findPreference(KEY_DARKMODE);
            if (darkModePreference != null) {
                darkModePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    if (callback != null)
                        callback.darkModeChanged((Boolean) newValue);
                    FragmentActivity activity = getActivity();
                    if (activity instanceof OnSettingsChanged)
                        ((OnSettingsChanged) activity).darkModeChanged((Boolean) newValue);
                    return true;
                });
            }

            Preference sharePreference = findPreference(KEY_SHARE);
            if (sharePreference != null) {
                sharePreference.setOnPreferenceClickListener(preference -> {
                    if (callback != null)
                        callback.shareClicked();
                    FragmentActivity activity = getActivity();
                    if (activity instanceof OnSettingsChanged)
                        ((OnSettingsChanged) activity).shareClicked();
                    return true;
                });
            }
        }
    }

    public interface OnSettingsChanged {
        void swipeChanged(boolean swipe);

        void darkModeChanged(boolean darkMode);

        void shareClicked();
    }

    public interface OnDialogCreated{
        void onDialogCreated();
    }

    public static class SettingsLogger extends Logging<String> {

        protected SettingsLogger() {
            super("Settings");
        }

        public void clearedLogs() {
            log("Logs cleared");
        }

        public void swipe(boolean enabled) {
            log("Swipe accounts: " + (enabled ? "enabled" : "disabled"));
        }

        public void darkMode(boolean enabled) {
            log("Dark mode: " + (enabled ? "enabled" : "disabled"));
        }

        public void share() {
            log("Sharing this app");
        }
    }


}
