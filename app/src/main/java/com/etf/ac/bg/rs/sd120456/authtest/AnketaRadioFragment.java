package com.etf.ac.bg.rs.sd120456.authtest;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AnketaRadioFragment extends Fragment implements  View.OnClickListener{

    FirebaseDatabase mDatabase;
    private ArrayList<String> pitanjaRadio;
    private ArrayList<String> odgovoriRadio;
    private static int qNumber = 1;
    private RadioGroup radioGroup;


    private ArrayList<String> odgovoriInput;

    private TextView questionTV;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pitanjaRadio = new ArrayList<>();
        odgovoriRadio = new ArrayList<>();
        mDatabase = Utils.getDatabase();
        odgovoriInput = getArguments().getStringArrayList("inputOdgovori");
        qNumber = 1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_anketa_radio, container, false);

        questionTV = (TextView)view.findViewById(R.id.questionTextView);
        Button nextBtn = (Button) view.findViewById(R.id.sledeceBtn);
        nextBtn.setOnClickListener(this);
        radioGroup = (RadioGroup)view.findViewById(R.id.ocenaRadioGroup);

        DatabaseReference pitanjaRadioRef = mDatabase.getReference("anketa").child("pitanjaRadio");

        pitanjaRadioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                String questionString;
                Object question;
                for(int i=0; i < snap.getChildrenCount(); i++){
                    question = snap.child("pitanje".concat(String.valueOf(i+1))).getValue();
                    if(question!=null){
                        questionString = question.toString();
                        pitanjaRadio.add(questionString);
                    }

                }
                questionTV.setText(pitanjaRadio.get(0));
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
                int selectedId = radioGroup.getCheckedRadioButtonId();
                String ocena = "";
                switch (selectedId){
                    case R.id.ocenaRadiobButton1:
                        ocena+="1";
                        break;
                    case R.id.ocenaRadiobButton2:
                        ocena+="2";
                        break;
                    case R.id.ocenaRadiobButton3:
                        ocena+="3";
                        break;
                    case R.id.ocenaRadiobButton4:
                        ocena+="4";
                        break;
                    case R.id.ocenaRadiobButton5:
                        ocena+="5";
                }
                odgovoriRadio.add(ocena);
                qNumber++;
                if(qNumber > pitanjaRadio.size()){
                    //prosao kroz sva pitanja

                    showDialogBox();
                    }else{
                        questionTV.setText(pitanjaRadio.get(qNumber-1));
                    }
                }
        }
    public void showDialogBox(){
        new AlertDialog.Builder(getActivity())
                .setTitle("Kraj ankete")
                .setMessage("Vaši odgovori će biti sačuvani.")
                .setPositiveButton("U redu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //sacuvaj odgovore u bazi
                        submitAnswers();
                        Intent backIntent = new Intent(getActivity(), MainActivity.class);
                        startActivity(backIntent);
                    }
                })
                .setNegativeButton("Odbaci", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent backIntent = new Intent(getActivity(), MainActivity.class);
                        startActivity(backIntent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }


    public void submitAnswers(){
        DatabaseReference odgovoriRef;
        DatabaseReference odgovoriRadioRef;

        odgovoriRef = mDatabase.getReference("anketa").child("odgovori").push();

        for(int i=0; i < odgovoriInput.size(); i++){
            odgovoriRef.child("odgovor"+(i+1)).setValue(odgovoriInput.get(i));
        }

        odgovoriRadioRef = mDatabase.getReference("anketa").child("odgovoriRadio");
        //ubaci radio odgovore u bazu
        for(int i=0; i < odgovoriRadio.size(); i++){
            odgovoriRadioRef.child("odgovori"+(i+1)).push().setValue(odgovoriRadio.get(i));
        }

      }
    }

