package com.example.chillbill;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chillbill.helpers.AccountHelper;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ChillBill_MainActivity";
    private static final int RC_SIGN_IN = 1001;

    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth firebaseAuth;
    private View sharedTransition;
    private SignInButton signInButton;
    private AccountHelper accountHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accountHelper = new AccountHelper(this);
        accountHelper.setSignInSuccessful(new AccountHelper.SignInSuccessful() {
            @Override
            public void signInSuccessful(FirebaseUser user) {
                launchStartActivity(user);
            }
        });

        signInButton = findViewById(R.id.google_signin);
        signInButton.setOnClickListener(v -> accountHelper.signInUsingGoogle());

        accountHelper.configureGoogleClient();
        sharedTransition = findViewById(R.id.imageView5);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MainActivity that = this;
        if (requestCode == accountHelper.getRCSGININCode()) {
            accountHelper.verifySignInResults(TAG,data);
        }
    }


    private void launchStartActivity(FirebaseUser user) {
            Intent intent = new Intent(getApplicationContext(), StartScreen.class);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, Pair.create(sharedTransition, "cart"),
                            Pair.create(signInButton,"button"));
            startActivity(intent, options.toBundle());
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}