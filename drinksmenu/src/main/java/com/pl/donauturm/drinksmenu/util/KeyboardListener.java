package com.pl.donauturm.drinksmenu.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

/**
 * Based on the following Stackoverflow answer:
 * http://stackoverflow.com/questions/2150078/how-to-check-visibility-of-software-keyboard-in-android
 */
@SuppressWarnings("unused")
public class KeyboardListener implements ViewTreeObserver.OnGlobalLayoutListener
{

    private final Activity callingActivity;
    private final SoftKeyboardToggleListener listener;
    private final View mRootView;
    private final float mScreenDensity;
    private View heightReference;
    private int heightReferenceDefault;

    private boolean prevValue = false;
    private int prevHeight = 0;


    public interface SoftKeyboardToggleListener
    {
        void onToggleSoftKeyboard(boolean isVisible, int keyboardHeight);
    }


    @Override
    public void onGlobalLayout() {
        Rect r = new Rect();
        mRootView.getWindowVisibleDisplayFrame(r);

        // determine keyboard state
        int heightDiff = mRootView.getRootView().getHeight() - (r.bottom - r.top);
        float dp = heightDiff/ mScreenDensity;
        int keyboardLimit = 200;
        boolean isVisible = dp > keyboardLimit;

        int keyboardHeight = 0;
        // determine keyboard height
        if (heightReference == null) {
            int defaultKeyboardDP = 100;
            // Lollipop includes button bar in the root. Add height of button bar (48dp) to maxDiff
            int estimatedKeyboardDP = defaultKeyboardDP + 48;
            keyboardHeight = (int) TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, estimatedKeyboardDP, mRootView.getResources().getDisplayMetrics());
        }else{
            int refH = getReferenceHeight();
            if (refH != heightReferenceDefault){
                keyboardHeight = heightReferenceDefault - refH;
            }
        }

        if (listener != null && (isVisible != prevValue || keyboardHeight != prevHeight)) {
            prevValue = isVisible;
            prevHeight = heightDiff;
            listener.onToggleSoftKeyboard(isVisible, keyboardHeight);
        }
    }

    /**
     * Manually toggle soft keyboard visibility
     * @param context calling context
     */
    public static void toggleKeyboardVisibility(Context context)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void toggleKeyboardVisibility()
    {
        InputMethodManager inputMethodManager = (InputMethodManager) callingActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void forceCloseKeyboard(Activity activity)
    {
        View activeView = activity.getCurrentFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) activeView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null)
            inputMethodManager.hideSoftInputFromWindow(activeView.getWindowToken(), 0);
    }

    public void forceCloseKeyboard()
    {
        View activeView = callingActivity.getCurrentFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) activeView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null)
            inputMethodManager.hideSoftInputFromWindow(activeView.getWindowToken(), 0);
    }

    public KeyboardListener(@NonNull Activity activity, SoftKeyboardToggleListener listener)
    {
        this.listener = listener;
        this.callingActivity = activity;
        mRootView = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mScreenDensity = activity.getResources().getDisplayMetrics().density;
    }

    public void provideHeightReference(View heightReference){
        this.heightReference = heightReference;
        this.heightReferenceDefault = getReferenceHeight();
    }

    private int getReferenceHeight(){
        int[] location = new int[2];
        heightReference.getLocationOnScreen(location);
        return location[1];
    }

}
