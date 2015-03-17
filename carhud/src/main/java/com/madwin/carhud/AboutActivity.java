package com.madwin.carhud;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class AboutActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setupToolbar();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.email) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "madwin.developers@gmail.com" });
            startActivity(Intent.createChooser(intent, ""));
        }
        if(v.getId() == R.id.google_plus) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/b/115253383385955731681/115253383385955731681/posts"));
            startActivity(browserIntent);
        }
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (null != toolbar) {
            Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            toolbar.setNavigationIcon(upArrow);

            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            toolbar.setTitle(R.string.about);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
