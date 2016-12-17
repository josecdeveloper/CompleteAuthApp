package com.android.completeauthapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ui.ResultCodes;
import com.firebase.ui.auth.ui.email.SignInActivity;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private static final String TAG = AuthActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 179;

    private static final String FIREBASE_TOS_URL = "https://www.firebase.com/terms/terms-of-service.html";
    private View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_auth);

        mRootView = findViewById(android.R.id.content);

        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(R.style.DarkTheme)
                        .setLogo(R.mipmap.ic_launcher)
                        .setProviders(getSelectedProviders())
                        .setTosUrl(getSelectedTosUrl())
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .build(),
                RC_SIGN_IN
        );

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(AuthActivity.this, MainActivity.class));

            finish();
        }
    }

    @MainThread
    private List<AuthUI.IdpConfig> getSelectedProviders() {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();

        selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());


        selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER)
                .setPermissions(getFacebookPermissions()).build());


        selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                .setPermissions(getGooglePermissions()).build());


        selectedProviders.add(
                new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build()
        );


        return selectedProviders;
    }

    @MainThread
    private List<String> getFacebookPermissions() {
        List<String> result = new ArrayList<>();
        result.add("user_friends");

        result.add("user_photos");
        return result;
    }

    @MainThread
    private List<String> getGooglePermissions() {
        List<String> result = new ArrayList<>();

        result.add(Scopes.GAMES);

        result.add(Scopes.DRIVE_FILE);

        return result;
    }

    @MainThread
    private String getSelectedTosUrl() {

        return FIREBASE_TOS_URL;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }

        showSnackbar(R.string.unknown_response);
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
//            startActivity(SignedInActivity.createIntent(this, IdpResponse.fromResultIntent(data)));
            startActivity(new Intent(AuthActivity.this, MainActivity.class));
            finish();
            return;
        }

        if (resultCode == RESULT_CANCELED) {
            showSnackbar(R.string.sign_in_cancelled);
            return;
        }

        if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
            showSnackbar(R.string.no_internet_connection);
            return;
        }

        showSnackbar(R.string.unknown_sign_in_response);
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {

        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();

    }

    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, AuthActivity.class);
        return in;
    }
}
