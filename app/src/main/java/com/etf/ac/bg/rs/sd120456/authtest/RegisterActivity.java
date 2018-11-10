package com.etf.ac.bg.rs.sd120456.authtest;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RegisterActivity extends AppCompatActivity {

    public boolean mRegistered;
    public boolean mVerified;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);

        AuthTestApplicationV1 mApplication = (AuthTestApplicationV1)getApplicationContext();
        mAuth = mApplication.getFirebaseAuth();

        mRegistered = false;
        mVerified = false;

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

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        final RegisterFragment regFragment = RegisterFragment.newInstance(tabLayout);

        getFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, regFragment).commit();


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Fragment selected = null;
                int position = tab.getPosition();
                TabLayout.Tab regTab = tabLayout.getTabAt(0);

                if(position == 0) {
                    selected = regFragment;
                    getFragmentManager()
                            .beginTransaction().replace(R.id.fragmentContainer, selected).commit();
                }
                if(position == 1){
                    if(mRegistered) //moze da predje na verifikaciju samo ako se registrovao
                    {
                        selected = new VerificationFragment();
                        getFragmentManager()
                                .beginTransaction().replace(R.id.fragmentContainer, selected).commit();
                    }else
                        if(regTab!= null)
                            regTab.select();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


    }

    public void setRegistered(boolean registered){
        mRegistered = registered;
    }

    public void setVerified(boolean verified){
        mVerified = verified;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseUser user = mAuth.getCurrentUser();
        if(!mVerified){
            if(user!=null){
                user.delete();
            }
        }
        Intent testIntent = new Intent(this, TestActivity.class);
        startActivity(testIntent);
    }
}


