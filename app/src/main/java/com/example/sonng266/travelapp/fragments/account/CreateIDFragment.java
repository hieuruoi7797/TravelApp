package com.example.sonng266.travelapp.fragments.account;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.activities.ChooseStartingPointActivity;
import com.example.sonng266.travelapp.fragments.BaseFragment;
import com.example.sonng266.travelapp.models.TripModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateIDFragment extends BaseFragment {


    private EditText edtName;
    private TextView tvID;
    private TextView tv;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Context context;

    public CreateIDFragment() {
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
        View view = inflater.inflate(R.layout.fragment_create_id, container, false);
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        setUpUI(view);
        return view;
    }

    private void setUpUI(View view) {
        context = view.getContext();
        this.edtName = view.findViewById(R.id.edt_name_create_trip);
        tv = view.findViewById(R.id.tv_continue_create_id);
        tvID = view.findViewById(R.id.tv_id_create);

        databaseReference = firebaseDatabase.getReference("Trip");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    List<TripModel> list = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        TripModel tripModel = d.getValue(TripModel.class);
                        list.add(tripModel);
                    }
                    boolean created = true;
                    Random random = new Random();
                    int number = 2222;
                    while (created) {
                        number = random.nextInt(9000) + 1000;
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).tId.equals(String.valueOf(number))) {
                                break;
                            }
                            if (i == (list.size() - 1)) {
                                created = false;
                            }
                        }
                    }

                    tvID.setText(String.valueOf(number));
                } else {
                    tvID.setText("26697");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvID.getText().toString().equals("")) {
                    Toast.makeText(context, "Xin chờ", Toast.LENGTH_SHORT).show();
                } else if (edtName.getText().toString().equals("")) {
                    Toast.makeText(context, "Nhập tên chuyến đi", Toast.LENGTH_SHORT).show();
                } else {

                    Log.d("cuonghx", "onClick: " + edtName.getText().toString() + FirebaseAuth.getInstance().getUid());
                    TripModel newTrip = new TripModel();
                    newTrip.leaderId = FirebaseAuth.getInstance().getUid();
                    newTrip.tId = tvID.getText().toString();
                    newTrip.name = edtName.getText().toString();
                    EventBus.getDefault().postSticky(newTrip);
                    EventBus.getDefault().postSticky(newTrip.tId);
                    startActivity(new Intent(getActivity(), ChooseStartingPointActivity.class));
                }
            }
        });
    }

}
