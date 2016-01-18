package com.turingtechnologies.materialscrollbardemo;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {

    private Activity act;

    IconAdapter(Activity a){
        act = a;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView icon;
        public ViewHolder(View v) {
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
        holder.icon.setImageDrawable(SplashActivity.pkgIconList.get(position));
    }

    @Override
    public int getItemCount() {
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
