package com.invincible.neuralingo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.res.Resources;
import android.os.Bundle;

import com.invincible.neuralingo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportPostponeEnterTransition();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.real_matte_black));
        supportStartPostponedEnterTransition();
        binding.titleImage.setImageResource(R.drawable.google_g_logo);
        super.setContentView(binding.getRoot());
    }
}