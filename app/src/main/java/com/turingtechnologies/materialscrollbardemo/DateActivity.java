package com.turingtechnologies.materialscrollbardemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.turingtechnologies.materialscrollbar.DateAndTimeIndicator;
import com.turingtechnologies.materialscrollbar.TouchScrollBar;

public class DateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new DemoAdapter(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ((TouchScrollBar) findViewById(R.id.touchScrollBar)).setIndicator(new DateAndTimeIndicator(this, false, true, true, true), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_date, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_toName) {
            Intent i = new Intent(this, NameActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
