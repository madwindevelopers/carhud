package com.madwin.carhud.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;

public class RoundAppIcon extends Drawable {

    private static final String TAG = "RoundAppIcon.java";
    Drawable appIcon;

    public RoundAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;

    }

    @Override
    public void draw(Canvas canvas) {

        int insetBounds = (int) DisplayUtils.convertDpToPixel(4);

        int backgroundColor = Color.argb(130, 0, 121, 107);

        Bitmap bitmap = Bitmap.createBitmap(canvas.getHeight(),
                canvas.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvasNew = new Canvas(bitmap);
        Log.d(TAG, "canvasNew size = " + canvasNew.getWidth() + " / " + canvasNew.getHeight());

        appIcon.setBounds(insetBounds, insetBounds, canvasNew.getWidth() - insetBounds, canvasNew.getHeight() - insetBounds);
        appIcon.draw(canvasNew);
        appIcon = new RoundDrawable(bitmap);

        ShapeDrawable backgroundDrawable = new ShapeDrawable(new OvalShape());
        backgroundDrawable.getPaint().setColor(backgroundColor);
        backgroundDrawable.setVisible(true, true);
        backgroundDrawable.setIntrinsicHeight(canvas.getHeight());
        backgroundDrawable.setIntrinsicWidth(canvas.getWidth());

        Drawable drawableArray[] = new Drawable[]{backgroundDrawable, appIcon};

        LayerDrawable layerDrawable = new LayerDrawable(drawableArray);
        layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        layerDrawable.findDrawableByLayerId(layerDrawable.getId(0))
                .setBounds(insetBounds, insetBounds,
                            canvas.getWidth() - insetBounds, canvas.getHeight() - insetBounds);
        layerDrawable.draw(canvas);

    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
