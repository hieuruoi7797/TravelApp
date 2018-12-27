package com.example.sonng266.travelapp.ultis.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.example.sonng266.travelapp.R;


public class DialogNotification extends Dialog implements View.OnClickListener {

    private Button btDone;

    public Activity mActivity;
    public Dialog mDialog;
    public Button btOk;

    public DialogNotification(Activity activity) {
        super(activity);
        this.mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_lets_go);
        btOk = findViewById(R.id.bt_ok);
        btOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ok:
                dismiss();
                break;
        }
    }
}
