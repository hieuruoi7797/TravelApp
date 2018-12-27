package com.example.sonng266.travelapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.eventBus.ShowDialogSOSEvent;
import com.example.sonng266.travelapp.eventBus.ShowDialogTripStartedEvent;
import com.example.sonng266.travelapp.eventBus.ShowDialogWrongWayEvent;
import com.example.sonng266.travelapp.fragments.BaseFragment;
import com.example.sonng266.travelapp.fragments.HomeFragment;
import com.example.sonng266.travelapp.fragments.UserFragment;
import com.example.sonng266.travelapp.fragments.account.LoginFragment;
import com.example.sonng266.travelapp.fragments.account.SelectTripFragment;
import com.example.sonng266.travelapp.fragments.chat.ChatFragment;
import com.example.sonng266.travelapp.models.LocationModel;
import com.example.sonng266.travelapp.models.TripModel;
import com.example.sonng266.travelapp.models.UserModel;
import com.example.sonng266.travelapp.services.ChatService;
import com.example.sonng266.travelapp.services.UpdateLocationServer;
import com.example.sonng266.travelapp.ultis.Utils;
import com.example.sonng266.travelapp.ultis.dialogs.DialogNotification;
import com.example.sonng266.travelapp.ultis.firebase.DatabaseFirebase;
import com.example.sonng266.travelapp.ultis.map_direction.DirectionHandler;
import com.example.sonng266.travelapp.widgets.ActionBarView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSION = 1;
    private String LINK_SHARE = "https://drive.google.com/drive/folders/1-da19gbSSFcixWMVquDTfO0FpOXJPcXA";

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.app_bar_title)
    TextView mTitleBar;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    protected ActionBarView actionBarView;

    private HomeFragment homeFragment;
    private DrawerLayout drawer;
    private MenuItem menuItemWaiting;
    private Boolean isALeader = false;
    private String tId;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onInitializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(mToolBar);
        displayHomeAsUp(false);
        drawer = findViewById(R.id.drawer_layout);
        mNavigationView.setNavigationItemSelectedListener(this);
//        menuItemWaiting = (MenuItem) mNavigationView.getMenu();

        View headView = mNavigationView.getHeaderView(0);
        ImageView imageView = headView.findViewById(R.id.nav_imageView);
        TextView tvUser = headView.findViewById(R.id.nav_tv_user);
        TextView tvMail = headView.findViewById(R.id.nav_tv_mail);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        tvMail.setText(user.getEmail());
        tvUser.setText(user.getDisplayName());
        Picasso.with(this).load(user.getPhotoUrl()).transform(new CropCircleTransformation()).into(imageView);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToolbarNavigationClicked();
            }
        });

        actionBarView = new ActionBarView(mToolBar, mTitleBar);

        if (Build.VERSION.SDK_INT >= 23) {
            setupPermission();
        }

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (menuItemWaiting != null) {
                    onNavigationItemSelected(menuItemWaiting);
                }
            }
        });
        startService(new Intent(getBaseContext(), ChatService.class));
        setOnboarding();
    }

    private void setOnboarding() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Log.d(TAG, "setOnboarding: " + sharedPref.getBoolean("first", true));
        if (!sharedPref.getBoolean("first", false)) {
            new MaterialTapTargetPrompt.Builder(this)
                    .setTarget(getToolbarNavigationButton())
                    .setBackgroundColour(getResources().getColor(R.color.colorbackgrond))
                    .setIcon(R.drawable.ic_menu_white_24dp)
                    .setPrimaryText("Here is an interesting feature")
                    .setSecondaryText("Tap here and you will be surprised")
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                    {
                        @Override
                        public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                        {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                            {
                                Log.d(TAG, "onPromptStateChanged: cuonghx");
                                MainActivity.this.displayHomeAsUp(false);
                            }
                        }
                    })
                    .show();

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("first", true);
            editor.commit();
        }
    }

    private void setupPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION);
        } else return;
    }

    public ImageButton getToolbarNavigationButton() {
        int size = mToolBar.getChildCount();
        for (int i = 0; i < size; i++) {
            View child = mToolBar.getChildAt(i);
            if (child instanceof ImageButton) {
                ImageButton btn = (ImageButton) child;
                if (btn.getDrawable() == mToolBar.getNavigationIcon()) {
                    return btn;
                }
            }
        }
        return null;
    }


    private void onToolbarNavigationClicked() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else {
            getCurrentFragment().showBack();
        }
    }

    private void displayHomeAsUp(boolean display) {
        int res = display ? R.drawable.ic_arrow_back_white_24dp : R.drawable.ic_menu_white_24dp;
        mToolBar.setNavigationIcon(res);
    }

    @Override
    protected BaseFragment onCreateHomePage() {
        mTitleBar.setText(getString(R.string.app_name_upper_case));
        homeFragment = new HomeFragment();
        return homeFragment;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public int getFragmentContentId() {
        return R.id.base_fragment_content_view;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            BaseFragment currentFragment = getCurrentFragment();
            if (currentFragment != null && currentFragment.onBackPressed()) {
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_map:


                if (!getCurrentFragment().getPageTitle().equals(new HomeFragment().getPageTitle())) {
                    getCurrentFragment().showNext(new HomeFragment(), false);
                }
                break;
            case R.id.nav_forum:
                if (!getCurrentFragment().getPageTitle().equals(new ChatFragment().getPageTitle()))
                    getCurrentFragment().showNext(new ChatFragment(), false);
                break;
            case R.id.nav_lt:
                break;
            case R.id.nav_user:
                if (!getCurrentFragment().getPageTitle().equals(new UserFragment()
                        .getPageTitle()))
                    getCurrentFragment().showNext(new UserFragment(), false);
                EventBus.getDefault().postSticky(tId);
                break;
            case R.id.nav_other:

                //unsubcribe Notification

                if (!isALeader) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(tId);
                }

                FirebaseMessaging.getInstance().unsubscribeFromTopic("sos" + tId);

                //

                // kill service
                stopService(new Intent(getBaseContext(),UpdateLocationServer.class));
                //

                Intent intent1 = new Intent(this, LoginActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Utils.delete();
                startActivity(intent1);
                break;
            case R.id.nav_logout:
                //unsubcribe Notification

                if (!isALeader) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(tId);
                }

                FirebaseMessaging.getInstance().unsubscribeFromTopic("sos" + tId);

                //

                // kill service
                    stopService(new Intent(getBaseContext(),UpdateLocationServer.class));
                //

                FirebaseAuth.getInstance().signOut();
                Utils.delete();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.nav_share:
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_forget);
                final EditText mail = dialog.findViewById(R.id.edt_forgotPassword_dialog);
                dialog.findViewById(R.id.tv_send).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendmail(mail.getText().toString());
                        dialog.cancel();
                    }
                });
                dialog.findViewById(R.id.tv_cancel_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });


                dialog.show();
                break;
            case R.id.nav_feedback:
                final Dialog d = new Dialog(this);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setCancelable(false);
                d.setContentView(R.layout.dialog_feedback);
                d.findViewById(R.id.dialog_send).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, "Cảm ơn bạn đã gửi phản hồi cho chúng tôi", Toast.LENGTH_SHORT).show();
                        d.cancel();
                    }
                });
                d.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.cancel();
                    }
                });
                d.show();
                break;
            case R.id.out_trip:

                final AVLoadingIndicatorView av = getCurrentFragment().getView().findViewById(R.id.avi);
                av.show();
                if (!isALeader) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(tId);
                }
                FirebaseMessaging.getInstance().unsubscribeFromTopic("sos" + tId);

                //

                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("Trip");
                Log.d(TAG, "onNavigationItemSelected: " + tId);
                databaseReference.orderByChild("tId").equalTo(tId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0){
                            databaseReference = firebaseDatabase.getReference("Trip");
                            for (DataSnapshot d : dataSnapshot.getChildren()){
                                TripModel tripModel = d.getValue(TripModel.class);
                                Log.d(TAG, "onDataChange: cuonghx" + tripModel.memberList +"/n" + FirebaseAuth.getInstance().getUid() );
                                String s = Utils.getStringById(tripModel.memberList,FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Log.d(TAG, "onDataChange: cuonghx" + s);
                                tripModel.memberList.remove(s);
                                Log.d(TAG, "onDataChange: cuonghx1 " + tripModel.memberList);
                                databaseReference.child(d.getKey()).setValue(tripModel);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                databaseReference = firebaseDatabase.getReference("UserModel");
                databaseReference.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0){
                            databaseReference = firebaseDatabase.getReference("UserModel");
                            for (DataSnapshot d : dataSnapshot.getChildren()){
                                UserModel userModel = d.getValue(UserModel.class);
                                TripModel tripModel = Utils.getTripById(userModel.tripModelJoinedList, tId);
                                userModel.tripModelJoinedList.remove(tripModel);
                                databaseReference.child(d.getKey()).setValue(userModel);
                            }
                        }
                        Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Utils.delete();
                        startActivity(intent2);

                        av.hide();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void sendmail(String mail) {

        BackgroundMail.newBuilder(this)
                .withUsername("supcentertravelapp@gmail.com")
                .withPassword("cuonghx2709")
                .withMailto(mail)
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Tham gia chuyến đi")
                .withBody("Một người bạn mời bạn tham gia chuyến đi có id là: " + tId + "\n" +
                        "Nếu bạn chưa cài app hãy click vào đường link sau \n " + LINK_SHARE)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        //do some magic
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        //do some magic
                    }
                })
                .send();
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventCheckLeader(Boolean isALeader) {
        this.isALeader = isALeader;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventTripId(String tId) {
        this.tId = tId;
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventTripStarted(ShowDialogTripStartedEvent showDialogTripStartedEvent) {
        if (showDialogTripStartedEvent.isShow) {
            EventBus.getDefault().postSticky(new ShowDialogTripStartedEvent(false));

            DialogPlus dialog = DialogPlus.newDialog(this)
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(DialogPlus dialog, View view) {
                            switch (view.getId()) {
                                case R.id.bt_ok:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    })
                    .setGravity(Gravity.BOTTOM)
                    .setPadding(10, 10, 10, 10)
                    .setExpanded(true)
                    .setContentHolder(new ViewHolder(R.layout.dialog_lets_go))
                    .create();
            dialog.show();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventSOS(ShowDialogSOSEvent showDialogSOSEvent) {
        if (showDialogSOSEvent.isShow) {
            EventBus.getDefault().postSticky(new ShowDialogSOSEvent(false, ""));

            DialogPlus dialog = DialogPlus.newDialog(this)
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(DialogPlus dialog, View view) {
                            switch (view.getId()) {
                                case R.id.bt_ok:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    })
                    .setGravity(Gravity.BOTTOM)
                    .setExpanded(true)
                    .setPadding(10, 10, 10, 10)
                    .setContentHolder(new ViewHolder(R.layout.dialog_lets_go))
                    .create();
            View view = dialog.getHolderView();

            TextView tvMessage = view.findViewById(R.id.tv_message);
            TextView tvTitle = view.findViewById(R.id.tv_title_notification);
            Button button = view.findViewById(R.id.bt_ok);

            tvTitle.setText("SOS");
            tvTitle.setTextColor(Color.RED);

            tvMessage.setText(showDialogSOSEvent.nameSender);
            tvMessage.setAllCaps(true);

            button.setText("OK");

            dialog.show();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventWarning(ShowDialogWrongWayEvent showDialogWrongWayEvent) {
        if (showDialogWrongWayEvent.isShow) {
            EventBus.getDefault().postSticky(new ShowDialogWrongWayEvent(false, ""));

            DialogPlus dialog = DialogPlus.newDialog(this)
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(DialogPlus dialog, View view) {
                            switch (view.getId()) {
                                case R.id.bt_ok:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    })
                    .setGravity(Gravity.BOTTOM)
                    .setExpanded(true)
                    .setPadding(10, 10, 10, 10)
                    .setContentHolder(new ViewHolder(R.layout.dialog_lets_go))
                    .create();
            View view = dialog.getHolderView();

            TextView tvMessage = view.findViewById(R.id.tv_message);
            TextView tvTitle = view.findViewById(R.id.tv_title_notification);
            Button button = view.findViewById(R.id.bt_ok);

            tvTitle.setText("WARNING!!!");
            tvTitle.setTextColor(Color.RED);

            tvMessage.setText(showDialogWrongWayEvent.nameSender);
            tvMessage.setAllCaps(true);

            button.setText("OK");

            dialog.show();
        }
    }
}
