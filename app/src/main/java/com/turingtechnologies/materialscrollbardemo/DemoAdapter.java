package com.turingtechnologies.materialscrollbardemo;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.turingtechnologies.materialscrollbar.ICustomAdapter;
import com.turingtechnologies.materialscrollbar.IDateableAdapter;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.Date;

class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.ViewHolder> implements INameableAdapter, IDateableAdapter, ICustomAdapter {

    private Activity act;

    DemoAdapter(Activity a) {
        act = a;
    }

    @Override
    public Character getCharacterForElement(int element) {
        Character c = AppData.pkgLabelList.get(element).charAt(0);
        if(Character.isDigit(c)) {
            c = '#';
        }
        return c;
    }

    @Override
    public Date getDateForElement(int element) {
        return new Date(AppData.pkgDateList.get(element));
    }

    @Override
    public String getCustomStringForElement(int element) {
        return AppData.pkgLabelList.get(element);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView label;
        ImageView icon;
        ViewHolder(View v) {
            super(v);
            label = v.findViewById(R.id.textView);
            icon = v.findViewById(R.id.imageView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.label.setText(AppData.pkgLabelList.get(position));
        holder.icon.setImageDrawable(AppData.pkgIconList.get(position));
    }

    @Override
    public int getItemCount() {
        try{
            return AppData.pkgLabelList.size();
        } catch (NullPointerException e) {
            Intent i = new Intent(act, SplashActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            act.startActivity(i);
        }
        return 0;
    }

}
