package com.example.sonng266.travelapp.activities;

import android.content.Intent;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.models.LocationModel;
import com.example.sonng266.travelapp.models.PlaceModel;
import com.google.android.gms.location.places.Place;

import org.greenrobot.eventbus.EventBus;


public class ChooseStartingPointActivity extends BaseSearchActivity {
    @Override
    protected String getHintSearchView() {
        return getString(R.string.tim_kiem_tai_day);
    }

    @Override
    public String getTitleBar() {
        return getString(R.string.chon_diem_dau_upper_case);
    }

    @Override
    public void handlePlaceSelected(Place place) {
        LocationModel location = new LocationModel(place.getLatLng().latitude, place.getLatLng().longitude);

        PlaceModel startingPoint = new PlaceModel(location, place.getName().toString());
        EventBus.getDefault().postSticky(startingPoint);

        startActivity(new Intent(getApplicationContext(), ChooseEndingPointActivity.class));
    }

}
