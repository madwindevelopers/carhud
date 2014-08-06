package com.madwin.carhud.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.madwin.carhud.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppListDialogFragment extends DialogFragment {
    View v;
    ListView mAppList;
    PackageManager pm;
    List pkgAppsList;
    ArrayList<String> apps_and_package_name;
    ArrayList<String> apps_name;
    View mContentView;
    View mLoadingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        final Resources res = getResources();
        final int dividerColor = res.getColor(R.color.DividerGray);
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
        apps_and_package_name = new ArrayList<String>();
        apps_name = new ArrayList<String>();

        v = inflater.inflate(R.layout.app_list_view_dialog, null);
        mContentView = v.findViewById(R.id.list_view);
        mLoadingView = v.findViewById(R.id.loadingPanel);
        mContentView.setVisibility(View.GONE);

        if (getDialog() != null) {
            getDialog().setTitle(getResources().getString(R.string.select_application));
        }

        AppListTask appListTask = new AppListTask();
        appListTask.execute();

        mAppList = (ListView)mContentView;
        mAppList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String the_package = apps_and_package_name.get(position);
                the_package = the_package.substring(the_package.indexOf("%") + 1, the_package.length());
                Toast.makeText(getActivity(), "package name = " + the_package, Toast.LENGTH_SHORT).show();
                Intent intent;
                PackageManager manager = getActivity().getPackageManager();
                try{
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


            for (Object aPkgAppsList : pkgAppsList) {
                //Log.d("AppListDialogFragment", "Got Package Data  " + aPkgAppsList.toString());
                String a = aPkgAppsList.toString();
                a = a.substring(21, a.indexOf("/"));
                try {
                    PackageInfo p = pm.getPackageInfo(a, 0);
                    apps_name.add(p.applicationInfo.loadLabel(pm).toString());
                } catch (final PackageManager.NameNotFoundException e) {
                    apps_name.add("label not found");
                }
                //Log.d("AppsList", "package name = " + a);

                try {
                    PackageInfo p = pm.getPackageInfo(a, 0);
                    a = p.applicationInfo.loadLabel(pm).toString() + "%" + a;
                } catch (final PackageManager.NameNotFoundException e) {
                    a = "label not found";
                }
                apps_and_package_name.add(a);

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            Collections.sort(apps_name, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });
            Collections.sort(apps_and_package_name, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });

            crossfade();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, apps_name);
            mAppList.setAdapter(adapter);
        }
    }
}
