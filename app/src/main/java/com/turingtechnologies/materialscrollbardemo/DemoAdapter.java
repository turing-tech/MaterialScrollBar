package com.turingtechnologies.materialscrollbardemo;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.turingtechnologies.materialscrollbar.INameableAdapter;

public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.ViewHolder> implements INameableAdapter{

    private Activity act;

    DemoAdapter(Activity a){
        act = a;
    }

    @Override
    public Character getCharacterForElement(int element) {
        Character c = SplashActivity.pkgLabelList.get(element).charAt(0);
        if(Character.isDigit(c)){
            c = '#';
        }
        return c;
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
        holder.label.setText(SplashActivity.pkgLabelList.get(position));
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
