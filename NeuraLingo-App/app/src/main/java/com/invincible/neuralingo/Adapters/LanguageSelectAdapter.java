package com.invincible.neuralingo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.invincible.neuralingo.R;
import com.invincible.neuralingo.databinding.LanguageItemLayoutBinding;

public class LanguageSelectAdapter extends RecyclerView.Adapter<LanguageSelectAdapter.LanguageItemHolder>{

    Context context;
    public static String[] languages = {"English", "German", "Hindi", "French", "Spanish"};
    public static boolean[] selected = {true, false, false, false, false};
    public static int totalSelected = 0;
    LanguageItemLayoutBinding binding;
    public LanguageSelectAdapter(Context context)
    {
        this.context = context;
    }
    @NonNull
    @Override
    public LanguageItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = LanguageItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false);
        return new LanguageItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageItemHolder holder, int position) {
        holder.languageName.setText(languages[position]);
        holder.itemView.setOnClickListener(view ->
        {
            if(position != 0){

                selected[position] = !selected[position];
                if(selected[position])
                {
                    totalSelected += 1;
                }else {
                    totalSelected -= 1;
                }
                this.notifyItemChanged(position);
            }else if(totalSelected > 1)
            {
                Snackbar.make(holder.itemView, "Please Select maximum 2 Lang", Snackbar.LENGTH_SHORT).show();
            }
        });
        if(selected[position])
        {
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.selected_language_background));
        }else{
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.deselected_language_background));
        }
    }

    @Override
    public int getItemCount() {
        return languages.length;
    }

    public static class LanguageItemHolder extends RecyclerView.ViewHolder {
        TextView languageName;
        public LanguageItemHolder(LanguageItemLayoutBinding binding) {
            super(binding.getRoot());
            languageName = binding.languageName;
        }
    }
}
