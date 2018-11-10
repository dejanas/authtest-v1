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


public class LogInLastFragment extends Fragment {

    private TextView proceedToAnketaTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_last_login, container, false);

        proceedToAnketaTV = (TextView)view.findViewById(R.id.proceedToAnketaTV);
        proceedToAnketaTV.setPaintFlags(proceedToAnketaTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        view.findViewById(R.id.successLoginTV)
                .startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.custom_anim));

        proceedToAnketaTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToAnketaTV.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent,null));
                Intent anketaIntent = new Intent(getActivity(), AnketaActivity.class);
                startActivity(anketaIntent);
            }
        });

        return view;
    }
}


