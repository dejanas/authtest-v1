package com.etf.ac.bg.rs.sd120456.authtest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final int RC_SIGN_IN_GOOGLE = 13 ;
    private FirebaseAuth mAuth;
    private Context mContext;
    CallbackManager callbackManager;
    private FirebaseUser mUser;
    private LoginManager loginManager;

    private EditText emailET;
    private EditText passwordET;
    private TextView loginInfoNameTV;
    private TextView loginInfoEmailTV;
    private Button logoutBtn;
    public int FB_GOOGLE_FLAG = 0;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        AuthTestApplicationV1 mApplication = (AuthTestApplicationV1)mContext.getApplicationContext();
        mAuth = mApplication.getFirebaseAuth();
        mUser = mAuth.getCurrentUser();
        loginManager = mApplication.getLoginManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_pass_login, container, false);

        emailET = (EditText)view.findViewById(R.id.log_email_ET);
        passwordET = (EditText)view.findViewById(R.id.log_password_ET);

        loginInfoNameTV = (TextView)view.findViewById(R.id.loginInfoName);
        loginInfoEmailTV = (TextView)view.findViewById(R.id.loginInfoEmail);

        Button loginBtn = (Button) view.findViewById(R.id.login_btn);
        SignInButton googleBtn = (SignInButton) view.findViewById(R.id.googleSignInBtn);
        LoginButton facebookBtn = (LoginButton) view.findViewById(R.id.facebookSignInBtn);
        logoutBtn = (Button)view.findViewById(R.id.logoutBtn);

        loginBtn.setOnClickListener(this);
        googleBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

        facebookBtn.setFragment(this);
        facebookBtn.setReadPermissions("email", "public_profile");

        facebookBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException exception) {
            }
        });

        if(mUser != null){
            loginInfoNameTV.setText("Ulogovani ste sa nalogom: " + mUser.getDisplayName());
            loginInfoEmailTV.setText(" (" + mUser.getEmail() + ")");
            loginInfoNameTV.setVisibility(View.VISIBLE);
            loginInfoNameTV.setVisibility(View.VISIBLE);
        }
        else{
            loginInfoNameTV.setVisibility(View.GONE);
            loginInfoEmailTV.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.login_btn:
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                if(validateInputs(email, password)){
                    loginUserWithEmailAndPassword(email, password);
                }
                break;

            case R.id.googleSignInBtn:
                loginUserWithGoogleSignUp();
                break;

            case R.id.logoutBtn:
                if(mUser!=null){
                    mAuth.signOut();
                    loginManager.logOut();
                    loginInfoNameTV.setText("Izlogovali ste se.");
                    loginInfoEmailTV.setVisibility(View.GONE);
                    logoutBtn.setVisibility(View.GONE);
                }
        }
    }

    private void loginUserWithGoogleSignUp() {
        Intent signInIntent = GoogleSignIn.registerUserWithGoogleSignUp(getActivity());
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount acct = GoogleSignIn.handleGoogleSignInResult(getActivity(),mAuth, result);
            if(acct != null)
                firebaseAuthWithGoogle(acct);
        }

        //Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {;

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            String name;
                            if(user!=null) {
                                name = user.getDisplayName();
                                Toast.makeText(mContext, "Logged in as " + name + " with Google account.", Toast.LENGTH_SHORT).show();
                                FB_GOOGLE_FLAG = 1;
                                moveToVerification(user.getEmail());
                            }

                        } else {
                            String errorMsg = "";
                            if(task.getException()!= null)
                                errorMsg += task.getException().getMessage();
                            else
                                errorMsg = "unknown error.";
                            Toast.makeText(mContext, "Login failed: " + errorMsg, Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    public void moveToVerification(String email){
        emailET.setText(email);
        passwordET.setText("");

        ((LoginActivity)getActivity()).setLogedIn(true);
        if(FB_GOOGLE_FLAG == 1)
            ((LoginActivity)getActivity()).setGoogleFbFlag(true);
                getFragmentManager().beginTransaction().replace(R.id.fragmentContainerLogin,
                        new VerificationFragment()).commit();

    }

    private boolean validateInputs(String email, String password){
        boolean isValid = true;

        if(email.equals("")){
            emailET.setError(getString(R.string.email_insert_warning));
            isValid = false;
        }
        if(password.equals("")){
            passwordET.setError(getString(R.string.insert_password_warning));
            isValid = false;
        }

        return isValid;
    }

    private void loginUserWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null)
                                moveToVerification(user.getEmail());
                            else
                                Toast.makeText(mContext, "Login failed.",
                                        Toast.LENGTH_SHORT).show();
                            FB_GOOGLE_FLAG = 0;
                        } else {
                            String errorMsg = "";
                            if (task.getException() != null)
                                errorMsg += task.getException().getMessage();
                            else
                                errorMsg = "unknown error.";
                            Toast.makeText(mContext, "Login failed: " + errorMsg,
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null) {
                                Toast.makeText(mContext, "Logged in as " + user.getDisplayName()
                                        + " via Facebook.", Toast.LENGTH_SHORT).show();
                                FB_GOOGLE_FLAG = 1;
                                moveToVerification(user.getEmail());
                            }else
                                Toast.makeText(mContext, "FB login failed.", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = "";
                            if(task.getException()!= null)
                                errorMsg += task.getException().getMessage();
                            else
                                errorMsg = "unknown error.";
                            Toast.makeText(mContext, "FB login failed: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}
