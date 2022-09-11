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
import com.pl.donauturm.drinksmenu.model.content.DrinksMenuItem;
import com.pl.donauturm.drinksmenu.view.views.ItemView;

public class PositionEditorFragment extends Fragment implements TextWatcher, View.OnClickListener, DrinkGroupBottomSheet.EditorUpdate {

    private DrinksMenuItem item;
    private OnReposition callback;

    private EditText mEditTextLeft;
    private EditText mEditTextTop;
    private ImageView mLock;

    public PositionEditorFragment() {
        // Required empty public constructor
    }

    public PositionEditorFragment(DrinksMenuItem item, OnReposition callback) {
        this.item = item;
        this.callback = callback;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DrinksMenuFragment.
     */
    public static PositionEditorFragment newInstance(DrinksMenuItem item, OnReposition callback) {
        return new PositionEditorFragment(item, callback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_position, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mEditTextLeft = view.findViewById(R.id.pos_edit_left);
        mEditTextTop = view.findViewById(R.id.pos_edit_top);
        mLock = view.findViewById(R.id.lock);

        mEditTextLeft.setText(String.valueOf(((int) item.getLeft())));
        mEditTextTop.setText(String.valueOf(((int) item.getTop())));
        mLock.setImageResource(item.isPositionLocked() ? R.drawable.ic_locked : R.drawable.ic_unlocked);

        mEditTextLeft.addTextChangedListener(this);
        mEditTextTop.addTextChangedListener(this);
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
        String l = String.valueOf(mEditTextLeft.getText());
        String t = String.valueOf(mEditTextTop.getText());
        if (l.isEmpty() || !l.matches("-?\\d+")) return;
        if (t.isEmpty() || !t.matches("-?\\d+")) return;
        int left = Integer.parseInt(l);
        int top = Integer.parseInt(t);
        callback.onReposition(left, top, null);
    }

    @Override
    public void onDataChanged(DrinksMenuItem item) {
        if (!String.valueOf(((int) item.getLeft())).equals(String.valueOf(mEditTextLeft.getText()))) {
            mEditTextLeft.removeTextChangedListener(this);
            mEditTextLeft.setText(String.valueOf(((int) item.getLeft())));
            mEditTextLeft.addTextChangedListener(this);
        }
        if (!String.valueOf(((int) item.getTop())).equals(String.valueOf(mEditTextTop.getText()))) {
            mEditTextTop.removeTextChangedListener(this);
            mEditTextTop.setText(String.valueOf(((int) item.getTop())));
            mEditTextTop.addTextChangedListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        boolean wasLocked = item.isPositionLocked();
        boolean nowLocked = !wasLocked;
        mLock.setImageResource(nowLocked ? R.drawable.ic_locked : R.drawable.ic_unlocked);
        callback.onPositionLock(nowLocked);
    }

    public interface OnReposition {
        void onReposition(float left, float top, @Nullable ItemView itemView);
        void onPositionLock(boolean nowLocked);
    }
}
