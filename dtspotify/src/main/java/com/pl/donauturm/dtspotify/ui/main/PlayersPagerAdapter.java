package com.pl.donauturm.dtspotify.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.pl.donauturm.dtspotify.R;
import com.pl.donauturm.spotify.SpotifyAuthHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class PlayersPagerAdapter extends FragmentStateAdapter {

    @StringRes
    private static final Integer[] TAB_TITLES = new Integer[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;
    private final SpotifyAuthHelper.AuthData[] authData;
    private final PlayerFragment[] fragments = new PlayerFragment[getItemCount()];

    private final List<FragmentCreated> callbacks = new ArrayList<>();


    public PlayersPagerAdapter(@NonNull FragmentActivity fragment, @NonNull Context context, SpotifyAuthHelper.AuthData[] authData) {
        super(fragment);
        this.mContext = context;
        this.authData = authData;
    }


    @Nullable
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @NonNull
    public CharSequence getPageTitleFromRes(@StringRes int resource) {
        return mContext.getResources().getString(resource);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        PlayerFragment playerFragment = PlayerFragment.newInstance(position + 1, authData[position]);
        onFragmentCreated(playerFragment, position);
        return fragments[position] = playerFragment;
    }

    public void onFragmentCreated(Fragment fragment, int position){
        for (FragmentCreated callback : callbacks) {
            callback.onFragmentCreated(fragment, position, fragments);
        }
    }

    @Override
    public int getItemCount() {
        return TAB_TITLES.length;
    }

    public List<PlayerFragment> getFragments(){
        return List.of(fragments);
    }

    public void notifyFragmentCreated(FragmentCreated callback){
        callbacks.add(callback);
        if (fragments.length >= 1)
            for (int i = 0; i < fragments.length; i++) {
                Fragment fragment = fragments[i];
                if (fragment == null) continue;
                callback.onFragmentCreated(fragment, i, fragments);
            }
    }

    public int getIndexOfTab(TabLayout.Tab tab) {
        List<Integer> titles = Arrays.stream(TAB_TITLES).collect(Collectors.toList());
        Integer selectedTabId = titles.stream().filter(title -> getPageTitleFromRes(title).equals(tab.getText())).findFirst().orElse(-1);
        return titles.indexOf(selectedTabId);
    }

    public interface FragmentCreated{
        void onFragmentCreated(Fragment newFragment, int position, Fragment[] activeFragments);
    }

}