package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.model.content.Drink;
import com.pl.donauturm.drinksmenu.model.content.DrinkGroup;
import com.pl.donauturm.drinksmenu.model.interfaces.Group;
import com.pl.donauturm.drinksmenu.model.Item;
import com.pl.donauturm.drinksmenu.view.preferences.drink.DrinkDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class ItemsEditorFragment extends Fragment {

    private Group<? extends Item> item;
    private OnItemsChanged callback;
    private RecyclerView mRecyclerView;
    private ItemsAdapter mAdapter;
    private ItemDragHelper mDragHelper;
    private ItemTouchHelper mTouchHelper;

    public ItemsEditorFragment() {
        // Required empty public constructor
    }

    public ItemsEditorFragment(Group<? extends Item> item, OnItemsChanged callback) {
        this.item = item;
        this.callback = callback;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DrinksMenuFragment.
     */
    public static ItemsEditorFragment newInstance(DrinkGroup drinkGroup, OnItemsChanged callback) {
        return new ItemsEditorFragment(drinkGroup, callback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.items_holder);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mAdapter = new ItemsAdapter(requireContext(), itemListToDrinkList(item.getItems()));
        mAdapter.setDeleteListener(this::onItemDelete);
        mAdapter.setDragListener(this::onItemDrag);
        mAdapter.setAddListener(this::onItemAdd);
        mRecyclerView.setAdapter(mAdapter);
        mDragHelper = new ItemDragHelper();
        mDragHelper.setClickListener(this::onItemMove);
        mTouchHelper = new ItemTouchHelper(mDragHelper);
        mTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public void onItemDrag(int position, RecyclerView.ViewHolder viewHolder) {
        mTouchHelper.startDrag(viewHolder);
    }

    public void onItemDelete(int position, RecyclerView.ViewHolder viewHolder) {
        mAdapter.mData.remove(position);
        mAdapter.notifyItemRemoved(position);
        callback.onItemDelete(position);
        callback.onItemsChanged(mAdapter.mData);
    }

    public void onItemAdd(int position, RecyclerView.ViewHolder viewHolder) {
        DrinkDialog fragment = DrinkDialog.newInstance(null);
        fragment.setOnDrinkSelectedListener(drink -> onItemAdded(position, drink));
        fragment.show(((AppCompatActivity) requireContext()).getSupportFragmentManager(), "drink_" + item.hashCode());
    }

    public void onItemAdded(int position, Drink drink) {
        mAdapter.mData.add(drink);
        mAdapter.notifyItemInserted(position);
        callback.onItemAdded(position, drink);
        callback.onItemsChanged(mAdapter.mData);
    }

    public void onItemMove(int from, int to) {
        if (to == mAdapter.mData.size()) return;
        Collections.swap(mAdapter.mData, from, to);
        mAdapter.notifyItemMoved(from, to);
        callback.onItemMoved(from, to);
        callback.onItemsChanged(mAdapter.mData);
    }

    private static List<Drink> itemListToDrinkList(List<? extends Item> itemList) {
        List<Drink> drinkList = new ArrayList<>();
        for (Item item : itemList) {
            if (item instanceof Drink)
                drinkList.add((Drink) item);
        }
        return drinkList;
    }

    public static class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public static final int ITEM_VIEW_TYPE = 0;
        public static final int ADDI_VIEW_TYPE = 1;

        private final List<Drink> mData;
        private final LayoutInflater mInflater;
        private ItemClickListener mDeleteListener;
        private ItemClickListener mDragListener;
        private ItemClickListener mAddItemListener;

        // data is passed into the constructor
        ItemsAdapter(Context context, List<Drink> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = new ArrayList<>(data);
        }

        // inflates the row layout from xml when needed
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ADDI_VIEW_TYPE) {
                View view = mInflater.inflate(R.layout.pref_list_add_button, parent, false);
                return new AddViewHolder(view);
            }
            View view = mInflater.inflate(R.layout.pref_itemgroup_item, parent, false);
            return new ViewHolder(view);

        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ViewHolder) {
                Drink drink = mData.get(position);
                ((ViewHolder) holder).mNameView.setText(drink.getName());
                ((ViewHolder) holder).mDescriptionView.setText(drink.getDescription());
            }
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == mData.size())
                return ADDI_VIEW_TYPE;
            return ITEM_VIEW_TYPE;
        }

        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mNameView;
            private final TextView mDescriptionView;
            private final ImageView mDragView;
            private final ImageView mDeleteView;

            @SuppressLint("ClickableViewAccessibility")
            ViewHolder(View itemView) {
                super(itemView);
                mNameView = itemView.findViewById(R.id.itemgroup_item_name);
                mDescriptionView = itemView.findViewById(R.id.itemgroup_item_description);
                mDragView = itemView.findViewById(R.id.itemgroup_item_handle);
                mDragView.setOnTouchListener(this::onDragHandle);
                mDeleteView = itemView.findViewById(R.id.itemgroup_item_delete);
                mDeleteView.setOnClickListener(this::onDelete);
            }

            public boolean onDragHandle(View view, MotionEvent event) {
                if (view != mDragView) return true;
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if (mDragListener != null)
                        mDragListener.onItemClick(getAdapterPosition(), this);
                    return true;
                }
                return true;
            }

            public void onDelete(View view) {
                if (mDeleteListener != null)
                    mDeleteListener.onItemClick(getAdapterPosition(), this);
            }
        }

        // stores and recycles views as they are scrolled off screen
        public class AddViewHolder extends RecyclerView.ViewHolder {

            AddViewHolder(View itemView) {
                super(itemView);
                ImageView add = itemView.findViewById(R.id.list_add_item_button);
                add.setOnClickListener(this::onClick);
            }

            public void onClick(View fab) {
                if (mAddItemListener != null)
                    mAddItemListener.onItemClick(mData.size(), this);
            }
        }

        // convenience method for getting data at click position
        @SuppressWarnings("unused")
        Drink getItem(int id) {
            return mData.get(id);
        }

        // allows clicks events to be caught
        void setDeleteListener(ItemClickListener itemClickListener) {
            this.mDeleteListener = itemClickListener;
        }

        // allows clicks events to be caught
        void setDragListener(ItemClickListener itemClickListener) {
            this.mDragListener = itemClickListener;
        }

        // allows items to be added
        void setAddListener(ItemClickListener itemClickListener) {
            this.mAddItemListener = itemClickListener;
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(int position, RecyclerView.ViewHolder viewHolder);
    }

    public static class ItemDragHelper extends ItemTouchHelper.SimpleCallback {

        private ItemMovedListener mMovedListener;

        public ItemDragHelper() {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        }

        @Override
        public boolean canDropOver(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder current, @NonNull RecyclerView.ViewHolder target) {
            if (recyclerView.getAdapter() == null) return false;
            return target.getAdapterPosition() < recyclerView.getAdapter().getItemCount() - 1;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            if (mMovedListener != null)
                mMovedListener.onItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState == ACTION_STATE_DRAG) {
                if (viewHolder != null) {
                    viewHolder.itemView.setAlpha(0.5f);
                }
            }
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setAlpha(1.0f);
        }


        // allows clicks events to be caught
        void setClickListener(ItemMovedListener itemMoveListener) {
            this.mMovedListener = itemMoveListener;
        }
    }

    public interface ItemMovedListener {
        void onItemMoved(int from, int to);
    }

    public interface OnItemsChanged {
        void onItemAdded(int index, Item item);

        void onItemMoved(int from, int to);

        void onItemDelete(int index);

        void onItemsChanged(List<? extends Item> items);
    }
}
