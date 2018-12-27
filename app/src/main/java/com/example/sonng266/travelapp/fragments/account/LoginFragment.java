package com.example.sonng266.travelapp.fragments.account;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.fragments.BaseFragment;
import com.example.sonng266.travelapp.models.LocationModel;
import com.example.sonng266.travelapp.models.UserModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */

@SuppressLint("ValidFragment")
public class LoginFragment extends BaseFragment implements View.OnClickListener {

   private EditText email;
   private EditText password;
   private TextView tvlogin;
   private ImageView ivFacebook;
   private ImageView ivGoogle ;
   private TextView tvForgot;
   private TextView tvCreate;



    private FirebaseAuth mAuth;
    private   CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private static boolean firstLogin;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Dialog dialog;
    private EditText mailForget;
    private Context context;
    private View view;

    AVLoadingIndicatorView av;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public String getPageTitle() {
        return "Login";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        setUpUI(view);
        context = view.getContext();
        return view;
    }


    private void setUpUI(View view) {

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("UserModel");

        if (mAuth.getCurrentUser() != null ){
            Log.d(TAG, "setUpUI: " + "oki" + mAuth.getCurrentUser().getDisplayName());
            showNext(new SelectTripFragment(), true);
        }else {
            Log.d(TAG, "setUpUI: " + "no");
        }

        av = view.findViewById(R.id.avi);
        av.hide();
        email = view.findViewById(R.id.edt_username_sign_in);
        password = view.findViewById(R.id.edt_password_sign_in);
        tvlogin = view.findViewById(R.id.tv_login);
//        ivFacebook = view.findViewById(R.id.iv_login_with_facebook);
        ivGoogle = view.findViewById(R.id.iv_login_with_google);
        tvForgot = view.findViewById(R.id.tv_forgot);
        tvCreate = view.findViewById(R.id.tv_create);

        ivGoogle.setOnClickListener(this);
        tvlogin.setOnClickListener(this);
        tvCreate.setOnClickListener(this);
        tvForgot.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(view.getContext(), gso);


        FacebookSdk.sdkInitialize(getApplicationContext());




        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = view.findViewById(R.id.button_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + "cuonghx");
                disableEnableControls(false, (ViewGroup) LoginFragment.this.view);
            }
        });
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });

// ...
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() != null)
        Log.d(TAG, "onResume: cuonghx"+ mAuth.getCurrentUser().isAnonymous());
        if (SignUpFragment.created && SignUpFragment.email != null){
            Log.d(TAG, "onResume: " +"setUpUI");
            email.setText(SignUpFragment.email);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Log.d(TAG, "onActivityResult: " + account.getDisplayName());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
            }
        }


    }
    private void disableEnableControls(boolean enable, ViewGroup vg){

        if (!enable){
            av.show();
        }else {
            av.hide();
        }
        for (int i = 0; i < vg.getChildCount(); i++){
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup){
                disableEnableControls(enable, (ViewGroup)child);
            }
        }
    }



    @Override
    public void onClick( View view) {
        switch (view.getId()){

            case R.id.iv_login_with_google:
//                Log.d("cuonghx", "onClick: ");
                signIn();

                break;
            case R.id.tv_login:
//                Log.d("cuonghx", "onClick: " + email.getText().toString() + password.getText().toString());
                login(view);
                break;
            case R.id.tv_create:
                this.showNext(new SignUpFragment(), true);
                email.setText("");
                mAuth.signOut();
                firstLogin = false;
                break;

            case R.id.tv_forgot:
                dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_forget);

                dialog.findViewById(R.id.tv_send).setOnClickListener(this);
                dialog.findViewById(R.id.tv_cancel_dialog).setOnClickListener(this);
                mailForget = dialog.findViewById(R.id.edt_forgotPassword_dialog);

                dialog.show();
                break;

            case R.id.tv_send:
                if (!mailForget.getText().toString().equals("")) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(mailForget.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                dialog.cancel();
                                Toast.makeText(context, "Kiểm tra email của bạn", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(context, "Nhập lại email và thử lại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                    Toast.makeText(view.getContext(), "Nhập email của bạn", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_cancel_dialog:
                dialog.cancel();
                break;


        }
    }

    private void login(final View view) {

        if (!email.getText().toString().equals("") && !password.getText().toString().equals("")) {
            disableEnableControls(false, (ViewGroup) LoginFragment.this.view);
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (!mAuth.getCurrentUser().isEmailVerified()) {
                            disableEnableControls(true, (ViewGroup) LoginFragment.this.view);
                            Toast.makeText(view.getContext(), "xác nhận email", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(view.getContext(), "Thành công ", Toast.LENGTH_SHORT).show();
                            final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            Log.d(TAG, "onComplete: " +  firebaseUser.getUid());

                            databaseReference.orderByChild("uid").equalTo(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0){
                                        Log.d(TAG, "onDataChange: " + "cuonghx" + "created");
//                                        Log.d(TAG, "onDataChange: " );
//                                        for (DataSnapshot d : dataSnapshot.getChildren()) {
//                                            Log.d(TAG, "onDataChange: " + "cuonghx");
////                                update
////                                databaseReference.child(d.getKey()).removeValue();
//                                        }
                                    }else {
                                        Log.d(TAG, "onDataChange: cuonghx" + "not now");
                                        UserModel userModel = new UserModel();
                                        userModel.location = new LocationModel(0, 0);
                                        userModel.name = firebaseUser.getDisplayName();
                                        userModel.uid = firebaseUser.getUid();
                                        userModel.uri = firebaseUser.getPhotoUrl().toString();
                                        databaseReference.child(userModel.uid).setValue(userModel);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            showNext(new SelectTripFragment(), true);
                        }
                    } else {
                        disableEnableControls(true, (ViewGroup) LoginFragment.this.view);
                        Toast.makeText(view.getContext(), "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(view.getContext(), "Nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(TAG, "handleFacebookAccessToken: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: " + "oki" +mAuth.getCurrentUser().toString());
                            Log.d(TAG, "onComplete: cuonghx"+ mAuth.getCurrentUser().isEmailVerified());
                            final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            Log.d(TAG, "onComplete: " +  firebaseUser.getUid());

                            databaseReference.orderByChild("uid").equalTo(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0){
                                        Log.d(TAG, "onDataChange: " + "cuonghx" + "created");
                                    }else {
                                        Log.d(TAG, "onDataChange: cuonghx" + "not now");
                                        UserModel userModel = new UserModel();
                                        userModel.location = new LocationModel(0, 0);
                                        userModel.name  = firebaseUser.getDisplayName();
                                        userModel.uid = firebaseUser.getUid();
                                        userModel.uri = firebaseUser.getPhotoUrl().toString();
                                        Log.d(TAG, "onDataChange: " + userModel.uid);
                                        databaseReference.child(userModel.uid).setValue(userModel);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            showNext(new SelectTripFragment(), true);
                            LoginManager.getInstance().logOut();
                        }else {
                            disableEnableControls(true, (ViewGroup) view);
                            Log.d(TAG, "onComplete: " + "fail " + task.toString());
                            Toast.makeText(getView().getContext(), "Fail" , Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();
                        }
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        disableEnableControls(false, (ViewGroup) view);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: " + "oki" + mAuth.getCurrentUser().getDisplayName());
                            final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            Log.d(TAG, "onComplete: " +  firebaseUser.getUid());

                            databaseReference.orderByChild("uid").equalTo(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0){
                                        Log.d(TAG, "onDataChange: " + "cuonghx" + "created");
                                    }else {
                                        Log.d(TAG, "onDataChange: cuonghx" + "not now");
                                        UserModel userModel = new UserModel();
                                        userModel.location = new LocationModel(0, 0);
                                        userModel.uid = firebaseUser.getUid();
                                        userModel.name = firebaseUser.getDisplayName();
                                        userModel.uri = firebaseUser.getPhotoUrl().toString();
                                        databaseReference.child(userModel.uid).setValue(userModel);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            showNext(new SelectTripFragment(), true);
                        }else {
                            disableEnableControls(true, (ViewGroup) view);
                            Log.d(TAG, "onComplete: " + task.toString());
                        }
                    }
                });
    }
}
