package com.invincible.neuralingo.Interfaces;

import androidx.fragment.app.Fragment;

import com.invincible.neuralingo.Fragments.ChatRoomFragment;
import com.invincible.neuralingo.Fragments.ProfileFragment;

public interface FragmentReturn {

    void getFragment(ProfileFragment fragment, ChatRoomFragment chatRoomFragment);
}
