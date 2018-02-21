package com.turingtechnologies.materialscrollbardemo;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.turingtechnologies.materialscrollbar.ICustomAdapter;

class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> implements ICustomAdapter {

    private Activity act;

    IconAdapter(Activity a) {
        act = a;
    }

    @Override
    public String getCustomStringForElement(int element) {
        return AppData.pkgLabelList.get(element);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView icon;
        ViewHolder(View v) {
            super(v);
            icon = (ImageView) v.findViewById(R.id.iconView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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
