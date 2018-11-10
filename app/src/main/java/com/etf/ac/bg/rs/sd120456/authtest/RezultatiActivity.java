package com.etf.ac.bg.rs.sd120456.authtest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RezultatiActivity extends AppCompatActivity {

    public static int NUMBER_OF_INPUT_QUESTIONS = 3;
    public static int NUMBER_OF_RADIO_QUESTIONS = 2;

    private Context mContext;
    private FirebaseDatabase mDatabase;

    ExpandableListView expandableLV;
    private ArrayList<String> questionsList;
    private ArrayList<String> radioQuestionsList;
    private HashMap<String, List<String>> questionsAnswersMap;
    private HashMap<String, List<String>> userAnswerMap;
    private ArrayList<Double> prosecneOceneList;
    private ExpandableListAdapter expandableListAdapter;
    private TextView prosecnaOcena1;
    private TextView prosecnaOcena2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rezultati);

        mContext = this;
        mDatabase = Utils.getDatabase();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        expandableLV = (ExpandableListView) findViewById(R.id.expandableLV);
        prosecnaOcena1 = (TextView) findViewById(R.id.ocena1TV);
        prosecnaOcena2 = (TextView) findViewById(R.id.ocena2TV);

        setExpandableView();
        populateExpandableList();
        populateAvgMarks();

    }

    private void setExpandableView() {
        questionsList = new ArrayList<>();
        questionsAnswersMap = new HashMap<>();
        radioQuestionsList = new ArrayList<>();
        userAnswerMap = new HashMap<>();
        prosecneOceneList = new ArrayList<>();
        expandableListAdapter = new ExpandableListAdapter(mContext, questionsList, questionsAnswersMap, expandableLV);
        expandableLV.setAdapter(expandableListAdapter);
    }

    public void populateExpandableList() {

        DatabaseReference pitanjaRef = mDatabase.getReference("anketa").child("pitanja");
        DatabaseReference odgovoriRef = mDatabase.getReference("anketa").child("odgovori");


        pitanjaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {

                String questionString;
                for (int i = 0; i < snap.getChildrenCount(); i++) {
                    Object question = snap.child("pitanje".concat(String.valueOf(i + 1))).getValue();
                    if(question!=null) {
                        questionString = question.toString();
                        questionsList.add(questionString);
                    }
                }
                expandableListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
        odgovoriRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                int j = 1;
                for (DataSnapshot child : snap.getChildren()) {
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < NUMBER_OF_INPUT_QUESTIONS; i++) {
                        Object answer = child.child("odgovor".concat(String.valueOf(i + 1))).getValue();
                        if(answer!=null)
                            list.add(answer.toString());
                    }
                    userAnswerMap.put("Korisnik" + j, list);
                    j++;
                }

                for (int i = 0; i < questionsList.size(); i++) {
                    List<String> odgovori = new ArrayList<>();
                    for (String key : userAnswerMap.keySet()) {
                        List<String> odgovoriKorisnika = userAnswerMap.get(key);
                        odgovori.add(key + ": " + odgovoriKorisnika.get(i));
                    }
                    questionsAnswersMap.put(questionsList.get(i), odgovori);
                }

                expandableListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    private void populateAvgMarks(){
        DatabaseReference pitanjaRadioRef = mDatabase.getReference("anketa").child("pitanjaRadio");
        DatabaseReference odgovoriRadioRef = mDatabase.getReference("anketa").child("odgovoriRadio");

        pitanjaRadioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                String questionString;
                for (int i = 0; i < snap.getChildrenCount(); i++) {
                    Object question = snap.child("pitanje".concat(String.valueOf(i + 1))).getValue();
                    if(question != null) {
                        questionString = question.toString();
                        radioQuestionsList.add(questionString);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

        for (int i = 0; i < NUMBER_OF_RADIO_QUESTIONS; i++) {
            DatabaseReference odgovoriRadio = odgovoriRadioRef.child("odgovori".concat(String.valueOf(i + 1)));
            odgovoriRadio.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snap) {
                    Object ocena;
                    String ocenaString;
                    double suma = 0;
                    int brOcena = 0;
                    for (DataSnapshot children : snap.getChildren()) {
                        ocena = children.getValue();
                        if(ocena!=null) {
                            ocenaString = ocena.toString();
                            suma += Integer.valueOf(ocenaString);
                            brOcena++;
                        }
                    }
                    double prosecna;
                    if(brOcena!=0)
                        prosecna = suma/brOcena;
                    else
                        prosecna = 5;
                    double zaokruzena = (double)Math.round(prosecna * 10d) / 10d;
                    prosecneOceneList.add(zaokruzena);
                    populate();
                }

                void populate(){
                    if(prosecneOceneList.size()>0)
                        prosecnaOcena1.setText(prosecneOceneList.get(0) + "");
                    if(prosecneOceneList.size()>1)
                        prosecnaOcena2.setText(prosecneOceneList.get(1) + "");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });
        }
    }
}