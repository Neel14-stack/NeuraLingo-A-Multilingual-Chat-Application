package com.invincible.neuralingo.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.invincible.neuralingo.Caching.CacheObject;
import com.invincible.neuralingo.IntegrationActivity;
import com.invincible.neuralingo.MainActivityBottomTab;
import com.invincible.neuralingo.Model.ProfileModel;
import com.invincible.neuralingo.R;
import com.invincible.neuralingo.databinding.ActivityIntegerationBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment implements MainActivityBottomTab.FloatingActionInterface {

    ActivityIntegerationBinding binding;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    ProfileModel profileModel;
    String uid;
    Context context;
    final int PICK_PHOTO = 77;
    String imageStr;
    boolean imageChanged = false;
    int[][] states = new int[][]{
            new int[]{android.R.attr.state_enabled}, // enabled
    };
    static int[] colorPeach;
    SharedPreferences sharedPref;

    public ProfileFragment()
    {

    }

    public ProfileFragment(Context context)
    {
        this.context = context;
    }
    public static ProfileFragment newInstance(Context context) {
        ProfileFragment fragment = new ProfileFragment(context);
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        fragment.auth = FirebaseAuth.getInstance();
        fragment.uid = fragment.auth.getUid();
        colorPeach = new int[]{
                ContextCompat.getColor(context, R.color.peach),
        };
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
        binding = ActivityIntegerationBinding.inflate(inflater, container, false);
        binding.toolbar.setVisibility(View.GONE);
        binding.progressCircular.setIndeterminateTintList(new ColorStateList(states, colorPeach));
        disableEdit();
        binding.progressCircular.setAlpha(1);
        binding.next.setIconSize(80);
        binding.next.setBackgroundColor(ContextCompat.getColor(context, R.color.peach));

        profileModel = (ProfileModel) CacheObject.obj.getSavedObject(CacheObject.PROFILE_OBJ, context);

        if(profileModel.getAbout().equals("") && profileModel.getName().equals(""))
        {
            databaseReference.child("Users").child(uid).child("Profile").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    profileModel = new ProfileModel(dataSnapshot.child("name").getValue(String.class), dataSnapshot.child("about").getValue(String.class),
                            dataSnapshot.child("image").getValue(String.class));
                    updateProfileUi(profileModel);
                    CacheObject.obj.saveObject(CacheObject.PROFILE_OBJ, profileModel, context);
                    binding.progressCircular.setAlpha(0);
                    FirebaseDatabase.getInstance(getResources().getString(R.string.database_url)).goOnline();
                }
            });
        }else {
            updateProfileUi(profileModel);
            binding.progressCircular.setAlpha(0);
        }

        binding.getRoot().setOnClickListener(view -> {
            binding.nameLayout.clearFocus();
            binding.about.clearFocus();
        });

        binding.imageEdit.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_PHOTO);

        });

        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.progressCircular.setAlpha(1);
                binding.next.setIcon(null);
                Map<String, String> data = new HashMap<>();
                data.put("name", Objects.requireNonNull(binding.name.getText()).toString());
                data.put("about", Objects.requireNonNull(binding.about.getText()).toString());
                if(imageChanged)
                {
                    data.put("image", imageStr);
                    profileModel.setImage(data.get("image"));
                }

                profileModel.setAbout(data.get("about"));

                profileModel.setName(data.get("name"));

                CacheObject.obj.saveObject(CacheObject.PROFILE_OBJ, profileModel, context);

                databaseReference.child("Users").child(uid).child("Profile").setValue(profileModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.progressCircular.setAlpha(0);
                        disableEdit();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showSnackbar("Error while saving the changes");
                        binding.progressCircular.setAlpha(0);
                        disableEdit();
                    }
                });

            }
        });

        return binding.getRoot();
    }



    void updateProfileUi(ProfileModel profileModel)
    {
        binding.name.setText(profileModel.getName());
        binding.about.setText(profileModel.getAbout());
        profileImageOperation(profileModel.getImage());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == PICK_PHOTO)
        {
            if(data == null)
            {
                return;
            }
            sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            Bitmap profileBitmap;
            InputStream inputStream = null;
            try {
                inputStream = context.getContentResolver().openInputStream(data.getData());
                profileBitmap = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                profileBitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);
                binding.appIcon.setImageBitmap(profileBitmap);
                byte[] imageBytes = outputStream.toByteArray();
                Bitmap smallBitmap = IntegrationActivity.decodeSampledBitmap(imageBytes, 200, 200);
                outputStream = new ByteArrayOutputStream();
                smallBitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
                imageBytes = outputStream.toByteArray();
                imageStr = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                imageChanged = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    void profileImageOperation(String imageStr) {
        try {
            byte[] bytes = Base64.decode(Objects.requireNonNull(imageStr), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.appIcon.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onProfileEditClicked() {
        if(binding.name.getText() != null && !binding.name.getText().toString().trim().equals(""))
        {
            enableEdit();
        }
    }

    @Override
    public void onAddRoomClicked() {

    }


    private boolean checkData()
    {
        boolean flag = true;
        if(binding.name.getText() == null || binding.name.getText().toString().trim().equals(""))
        {
            binding.name.setError("Name can't be Empty");
            flag = false;
        }
        if(binding.about.getText() == null || binding.about.getText().toString().trim().equals(""))
        {
            binding.about.setError("About can't be Empty");
            flag = false;
        }


        return flag;
    }

    private void disableEdit()
    {
        binding.next.setVisibility(View.GONE);
        binding.imageEdit.setVisibility(View.GONE);
        binding.nameLayout.setEnabled(false);
        binding.aboutLayout.setEnabled(false);
    }

    private void enableEdit()
    {
        binding.next.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_baseline_done_white_24));
        binding.imageEdit.setVisibility(View.VISIBLE);
        binding.next.setVisibility(View.VISIBLE);
        binding.nameLayout.setEnabled(true);
        binding.aboutLayout.setEnabled(true);
        binding.nameLayout.setActivated(true);
    }

    private void showSnackbar(String message)
    {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }
}
