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


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;

import org.apache.commons.lang3.StringUtils;

/**
 * Internal class. Provides the sectionIndicator created by using the addSectionIndicator() method.
 */
@SuppressLint("ViewConstructor")
class SectionIndicator extends RelativeLayout{

    private RelativeLayout indicator;
    private TextView textView;

    public SectionIndicator(Context context, MaterialScrollBar materialScrollBar) {
        super(context);

        indicator = new RelativeLayout(context);
        if(Build.VERSION.SDK_INT >= 16){
            indicator.setBackground(ContextCompat.getDrawable(context, R.drawable.indicator));
        } else {
            indicator.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.indicator));
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Utils.getDP(100, this), Utils.getDP(100, this));
        lp.setMargins(0, 0, Utils.getDP(8, this), 0);
        setVisibility(INVISIBLE);

        textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
       // textView.setTextColor(getResources().getColor()materialScrollBar.textColour);
        LayoutParams tvlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvlp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        addView(indicator, lp);
        indicator.addView(textView, tvlp);

        ((GradientDrawable)indicator.getBackground()).setColor(materialScrollBar.handleColour);

        LayoutParams layoutParams = new LayoutParams(Utils.getDP(100, this), ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(ALIGN_RIGHT, materialScrollBar.getId());
        layoutParams.addRule(ALIGN_TOP, materialScrollBar.getId());
        layoutParams.addRule(ALIGN_BOTTOM, materialScrollBar.getId());
        ((ViewGroup)materialScrollBar.getParent()).addView(this, layoutParams);
    }

    /**
     * Used by the materialScrollBar to move the indicator with the handle
     * @param y Position to which the indicator should move.
     */
    void setScroll(float y){
        //Displace the indicator upward so that the carrot extends from the centre of the handle.
        y -= Utils.getDP(74, this);
        //If the indicator is hidden by the top of the screen, it is inverted and displaced downward.
        if(y < 0){
            y += Utils.getDP(100, this);
            ViewHelper.setScaleY(indicator, -1F);
            ViewHelper.setScaleY(textView, -1F);
            ViewHelper.setY(indicator, y);
        } else {
            ViewHelper.setScaleY(indicator, 1F);
            ViewHelper.setScaleY(textView, 1F);
            ViewHelper.setY(indicator, y);
        }
    }

    /**
     * Used by the materialScrollBar to change the character of the indicator.
     * @param c The character which should be indicated.
     */
    void setCharacter(Character c){
        textView.setText(StringUtils.capitalize(c.toString()));
    }

    /**
     * Used by the materialScrollBar to change the text colour for the indicator.
     * @param colour The desired text colour.
     */
    void setTextColour(int colour){
        textView.setTextColor(getResources().getColor(colour));
    }

}