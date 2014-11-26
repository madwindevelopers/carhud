package com.madwin.carhud.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

public class RoundAppIcon extends Drawable {

    Drawable appIcon;

    public RoundAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;

    }

    @Override
    public void draw(Canvas canvas) {

        int insetBounds = 20;

        InsetDrawable insetDrawable =
                new InsetDrawable(appIcon, insetBounds, insetBounds, insetBounds, insetBounds);

        ShapeDrawable backgroundDrawable = new ShapeDrawable(new OvalShape());
        backgroundDrawable.getPaint().setColor(Color.argb(180, 0, 121, 107));
        backgroundDrawable.setVisible(true, true);
        backgroundDrawable.setIntrinsicHeight(40);
        backgroundDrawable.setIntrinsicWidth(40);

        Drawable drawableArray[] = new Drawable[]{backgroundDrawable, insetDrawable};

        LayerDrawable layerDrawable = new LayerDrawable(drawableArray);

        int width = layerDrawable.getIntrinsicWidth();
        int height = layerDrawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
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
