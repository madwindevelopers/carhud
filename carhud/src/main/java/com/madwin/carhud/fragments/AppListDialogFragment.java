package com.madwin.carhud.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.madwin.carhud.AppAsyncTask;
import com.madwin.carhud.AppListArrayAdapter;
import com.madwin.carhud.R;

import java.util.ArrayList;
import java.util.List;

public class AppListDialogFragment extends DialogFragment {
    private View v;
    private ListView mAppList;
    private PackageManager pm;
    private List pkgAppsList;
    private ArrayList<String> appsAndPackageName;
    private ArrayList<String> appsName;
    private ArrayList<Drawable> appIconList;
    private View mContentView;
    private View mLoadingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        final Resources res = getResources();
        final int dividerColor = res.getColor(R.color.DividerGrey);
        final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
        final View titleDivider = getDialog().findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(dividerColor);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pm = getActivity().getApplicationContext().getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        pkgAppsList = pm.queryIntentActivities(mainIntent, 0);
        appsAndPackageName = new ArrayList<String>();
        appsName = new ArrayList<String>();
        appIconList = new ArrayList<Drawable>();

        v = inflater.inflate(R.layout.app_list_view_dialog, null);
        mContentView = v.findViewById(R.id.list_view);
        mLoadingView = v.findViewById(R.id.loadingPanel);
        mContentView.setVisibility(View.GONE);

        if (getDialog() != null) {
            getDialog().setTitle(getResources().getString(R.string.select_application));
        }

        AppListTask appListTask = new AppListTask();
        appListTask.execute();

        mAppList = (ListView) mContentView;
        mAppList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String the_package = AppAsyncTask.apps_and_package_name.get(position);
                the_package = the_package.substring(the_package.indexOf("%") + 1, the_package.length());
                Toast.makeText(getActivity(), "package name = " + the_package, Toast.LENGTH_SHORT).show();
                Intent intent;
                PackageManager manager = getActivity().getPackageManager();
                try {
                    intent = manager.getLaunchIntentForPackage(the_package);
                    if (intent == null)
                        throw new PackageManager.NameNotFoundException();
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(intent);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getActivity(), "package name not found", Toast.LENGTH_SHORT).show();
                }
                Log.d("AppListDialogFragment", "position + id = " + position + " + " + id);
                getDialog().dismiss();
            }
        });
        return v;
    }

    private void crossfade() {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        mContentView.setAlpha(0f);
        mContentView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        int mShortAnimationDuration = 1000;
        mContentView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mLoadingView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoadingView.setVisibility(View.GONE);
                    }
                });

    }

    private class AppListTask extends AsyncTask<Long, String, Long> {

        @Override
        protected Long doInBackground(Long... params) {

            appsName = AppAsyncTask.apps_name;
            appIconList = AppAsyncTask.app_icon_list;

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            crossfade();
            AppListArrayAdapter adapter = new AppListArrayAdapter(getActivity(),
                    appsName, appIconList);
            mAppList.setAdapter(adapter);

        }
    }
}
