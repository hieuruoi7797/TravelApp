package com.example.sonng266.travelapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.fragments.BaseFragment;
import com.example.sonng266.travelapp.fragments.account.LoginFragment;

import io.realm.Realm;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onInitializeActivity(@Nullable Bundle savedInstanceState) {
        Realm.init(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else return;
    }

    @Override
    protected BaseFragment onCreateHomePage() {
        return new LoginFragment();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    public int getFragmentContentId() {
        return R.id.rl_login;
    }

    @Override
    public void onBackPressed() {
//        BaseFragment currentFragment = getCurrentFragment();
//        if (currentFragment != null && currentFragment.onBackPressed()) {
//            return;
//        }
        super.onBackPressed();
    }
}
