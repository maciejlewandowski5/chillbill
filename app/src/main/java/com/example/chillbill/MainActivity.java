package com.example.chillbill;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chillbill.helpers.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ChillBill_MainActivity";
    private static final int RC_SIGN_IN = 1001;

    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth firebaseAuth;
    private View sharedTransition;
    private SignInButton signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.google_signin);
        signInButton.setOnClickListener(v -> signInUsingGoogle());

        configureGoogleClient();
        sharedTransition = findViewById(R.id.imageView5);

    }

    private void configureGoogleClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void signInUsingGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MainActivity that = this;

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Utils.toastMessage("Firebase Authentication failed:" + task.getException(),that);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Utils.toastMessage("Firebase Authentication failed:" + task.getException(),that);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        MainActivity that = this;

        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                assert user != null;
                Log.d(TAG, "signInWithCredential:success: currentUser: " + user.getEmail());
                Utils.toastMessage("Firebase Authentication failed:" + task.getException(),that);
                launchStartActivity(user);
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Utils.toastMessage("Firebase Authentication failed:" + task.getException(),that);
            }
        });

    }

    private void launchStartActivity(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), StartScreen.class);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, Pair.create(sharedTransition, "cart"),
                            Pair.create(signInButton,"button"));
            startActivity(intent, options.toBundle());

        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}