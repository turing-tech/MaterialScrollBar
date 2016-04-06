package com.turingtechnologies.materialscrollbardemo;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppData {

    static List<ApplicationInfo> pkgAppsList;
    static ArrayList<String> pkgLabelList = new ArrayList<>();
    static ArrayList<Drawable> pkgIconList = new ArrayList<>();
    static ArrayList<String> pkgPackageList = new ArrayList<>();
    static ArrayList<Long> pkgDateList = new ArrayList<>();

    static void processApps(final AppCompatActivity activity){
        if(!pkgLabelList.isEmpty()){
            openMainActivity(activity);
            return;
        }
        //noinspection WrongConstant
        pkgAppsList = activity.getPackageManager().getInstalledApplications(PackageManager.GET_ACTIVITIES);
        for(int i = 0; i < pkgAppsList.size(); i++){
            if(activity.getPackageManager().getLaunchIntentForPackage(pkgAppsList.get(i).packageName) == null || (!BuildConfig.DEBUG && pkgAppsList.get(i).packageName.contains(AppData.class.getPackage().getName()))){
                pkgAppsList.remove(i);
                i--;
            }
        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Collections.sort(pkgAppsList, new Comparator<ApplicationInfo>() {
                    @Override
                    public int compare(ApplicationInfo o1, ApplicationInfo o2) {
                        return o1.loadLabel(activity.getPackageManager()).toString().compareToIgnoreCase(o2.loadLabel(activity.getPackageManager()).toString());
                    }
                });
                for(ApplicationInfo appInfo : pkgAppsList){
                    pkgLabelList.add(appInfo.loadLabel(activity.getPackageManager()).toString());
                    pkgPackageList.add(appInfo.packageName);
                    pkgIconList.add(appInfo.loadIcon(activity.getPackageManager()));
                    try {
                        pkgDateList.add(activity.getPackageManager().getPackageInfo(appInfo.packageName, 0).firstInstallTime);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                pkgAppsList = null;
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                openMainActivity(activity);
            }
        }.execute();
    }

    static void openMainActivity(AppCompatActivity activity){
        Intent main = new Intent(activity.getApplicationContext(), MainActivity.class);
        activity.startActivity(main);
    }

}
