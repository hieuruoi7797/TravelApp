package com.example.sonng266.travelapp.fragments.account;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.activities.MainActivity;
import com.example.sonng266.travelapp.adapter.TripAdapter;
import com.example.sonng266.travelapp.fragments.BaseFragment;
import com.example.sonng266.travelapp.models.ModelRealm;
import com.example.sonng266.travelapp.models.TripModel;
import com.example.sonng266.travelapp.models.UserModel;
import com.example.sonng266.travelapp.ultis.Utils;
import com.google.firebase.auth.FirebaseAuth;
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

import io.realm.Realm;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectTripFragment extends BaseFragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private ImageView ivUser ;
    private TextView tvUserName;
    private RecyclerView recyclerView;
    private TextView tvOther;
    private TextView tvCreate;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private List<TripModel> list = new ArrayList<>();

    private TripAdapter tripAdapter ;
    private AVLoadingIndicatorView av;

    private Context context;
    private Dialog dialog;

    public SelectTripFragment() {
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


        View view = inflater.inflate(R.layout.fragment_select_trip, container, false);
        if (Utils.getModelRealm() != null){
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            EventBus.getDefault().postSticky(Utils.getModelRealm().tId);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            view.getContext().startActivity(intent);
        }
        setUpUI(view);
        return view;
    }

    private void setUpUI(View view) {
        mAuth =FirebaseAuth.getInstance();

        av = view.findViewById(R.id.avi);

        this.context = view.getContext();
        this.ivUser = view.findViewById(R.id.iv_select);
//        this.tvUserName = view.findViewById(R.id.tv_username_id_select);
        this.recyclerView = view.findViewById(R.id.recycleview_select);
        this.tvOther = view.findViewById(R.id.tv_select_other);
        this.tvCreate = view.findViewById(R.id.tv_create_select);

        final TextView textView = view.findViewById(R.id.tv_non);


        TextView tvUserName = view.findViewById(R.id.tvusername_select);
        tvUserName.setText(mAuth.getCurrentUser().getDisplayName());

        this.tvOther.setOnClickListener(this);
        tvCreate.setOnClickListener(this);

//        tvUserName.setText(mAuth.getCurrentUser().getDisplayName());
        Picasso.with(view.getContext()).load(mAuth.getCurrentUser().getPhotoUrl()).transform(new CropCircleTransformation()).into(ivUser);

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("UserModel");

        databaseReference.orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0){
                    list.clear();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {

                        UserModel userModel = d.getValue(UserModel.class);
                        Log.d("cuonghx", "onDataChange: 2709");
                        if (userModel.tripModelJoinedList.size() == 0 || userModel.tripModelJoinedList == null) {
                            textView.setVisibility(View.VISIBLE);
                            av.hide();
                        } else {
                            for (final TripModel tripModel : userModel.tripModelJoinedList) {
                                databaseReference = FirebaseDatabase.getInstance().getReference("Trip");
                                databaseReference.orderByChild("tId").equalTo(tripModel.tId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        av.hide();
                                        if (dataSnapshot.getChildrenCount() > 0) {
                                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                                TripModel t = d.getValue(TripModel.class);
                                                list.add(t);
                                            }
                                            tripAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                }else {
                    av.hide();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        tripAdapter = new TripAdapter(list, view.getContext());
        recyclerView.setAdapter(tripAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getView() == null){
            return;
        }

        Log.d("cuonghx", "onResume: ");
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    // handle back button's click listener

                    dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.dialog_sign_out);
                    Button btYes = dialog.findViewById(R.id.btn_yes);
                    Button btNO = dialog.findViewById(R.id.btn_no);

                    btYes.setOnClickListener(SelectTripFragment.this);
                    btNO.setOnClickListener(SelectTripFragment.this);

                    dialog.show();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_select_other:
                showNext(new IDFragment(), true);
                break;
            case R.id.tv_create_select:
                showNext(new CreateIDFragment());
                break;
            case R.id.btn_yes:
                mAuth.signOut();
                dialog.cancel();
                showBack();
                break;
            case R.id.btn_no:
                dialog.cancel();
                break;
        }
    }
}
