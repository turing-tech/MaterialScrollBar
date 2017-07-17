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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;

/**
 * Indicator which should be used in all other cases.
 */
@SuppressLint("ViewConstructor")
public class CustomIndicator extends Indicator<ICustomAdapter, CustomIndicator> {

    private int textSize = 25;

    public CustomIndicator(Context context){
        super(context, ICustomAdapter.class);
    }

    @Override
    protected String getTextElement(Integer currentSection, ICustomAdapter adapter) {
        String text = adapter.getCustomStringForElement(currentSection);
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        if(layoutParams == null){
            return "";
        }
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        int width = Utils.getDP((int) paint.measureText(text), context) + Utils.getDP(30, context);
        if(width < Utils.getDP(75, context)){
            width = Utils.getDP(75, context);
        }
        layoutParams.width = width;
        setLayoutParams(layoutParams);
        return text;
    }

    @Override
    protected int getIndicatorHeight() {
        return 75;
    }

    @Override
    protected int getIndicatorWidth() {
        return 0;
    }

    @Override
    protected int getTextSize() {
        return textSize;
    }

    public CustomIndicator setTextSize(int textSize){
        this.textSize = textSize;
        return this;
    }

}
