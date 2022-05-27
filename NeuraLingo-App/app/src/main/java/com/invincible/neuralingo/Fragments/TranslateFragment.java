package com.invincible.neuralingo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.invincible.neuralingo.Api.ApiMainClass;
import com.invincible.neuralingo.ChatActivity;
import com.invincible.neuralingo.Interfaces.ApiCallbacks;
import com.invincible.neuralingo.Model.ApiDataModel;
import com.invincible.neuralingo.R;
import com.invincible.neuralingo.databinding.TranslateFragmentBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TranslateFragment extends Fragment {

    Context context;
    DatabaseReference databaseReference;
    TranslateFragmentBinding binding;
    JSONObject translateObject;
    ArrayList<String> languages = new ArrayList<>();
    String LOG_TAG = TranslateFragment.class.getSimpleName();
    public TranslateFragment()
    {

    }
    public TranslateFragment(Context context)
    {
        this.context = context;
    }

    public static TranslateFragment newInstance(Context context) {
        TranslateFragment fragment = new TranslateFragment(context);
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        fragment.translateObject = new JSONObject();
        fragment.databaseReference = FirebaseDatabase.getInstance(context.getResources().getString(R.string.database_url)).getReference();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = TranslateFragmentBinding.inflate(inflater, container, false);
        binding.englishInputCard.getRoot().setBackgroundResource(R.drawable.translate_card_background);
        binding.translateOutputCard.getRoot().setBackgroundResource(R.drawable.translate_card_background_two);
        languages.add("German");languages.add("Spanish");languages.add("Bengali");languages.add("Hindi");
        languages.add("French");
        SpinnerAdapter adapter = new ArrayAdapter<>(context, R.layout.item_spinner_layout, languages);
        binding.translateDropdown.setAdapter(adapter);
        binding.translateDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateOutputBox(ChatActivity.shortLangMap.get(languages.get(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.englishInputCard.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendApiRequest(getInputText());
            }
        });
        return binding.getRoot();
    }

    private void sendApiRequest(String inputText)
    {
//        Snackbar.make(binding.getRoot(), inputText, Snackbar.LENGTH_SHORT).show();
//        ApiMainClass.obj.sendMessage(null, null);
        binding.englishInputCard.postButton.setImageDrawable(null);
        binding.englishInputCard.postProgressBar.setVisibility(View.VISIBLE);
        translateObject = new JSONObject();
        ApiDataModel apiDataModel = new ApiDataModel(inputText, null, null, null);
        for(String model : ApiMainClass.allModelNames)
        {
            apiDataModel.addModelName(model);
        }
        ApiMainClass.obj.getAllTranslation(apiDataModel, new ApiCallbacks() {
            @Override
            public void onMessageTranslated(JSONObject jsonObject) {
                super.onMessageTranslated(jsonObject);
                translateObject = jsonObject;
                binding.englishInputCard.postButton.setImageResource(R.drawable.ic_baseline_done_white_24);
                binding.englishInputCard.postProgressBar.setVisibility(View.GONE);
                updateOutputBox(ChatActivity.shortLangMap.get(binding.translateDropdown.getSelectedItem().toString()));
                Log.e(LOG_TAG, jsonObject.toString());
                Toast.makeText(context, jsonObject.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String errorMessage) {
                super.onError(errorMessage);
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void updateOutputBox(String key)
    {
        String permanentKey = "eng-";
        try {
            binding.translateOutputCard.outputText.setText(translateObject.getString(permanentKey+key));
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    private String getInputText()
    {
        String output = "";
        if(binding.englishInputCard.inputText.getText()!=null)
        {
            output = binding.englishInputCard.inputText.getText().toString();
        }
        return output;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
