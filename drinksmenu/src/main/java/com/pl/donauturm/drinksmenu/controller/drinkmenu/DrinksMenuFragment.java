package com.pl.donauturm.drinksmenu.controller.drinkmenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.pl.donauturm.drinksmenu.controller.DrinksMenuAPI;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DrinksMenuEditorActivity;
import com.pl.donauturm.drinksmenu.controller.MainitemDrinksMenu;
import com.pl.donauturm.drinksmenu.model.DrinksMenu;

import static com.pl.donauturm.drinksmenu.model.DrinksMenu.deserializer;
import static com.pl.donauturm.drinksmenu.model.DrinksMenu.serializer;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DrinksMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrinksMenuFragment extends Fragment {

    private Button mEditButton;
    private TextView mVersionTextView;
    private ImageView mMenuView;
    private DrinksMenu drinksMenu;
    private CloudState cloudState = CloudState.UNKNOWN;


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
        this.mVersionTextView = view.findViewById(R.id.drinksmenu_version);
        this.mEditButton.setOnClickListener(this::edit);
        this.mMenuView.setOnClickListener(this::reloadImage);
        this.mMenuView.post(() -> this.reloadImage(null));
    }

    @SuppressLint("SetTextI18n")
    public void reloadImage(View v) {
        new DrinksMenuRenderer().renderFromMenu(getContext(), drinksMenu, bm -> {
            this.mMenuView.setImageBitmap(bm);
            this.mMenuView.postInvalidate();
            this.mVersionTextView.setText("v." + drinksMenu.getVersion());
        });
    }

    public void edit(View v) {
        this.editor.launch(drinksMenu);
    }

    public void cloudClicked(DrinksMenuAPI api){
        switch (cloudState){
            case UNKNOWN:
            case PULLING:
            case PUSHING:
            case UP_TO_DATE:
            case READY_FOR_PULL:
                break;
            case READY_FOR_PUSH:
                uploadDrinksMenu(api);
                break;
        }
    }

    public void setCloudState(CloudState cloudState) {
        this.cloudState = cloudState;
        requireActivity().invalidateOptionsMenu();
    }

    public CloudState getCloudState() {
        return cloudState;
    }


    void uploadDrinksMenu(DrinksMenuAPI api){
        setCloudState(CloudState.PUSHING);
        api.asynchronous.uploadDrinkMenu(drinksMenu, (v) -> setCloudState(CloudState.UP_TO_DATE));
    }

    final ActivityResultLauncher<DrinksMenu> editor = registerForActivityResult(new ActivityResultContract<DrinksMenu, DrinksMenu>() {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, DrinksMenu input) {
            Intent intent = new Intent(getActivity(), DrinksMenuEditorActivity.class);
            String menuString = serializer(context).toJson(drinksMenu);
            intent.putExtra("menu", menuString);
            return intent;
        }

        @NonNull
        @Override
        public DrinksMenu parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == DrinksMenuEditorActivity.RESULT_CANCELED || intent == null)
                return drinksMenu;
            String menuString = intent.getStringExtra("menu");
            return deserializer(getContext()).fromJson(menuString, DrinksMenu.class);
        }
    }, result -> {
        if (result != null) {
            MainitemDrinksMenu.largeLog(getClass().getSimpleName(), result);
            mMenuView.post(() -> {
                DrinkMenuRegistry.getInstance().replace(result.getName(), result);
                setCloudState(CloudState.READY_FOR_PUSH);
                drinksMenu = result;
                reloadImage(null);
            });

        }
    });

    public enum CloudState {
        UNKNOWN(R.drawable.ic_cloud),
        UP_TO_DATE(R.drawable.ic_cloud_done),
        READY_FOR_PUSH(R.drawable.ic_cloud_upload),
        PUSHING(R.drawable.ic_cloud_uploading),
        READY_FOR_PULL(R.drawable.ic_cloud_download),
        PULLING(R.drawable.ic_cloud_downloading),
        NO_CONNECTION(R.drawable.ic_cloud);

        private final int iconResource;

        public int getIconResource() {
            return iconResource;
        }

        CloudState(int iconResource) {
            this.iconResource = iconResource;
        }
    }
}