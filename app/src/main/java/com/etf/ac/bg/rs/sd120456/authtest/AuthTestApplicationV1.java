package com.etf.ac.bg.rs.sd120456.authtest;

import android.app.Application;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AuthTestApplicationV1 extends Application{

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private LoginManager loginManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mAuth = FirebaseAuth.getInstance();
        loginManager = LoginManager.getInstance();
        mUser = mAuth.getCurrentUser();

        if(mUser!=null)
            mAuth.signOut();
        loginManager.logOut();

        
    }

    public FirebaseAuth getFirebaseAuth(){
        return mAuth;
    }

    public LoginManager getLoginManager(){
        return loginManager;
    }

}
