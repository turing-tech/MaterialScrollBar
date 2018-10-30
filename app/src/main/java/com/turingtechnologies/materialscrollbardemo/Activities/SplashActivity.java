/*
 * Copyright © 2015, Turing Technologies, an unincorporated organisation of Wynne Plaga
 */

package com.turingtechnologies.materialscrollbardemo.Activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.turingtechnologies.materialscrollbardemo.AppData;
import com.turingtechnologies.materialscrollbardemo.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        AppData.processApps(this);

    }
}