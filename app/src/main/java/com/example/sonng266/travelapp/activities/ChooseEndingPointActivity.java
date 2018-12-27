package com.example.sonng266.travelapp.activities;

import android.content.Intent;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.models.LocationModel;
import com.example.sonng266.travelapp.models.PlaceModel;
import com.example.sonng266.travelapp.models.TripModel;
import com.example.sonng266.travelapp.models.UserModel;
import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;
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

public class ChooseEndingPointActivity extends BaseSearchActivity {

    private static final String TAG = "ChooseEndingPointActivi";

    private PlaceModel startingPoint;
    private TripModel newTripModel;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Trip");
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected String getHintSearchView() {
        return getString(R.string.tim_kiem_tai_day);
    }

    @Override
    public String getTitleBar() {
        return getString(R.string.chon_diem_cuoi_upper_case);
    }

    @Override
    public void handlePlaceSelected(Place place) {
        List<PlaceModel> places = new ArrayList<>();
        places.add(startingPoint);

        LocationModel endingPoint = new LocationModel(place.getLatLng().latitude, place.getLatLng().longitude);
        places.add(new PlaceModel(endingPoint, place.getName().toString()));

        newTripModel.placeList = places;

        if (newTripModel.memberList == null){
            newTripModel.memberList = new ArrayList<>();
        }
        newTripModel.memberList.add(newTripModel.leaderId);
        newTripModel.isGoing = false;

        databaseReference.child(newTripModel.tId).setValue(newTripModel);

        databaseReference = firebaseDatabase.getReference("UserModel");
        databaseReference.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        UserModel userModel = d.getValue(UserModel.class);
                        TripModel tripModel = new TripModel();
                        tripModel.tId = newTripModel.tId;
                        tripModel.leaderId = newTripModel.leaderId;
                        tripModel.name = newTripModel.name;
                        userModel.tripModelJoinedList.add(tripModel);
                        databaseReference.child(d.getKey()).setValue(userModel);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        startActivity(new Intent(this, MainActivity.class));
    }

    @ Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventStartingPoint(PlaceModel startingPoint) {
        this.startingPoint = startingPoint;
    }

    @ Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventCreateNewTrip(TripModel newTrip) {
        this.newTripModel = newTrip;
    }

}
