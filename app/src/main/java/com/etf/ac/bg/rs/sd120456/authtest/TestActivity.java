package com.etf.ac.bg.rs.sd120456.authtest;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class TestActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        context= this;

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

        Button regBtn = (Button) findViewById(R.id.regBtn);
        Button loginBtn = (Button) findViewById(R.id.loginBtn);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent =  new Intent(context, RegisterActivity.class);
                startActivity(regIntent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent =  new Intent(context, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        Drawable img = ResourcesCompat.getDrawable( getResources(), R.drawable.ic_reg, null );
        if(img!=null) {
            img.setBounds(0, 0, 60, 60);
            regBtn.setCompoundDrawables(img, null, null, null);
        }

        Drawable img1 = ResourcesCompat.getDrawable( getResources(), R.drawable.ic_login, null );
        if(img1!=null) {
            img1.setBounds(0, 0, 60, 60);
            loginBtn.setCompoundDrawables(img1, null, null, null);
        }

    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }
}
