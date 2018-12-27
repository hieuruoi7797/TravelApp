package com.example.sonng266.travelapp.fragments.account;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.fragments.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends BaseFragment {


    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public String getPageTitle() {
        return EditProfileFragment.class.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

}
