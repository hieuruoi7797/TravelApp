package com.example.sonng266.travelapp.ultis;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sonng266 on 13/12/2017.
 */

public final class FontManager {
    private Map<String, Typeface> mFonts = new HashMap<>();

    private static FontManager sFontManager;

    private FontManager() {

    }

    public static FontManager getInstance() {
        if (sFontManager == null) {
            sFontManager = new FontManager();
        }
        return sFontManager;
    }

    public void update(Context context, String font, TextView... views) {
        for (TextView view : views) {
            Typeface typeface = mFonts.get(font);
            if (typeface == null) {
                typeface = Typeface.createFromAsset(context.getAssets(), font);
                mFonts.put(font, typeface);
            }
            view.setTypeface(typeface);
        }
    }
}
