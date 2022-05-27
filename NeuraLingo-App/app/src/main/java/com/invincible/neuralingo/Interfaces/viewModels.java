package com.invincible.neuralingo.Interfaces;

import com.invincible.neuralingo.Model.ProfileModel;
import com.invincible.neuralingo.databinding.ActivityIntegerationBinding;

public abstract class viewModels {

    void profileViewModel(ActivityIntegerationBinding binding, ProfileModel profileModel)
    {
        binding.name.setText(profileModel.getName());
    }
}
