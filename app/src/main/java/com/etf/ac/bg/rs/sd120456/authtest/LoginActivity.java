package com.etf.ac.bg.rs.sd120456.authtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class LoginActivity extends AppCompatActivity {

    private boolean logedIn;
    private boolean mVerified;
    private FirebaseAuth mAuth;
    private LoginManager mLoginManager;
    private boolean mGoogleFbFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logedIn = false;
        mVerified = false;

        AuthTestApplicationV1 mApplication = (AuthTestApplicationV1)getApplicationContext();
        mAuth = mApplication.getFirebaseAuth();
        mLoginManager = mApplication.getLoginManager();

        getFragmentManager().beginTransaction()
                .add(R.id.fragmentContainerLogin, new LoginFragment()).commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setLogedIn(boolean logedIn){
        this.logedIn = logedIn;
    }
    public void setVerified(boolean verified){ mVerified = verified; }

    @Override
    public void onBackPressed() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (!mVerified && (currentUser!=null))
            for (UserInfo user : currentUser.getProviderData()) {
                if (user.getProviderId().equals("facebook.com") || user.getProviderId().equals("google.com") ) {
                    currentUser.delete();
                    mLoginManager.logOut();
                }
            }
            mAuth.signOut();

        Intent testIntent = new Intent(this, TestActivity.class);
        startActivity(testIntent);
    }

    public boolean getGoogleFbFlag() {
        return mGoogleFbFlag;
    }

    public void setGoogleFbFlag(boolean mGoogleFbFlag) {
        this.mGoogleFbFlag = mGoogleFbFlag;
    }


}
