package com.etf.ac.bg.rs.sd120456.authtest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private Button testBtn;
    private Button anketaBtn;
    private Button rezultatiBtn;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        testBtn = (Button)findViewById(R.id.testBtn);
        anketaBtn = (Button)findViewById(R.id.anketaBtn);
        rezultatiBtn = (Button)findViewById(R.id.rezultatiBtn);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent testIntent =  new Intent(context, TestActivity.class);
                startActivity(testIntent);
            }
        });

        anketaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent anketaIntent =  new Intent(context, AnketaActivity.class);
                startActivity(anketaIntent);
            }
        });

        rezultatiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rezultatiIntent =  new Intent(context, RezultatiActivity.class);
                startActivity(rezultatiIntent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Drawable dr = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_key,null);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 75, 75, true));
        toolbar.setNavigationIcon(d);

        Drawable img1 = ResourcesCompat.getDrawable( getResources(), R.drawable.ic_bulb, null );
        img1.setBounds( 0, 0, 100, 100 );
        testBtn.setCompoundDrawables( img1 , null, null, null );

        Drawable img2 = ResourcesCompat.getDrawable( getResources(), R.drawable.ic_question, null );
        img2.setBounds( 0, 0, 90, 90 );
        anketaBtn.setCompoundDrawables( img2 , null, null, null );

        Drawable img3 = ResourcesCompat.getDrawable( getResources(), R.drawable.ic_info_my, null );
        img3.setBounds( 0, 0, 90, 90 );
        rezultatiBtn.setCompoundDrawables( img3 , null, null, null );
    }
}
