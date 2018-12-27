package com.example.sonng266.travelapp.widgets;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class ActionBarView {
    private Toolbar toolbar;
    private TextView title;

    public ActionBarView(Toolbar toolbar, TextView textView) {
        this.toolbar = toolbar;
        this.title = textView;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }
}
