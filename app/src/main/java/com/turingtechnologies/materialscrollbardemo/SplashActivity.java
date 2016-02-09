/*
 * Copyright © 2015, Turing Technologies, an unincorporated organisation of Wynne Plaga
 */

package com.turingtechnologies.materialscrollbardemo;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private List<ApplicationInfo> pkgAppsList;
    public static ArrayList<String> pkgLabelList = new ArrayList<>();
    public static ArrayList<Drawable> pkgIconList = new ArrayList<>();
    public static ArrayList<String> pkgPackageList = new ArrayList<>();
    public static ArrayList<Long> pkgDateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        pkgAppsList = getPackageManager().getInstalledApplications(PackageManager.GET_ACTIVITIES);
        for(int i = 0; i < pkgAppsList.size(); i++){
            if(getPackageManager().getLaunchIntentForPackage(pkgAppsList.get(i).packageName) == null || (!BuildConfig.DEBUG && pkgAppsList.get(i).packageName.contains("com.turingtechnologies.youNote"))){
                pkgAppsList.remove(i);
                i--;
            }
        }

        class Alpha extends Thread{

            @Override
            public void run() {
                Collections.sort(pkgAppsList, new Comparator<ApplicationInfo>() {
                    @Override
                    public int compare(ApplicationInfo o1, ApplicationInfo o2) {
                        return o1.loadLabel(getPackageManager()).toString().compareToIgnoreCase(o2.loadLabel(getPackageManager()).toString());
                    }
                });
                for(ApplicationInfo appInfo : pkgAppsList){
                    pkgLabelList.add(appInfo.loadLabel(getPackageManager()).toString());
                    pkgPackageList.add(appInfo.packageName);
                    pkgIconList.add(appInfo.loadIcon(getPackageManager()));
                    try {
                        pkgDateList.add(getPackageManager().getPackageInfo(appInfo.packageName, 0).firstInstallTime);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                pkgAppsList = null;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent main = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(main);
                    }
                });
            }
        }

        Alpha alpha = new Alpha();
        alpha.setName("App Sorter");
        alpha.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        alpha.start();
    }
}