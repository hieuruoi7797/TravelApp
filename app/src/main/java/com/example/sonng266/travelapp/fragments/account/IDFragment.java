package com.example.sonng266.travelapp.fragments.account;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.activities.MainActivity;
import com.example.sonng266.travelapp.fragments.BaseFragment;
import com.example.sonng266.travelapp.models.TripModel;
import com.example.sonng266.travelapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class IDFragment extends BaseFragment implements View.OnClickListener {


    private ImageView ivProfile;
    private TextView tvUserName;
    private EditText edtID;
    private TextView tvCreate;
    private TextView tvContiune;

    private FirebaseAuth mAuth;
    private TextView tvSignOut;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private String idTrip;
    private ImageView check;

    private View view;

    private AVLoadingIndicatorView av;

    public IDFragment() {
        // Required empty public constructor
    }


    @Override
    public String getPageTitle() {
        return getClass().toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_id, container, false);
        setUpUI(view);
        return view;
    }

    @Override
    public boolean onBackPressed() {
        Log.d("cuonghx", "onBackPressed: 2709");
        return super.onBackPressed();
    }



    private void setUpUI(View view) {

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Trip");


        this.edtID = view.findViewById(R.id.edt_id);
        this.ivProfile = view.findViewById(R.id.iv_profile_id);
        this.tvUserName = view.findViewById(R.id.tv_username_id);
        this.tvContiune = view.findViewById(R.id.tv_continue_id);
        this.tvCreate = view.findViewById(R.id.tv_create_id);

        check = view.findViewById(R.id.iv_check_id);
        av = view.findViewById(R.id.avi);
        av.hide();

        Picasso.with(view.getContext()).load(mAuth.getCurrentUser().getPhotoUrl()).transform(new CropCircleTransformation()).into(ivProfile);
        this.tvUserName.setText(mAuth.getCurrentUser().getDisplayName());

        this.tvCreate.setOnClickListener(this);
        this.tvContiune.setOnClickListener(this);

    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()){
            case R.id.tv_create_id:
                showNext(new CreateIDFragment());
                break;
            case R.id.tv_continue_id:
                disableEnableControls(false, (ViewGroup) IDFragment.this.view);
                databaseReference.orderByChild("tId").equalTo(edtID.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                Log.d("cuonghx", "onDataChange: oki");
                                for (DataSnapshot d : dataSnapshot.getChildren()){
                                    final TripModel t = d.getValue(TripModel.class);
                                    Log.d("cuonghx", "onDataChange: " + t.name);
                                    boolean joined = false;
                                    if (t.memberList == null){
                                        t.memberList = new ArrayList<>();
                                    }

                                    Log.d("cuonghx", "onDataChange: " + mAuth.getCurrentUser().getUid());
                                    for (String id : t.memberList){
                                        if (mAuth.getCurrentUser().getUid().equals(id)){
                                            joined = true;
                                            break;
                                        }
                                    }
                                    if (!joined){
                                        t.memberList.add(mAuth.getUid());
                                        databaseReference.child(d.getKey()).setValue(t);

                                        databaseReference = FirebaseDatabase.getInstance().getReference("UserModel");
                                        databaseReference.orderByChild("uid").equalTo(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getChildrenCount() > 0) {
                                                    for (DataSnapshot d2 : dataSnapshot.getChildren()) {
                                                        UserModel userModel = d2.getValue(UserModel.class);
                                                        if (userModel.tripModelJoinedList == null){
                                                            userModel.tripModelJoinedList = new ArrayList<>();
                                                        }
                                                        TripModel tripModel = new TripModel();
                                                        tripModel.name = t.name;
                                                        tripModel.leaderId = t.leaderId;
                                                        tripModel.tId = t.tId;

                                                        userModel.tripModelJoinedList.add(tripModel);

                                                        databaseReference.child(d2.getKey()).setValue(userModel);

                                                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                                                        EventBus.getDefault().postSticky(t.tId);
                                                        view.getContext().startActivity(intent);


                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        joined = false;
                                    }else {
                                        Toast.makeText(view.getContext(), "Bạn đã ở trong chuyến đi rồi", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                check.setVisibility(View.VISIBLE);
                            } else {
                                Log.d("cuonghx", "onDataChange: " + "no");
                                Toast.makeText(view.getContext(), "Khong tim thay", Toast.LENGTH_SHORT).show();
                                check.setVisibility(View.INVISIBLE);
                            }
                            disableEnableControls(true, (ViewGroup) IDFragment.this.view);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                break;
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
}
