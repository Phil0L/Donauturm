package com.pl.donauturm.drinksmenu.controller.drinks;

import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.pl.donauturm.drinksmenu.databinding.PrefItemDrinkBinding;

public class DrinksAdapter extends RecyclerView.Adapter<DrinksAdapter.ViewHolder> {

    private final RecyclerView mRecyclerView;
    private int expandedPosition = -1;

    public DrinksAdapter(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
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
        holder.binding.card.setOnClickListener(v -> {
            if (holder.binding.expContent.getRoot().getVisibility() == View.VISIBLE) {
                expandedPosition = -1;
                TransitionManager.beginDelayedTransition(holder.binding.card, new AutoTransition());
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
                TransitionManager.beginDelayedTransition(holder.binding.card, new AutoTransition());
                holder.binding.expContent.getRoot().setVisibility(View.VISIBLE);
            }
        });

        holder.binding.expHeader.drinkName.setText(DrinkRegistry.DRINKS.get(position).getName());
        holder.binding.expHeader.drinkPrice.setText(String.valueOf(DrinkRegistry.DRINKS.get(position).getPriceFormatted()));
        holder.binding.expHeader.drinkDescription.setText(DrinkRegistry.DRINKS.get(position).getDescription());

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) holder.binding.card.getLayoutParams();
        if (position == 0){
            lp.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, holder.binding.getRoot().getResources().getDisplayMetrics());
        } else {
            lp.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, holder.binding.getRoot().getResources().getDisplayMetrics());
        }
        if (position == DrinkRegistry.DRINKS.size() - 1){
            lp.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, holder.binding.getRoot().getResources().getDisplayMetrics());
        } else {
            lp.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, holder.binding.getRoot().getResources().getDisplayMetrics());
        }
        holder.binding.card.setLayoutParams(lp);
    }

    @Override
    public int getItemCount() {
        return DrinkRegistry.DRINKS.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final PrefItemDrinkBinding binding;

        public ViewHolder(@NonNull PrefItemDrinkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}