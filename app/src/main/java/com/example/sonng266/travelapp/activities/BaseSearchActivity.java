package com.example.sonng266.travelapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.eventBus.MessageEvent;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonng266 on 25/11/2017.
 */

public abstract class BaseSearchActivity extends AppCompatActivity{
    private static final String TAG = "BaseSearchActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.app_bar_title)
    TextView mTitleBar;

    PlaceAutocompleteFragment placeAutocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mTitleBar.setText(getTitleBar());
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        placeAutocompleteFragment.setHint(getHintSearchView());

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.

                String placeDetailsStr = place.getName() + "\n"
                        + place.getId() + "\n"
                        + place.getLatLng().toString() + "\n"
                        + place.getAddress() + "\n"
                        + place.getAttributions();
                Log.d(TAG, "Place: " + placeDetailsStr);

                handlePlaceSelected(place);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    protected abstract String getHintSearchView();

    protected abstract String getTitleBar();

    protected abstract void handlePlaceSelected(Place place);
}

//        11-26 17:33:02.546 3534-3534/com.example.sonng266.travelapp D/BaseSearchActivity: Place: Hanoi
//        ChIJKQqAE44ANTERDbkQYkF-mAI
//        lat/lng: (21.0031177,105.82014079999999)
//        Hanoi, Vietnam
//        null
//        11-26 17:33:31.645 3534-3534/com.example.sonng266.travelapp D/BaseSearchActivity: Place: Ho Chi Minh
//        ChIJI9kl2-8udTERFHIryt1Uz0s
//        lat/lng: (10.746903000000001,106.67629199999999)
//        Ho Chi Minh, Vietnam
//        null
//        11-26 17:34:14.617 3534-3534/com.example.sonng266.travelapp D/BaseSearchActivity: Place: Vinh
//        ChIJrR1aC2TOOTERobprhZBoJvg
//        lat/lng: (18.6795848,105.68133329999999)
//        Vinh, Nghe An, Vietnam
//        null