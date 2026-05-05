package com.pl.donauturm.bingo;

import static java.util.stream.Stream.generate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pl.donauturm.bingo.databinding.FragmentBingoBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class MainFragmentBingo extends Fragment implements BingoEditDialog.OnBingoEdit {

    public static final String TAG = "Bingo";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    public static final String ITEMS = "items";

    private GridLayout gridView;
    private KonfettiView konfettiView;

    private int height;
    private int width;
    private List<String> items;
    private BingoAdapter bingoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentBingoBinding binding = FragmentBingoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        gridView = view.findViewById(R.id.bingo_grid);
        konfettiView = view.findViewById(R.id.konfetti);
        updateAdapter();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.bingo_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Reset":
                AreYouSure.newInstance().setOnYesListener(this::updateAdapter).show(getChildFragmentManager(), TAG);
                break;
            case "Edit":
                BingoEditDialog.newInstance(getWidth(), getHeight(), getItems().stream().map(BingoItem::getTitle).collect(Collectors.toList())).setOnBingoEdit(this).show(getChildFragmentManager(), TAG);
                break;
            default:
                return false;
        }
        return true;
    }

    private void updateAdapter() {
        gridView.removeAllViews();
        bingoAdapter = new BingoAdapter(requireContext(), gridView, new ArrayList<>(getItems()), getHeight(), getWidth());
        bingoAdapter.setOnItemClickListener(this::onItemClicked);
    }

    public void onItemClicked(View v, int index) {
        PartyFactory partyFactory = new PartyFactory(new Emitter(100, TimeUnit.MILLISECONDS).perSecond(2000))
                .position(getPositionInParent(v))
                .angle(Angle.TOP)
                .spread(90)
                .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE))
                .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(0f, 30f);
        konfettiView.start(partyFactory.build());
    }

    private Position getPositionInParent(@NonNull View childView) {
        return new Position.Absolute(childView.getX() + childView.getWidth() / 2f, childView.getY() + childView.getHeight() / 2f);
    }

    private int getHeight() {
        if (height != 0) return height;
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return height = sharedPreferences.getInt(HEIGHT, 4);
    }

    private int getWidth() {
        if (width != 0) return width;
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return width = sharedPreferences.getInt(WIDTH, 4);
    }

    private List<BingoItem> getItems() {
        if (items != null && !items.isEmpty())
            return items.stream().map(BingoItem::new).collect(Collectors.toList());
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        String data = sharedPreferences.getString(ITEMS, "test1, test2, test3, test4, test5, test6").trim();
        String[] is = data.split(", ");
        items = Arrays.asList(is);
        return Arrays.stream(is).map(BingoItem::new).collect(Collectors.toList());

    }

    @Override
    public void onHeightChange(int newHeight) {
        if (newHeight < 2 || newHeight > 20) return;
        height = newHeight;
        List<BingoItem> state = bingoAdapter.getState();
        gridView.removeAllViews();
        bingoAdapter = new BingoAdapter(requireContext(), gridView, getHeight(), getWidth(), state);
        bingoAdapter.setOnItemClickListener(this::onItemClicked);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(HEIGHT, newHeight);
        editor.apply();
    }

    @Override
    public void onWidthChange(int newWidth) {
        if (newWidth < 2 || newWidth > 20) return;
        width = newWidth;
        List<BingoItem> state = bingoAdapter.getState();
        gridView.removeAllViews();
        bingoAdapter = new BingoAdapter(requireContext(), gridView, getHeight(), getWidth(), state);
        bingoAdapter.setOnItemClickListener(this::onItemClicked);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(WIDTH, newWidth);
        editor.apply();
    }

    @Override
    public void onItemsAdded(String... item) {
        List<BingoItem> state = bingoAdapter.getState();
        state.addAll(items.stream().map(BingoItem::new).collect(Collectors.toList()));
        if (state.size() > getHeight() * getWidth()) {
            int overflow = state.size() - getHeight() * getWidth();
            int additionalRows = (overflow / getWidth()) + 1;
            onHeightChange(getHeight() + additionalRows);
        }
        gridView.removeAllViews();
        bingoAdapter = new BingoAdapter(requireContext(), gridView, getHeight(), getWidth(), state);
        bingoAdapter.setOnItemClickListener(this::onItemClicked);
    }

    @Override
    public void onItemChanged(String itemString) {
        if (itemString == null || itemString.isEmpty()) return;
        items = new ArrayList<>();
        String[] words = itemString.trim().replaceAll(",[, ]*", ",").split(",");
        for (String word : words){
            if (word.isEmpty() || word.equals(",")) continue;
            items.add(word);
        }
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ITEMS, String.join(", ", items));
        editor.apply();
    }

    public static class BingoAdapter extends ArrayAdapter<BingoItem> {

        private final GridLayout gridView;
        private OnItemClickListener onItemClickListener;

        public BingoAdapter(@NonNull Context context, GridLayout grid, @NonNull List<BingoItem> objects, int height, int width) {
            super(context, 0, new ArrayList<>());
            gridView = grid;
            gridView.setColumnCount(height);
            gridView.setRowCount(height);
            Collections.shuffle(objects);
            if (objects.size() < width * height)
                objects.addAll(generate(() -> (BingoItem) null).limit((long) height * width - objects.size()).collect(Collectors.toList()));
            List<BingoItem> items = objects.stream().limit((long) height * width).collect(Collectors.toList());
            addAll(items);
            gridView.invalidate();
        }

        public BingoAdapter(@NonNull Context context, GridLayout grid, int height, int width, @NonNull List<BingoItem> state) {
            super(context, 0, new ArrayList<>());
            gridView = grid;
            gridView.setColumnCount(height);
            gridView.setRowCount(height);
            if (state.size() < width * height)
                state.addAll(generate(() -> (BingoItem) null).limit((long) height * width - state.size()).collect(Collectors.toList()));
            List<BingoItem> items = state.stream().limit((long) height * width).collect(Collectors.toList());
            addAll(items);
            gridView.invalidate();
            updateAllCrowns();
        }

        @SuppressLint({"SetTextI18n", "ViewHolder"})
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.prefab_bingo_button, parent, false);
            GridLayout.LayoutParams parem = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f), GridLayout.spec(GridLayout.UNDEFINED, 1f));
            view.setLayoutParams(parem);

            BingoItem bingoItem = getItem(position);
            if (bingoItem == null) {
                view.setVisibility(View.INVISIBLE);
                return view;
            }
            Button button = view.findViewById(R.id.button);
            TextView multiplier = view.findViewById(R.id.multiplier);
            ImageView crown = view.findViewById(R.id.crown);
            button.setText(bingoItem.title);
            multiplier.setText("x" + bingoItem.multiplier);
            button.setOnClickListener(v -> {
                onItemClicked(view, position);
                BingoItem bingoItem1 = getItem(position);
                bingoItem1.increaseMultiplier();
//                TextView multiplier1 = getViewAt(position).findViewById(R.id.multiplier);
                multiplier.setText("x" + bingoItem1.multiplier);
                crown.setVisibility(isLeadingItem(bingoItem1) ? View.VISIBLE : View.INVISIBLE);
                updateAllCrowns();
            });

            return view;
        }

        private void onItemClicked(View v, int position) {
            if (onItemClickListener != null)
                onItemClickListener.onItemClicked(v, position);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void clear() {
            super.clear();
            gridView.removeAllViews();
        }

        @Override
        public void addAll(@NonNull Collection<? extends BingoItem> collection) {
            super.addAll(collection);
            int i = -1;
            for (BingoItem bingoItem : collection) {
                add(bingoItem, ++i);
            }
        }

        public void add(@Nullable BingoItem object, int index) {
            View view = getView(index, null, gridView);
            gridView.addView(view);
        }

        public View getViewAt(int position) {
            return gridView.getChildAt(position);
        }

        public boolean isLeadingItem(BingoItem bi) {
            int leading = 0;
            for (int i = 0; i < getCount(); i++) {
                BingoItem item = getItem(i);
                if (item == null) continue;
                if (item.getMultiplier() > leading)
                    leading = item.getMultiplier();
            }
            if (leading == 0) return false;
            return bi.getMultiplier() >= leading;
        }

        public void updateAllCrowns() {
            int leading = 0;
            for (int i = 0; i < getCount(); i++) {
                BingoItem item = getItem(i);
                if (item == null) continue;
                if (item.getMultiplier() > leading)
                    leading = item.getMultiplier();
            }
            if (leading == 0) leading = 10; // this line is for not showing crowns on 0 to 0
            for (int i = 0; i < getCount(); i++) {
                BingoItem item = getItem(i);
                if (item == null) continue;
                View view = getViewAt(i);
                ImageView crown = view.findViewById(R.id.crown);
                crown.setVisibility(item.getMultiplier() >= leading ? View.VISIBLE : View.INVISIBLE);
            }
        }

        public List<BingoItem> getState() {
            List<BingoItem> items = new ArrayList<>();
            for (int i = 0; i < getCount(); i++) {
                BingoItem item = getItem(i);
                if (item == null) continue;
                items.add(item);
            }
            return items;
        }
    }

    public static class BingoItem {

        final String title;
        int multiplier;

        public BingoItem(String title) {
            this.title = title;
        }

        BingoItem(String title, int multiplier) {
            this.title = title;
            this.multiplier = multiplier;
        }

        public void increaseMultiplier() {
            multiplier++;
        }

        public int getMultiplier() {
            return multiplier;
        }

        public String getTitle() {
            return title;
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(View v, int index);
    }
}
