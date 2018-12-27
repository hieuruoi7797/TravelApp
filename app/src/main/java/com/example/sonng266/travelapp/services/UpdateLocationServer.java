package com.example.sonng266.travelapp.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.activities.LoginActivity;
import com.example.sonng266.travelapp.activities.MainActivity;
import com.example.sonng266.travelapp.models.LocationModel;
import com.example.sonng266.travelapp.ultis.map_direction.DirectionHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class UpdateLocationServer extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static String KEY_TRIP_ID = "KEY_TRIP_ID";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private boolean isPushing;
    private LocationModel leaderLocation;

    private static String tId;

    public UpdateLocationServer() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        isPushing = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getStringExtra(KEY_TRIP_ID) != null) {
            tId = intent.getStringExtra(KEY_TRIP_ID);
        }

        if (tId != null) {
            mGoogleApiClient.connect();

            FirebaseDatabase.getInstance().getReference("Trip").child(tId).child("isGoing").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!(boolean) dataSnapshot.getValue()) {
                        stopSelf();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            FirebaseDatabase.getInstance().getReference("Trip").child(tId).child("leaderId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String leaderId = (String) dataSnapshot.getValue();

                    if (!leaderId.equals(FirebaseAuth.getInstance().getUid())) {
                        FirebaseDatabase.getInstance().getReference("UserModel").child(leaderId).child("location").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                leaderLocation = dataSnapshot.getValue(LocationModel.class);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        FirebaseDatabase.getInstance().getReference("UserModel").child(FirebaseAuth.getInstance().getUid())
                .child("location").setValue(new LocationModel(location.getLatitude(), location.getLongitude()));

        LocationModel myLocation = new LocationModel(location.getLatitude(), location.getLongitude());

        boolean wrongWay = false;

        if (leaderLocation != null) {
            wrongWay = DirectionHandler.distance(myLocation.lat, leaderLocation.lat, myLocation.lng, leaderLocation.lng, 0, 0) > 2000;
        }

        if (!isPushing && wrongWay) {
            isPushing = true;
            PugNotification.with(getApplicationContext())
                    .load()
                    .title("Warning!!!")
                    .message("Ban xa leader hon 2 km")
                    .smallIcon(R.drawable.ic_location_on_red_400_24dp)
                    .flags(Notification.DEFAULT_ALL)
                    .click(LoginActivity.class)
                    .vibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .sound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .autoCancel(true)
                    .simple()
                    .build();
        } else if (!wrongWay) {
            isPushing = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
}
