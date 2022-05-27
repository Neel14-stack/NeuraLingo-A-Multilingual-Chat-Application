package com.invincible.neuralingo;

import android.content.Intent;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.invincible.neuralingo.Api.ApiMainClass;
import com.invincible.neuralingo.Caching.CacheObject;
import com.invincible.neuralingo.Interfaces.ApiCallbacks;
import com.invincible.neuralingo.databinding.ActivitySigninBinding;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    ActivitySigninBinding binding;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    final int RC_SIGN_IN = 55;
    final String LOG_TAG = SignInActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            FirebaseDatabase.getInstance(getResources().getString(R.string.database_url)).setPersistenceEnabled(true);
        }catch (DatabaseException e)
        {
            Log.e(LOG_TAG, e.toString());
        }

        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        super.setContentView(binding.getRoot());
        CacheObject.createCacheObject();
        binding.googleSignInButton.animate().alpha(1).setDuration(1000);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        binding.googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        if(firebaseAuth.getCurrentUser() != null)
        {
            binding.googleSignInButton.setVisibility(View.GONE);
            checkFirebaseUser();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            binding.googleSignInButton.animate().alpha(0);
                            checkFirebaseUser();
                        } else {
                            Log.w(LOG_TAG, "signInWithCredential: failed");
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = result.getResult(ApiException.class);

                assert account != null;
                Log.d(LOG_TAG, result.getResult().getEmail());
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                Toast.makeText(this, "Sign in failed. Try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkFirebaseUser()
    {
        String uid = firebaseAuth.getUid();
        Log.e(LOG_TAG, uid);
        if(uid != null)
        {
            databaseReference.child(uid).child("Profile").child("name").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    ApiMainClass.getApiMainClassObject(new ApiCallbacks() {
                        @Override
                        public void onSuccessfulSetup() {
                            super.onSuccessfulSetup();
                            Log.e(LOG_TAG, "ApiMainClass Object Created");
                            if(dataSnapshot.exists())
                            {
                                startMainActivity();
                            }
                            else {
                                startIntegrationActivity();
                            }
                        }
                    });

                }
            });
        }
    }

    private void startMainActivity()
    {
        Intent intent = new Intent(SignInActivity.this, MainActivityBottomTab.class);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                        SignInActivity.this,
                        new Pair<>(binding.appIcon, Objects.requireNonNull(ViewCompat.getTransitionName(binding.appIcon))));
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
        finish();
    }
    private void startIntegrationActivity()
    {
        Intent intent = new Intent(SignInActivity.this, IntegrationActivity.class);
//        binding.appIcon.animate().translationY(-400).setDuration(1000);
//
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                        SignInActivity.this,
                        new Pair<>(binding.getRoot(), Objects.requireNonNull(ViewCompat.getTransitionName(binding.getRoot()))),
                        new Pair<>(binding.appIcon, Objects.requireNonNull(ViewCompat.getTransitionName(binding.appIcon))));
        startActivity(intent, optionsCompat.toBundle());
        finish();
    }
}
