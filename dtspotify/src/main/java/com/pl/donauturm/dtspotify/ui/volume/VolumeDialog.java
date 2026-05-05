package com.pl.donauturm.dtspotify.ui.volume;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.pl.donauturm.dtspotify.DTSpotify;
import com.pl.donauturm.dtspotify.commons.AgingList;
import com.pl.donauturm.dtspotify.commons.PopupDialog;
import com.pl.donauturm.dtspotify.databinding.DialogVolumeBinding;
import com.pl.donauturm.spotify.CallbackList;

import org.jetbrains.annotations.Contract;

import java.util.Locale;

public class VolumeDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener, View.OnTouchListener, PopupDialog.OnDispatchTouchEvent {

    private final CallbackList<OnVolumeSliderChanged> callbackList;
    private final Runnable autoClose;
    private final Handler handler;
    private final AgingList<Integer> volumesToIgnore;
    private DialogVolumeBinding binding;
    private int volume;
    private boolean dragging;

    private VolumeDialog() {
        callbackList = new CallbackList<>();
        volumesToIgnore = new AgingList<>();
        handler = new Handler(Looper.getMainLooper());
        autoClose = this::dismiss;
    }

    @NonNull
    @Contract(" -> new")
    public static VolumeDialog newInstance() {
        return new VolumeDialog();
    }

    public void setVolume(int volume) {
        this.setVolume(volume, false);
    }

    public void setVolume(int volume, boolean keepAlive) {
        volumesToIgnore.add(Integer.valueOf(volume), 5);
        if (keepAlive)
            keepAlive();
        updateVolume(volume);
    }

    public void setVolumeFromApi(int volume) {
        volumesToIgnore.age();
        if (!volumesToIgnore.contains(volume))
            updateVolume(volume);
    }

    private void updateVolume(int volume) {
        this.volume = volume;
        if (binding == null)
            return;
        binding.volumeText.setText(String.format(Locale.getDefault(), "%02d%s", volume, "%"));
        if (!dragging)
            binding.seekBar.setProgress(volume);
    }

    private void keepAlive() {
        handler.removeCallbacks(autoClose);
        handler.postDelayed(autoClose, 5 * 1000);
    }

    private void immortal() {
        handler.removeCallbacks(autoClose);
    }

    public void onVolumeSliderChanged(OnVolumeSliderChanged callback) {
        callbackList.add(callback);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogVolumeBinding.inflate(getLayoutInflater());

        binding.seekBar.setOnSeekBarChangeListener(this);
        binding.seekBar.setProgress(volume);
        binding.volumeText.setText(String.format(Locale.getDefault(), "%02d%s", volume, "%"));

        new Handler(Looper.getMainLooper()).postDelayed(this::onDialogCreated, 100);

        PopupDialog popupDialog = new PopupDialog(getActivity());
        popupDialog.setView(binding.dialogRoot);
        popupDialog.onDispatchTouchEvent(this);
        return popupDialog;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onDialogCreated() {
        Dialog dialog = requireDialog();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnKeyListener((d, keyCode, event) ->
                ((DTSpotify) requireActivity()).dispatchKeyEvent(event, volume));
        dialog.getWindow().getDecorView().setOnTouchListener((v, event) -> {
            requireActivity().dispatchTouchEvent(event);
            return false;
        });
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
        this.keepAlive();
        super.show(manager, tag);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
            callbackList.fire(c -> c.onVolumeSliderChanged(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        dragging = true;
        immortal();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        dragging = false;
        keepAlive();
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        keepAlive();
        Log.d(getTag(), "Action: " + event.getAction());
        return true;
    }

    public interface OnVolumeSliderChanged {
        void onVolumeSliderChanged(int newVolume);
    }


}
