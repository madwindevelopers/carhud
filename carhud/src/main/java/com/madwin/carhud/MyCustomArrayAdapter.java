package com.madwin.carhud;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.madwin.carhud.utils.RoundAppIcon;

import java.util.ArrayList;

/**
 * Created by andrew on 8/24/14.
 */
public class MyCustomArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> app_name;
    private final ArrayList<Drawable> app_icon;

    public MyCustomArrayAdapter(Context context, ArrayList<String> app_name, ArrayList<Drawable> app_icon) {
        super(context, R.layout.custom_list_item, app_name);
        this.context = context;
        this.app_name = app_name;
        this.app_icon = app_icon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_list_item, parent, false);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.app_image_item);
        TextView textView = (TextView) rowView.findViewById(R.id.app_name_item);
        textView.setText(app_name.get(position));
        imageView.setImageDrawable(new RoundAppIcon(app_icon.get(position)));

        return rowView;
    }
}
