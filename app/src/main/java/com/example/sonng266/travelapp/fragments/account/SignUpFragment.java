package com.example.sonng266.travelapp.fragments.account;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.activities.MainActivity;
import com.example.sonng266.travelapp.fragments.BaseFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends BaseFragment {

    private EditText edtUserName;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConfirmPass;
    private TextView tvSignUp;
    final Handler handler = new Handler();
    private Runnable runnable;
    public static boolean created;
    public static String email = null;

    private FirebaseAuth mAuthor;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public String getPageTitle() {
        return SignUpFragment.class.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sign_up, container, false);
        setUpUI(view);
        return view;
    }

    private void setUpUI(View view) {

        final ImageView checkUserName = view.findViewById(R.id.iv_check_userName);
        final ImageView checkConfirm = view.findViewById(R.id.iv_check_confirm);
        final TextView tv = view.findViewById(R.id.tv_mk);
        final ImageView checkPassword = view.findViewById(R.id.iv_check_pass);

        edtUserName = view.findViewById(R.id.edt_username_sign_up);
        edtEmail = view.findViewById(R.id.edt_email_sign_up);
        edtPassword = view.findViewById(R.id.edt_password_sign_up);
        edtConfirmPass = view.findViewById(R.id.edt_confirm_password_sign_up);
        tvSignUp = view.findViewById(R.id.tv_sign_up);

        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    tv.setVisibility(View.GONE);
                }else {
                    tv.setVisibility(View.VISIBLE);
                }
            }
        });



         this.runnable = new Runnable() {
            @Override
            public void run() {
//                Log.d("cuonghx", "run: " + edtUserName.getText().toString());
                if (!edtUserName.getText().toString().equals("")){
                    checkUserName.setVisibility(View.VISIBLE);
                    created = true;
                }else {
                    checkUserName.setVisibility(View.INVISIBLE);
                    created = false;
                }
                if (edtPassword.getText().toString().equals(edtConfirmPass.getText().toString()) &&
                        !edtPassword.getText().toString().equals("")){
                    checkConfirm.setVisibility(View.VISIBLE);
                    created = true;
                }else {
                    checkConfirm.setVisibility(View.INVISIBLE);
                    created = false;
                }
                if (edtPassword.getText().toString().length() >= 6){
                    checkPassword.setVisibility(View.VISIBLE);
                }else {
                    checkPassword.setVisibility(View.INVISIBLE);
                }

                handler.postDelayed(this,500);
            }
        };

        runnable.run();

        mAuthor = FirebaseAuth.getInstance();


        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (created && !edtEmail.getText().toString().equals("")){
                    mAuthor.createUserWithEmailAndPassword(
                            edtEmail.getText().toString(),
                            edtPassword.getText().toString()
                    ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                mAuthor.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(view.getContext(), "Xác nhận tài khoản qua email", Toast.LENGTH_SHORT)
                                                    .show();

                                            UserProfileChangeRequest user = new UserProfileChangeRequest.Builder().setDisplayName(edtUserName.getText().toString())
                                                    .setPhotoUri(Uri.parse("https://scontent.xx.fbcdn.net/v/t1.0-1/c29.0.100.100/p100x100/10354686_10150004552801856_220367501106153455_n.jpg?oh=049ecfece14dfe681a2cc083eeaabc6f&oe=5AA0FC77"))
                                                    .build();
                                            mAuthor.getCurrentUser().updateProfile(user);
                                            SignUpFragment.email = edtEmail.getText().toString();
                                            Log.d("cuonghx", "onComplete: "+ !mAuthor.getCurrentUser().isAnonymous());
                                            mAuthor.signOut();
                                            onBackPressed();
                                        }else {
                                            Toast.makeText(view.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(view.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(view.getContext(), "Nhập lại", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
