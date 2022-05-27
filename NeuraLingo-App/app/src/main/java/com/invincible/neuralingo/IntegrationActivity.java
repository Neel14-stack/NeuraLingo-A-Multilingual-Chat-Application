package com.invincible.neuralingo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.invincible.neuralingo.databinding.ActivityIntegerationBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IntegrationActivity extends AppCompatActivity {
    ActivityIntegerationBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    final int PICK_PHOTO = 66;
    String imageStr = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportPostponeEnterTransition();
        binding = ActivityIntegerationBinding.inflate(getLayoutInflater());
        auth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance(getResources().getString(R.string.database_url)).getReference().child("Users");
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.real_matte_black));
        supportStartPostponedEnterTransition();
        binding.next.setOnClickListener(view ->
        {
            if(checkData())
            {
                binding.next.setIcon(null);
                binding.progressCircular.setAlpha(1);
                Map<String, String> data = new HashMap<>();
                data.put("name", Objects.requireNonNull(binding.name.getText()).toString());
                data.put("about", Objects.requireNonNull(binding.about.getText()).toString());
                data.put("image", imageStr);
                String uid = auth.getUid();
                if(uid != null)
                {
                    reference.child(uid).child("Profile").setValue(data).addOnSuccessListener(unused ->
                    {
                        startMainActivity();
                    })
                    .addOnFailureListener(e -> {
                        binding.next.setIcon(ContextCompat.getDrawable(IntegrationActivity.this, R.drawable.ic_baseline_arrow_forward_ios_24));
                        binding.progressCircular.setAlpha(0);
                        showSnackbar("Error While Saving values");
                    });
                }

            }
        });

        binding.imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO);

            }
        });
        super.setContentView(binding.getRoot());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == PICK_PHOTO)
        {
            if(data == null)
            {
                return;
            }
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            Bitmap profileBitmap;
            InputStream inputStream = null;
            try {
                inputStream = IntegrationActivity.this.getContentResolver().openInputStream(data.getData());
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
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("profileImage", imageStr);
                editor.apply();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        if(imageStr == null)
        {
            showSnackbar("Please select Profile Image");
            flag = false;
        }
        return flag;
    }

    private void startMainActivity()
    {
        Intent intent = new Intent(IntegrationActivity.this, MainActivityBottomTab.class);
        startActivity(intent);
        this.finish();
    }

    private void showSnackbar(String message)
    {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmap(byte[] bmpByteArray,
                                             int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bmpByteArray, 0, bmpByteArray.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bmpByteArray, 0, bmpByteArray.length, options);
    }
}
