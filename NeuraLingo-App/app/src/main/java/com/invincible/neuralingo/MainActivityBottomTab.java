package com.invincible.neuralingo;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invincible.neuralingo.Fragments.ChatRoomFragment;
import com.invincible.neuralingo.Fragments.ProfileFragment;
import com.invincible.neuralingo.Interfaces.FragmentReturn;
import com.invincible.neuralingo.ui.main.SectionsPagerAdapter;
import com.invincible.neuralingo.databinding.ActivityMainBottomTabBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivityBottomTab extends AppCompatActivity {

    private ActivityMainBottomTabBinding binding;
    boolean backButtonPressesOnce = false;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private final String LOG_TAG = MainActivityBottomTab.class.getSimpleName();
    String tabSelected = "";
    String uid = "";
    FloatingActionInterface floatingActionInterface;
    ProfileFragment profileFragment;
    ChatRoomFragment chatRoomFragment;
    public static Map<String, String> language_map = new HashMap<>();
    static {
        language_map.put("English", "eng");
        language_map.put("German", "deu");
        language_map.put("Hindi", "hin");
        language_map.put("Spanish", "spa");
    }

    final String[] tabNames = {"Translate", "Chat", "Profile"};

    public interface FloatingActionInterface {
        void onProfileEditClicked();
        void onAddRoomClicked();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBottomTabBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.real_matte_black));


        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, new FragmentReturn() {
            @Override
            public void getFragment(ProfileFragment fragment, ChatRoomFragment chatRoomFrag) {
                profileFragment = fragment;
                chatRoomFragment = chatRoomFrag;
            }
        });
        ViewPager2 viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);

        new TabLayoutMediator(binding.tabs, viewPager,
                (tab, position) -> tab.setText(tabNames[position])
        ).attach();

        FloatingActionButton fab = binding.fab;

        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                Log.e(LOG_TAG, String.valueOf(tab.getText()));
                tabSelected = String.valueOf(tab.getText());
                if(tabSelected.equals("Translate"))
                {
                    fab.setVisibility(View.GONE);
                }
                if(tabSelected.equals("Chat")){
                    fab.setVisibility(View.VISIBLE);
                }
                if(tabSelected.equals("Profile"))
                {
                    fab.setImageResource(R.drawable.ic_baseline_edit_24);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(tabSelected.equals("Translate"))
                {
                    fab.setVisibility(View.VISIBLE);
                }
                if(String.valueOf(tab.getText()).equals("Profile"))
                {
                    fab.setImageResource(R.drawable.ic_baseline_add_24);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tabSelected.equals("Profile"))
                {
                    floatingActionInterface = profileFragment;
                    floatingActionInterface.onProfileEditClicked();
                }else if(tabSelected.equals("Chat"))
                {
                    floatingActionInterface = chatRoomFragment;
                    floatingActionInterface.onAddRoomClicked();
                }
                else{
                    Snackbar.make(view, "Room will be created using this Button", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        if(uid != null)
        {
            databaseReference = FirebaseDatabase.getInstance(getResources().getString(R.string.database_url)).getReference("Users").child(uid);
            setUpUi();
            profileImageOperation();
            profileListener();
        }
    }

    @Override
    public void onBackPressed() {
        if (backButtonPressesOnce) {
            MainActivityBottomTab.this.finish();
        } else {
            backButtonPressesOnce = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backButtonPressesOnce = false;
                }
            }, 2000);
        }
    }

    private void setUpUi()
    {
        FirebaseDatabase.getInstance(getResources().getString(R.string.database_url)).goOffline();
        databaseReference.child("Profile").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                binding.toolbar.setSubtitle("Hello " + dataSnapshot.child("name").getValue(String.class));
                FirebaseDatabase.getInstance(getResources().getString(R.string.database_url)).goOnline();
            }
        });
    }

    void profileImageOperation() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getString("profileImage", "false").equals("false")) {
            databaseReference.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("profileImage", snapshot.child("image").getValue(String.class));
                        editor.apply();
                        byte[] bytes = Base64.decode(Objects.requireNonNull(snapshot.child("image").getValue(String.class)), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        binding.titleImage.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        } else {
            try {
                byte[] bytes = Base64.decode(sharedPref.getString("profileImage", "false"), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                binding.titleImage.setImageBitmap(bitmap);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }
    }

    void profileListener()
    {
        databaseReference.child("Profile").child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profileImageOperation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
