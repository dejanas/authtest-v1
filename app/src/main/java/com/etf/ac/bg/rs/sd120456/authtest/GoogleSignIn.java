package com.etf.ac.bg.rs.sd120456.authtest;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

class GoogleSignIn {

    static Intent registerUserWithGoogleSignUp(Context context){
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.google_request_id_token))
                .requestEmail()
                .build();
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    }

    static GoogleSignInAccount handleGoogleSignInResult(Context context, FirebaseAuth auth, GoogleSignInResult result) {
        
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            return acct;

        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(context, "Registration failed.", Toast.LENGTH_SHORT).show();
            return null;
            
        }
    }
    
}
