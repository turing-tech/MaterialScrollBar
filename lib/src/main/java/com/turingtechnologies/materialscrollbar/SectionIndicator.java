/*
 * Copyright © 2015, Turing Technologies, an unincorporated organisation of Wynne Plaga
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.turingtechnologies.materialscrollbar;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;

public class SectionIndicator extends RelativeLayout{

    MaterialScrollBar materialScrollBar;
    RelativeLayout indicator;
    TextView textView;

    public SectionIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        indicator = new RelativeLayout(context);
        if(Build.VERSION.SDK_INT >= 16){
            indicator.setBackground(ContextCompat.getDrawable(context, R.drawable.indicator));
        } else {
            indicator.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.indicator));
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(getDP(100), getDP(100));
        lp.setMargins(0, 0, getDP(8), 0);
        setVisibility(INVISIBLE);

        textView = new TextView(context);
        textView.setText("#");
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
        LayoutParams tvlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvlp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        addView(indicator, lp);
        indicator.addView(textView, tvlp);
    }

    protected void setScroll(float y){
        y -= getDP(74);
        if(y < 0){
            y += getDP(98);
            indicator.setScaleY(-1F);
            textView.setScaleY(-1F);
            ViewHelper.setY(indicator, y);
        } else {
            indicator.setScaleY(1F);
            textView.setScaleY(1F);
            ViewHelper.setY(indicator, y);
        }
    }

    protected void setCharacter(Character c){
        textView.setText(c.toString());
    }

    private int getDP(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    protected void pairScrollBar(MaterialScrollBar msb){
        materialScrollBar = msb;
        ((GradientDrawable)indicator.getBackground()).setColor(materialScrollBar.handleColour);
    }

}