package com.madwin.carhud;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class AboutActivity extends ActionBarActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setupToolbar();

        if (getActionBar() != null) {
            getActionBar().setTitle(R.string.title_activity_about);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.about, menu);
////        return true;
//        return false;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
////        int id = item.getItemId();
////        if (id == R.id.action_settings) {
////            return true;
////        }
////        return super.onOptionsItemSelected(item);
//    }

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
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
    }
}
