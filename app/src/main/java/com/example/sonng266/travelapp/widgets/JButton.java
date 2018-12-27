package com.example.sonng266.travelapp.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.ultis.FontManager;

/**
 * Created by sonng266 on 13/12/2017.
 */

public class JButton extends AppCompatButton {
    public JButton(Context context) {
        super(context);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.JLabel, 0, 0);
        String fontName = a.getString(R.styleable.JLabel_jLabelFont);
        if (fontName != null) {
            FontManager.getInstance().update(context, fontName, this);
        }
        a.recycle();
    }

    public JButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public JButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

}
