package com.etf.ac.bg.rs.sd120456.authtest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class AnketaActivity extends AppCompatActivity implements AnketaInputFragment.OnDataPass{

    private Context mContext;
    private boolean mHasAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anketa);

        mContext =  AnketaActivity.this;
        mHasAnswer = false;

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
        getFragmentManager().beginTransaction().add(R.id.fragmentContainerAnketa, new AnketaInputFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        if(mHasAnswer)
            showDialogBox();
        else {
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }
    }

    public void showDialogBox(){
        new AlertDialog.Builder(this)
                .setTitle("Napomena")
                .setMessage("Uneti odgovori će biti izbrisani.")
                .setPositiveButton("Ostani", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("U redu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent backIntent = new Intent(mContext, MainActivity.class);
                        startActivity(backIntent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onDataPass(boolean hasAnswer) {
        mHasAnswer = hasAnswer;
    }
}
