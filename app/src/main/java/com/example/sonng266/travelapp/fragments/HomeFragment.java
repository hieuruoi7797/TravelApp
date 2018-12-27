package com.example.sonng266.travelapp.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.models.LocationModel;
import com.example.sonng266.travelapp.models.UserModel;
import com.example.sonng266.travelapp.services.UpdateLocationServer;
import com.example.sonng266.travelapp.ultis.firebase.DatabaseFirebase;
import com.example.sonng266.travelapp.ultis.map_direction.DirectionHandler;
import com.example.sonng266.travelapp.ultis.map_direction.DirectionResponse;
import com.example.sonng266.travelapp.ultis.RetrofitInstance;
import com.example.sonng266.travelapp.ultis.RetrofitService;
import com.example.sonng266.travelapp.ultis.map_direction.RouteModel;
import com.example.sonng266.travelapp.models.ModelRealm;
import com.example.sonng266.travelapp.models.PlaceModel;
import com.example.sonng266.travelapp.models.TripModel;
import com.example.sonng266.travelapp.ultis.Utils;
import com.example.sonng266.travelapp.ultis.notification_firebase.NotificationDataModel;
import com.example.sonng266.travelapp.ultis.notification_firebase.NotificationHandle;
import com.example.sonng266.travelapp.ultis.notification_firebase.NotificationModel;
import com.example.sonng266.travelapp.ultis.notification_firebase.SendingNotificationModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements OnMapReadyCallback, OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = "HomeFragment";

    public static final String SOS_TYPE = "SOS";
    public static final String STARTING_TYPE = "STARTING";
    public static final String SOMEBODY_WRONG_WAY = "WARNING";

    @BindView(R.id.bt_starting_point)
    Button btStartingPoint;
    @BindView(R.id.bt_ending_point)
    Button btEndingPoint;
    @BindView(R.id.bt_mid_point)
    Button btMidPoint;
    @BindView(R.id.bt_start)
    Button btStart;
    @BindView(R.id.bt_extra)
    ImageButton btExtra;
    @BindView(R.id.layout_going)
    CoordinatorLayout layoutGoing;
    @BindView(R.id.layout_not_going)
    ConstraintLayout layoutNotGoing;
    @BindView(R.id.fab_pause_trip)
    FloatingActionButton fabStopTrip;

    private Activity activity;

    private TripModel tripCurrent;
    private MapFragment mMapFragment;
    private String tId;
    private GoogleMap mMap;
    private LatLng startingPoint;
    private LatLng endingPoint;
    private boolean isALeader;
    private DialogPlus dialogSOS;
    private CountDownTimer countDownTimer;
    private View sosView;
    private UserModel currentUser;
    private boolean checkUser;
    private boolean checkTrip;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationModel myLocationCurent;
    private boolean followMe;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationModel leaderLocation;
    private DialogPlus dialogWarning;
    private boolean wrongWay;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public String getPageTitle() {
        return getClass().toString();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_home;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);

        firstSetup();
        setupMap();
        loadData();
    }

    private void firstSetup() {

        followMe = false;
        isALeader = false;

        EventBus.getDefault().register(this);

        dialogSOS = DialogPlus.newDialog(getActivity())
                .setOnClickListener(this)
                .setGravity(Gravity.BOTTOM)
                .setExpanded(true, 500)
                .setCancelable(false)
                .setContentHolder(new ViewHolder(R.layout.dialog_sos))
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .create();



        sosView = dialogSOS.getHolderView();
        final TextView tvCountdown = sosView.findViewById(R.id.tv_countdown);

        countDownTimer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountdown.setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                dialogSOS.dismiss();
                sendingSOS();
            }
        };

        dialogWarning = DialogPlus.newDialog(getActivity())
                .setOnClickListener(this)
                .setGravity(Gravity.BOTTOM)
                .setExpanded(true, 500)
                .setCancelable(false)
                .setContentHolder(new ViewHolder(R.layout.dialog_wrong_way))
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .create();

        checkTrip = false;
        checkUser = false;
        wrongWay = false;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            myLocationCurent = new LocationModel(location.getLatitude(), location.getLongitude());
                            FirebaseDatabase.getInstance().getReference("UserModel").child(FirebaseAuth.getInstance().getUid()).child("location").setValue(myLocationCurent);
                        }
                    }
                });

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void sendingSOS() {

        String to = "/topics/sos" + tripCurrent.tId;
        NotificationModel notificationModel = new NotificationModel("Thông báo được gửi từ " + currentUser.name, "SOS! SOS! SOS!");
        NotificationDataModel notificationDataModel = new NotificationDataModel(SOS_TYPE, FirebaseAuth.getInstance().getUid());
        SendingNotificationModel sendingNotificationModel = new SendingNotificationModel(to, notificationModel, notificationDataModel);

        NotificationHandle.sendingNotification(sendingNotificationModel, getActivity());

    }

    @Override
    public void handleFragmentClick(View view) {
        switch (view.getId()) {
            case R.id.bt_start:
                startingAwesomeTrip();
                goingHandle();
                break;
            case R.id.bt_starting_point:
                mMap.animateCamera(CameraUpdateFactory.newLatLng(startingPoint));
                break;
            case R.id.bt_ending_point:
                mMap.animateCamera(CameraUpdateFactory.newLatLng(endingPoint));
                break;
            case R.id.ib_sos:
                SOSHandle();
                break;
            case R.id.fab_pause_trip:
                FirebaseDatabase.getInstance().getReference("Trip").child(tripCurrent.tId).child("isGoing").setValue(false);
                break;
            default:
                break;
        }
    }

    private void SOSHandle() {
        dialogSOS.show();
        countDownTimer.start();
    }


    private void startingAwesomeTrip() {
        tripCurrent.isGoing = true;
        DatabaseFirebase.getInstance().getReference("Trip").child(tId).child("isGoing").setValue(true);
        String to = "/topics/" + tripCurrent.tId;
        NotificationModel notificationModel = new NotificationModel("Hãy cùng theo dõi vị trí của mọi người nhé", "Chuyến đi đã bắt đầu!");
        NotificationDataModel notificationDataModel = new NotificationDataModel(STARTING_TYPE, FirebaseAuth.getInstance().getUid());

        SendingNotificationModel sendingNotificationModel = new SendingNotificationModel(to, notificationModel, notificationDataModel);
        NotificationHandle.sendingNotification(sendingNotificationModel, getActivity());
    }

    private void loadData() {
        DatabaseReference databaseReference = DatabaseFirebase.getInstance().getReference("Trip").child(tId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tripCurrent = dataSnapshot.getValue(TripModel.class);

                if (tripCurrent.leaderId.equals(FirebaseAuth.getInstance().getUid())) {
                    isALeader = true;
                } else {
                    FirebaseMessaging.getInstance().subscribeToTopic(tripCurrent.tId);

                    DatabaseFirebase.getInstance().getReference("UserModel").child(tripCurrent.leaderId)
                            .child("location").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            leaderLocation = dataSnapshot.getValue(LocationModel.class);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                EventBus.getDefault().postSticky(isALeader);

                Utils.addOrUpdate(new ModelRealm(tId));

                FirebaseMessaging.getInstance().subscribeToTopic("sos" + tripCurrent.tId);

                checkTrip = true;
                setupUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });

        databaseReference.child("isGoing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean update = (boolean) dataSnapshot.getValue();

                if (tripCurrent != null && update != tripCurrent.isGoing) {
                    tripCurrent.isGoing = update;
                    if (tripCurrent.isGoing) {
                        goingHandle();
                    } else notGoingHandle();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseFirebase.getInstance().getReference("UserModel").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(UserModel.class);
                checkUser = true;
                setupUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setupUI() {

        if (!checkUser || !checkTrip) return;

        List<PlaceModel> places = tripCurrent.placeList;
        LatLng[] locations = new LatLng[places.size()];

        for (int i = 0; i < places.size(); i++) {
            locations[i] = new LatLng(places.get(i).location.lat, places.get(i).location.lng);
        }

        btStartingPoint.setText(places.get(0).name);
        btEndingPoint.setText(places.get(places.size() - 1).name);

        if (places.size() > 2) {
            btMidPoint.setVisibility(View.VISIBLE);
            btMidPoint.setText(String.format("%d điểm dừng", places.size() - 2));
        }

        startingPoint = new LatLng(locations[0].latitude, locations[0].longitude);
        endingPoint = new LatLng(locations[places.size() - 1].latitude, locations[places.size() - 1].longitude);

        if (!isALeader) {
            btStart.setVisibility(View.GONE);
        }

        if (!tripCurrent.isGoing) {
            notGoingHandle();
        } else {
            goingHandle();
        }
        drawMap();
    }

    private void goingHandle() {

        Intent serviceIntent = new Intent(getActivity().getApplicationContext(),UpdateLocationServer.class);
        serviceIntent.putExtra(UpdateLocationServer.KEY_TRIP_ID, tId);
        getActivity().getApplicationContext().startService(serviceIntent);

        followMe = true;

        if (isALeader) fabStopTrip.setVisibility(View.VISIBLE);

        layoutNotGoing.setVisibility(View.GONE);
        layoutGoing.setVisibility(View.VISIBLE);
    }

    private void notGoingHandle() {
        followMe = false;
        layoutGoing.setVisibility(View.GONE);
        layoutNotGoing.setVisibility(View.VISIBLE);
    }

    public void drawMap() {
        mMap.clear();

        if (!followMe) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 14f));
        } else {
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocationCurent.lat, myLocationCurent.lng), 14f));
        }

        mMap.addMarker(new MarkerOptions().position(endingPoint)
                .title(tripCurrent.placeList.get(tripCurrent.placeList.size() - 1).getName()));

        mMap.addMarker(new MarkerOptions().position(startingPoint)
                .title(tripCurrent.placeList.get(0).getName()));

        RetrofitService retrofitService = RetrofitInstance.getInstanceMapDirection().create(RetrofitService.class);
        retrofitService.getDirection(
                getLatLng(startingPoint),
                getLatLng(endingPoint),
                getResources().getString(R.string.google_maps_key)
        ).enqueue(new Callback<DirectionResponse>() {
            @Override
            public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
                PolylineOptions polylineOptions = new PolylineOptions()
                        .color(Color.BLUE)
                        .width(8);

                ArrayList<RouteModel> routeModels = (ArrayList<RouteModel>) DirectionHandler.getListRouteDetail(response.body());
                for (RouteModel routeModel : routeModels) {
                    for (LatLng latLng : routeModel.points) {
                        polylineOptions.add(latLng);
                    }
                }
                mMap.addPolyline(polylineOptions);
            }

            @Override
            public void onFailure(Call<DirectionResponse> call, Throwable t) {
            }
        });

    }

    public String getLatLng(LatLng latLng) {
        double lat = latLng.latitude;
        double lng = latLng.longitude;
        return lat + "," + lng;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventTripId(String tId) {
        this.tId = tId;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setupMap() {
        mMapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    private void setupViewOfMyLocationIcon() {
        if (mMapFragment.getView() != null && mMapFragment.getView().findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mMapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
    }

    @Override
    public void onClick(DialogPlus dialog, View view) {
        if (dialog == dialogSOS) {
            switch (view.getId()) {
                case R.id.bt_cancel:
                    dialog.dismiss();
                    countDownTimer.cancel();
                    break;
                case R.id.bt_send_now:
                    dialog.dismiss();
                    countDownTimer.cancel();
                    sendingSOS();
                    break;
            }
        } else if (dialog == dialogWarning) {
            switch (view.getId()) {
                case R.id.bt_dialog_cancel:
                    dialog.dismiss();
                    break;
                case R.id.bt_dialog_ok:
                    dialog.dismiss();
                    sendingWarning();
                    break;
            }
        }

    }

    private void sendingWarning() {
        String to = "/topics/sos" + tripCurrent.tId;
        NotificationModel notificationModel = new NotificationModel("Thành viên " + currentUser.name + " hiện đang cách xa leader hơn 2km", "WARNING!");
        NotificationDataModel notificationDataModel = new NotificationDataModel(SOMEBODY_WRONG_WAY, FirebaseAuth.getInstance().getUid());
        SendingNotificationModel sendingNotificationModel = new SendingNotificationModel(to, notificationModel, notificationDataModel);

        NotificationHandle.sendingNotification(sendingNotificationModel, getActivity());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        if (mMap != null && tripCurrent != null && tripCurrent.isGoing) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14f));

            if (!wrongWay && leaderLocation != null && DirectionHandler.distance(location.getLatitude()
                    ,leaderLocation.lat, location.getLongitude(), leaderLocation.lng, 0 , 0) > 2000 && !dialogWarning.isShowing()) {
                wrongWay = true;
                dialogWarning.show();
            } else if (leaderLocation != null && DirectionHandler.distance(location.getLatitude()
                    ,leaderLocation.lat, location.getLongitude(), leaderLocation.lng, 0 , 0) <= 2000) {
                wrongWay = false;
            }
        }
    }
}
