package com.etf.ac.bg.rs.sd120456.authtest;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AnketaInputFragment extends Fragment implements View.OnFocusChangeListener, View.OnClickListener {

    private TextView questionTV;
    private EditText answerET;

    FirebaseDatabase mDatabase;
    private ArrayList<String> pitanja;
    private ArrayList<String> odgovori;

    OnDataPass mDataPasser;

    private static int qNumber = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = Utils.getDatabase();
        pitanja = new ArrayList<>();
        odgovori = new ArrayList<>();
        qNumber = 1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anketa_input, container, false);

        questionTV = (TextView) view.findViewById(R.id.questionTextView);
        answerET = (EditText) view.findViewById(R.id.answerEditText);
        answerET.setFocusableInTouchMode(true);
        answerET.setOnFocusChangeListener(this);
        Button nextBtn = (Button) view.findViewById(R.id.sledeceBtn);
        nextBtn.setOnClickListener(this);

        DatabaseReference pitanjaRef = mDatabase.getReference("anketa").child("pitanja");

        pitanjaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                String questionString;
                Object question;
                for (int i = 0; i < snap.getChildrenCount(); i++) {
                    question = snap.child("pitanje".concat(String.valueOf(i + 1))).getValue();
                    if(question!=null) {
                        questionString = question.toString();
                        pitanja.add(questionString);
                    }
                }
                questionTV.setText(pitanja.get(0));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        int btnId = v.getId();

        switch(btnId){
            case R.id.sledeceBtn:
                //uzmi sta je uneo i zapamti LOKALNO u niz odgovora
                String answer = answerET.getText().toString();
                if(answer.equals("")){
                    answerET.setError("Upišite odgovor!");
                }else{
                    odgovori.add(answer);
                    qNumber++;
                    if(qNumber > 1){
                        //javi activity-ju AnketaActivity da je neki odgovor unet
                        mDataPasser.onDataPass(true);
                    }

                if(qNumber > pitanja.size()){
                    //prosao kroz sva pitanja
                    FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                    AnketaRadioFragment anketaRadioFragment = new AnketaRadioFragment();
                    Bundle args = new Bundle();
                    args.putStringArrayList("inputOdgovori", odgovori);
                    anketaRadioFragment.setArguments(args);
                    ft .replace(R.id.fragmentContainerAnketa, anketaRadioFragment).commit();

                }else{
                    questionTV.setText(pitanja.get(qNumber-1));
                    answerET.setText("");
                }
            }
            showHideKeyboard(false);
        }
    }

    public void showHideKeyboard(boolean show) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if(!show)
            inputMethodManager.hideSoftInputFromWindow(answerET.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        else
            inputMethodManager.hideSoftInputFromWindow(answerET.getWindowToken(),
                    InputMethodManager.SHOW_FORCED);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(answerET.requestFocus()){
            showHideKeyboard(true);
            answerET.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.border_selected, null));
        }
    }

    interface OnDataPass {
        void onDataPass(boolean hasAnswer);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDataPass){
            mDataPasser=(OnDataPass) context;
        }
    }
}
