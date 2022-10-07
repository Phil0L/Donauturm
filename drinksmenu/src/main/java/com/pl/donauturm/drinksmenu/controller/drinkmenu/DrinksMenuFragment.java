package com.pl.donauturm.drinksmenu.controller.drinkmenu;

import static com.pl.donauturm.drinksmenu.model.DrinksMenu.deserializer;
import static com.pl.donauturm.drinksmenu.model.DrinksMenu.serializer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DMEBitmapStorage;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DrinksMenuEditorActivity;
import com.pl.donauturm.drinksmenu.controller.util.CloudState;
import com.pl.donauturm.drinksmenu.controller.util.api.DrinksMenuAPI;
import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.DrinksMenuLocal;
import com.pl.donauturm.drinksmenu.view.popup.UpToDateInfo;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DrinksMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrinksMenuFragment extends Fragment implements DrinksMenu.OnMenuLoadedListener {

    private Button mEditButton;
    private TextView mVersionTextView;
    private TextView mIdTextView;
    private ImageView mMenuView;
    private DrinksMenu drinksMenu;


    public DrinksMenuFragment() {
        // Required empty public constructor
    }

    public DrinksMenuFragment(DrinksMenu drinksMenu) {
        this.drinksMenu = drinksMenu;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DrinksMenuFragment.
     */
    public static DrinksMenuFragment newInstance(DrinksMenu drinksMenu) {
        if (drinksMenu == null) return null;
        return new DrinksMenuFragment(drinksMenu);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null && drinksMenu == null) {
            drinksMenu = (DrinksMenu) savedInstanceState.getSerializable("drinksMenu");
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drinks_menu_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mEditButton = view.findViewById(R.id.edit_button);
        this.mMenuView = view.findViewById(R.id.drinkmenu_preview);
        this.mVersionTextView = view.findViewById(R.id.drinksmenu_version);
        this.mIdTextView = view.findViewById(R.id.drinksmenu_id);
        if (drinksMenu == null){
            Log.e("DrinksMenuFragment", "DrinksMenu is null");
            return;
        }
        this.mEditButton.setOnClickListener(this::edit);
        this.mMenuView.setOnClickListener(this::reloadImage);
        if (drinksMenu.isLoading()) {
            this.mMenuView.setAlpha(0.8f);
            this.mEditButton.setVisibility(View.GONE);
            this.drinksMenu.onLoaded(this);
        } else
            this.mMenuView.post(() -> this.reloadImage(null));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("drinksMenu", drinksMenu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public DrinksMenu getDrinksMenu() {
        return drinksMenu;
    }

    @SuppressLint("SetTextI18n")
    public void reloadImage(View v) {
        if (drinksMenu.isLoading()) return;
        this.mVersionTextView.setText("v." + drinksMenu.getVersion());
        this.mIdTextView.setText("id." + drinksMenu.getId());
        new DrinksMenuRenderer().renderFromMenu(getContext(), drinksMenu, bm -> {
            this.mMenuView.setImageBitmap(bm);
            this.mMenuView.postInvalidate();
        });
    }

    public void edit(View v) {
        this.editor.launch(drinksMenu);
    }

    public void cloudClicked(DrinksMenuAPI api, View v) {
        switch (drinksMenu.getCloudState()) {
            case UNKNOWN:
            case PULLING:
            case PUSHING:
            case READY_FOR_PULL:
                break;
            case UP_TO_DATE:
                new UpToDateInfo(getActivity()).show(v);
                break;
            case READY_FOR_PUSH:
                uploadDrinksMenu(api);
                break;
        }
    }


    void uploadDrinksMenu(DrinksMenuAPI api) {
        getDrinksMenu().setCloudState(CloudState.PUSHING);
        api.asynchronous.uploadDrinkMenu(drinksMenu, (v) -> {
            getDrinksMenu().setCloudState(CloudState.UP_TO_DATE);
            DrinksMenuLocal local = new DrinksMenuLocal(getDrinksMenu());
            local.setCloudVersion(getDrinksMenu().getVersion());
            LocalDrinksMenuManager.saveLocalSaveAsync(getContext(), local);
        });
    }

    @Override
    public void onMenuLoaded(DrinksMenu menu) {
        this.drinksMenu = menu;
        this.mMenuView.post(() -> {
            this.mEditButton.setVisibility(View.VISIBLE);
            this.mMenuView.setAlpha(1f);
            this.reloadImage(null);
        });

    }

    final ActivityResultLauncher<DrinksMenu> editor = registerForActivityResult(new ActivityResultContract<DrinksMenu, DrinksMenu>() {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, DrinksMenu input) {
            Intent intent = new Intent(getActivity(), DrinksMenuEditorActivity.class);
            String menuString = serializer().toJson(drinksMenu);
            intent.putExtra("menu", menuString);
            DMEBitmapStorage.getInstance().setBackground(drinksMenu.getBackGround());
            return intent;
        }

        @NonNull
        @Override
        public DrinksMenu parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == DrinksMenuEditorActivity.RESULT_CANCELED || intent == null)
                return drinksMenu;
            String menuString = intent.getStringExtra("menu");
            Bitmap background = DMEBitmapStorage.getInstance().getBackground();
            DrinksMenu drinksMenu = deserializer().fromJson(menuString, DrinksMenu.class);
            drinksMenu.provideBackGround(background);
            DMEBitmapStorage.getInstance().clear();
            return drinksMenu;
        }
    }, result -> {
        if (result != null) {
            mMenuView.post(() -> {
                DrinkMenuRegistry.getInstance().put(result.getName(), result);
                LocalDrinksMenuManager.saveLocalSaveAsync(getContext(), result);
                drinksMenu = result;
                drinksMenu.setCloudState(CloudState.READY_FOR_PUSH);
                drinksMenu.provideBackGround(drinksMenu.getBackGround()); //hack to set loading to false
                reloadImage(null);
            });

        }
    });


    public static class LoadingFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_drinks_menu_page_loading, container, false);
            ImageView animationView = view.findViewById(R.id.loading_spinner);
            if (animationView.getDrawable() instanceof AnimatedVectorDrawable)
                ((AnimatedVectorDrawable) animationView.getDrawable()).start();
            return view;
        }
    }
}