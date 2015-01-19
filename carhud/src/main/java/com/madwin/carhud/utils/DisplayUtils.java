package com.madwin.carhud.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

public class DisplayUtils {

    public static float convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }
}
