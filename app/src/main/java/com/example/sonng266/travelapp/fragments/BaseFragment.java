package com.example.sonng266.travelapp.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.activities.BaseActivity;
import com.example.sonng266.travelapp.widgets.ActionBarView;

import butterknife.ButterKnife;

/**
 * Created by sonng266 on 16/11/2017.
 */

public abstract class BaseFragment extends Fragment {
    public abstract String getPageTitle();

    public View mRootView = null;

    protected int getLayoutResourceId() {
        return 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView != null) {
            return mRootView;
        }

        int layoutResourceId = getLayoutResourceId();
        if (layoutResourceId != 0) {
            mRootView = inflater.inflate(layoutResourceId, container, false);
            ButterKnife.bind(this, mRootView);
            onBindView(savedInstanceState);
        }
        return mRootView;
    }

    protected void onBindView(Bundle savedInstanceState) {
    }

    public void handleFragmentClick(View view) {
        // for child class implement to handle event.
    }

    public void onUpdateActionBar(ActionBarView actionBarView) {
        actionBarView.setTitle(getPageTitle());
    }

    public boolean onBackPressed() {
        return showBack();
    }

    public boolean showBack() {
        int entryCount = getFragmentManager().getBackStackEntryCount();
        if (entryCount == 0) { // Home fragment now is visible.
            return false;
        }
        return getFragmentManager().popBackStackImmediate();
    }

    public void showNext(BaseFragment fragment, boolean addToBackStack) {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        BaseFragment currentFragment = baseActivity.getCurrentFragment();
        if (currentFragment != null) {
            transaction.remove(currentFragment);
        }

        int fragmentContentId = baseActivity.getFragmentContentId();
        transaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
                R.animator.fragment_slide_left_exit,
                R.animator.fragment_slide_right_enter,
                R.animator.fragment_slide_right_exit)
                .add(fragmentContentId, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void showNext(BaseFragment fragment) {
        showNext(fragment, true);
    }
}
