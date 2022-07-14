package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.DrinkGroupBottomSheet;
import com.pl.donauturm.drinksmenu.view.views.ItemView;
import com.pl.donauturm.drinksmenu.model.Item;

public class SizeEditorFragment extends Fragment implements TextWatcher, View.OnClickListener, DrinkGroupBottomSheet.EditorUpdate {

    private Item item;
    private OnResize callback;

    private EditText mEditTextHeight;
    private EditText mEditTextWidth;
    private ImageView mLock;

    public SizeEditorFragment(){
        // Required empty public constructor
    }

    public SizeEditorFragment(Item item, OnResize callback) {
        this.item = item;
        this.callback = callback;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DrinksMenuFragment.
     */
    public static SizeEditorFragment newInstance(Item item, OnResize callback) {
        return new SizeEditorFragment(item, callback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_size, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mEditTextHeight = view.findViewById(R.id.size_edit_height);
        mEditTextWidth = view.findViewById(R.id.size_edit_width);
        mLock = view.findViewById(R.id.lock);

        mEditTextHeight.setText(String.valueOf(((int) item.getHeight())));
        mEditTextWidth.setText(String.valueOf(((int) item.getWidth())));
        mLock.setImageResource(item.isSizeLocked() ? R.drawable.ic_locked : R.drawable.ic_unlocked);

        mEditTextHeight.addTextChangedListener(this);
        mEditTextWidth.addTextChangedListener(this);
        mLock.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String w = String.valueOf(mEditTextWidth.getText());
        String h = String.valueOf(mEditTextHeight.getText());
        if (w.isEmpty() || !w.matches("\\d+")) return;
        if (h.isEmpty() || !h.matches("\\d+")) return;
        int width = Integer.parseInt(w);
        int height = Integer.parseInt(h);
        callback.onResize(width, height, null);
    }

    @Override
    public void onDataChanged(Item item) {
        if (mEditTextHeight == null || mEditTextWidth == null) return;
        if (!String.valueOf(((int) item.getHeight())).equals(String.valueOf(mEditTextHeight.getText()))) {
            mEditTextHeight.removeTextChangedListener(this);
            mEditTextHeight.setText(String.valueOf(((int) item.getHeight())));
            mEditTextHeight.addTextChangedListener(this);
        }
        if (!String.valueOf(((int) item.getWidth())).equals(String.valueOf(mEditTextWidth.getText()))) {
            mEditTextHeight.removeTextChangedListener(this);
            mEditTextWidth.setText(String.valueOf(((int) item.getWidth())));
            mEditTextHeight.addTextChangedListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        boolean wasLocked = item.isSizeLocked();
        boolean nowLocked = !wasLocked;
        mLock.setImageResource(nowLocked ? R.drawable.ic_locked : R.drawable.ic_unlocked);
        callback.onSizeLock(nowLocked);
    }

    public interface OnResize{
        void onResize(float width, float height, @Nullable ItemView itemView);
        void onSizeLock(boolean nowLocked);
    }
}
