package com.etf.ac.bg.rs.sd120456.authtest;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class VerificationFragment extends Fragment {

    private EditText phoneET;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private Context mContext;
    boolean isLinked;
    private PhoneAuthCredential phoneCredential;
    private int mTryCnt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verification, container, false);

        phoneET = (EditText) view.findViewById(R.id.ver_phone_ET);
        Button potvrdiBtn = (Button) view.findViewById(R.id.potvrdiBtn);

        isLinked = false;
        mTryCnt = 0;

        mContext = getActivity();
        AuthTestApplicationV1 mApplication = (AuthTestApplicationV1) getActivity().getApplicationContext();
        mAuth = mApplication.getFirebaseAuth();

        final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
//                Toast.makeText(mContext, "onVerificationCompleted.", Toast.LENGTH_SHORT).show();
                phoneCredential = credential;
                updatePhoneNumber(phoneCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
//                Toast.makeText(mContext, "onVerificationFailed.",Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                deleteOrLogoutUser();
                startRegistrationLoginAgain();
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(mContext, "SMS kod poslat.", Toast.LENGTH_SHORT).show();
                startInsertCodeFragment(mVerificationId);
            }
        };

        potvrdiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = phoneET.getText().toString();
                if (mTryCnt > 3) {
                    startLastFragment();
                } else {
                    mTryCnt++;
                    if (phoneNo.equals("")) {
                        phoneET.setError(getString(R.string.insert_phone_warning));
                    } else {
                        if (!isPhoneNumberValid(phoneNo))
                            phoneET.setError(getString(R.string.phone_insert_not_valid_warning));
                        else {
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    phoneNo,
                                    60,
                                    TimeUnit.SECONDS,
                                    getActivity(),
                                    mCallbacks,
                                    mResendToken);
                        }
                    }
                }
            }
        });
        return view;
    }

    public boolean isPhoneNumberValid(String phoneNo) {
        return !phoneNo.isEmpty() && !(phoneNo.length() < 9
                    || phoneNo.length() > 13) && !(phoneNo.length() > 10
                        && (phoneNo.length() != 13 || phoneNo.length() != 12))
                            && android.util.Patterns.PHONE.matcher(phoneNo).matches();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Ulogovan prilikom automatske verifikacije.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "Greška prilikom automatske verifikacije.",
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(mContext, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
//                            deleteOrLogoutUser();
//                            startRegistrationLoginAgain();
                        }
                    }
                });
    }

    private void updatePhoneNumber(PhoneAuthCredential credential){
        FirebaseUser user =  mAuth.getCurrentUser();
        if(user!=null) {
            user.updatePhoneNumber(credential)
                    .addOnCompleteListener((Activity) mContext, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(mContext, "Automatska SMS verifikacija uspešna.",
                                Toast.LENGTH_LONG).show();
                        if (mContext instanceof LoginActivity){
                            signInWithPhoneAuthCredential(phoneCredential);
                            ((LoginActivity) mContext).setVerified(true);
                        }
                        if (mContext instanceof RegisterActivity)
                            ((RegisterActivity) mContext).setVerified(true);
                        startLastFragment();
                    } else {
                        Toast.makeText(mContext, task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        deleteOrLogoutUser();
                        startRegistrationLoginAgain();
                        }
                    }

            });
            user.reload();
        }
    }

    private void linkAccountWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null)
            user.linkWithCredential(credential)
                    .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(mContext, "Linkovanje uspesno.", Toast.LENGTH_LONG).show();
                                signInWithPhoneAuthCredential(phoneCredential);
                            } else {
                                Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                deleteOrLogoutUser();
                            }
                        }
                    });
    }

    private void startInsertCodeFragment(String verificationId) {
        FragmentTransaction ft = ((Activity) mContext).getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        Fragment insertCodeFragment = new InsertCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("verification_id", verificationId);
        insertCodeFragment.setArguments(bundle);
        if (mContext instanceof LoginActivity) {
            ft.replace(R.id.fragmentContainerLogin, insertCodeFragment).commit();
        }
        if (mContext instanceof RegisterActivity) {
            ft.replace(R.id.fragmentContainer, insertCodeFragment).commit();
        }
    }

    private void startLastFragment() {
        FragmentTransaction ft = ((Activity) mContext).getFragmentManager().beginTransaction();
        if (mContext instanceof LoginActivity)
            ft.replace(R.id.fragmentContainerLogin, new LogInLastFragment()).commit();
        if (mContext instanceof RegisterActivity)
            ft.replace(R.id.fragmentContainer, new RegisterLastFragment()).commit();
    }

    private void deleteOrLogoutUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
//                                Toast.makeText(mContext, "Obrisan!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void startRegistrationLoginAgain(){
        FragmentTransaction ft = ((Activity) mContext).getFragmentManager().beginTransaction();
        if (mContext instanceof LoginActivity)
            ft.replace(R.id.fragmentContainerLogin, new LoginFragment()).commit();
        if (mContext instanceof RegisterActivity)
            ft.replace(R.id.fragmentContainer, new RegisterFragment()).commit();

    }

}
