package com.pl.donauturm.drinksmenu.controller.drinks;


import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.databinding.PrefItemDrinkBinding;
import com.pl.donauturm.drinksmenu.model.Drink;

import java.util.ArrayList;
import java.util.List;

public class DrinksAdapter extends RecyclerView.Adapter<DrinksAdapter.ViewHolder> {

    private final RecyclerView mRecyclerView;
    private final List<Drink> drinks;
    private final List<OnActionListener> onActionListeners;
    private int expandedPosition = -1;

    public DrinksAdapter(RecyclerView recyclerView, List<Drink> drinks) {
        this.mRecyclerView = recyclerView;
        this.drinks = drinks;
        this.onActionListeners = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PrefItemDrinkBinding binding = PrefItemDrinkBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        new Handler(Looper.getMainLooper()).post(() -> binding.expContent.getRoot().setVisibility(View.GONE));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // expanding and collapsing
        holder.binding.card.setOnClickListener(v -> {
            if (holder.binding.expContent.getRoot().getVisibility() == View.VISIBLE) {
                expandedPosition = -1;
                holder.binding.expContent.getRoot().setVisibility(View.GONE);
            } else {
                int oldPosition = expandedPosition;
                expandedPosition = holder.getAdapterPosition();
                if (oldPosition != -1) {
                    RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(oldPosition);
                    if (viewHolder instanceof ViewHolder) {
                        ((ViewHolder) viewHolder).binding.expContent.getRoot().setVisibility(View.GONE);
                    }
                }
                holder.binding.expContent.getRoot().setVisibility(View.VISIBLE);
            }
            notifyItemChanged(holder.getAdapterPosition());
        });


        // setting data
        holder.binding.expHeader.drinkName.setText(drinks.get(position).getName());
        holder.binding.expHeader.drinkPrice.setText(String.valueOf(drinks.get(position).getPriceFormatted()));
        holder.binding.expHeader.drinkDescription.setText(drinks.get(position).getDescription());
        TypedValue typedValue = new TypedValue();
        holder.binding.getRoot().getContext().getTheme().resolveAttribute(android.R.attr.colorControlNormal, typedValue, true);
        colorizeAction(holder.binding.expContent.actionDelete, R.color.delete);
        colorizeAction(holder.binding.expContent.actionStock, drinks.get(position).crossedOut ? R.color.activated : typedValue.resourceId);
        colorizeAction(holder.binding.expContent.actionHide, drinks.get(position).hidden ? R.color.activated : typedValue.resourceId);

        // top most and bottom most card
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) holder.binding.card.getLayoutParams();
        if (position == 0){
            lp.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, holder.binding.getRoot().getResources().getDisplayMetrics());
        } else {
            lp.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, holder.binding.getRoot().getResources().getDisplayMetrics());
        }
        if (position == DrinkRegistry.getInstance().size() - 1){
            lp.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, holder.binding.getRoot().getResources().getDisplayMetrics());
        } else {
            lp.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, holder.binding.getRoot().getResources().getDisplayMetrics());
        }
        holder.binding.card.setLayoutParams(lp);

        //buttons
        holder.binding.expContent.actionDelete.setOnClickListener(v -> {
            //call to listener
            for (OnActionListener listener : onActionListeners) {
                listener.onDelete(drinks.get(position), position);
            }
        });

        holder.binding.expContent.actionEdit.setOnClickListener(v -> {
            //call to listener
            for (OnActionListener listener : onActionListeners) {
                listener.onEdit(drinks.get(position), position);
            }
        });

        holder.binding.expContent.actionHide.setOnClickListener(v -> {
            //call to listener
            for (OnActionListener listener : onActionListeners) {
                listener.onHide(drinks.get(position), position);
            }
        });

        holder.binding.expContent.actionStock.setOnClickListener(v -> {
            //call to listener
            for (OnActionListener listener : onActionListeners) {
                listener.onStock(drinks.get(position), position);
            }
        });
    }

    private void colorizeAction(ViewGroup view, int color){
        ((ImageView) view.getChildAt(0)).setColorFilter(ResourcesCompat.getColor(view.getResources(), color, null));
        ((TextView) view.getChildAt(1)).setTextColor(ResourcesCompat.getColor(view.getResources(), color, null));
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.getAdapterPosition() != expandedPosition) {
            new Handler(Looper.getMainLooper()).post(() -> holder.binding.expContent.getRoot().setVisibility(View.GONE));
        }
    }

    @Override
    public int getItemCount() {
        return drinks.size();
    }

    public void addOnActionListener(OnActionListener onActionListener){
        onActionListeners.add(onActionListener);
    }

    public void removeItem(int position){
        drinks.remove(position);
        notifyItemRemoved(position);
        if (position == 0){
            notifyItemChanged(0);
        }
        if (position == drinks.size()){
            notifyItemChanged(position - 1);
        }
    }

    public void addItem(Drink drink){
        drinks.add(drink);
        notifyItemInserted(drinks.size() - 1);
        notifyItemChanged(drinks.size() - 2);
    }

    public void updateItem(Drink drink, int position){
        drinks.set(position, drink);
        notifyItemChanged(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final PrefItemDrinkBinding binding;

        public ViewHolder(@NonNull PrefItemDrinkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnActionListener {
        void onDelete(Drink drink, int position);
        void onEdit(Drink drink, int position);
        void onHide(Drink drink, int position);
        void onStock(Drink drink, int position);
    }
}
