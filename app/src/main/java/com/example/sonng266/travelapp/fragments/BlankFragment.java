package com.example.sonng266.travelapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sonng266.travelapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends BaseFragment {

    @Override
    public String getPageTitle() {
        return "asd";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment;
    }

    @Override
    public void handleFragmentClick(View view) {
        switch (view.getId()) {
            case R.id.bt_back:
                showBack();
            case R.id.bt_next:
                showNext(new BlankFragment());
        }
    }
}
