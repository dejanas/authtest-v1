package com.etf.ac.bg.rs.sd120456.authtest;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


public class RegisterLastFragment extends Fragment {

    private TextView proceedToLoginTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_last_register, container, false);

        proceedToLoginTV = (TextView)view.findViewById(R.id.proceedToLoginTV);
        proceedToLoginTV.setPaintFlags(proceedToLoginTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        view.findViewById(R.id.successRegisteredTV)
                .startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.custom_anim));
        proceedToLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToLoginTV.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent,null));
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        return view;
    }
}
