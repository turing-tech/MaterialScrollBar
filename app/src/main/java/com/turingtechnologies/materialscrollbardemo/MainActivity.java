package com.turingtechnologies.materialscrollbardemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.turingtechnologies.materialscrollbar.MaterialScrollBar;
import com.turingtechnologies.materialscrollbar.SectionIndicator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = ((RecyclerView)findViewById(R.id.recyclerView));
        MaterialScrollBar materialScrollBar = ((MaterialScrollBar) findViewById(R.id.materialScrollBar));
        recyclerView.setAdapter(new DemoAdapter(getPackageManager(), this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        materialScrollBar.setRecyclerView(recyclerView);
        materialScrollBar.setSectionIndicator((SectionIndicator) findViewById(R.id.sectionIndicator));

    }

}
