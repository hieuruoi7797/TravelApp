package com.example.sonng266.travelapp.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.ultis.FontManager;

/**
 * Created by sonng266 on 13/12/2017.
 */

public class JLabel extends AppCompatTextView {
    public JLabel(Context context) {
        super(context);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JLabel, 0, 0);
        String fontName = a.getString(R.styleable.JLabel_jLabelFont);
        if (fontName != null) {
            FontManager.getInstance().update(context, fontName, this);
        }
        a.recycle();
    }

    public JLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
