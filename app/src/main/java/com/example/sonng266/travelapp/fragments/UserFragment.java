package com.example.sonng266.travelapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.adapter.UserAdapter;
import com.example.sonng266.travelapp.models.TripModel;
import com.example.sonng266.travelapp.models.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends BaseFragment {

    private RecyclerView rcvUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<UserModel> list = new ArrayList<>();
    private UserAdapter userAdapter;
    private String tId;

    public UserFragment() {
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
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        EventBus.getDefault().register(this);
        setUpUI(view);
        return view;
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventTripId(String tId) {
        this.tId = tId;
    }



    private void setUpUI(View view) {


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Trip");

        databaseReference.orderByChild("tId").equalTo(tId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        TripModel tripModel = d.getValue(TripModel.class);
                        Log.d("'cuonghx", "onDataChange: " + tripModel.leaderId);
                        userAdapter.setIdLeader(tripModel.leaderId);
                        for (int i = 0; i < tripModel.memberList.size() ; i++){
                            String s = tripModel.memberList.get(i);
                            databaseReference = firebaseDatabase.getReference("UserModel");
                            databaseReference.orderByChild("uid").equalTo(s).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0){
                                        for (DataSnapshot d : dataSnapshot.getChildren()){
                                            UserModel userModel = d.getValue(UserModel.class);
                                            list.add(userModel);

                                            userAdapter.notifyItemChanged(list.size() - 1);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rcvUser = view.findViewById(R.id.recycleview_user);
        userAdapter = new UserAdapter(list, view.getContext());
        rcvUser.setAdapter(userAdapter);
        rcvUser.setLayoutManager(new LinearLayoutManager(view.getContext()));

        SlideInLeftAnimator leftAnimator = new SlideInLeftAnimator();
        leftAnimator.setAddDuration(500);
        rcvUser.setItemAnimator(leftAnimator);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(),
//                DividerItemDecoration.VERTICAL);
//        rcvUser.addItemDecoration(dividerItemDecoration);
    }

}
