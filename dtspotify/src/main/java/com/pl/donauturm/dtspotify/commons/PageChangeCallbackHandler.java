package com.pl.donauturm.dtspotify.commons;

import androidx.viewpager2.widget.ViewPager2;

public class PageChangeCallbackHandler extends ViewPager2.OnPageChangeCallback {

    public OnPageChangeCallbackInterface anInterface;

    public PageChangeCallbackHandler(OnPageChangeCallbackInterface anInterface) {
        this.anInterface = anInterface;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.anInterface.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        this.anInterface.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        this.anInterface.onPageScrollStateChanged(state);
    }

    public interface OnPageChangeCallbackInterface{

        default void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){

        }

        default void onPageSelected(int position){

        }

        default void onPageScrollStateChanged(int state){

        }
    }

}
