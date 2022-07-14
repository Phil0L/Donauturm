package com.pl.donauturm.drinksmenu.controller.drinkmenu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DrinksMenuEditorActivity;
import com.pl.donauturm.drinksmenu.controller.MainitemDrinksMenu;

import static com.pl.donauturm.drinksmenu.controller.MainitemDrinksMenu.largeLog;
import static com.pl.donauturm.drinksmenu.controller.drinkmenu.DrinkMenuRegistry.DrinkMenus;
import static com.pl.donauturm.drinksmenu.model.DrinksMenu.deserializer;
import static com.pl.donauturm.drinksmenu.model.DrinksMenu.serializer;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DrinksMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrinksMenuFragment extends Fragment {

    private Button mEditButton;
    private ImageView mMenuView;
    private com.pl.donauturm.drinksmenu.model.DrinksMenu drinksMenu;


    public DrinksMenuFragment() {
        // Required empty public constructor
    }

    public DrinksMenuFragment(com.pl.donauturm.drinksmenu.model.DrinksMenu drinksMenu) {
        this.drinksMenu = drinksMenu;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DrinksMenuFragment.
     */
    public static DrinksMenuFragment newInstance(com.pl.donauturm.drinksmenu.model.DrinksMenu drinksMenu) {
        return new DrinksMenuFragment(drinksMenu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drinks_menu_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mEditButton = view.findViewById(R.id.edit_button);
        this.mMenuView = view.findViewById(R.id.drinkmenu_preview);
        this.mEditButton.setOnClickListener(this::edit);
        this.mMenuView.setOnClickListener(this::reloadImage);
        this.mMenuView.post(() -> this.reloadImage(null));
    }

    public void reloadImage(View v) {
        new DrinksMenuRenderer().renderFromMenu(getContext(), drinksMenu, bm -> {
            this.mMenuView.setImageBitmap(bm);
            this.mMenuView.postInvalidate();
        });
    }

    public void edit(View v) {
        this.editor.launch(drinksMenu);
    }

    final ActivityResultLauncher<com.pl.donauturm.drinksmenu.model.DrinksMenu> editor = registerForActivityResult(new ActivityResultContract<com.pl.donauturm.drinksmenu.model.DrinksMenu, com.pl.donauturm.drinksmenu.model.DrinksMenu>() {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, com.pl.donauturm.drinksmenu.model.DrinksMenu input) {
            Intent intent = new Intent(getActivity(), DrinksMenuEditorActivity.class);
            String menuString = serializer(context).toJson(drinksMenu);
            intent.putExtra("menu", menuString);
            return intent;
        }

        @NonNull
        @Override
        public com.pl.donauturm.drinksmenu.model.DrinksMenu parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == DrinksMenuEditorActivity.RESULT_CANCELED || intent == null)
                return drinksMenu;
            String menuString = intent.getStringExtra("menu");
            return deserializer(getContext()).fromJson(menuString, com.pl.donauturm.drinksmenu.model.DrinksMenu.class);
        }
    }, result -> {
        if (result != null) {
            MainitemDrinksMenu.largeLog(getClass().getSimpleName(), result);
            DrinkMenus.replace(result.getName(), result);
            //todo update adapter
            drinksMenu = result;
        }
    });
}