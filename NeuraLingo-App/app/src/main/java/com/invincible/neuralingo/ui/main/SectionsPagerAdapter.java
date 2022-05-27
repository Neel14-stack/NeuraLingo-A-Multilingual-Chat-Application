package com.invincible.neuralingo.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.invincible.neuralingo.Fragments.ChatRoomFragment;
import com.invincible.neuralingo.Fragments.ProfileFragment;
import com.invincible.neuralingo.Fragments.TranslateFragment;
import com.invincible.neuralingo.Interfaces.FragmentReturn;
import com.invincible.neuralingo.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStateAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    ProfileFragment profileFragment;
    TranslateFragment translateFragment;
    ChatRoomFragment chatRoomFragment;

    public SectionsPagerAdapter(FragmentActivity fragmentActivity, FragmentReturn fragmentReturn) {
        super(fragmentActivity);
        profileFragment = ProfileFragment.newInstance(fragmentActivity);
        translateFragment = TranslateFragment.newInstance(fragmentActivity);
        chatRoomFragment = ChatRoomFragment.newInstance(fragmentActivity);
        fragmentReturn.getFragment(profileFragment, chatRoomFragment);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 2)
        {
            return profileFragment;
        }
        if(position == 1)
        {
            return chatRoomFragment;
        }
        if(position == 0)
        {
            return translateFragment;
        }
        return PlaceholderFragment.newInstance(position + 1);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}