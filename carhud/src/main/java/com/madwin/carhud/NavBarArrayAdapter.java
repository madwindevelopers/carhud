package com.madwin.carhud;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class NavBarArrayAdapter extends ArrayAdapter {

    private final ArrayList<String> listItemNames;
    private final ArrayList<Drawable> listItemIcons;

    public NavBarArrayAdapter(Context context, ArrayList<String> listItemNames,
                              ArrayList<Drawable> listItemIcons) {
        super(context, R.layout.drawer_list_item, listItemNames);
        this.listItemNames = listItemNames;
        this.listItemIcons = listItemIcons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView;
        if (position == 0) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.drawer_header, parent, false);
        } else {

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.drawer_list_item, parent, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.drawer_item_icon);
            TextView textView = (TextView) itemView.findViewById(R.id.drawer_item_text);
            textView.setText(getListItemNames().get(position));
            imageView.setImageDrawable(getListItemIcons().get(position));
        }

        return itemView;
    }

    public ArrayList<String> getListItemNames() {
        return listItemNames;
    }

    public ArrayList<Drawable> getListItemIcons() {
        return listItemIcons;
    }
}
