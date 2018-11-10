package com.etf.ac.bg.rs.sd120456.authtest;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;


public class InsertCodeFragment extends Fragment {

    private EditText verCodeET;
    private Button verifikujBtn;
    private TextView nazadTV;
    private TextView failedTV;
    private FirebaseAuth mAuth;
    private Context mContext;
    private int mTryCnt;
    private TextInputLayout verCodeTIL;
    private PhoneAuthCredential phoneCredential;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insert_code, container, false);

        AuthTestApplicationV1 mApplication = (AuthTestApplicationV1) getActivity().getApplicationContext();
        mAuth = mApplication.getFirebaseAuth();
        mContext = getActivity();
        mTryCnt = 0;

        verCodeET = (EditText) view.findViewById(R.id.ver_code_ET);
        verifikujBtn = (Button) view.findViewById(R.id.verifikujBtn);
        nazadTV = (TextView) view.findViewById(R.id.neuspehNazadTV);
        failedTV = (TextView) view.findViewById(R.id.failedTV);
        verCodeTIL = (TextInputLayout) view.findViewById(R.id.ver_code_TIL);

        final String verification_id = getArguments().getString("verification_id");

        nazadTV.setPaintFlags(nazadTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        nazadTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nazadTV.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                startRegisterAgain();
            }

        });

        verifikujBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTryCnt++;
                String insertedCode = verCodeET.getText().toString();
                if(!insertedCode.isEmpty()) {
                    phoneCredential = PhoneAuthProvider.getCredential(verification_id, insertedCode);
                    updatePhoneNumber(phoneCredential);
                }

                if (mTryCnt > 3) {
                    deleteOrLogoutUser();
                    verCodeTIL.setVisibility(View.GONE);
                    verifikujBtn.setVisibility(View.GONE);
                    nazadTV.setVisibility(View.VISIBLE);
                    failedTV.setVisibility(View.VISIBLE);
                } else {
                    mTryCnt++;
                }
            }
        });
        return view;
    }

    private void updatePhoneNumber(PhoneAuthCredential credential) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePhoneNumber(credential).addOnCompleteListener((Activity) mContext, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
//                        signInWithPhoneAuthCredential(phoneCredential);
                        Toast.makeText(mContext, "SMS verifikacija uspešna.", Toast.LENGTH_SHORT).show();
                        if (mContext instanceof LoginActivity) {
                            ((LoginActivity) mContext).setVerified(true);
                            signInWithPhoneAuthCredential(phoneCredential);
                        }
                        if (mContext instanceof RegisterActivity)
                            ((RegisterActivity) mContext).setVerified(true);
                        startLastFragment();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(mContext, "Kod nije validan.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            user.reload();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "SMS verifikacija uspešna.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(mContext, "SMS verifikacija neuspešna.", Toast.LENGTH_SHORT).show();
                                Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void startRegisterAgain() {
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        if (getActivity() instanceof LoginActivity)
            ft.replace(R.id.fragmentContainerLogin, new LoginFragment()).commit();
        if (getActivity() instanceof RegisterActivity)
            ft.replace(R.id.fragmentContainer, new RegisterFragment()).commit();
    }

    private void startLastFragment() {
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        if (mContext instanceof LoginActivity) {
            ((LoginActivity) mContext).setVerified(true);
            ft.replace(R.id.fragmentContainerLogin, new LogInLastFragment()).commit();
        }
        if (mContext instanceof RegisterActivity) {
            ((RegisterActivity) mContext).setVerified(true);
            ft.replace(R.id.fragmentContainer, new RegisterLastFragment()).commit();
        }
    }

    private void deleteOrLogoutUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
//                                Toast.makeText(getActivity(), "Obrisan!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}
