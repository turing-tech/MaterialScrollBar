/*
 *  Copyright © 2016, Turing Technologies, an unincorporated organisation of Wynne Plaga
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.turingtechnologies.materialscrollbar;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

abstract class Indicator extends RelativeLayout{

    protected TextView textView;
    private Context context;
    private boolean addSpace;

    public Indicator(Context context) {
        super(context);
        this.context = context;
    }

    public void setSizeCustom(int size){
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)getLayoutParams();
        if(addSpace){
            lp.setMargins(0, 0, size + Utils.getDP(10, this), 0);
        } else {
            lp.setMargins(0, 0, size, 0);
        }
        setLayoutParams(lp);
    }

    void linkToScrollBar(MaterialScrollBar materialScrollBar, boolean addSpace){
        this.addSpace = addSpace;
        if(Build.VERSION.SDK_INT >= 16){
            setBackground(ContextCompat.getDrawable(context, R.drawable.indicator));
        } else {
            setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.indicator));
        }

        textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getTextSize());
        RelativeLayout.LayoutParams tvlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvlp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        addView(textView, tvlp);

        setVisibility(INVISIBLE);
        ((GradientDrawable)getBackground()).setColor(materialScrollBar.handleColour);

        final int width = Utils.getDP(getIndicatorWidth(), this);
        final int height = Utils.getDP(getIndicatorHeight(), this);

        ViewGroup parent = (ViewGroup) materialScrollBar.getParent();
        MarginLayoutParams params;
        if (parent instanceof RelativeLayout) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
            lp.addRule(ALIGN_RIGHT, materialScrollBar.getId());
            params = lp;
        } else if (parent instanceof CoordinatorLayout) {
            CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(width, height);
            lp.gravity = Gravity.END;
            params = lp;
        } else {
            params = new MarginLayoutParams(width, height);
        }

        ViewGroup.LayoutParams rawScrollBarParams = materialScrollBar.getLayoutParams();
        if (rawScrollBarParams instanceof MarginLayoutParams) {
            MarginLayoutParams srcParams = (MarginLayoutParams) rawScrollBarParams;
            if (addSpace) {
                MarginLayoutParamsCompat.setMarginEnd(params, Utils.getDP(22, this));
            } else {
                MarginLayoutParamsCompat.setMarginEnd(params, Utils.getDP(12, this));
            }

            params.leftMargin += srcParams.leftMargin;
            params.topMargin += srcParams.topMargin;
            params.rightMargin += srcParams.rightMargin;
            params.bottomMargin += srcParams.bottomMargin;
        }

        parent.addView(this, params);
    }

    /**
     * Used by the materialScrollBar to move the indicator with the handle
     * @param y Position to which the indicator should move.
     */
    void setScroll(float y){
        //Displace the indicator upward so that the carrot extends from the centre of the handle.
        y -= Utils.getDP(getIndicatorHeight() + 25, this);
        //If the indicator is hidden by the top of the screen, it is inverted and displaced downward.
        if(y < 0){
            y += Utils.getDP(getIndicatorHeight(), this);
            setScaleY(-1F);
            textView.setScaleY(-1F);
            setY(y);
        } else {
            setScaleY(1F);
            textView.setScaleY(1F);
            setY(y);
        }
    }

    /**
     * Used by the materialScrollBar to change the text colour for the indicator.
     * @param colour The desired text colour.
     */
    void setTextColour(int colour){
        textView.setTextColor(colour);
    }

    abstract String getTextElement(Integer currentSection, RecyclerView.Adapter adapter);

    abstract int getIndicatorHeight();

    abstract int getIndicatorWidth();

    abstract void testAdapter(RecyclerView.Adapter adapter);

    abstract int getTextSize();

}
