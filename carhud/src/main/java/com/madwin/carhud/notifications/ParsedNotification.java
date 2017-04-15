package com.madwin.carhud.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.service.notification.StatusBarNotification;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import jp.yokomark.remoteview.reader.RemoteViewsInfo;
import jp.yokomark.remoteview.reader.RemoteViewsReader;
import jp.yokomark.remoteview.reader.action.BitmapReflectionAction;
import jp.yokomark.remoteview.reader.action.ReflectionAction;
import jp.yokomark.remoteview.reader.action.RemoteViewsAction;

public class ParsedNotification {

    private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private ArrayList<String> texts = new ArrayList<String>();

    public ParsedNotification() {}

    public ParsedNotification(Context context, StatusBarNotification sbn) {
        RemoteViews remoteViews = sbn.getNotification().contentView;
        RemoteViewsInfo info = RemoteViewsReader.read(context, remoteViews);
        List<RemoteViewsAction> actions = info.getActions();

        for (RemoteViewsAction action : actions) {

            if (action instanceof BitmapReflectionAction) {
                Bitmap bm = ((BitmapReflectionAction) action).getBitmap();
                this.addBitmap(bm);

            } else if (action instanceof ReflectionAction) {
                if (((ReflectionAction)action).getMethodName().equals("setText"))
                    this.addText(((ReflectionAction) action).getValue().toString().trim());
            }
        }
    }

    public ArrayList<Bitmap> getBitmaps() {
        return bitmaps;
    }

    public Bitmap getBitmap(int index) {
        if (bitmaps.size() > index)
            return bitmaps.get(index);
        return null;
    }

    public void addBitmap(Bitmap b) {
        bitmaps.add(b);
    }
    public void setBitmaps(ArrayList<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }

    public ArrayList<String> getTexts() {
        return texts;
    }

    public String getText(int index) {
        if (texts.size() > index) {
            return texts.get(index);
        }
        return null;
    }

    public void setTexts(ArrayList<String> texts) {
        this.texts = texts;
    }

    public void addText(String s) {
        texts.add(s);
    }


}
