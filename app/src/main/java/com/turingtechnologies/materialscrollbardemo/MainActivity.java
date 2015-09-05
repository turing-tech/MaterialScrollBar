package com.turingtechnologies.materialscrollbardemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.turingtechnologies.materialscrollbar.MaterialScrollBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = ((RecyclerView)findViewById(R.id.recyclerView));
        recyclerView.setAdapter(new DemoAdapter(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MaterialScrollBar materialScrollBar = new MaterialScrollBar(this, recyclerView);
        materialScrollBar.addSectionIndicator(this);
    }
}