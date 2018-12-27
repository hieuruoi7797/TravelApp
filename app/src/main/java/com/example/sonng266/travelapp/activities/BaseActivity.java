package com.example.sonng266.travelapp.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.sonng266.travelapp.eventBus.MessageEvent;
import com.example.sonng266.travelapp.fragments.BaseFragment;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    protected abstract void onInitializeActivity(@Nullable Bundle savedInstanceState);

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int contentViewId = getContentViewId();
        if (contentViewId <= 0) {
            throw new NullPointerException("getContentViewId() must not be null");
        }
        setContentView(contentViewId);
        ButterKnife.bind(this);
        onInitializeActivity(savedInstanceState);

        // Add back stack change listener
        getFragmentManager().addOnBackStackChangedListener(BaseActivity.this);

        int fragmentContentId = getFragmentContentId();
        BaseFragment homePage = onCreateHomePage();
        if (homePage != null) {
            FragmentManager fm = getFragmentManager();
            fm.popBackStack(0, 0);
            fm.beginTransaction()
                    .add(fragmentContentId, homePage)
                    .commit();
        } else {
            Toast.makeText(this, "Chỉ định 1 home fragment onCreateHomePage", Toast.LENGTH_SHORT).show();
        }
    }

    protected abstract BaseFragment onCreateHomePage();

    public abstract int getContentViewId();

    public abstract int getFragmentContentId();

    public BaseFragment getCurrentFragment() {
        int resourceId = getFragmentContentId();
        return (BaseFragment) getFragmentManager().findFragmentById(resourceId);
    }

    /**
     * Phương thức này handle các click event của các view trong các layout khi
     * các view set attribute "onClick" là method này.
     * Khi method này nhận được 1 click event bất kỳ, nó sẽ dispatch xuống current controller để xử lý.
     *
     * @param v
     */
    public void handleFragmentClick(View v) {
        BaseFragment currentFragment = getCurrentFragment();
        if (currentFragment != null) {
            currentFragment.handleFragmentClick(v);
        }
    }

    @Override
    public void onBackStackChanged() {
    }


}

