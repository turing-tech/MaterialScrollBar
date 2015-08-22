package com.turingtechnologies.materialscrollbardemo;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.turingtechnologies.materialscrollbar.INameableAdapter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.ViewHolder> implements INameableAdapter{

    public ArrayList<String> filterLabel = new ArrayList<>();
    public ArrayList<Drawable> filterIcon = new ArrayList<>();
    public ArrayList<String> filterPackage = new ArrayList<>();
    public boolean fil = false;
    private Activity act;

    DemoAdapter(PackageManager pman, Activity a){
        act = a;
    }

    public void setFilter(final String f) {
        filterLabel.clear();
        filterIcon.clear();
        filterPackage.clear();
        if (!f.equals("")) {
            fil = true;
            for (int i = 0; i < SplashActivity.pkgLabelList.size(); i++) {
                if (StringUtils.containsIgnoreCase(SplashActivity.pkgLabelList.get(i), f)) {
                    filterLabel.add(SplashActivity.pkgLabelList.get(i));
                    filterIcon.add(SplashActivity.pkgIconList.get(i));
                    filterPackage.add(SplashActivity.pkgPackageList.get(i));
                }
            }
        } else {
            fil = false;
        }
        notifyDataSetChanged();
    }

    @Override
    public Character getCharacterForElement(int element) {
        if(fil){
            Character c = filterLabel.get(element).charAt(0);
            if(Character.isDigit(c)){
                c = '#';
            }
            return c;
        } else {
            Character c = SplashActivity.pkgLabelList.get(element).charAt(0);
            if(Character.isDigit(c)){
                c = '#';
            }
            return c;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView label;
        public ImageView icon;
        public ViewHolder(View v) {
            super(v);
            label = (TextView) v.findViewById(R.id.textView);
            icon = (ImageView) v.findViewById(R.id.imageView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(fil){
            holder.label.setText(filterLabel.get(position));
            holder.icon.setImageDrawable(filterIcon.get(position));
        } else {
            holder.label.setText(SplashActivity.pkgLabelList.get(position));
            holder.icon.setImageDrawable(SplashActivity.pkgIconList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if(fil){
            return filterLabel.size();
        } else {
            try{
                return SplashActivity.pkgLabelList.size();
            } catch (NullPointerException e){
                Intent i = new Intent(act, SplashActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                act.startActivity(i);
            }
            return 0;
        }
    }

}
