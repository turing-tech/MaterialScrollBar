/*
 *  Copyright © 2016-2018, Turing Technologies, an unincorporated organisation of Wynne Plaga
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
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class DragScrollBar extends MaterialScrollBar<DragScrollBar>{

    float handleOffset = 0;
    float indicatorOffset = 0;

    public DragScrollBar(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public DragScrollBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DragScrollBar(Context context, RecyclerView recyclerView, boolean lightOnTouch) {
        super(context, recyclerView, lightOnTouch);
    }

    boolean held = false;

    @Override
    void setTouchIntercept() {
        final Handle handle = super.handleThumb;
        OnTouchListener otl = (v, event) -> {
            if(!hiddenByUser) {
                boolean valid = validTouch(event);

                // check valid touch region only on action down => otherwise the check will fail if users scrolls very fast
                if(event.getAction() == MotionEvent.ACTION_DOWN && !valid) {
                    return false;
                }

                if(event.getAction() == MotionEvent.ACTION_DOWN && valid) {
                    held = true;

                    indicatorOffset = event.getY() - handle.getY() - handle.getLayoutParams().height / 2;
                    float offset2 = event.getY() - handle.getY();
                    float balance = handle.getY() / scrollUtils.getAvailableScrollBarHeight();
                    handleOffset = (offset2 * balance) + (indicatorOffset * (1 - balance));
                }
                //On Down
                if((event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) && held) {
                    onDown(event);
                    fadeIn();
                    //On Up
                } else {
                    onUp();

                    held = false;

                    fadeOut();
                }
                performClick();
                return true;
            }
            return false;
        };
        setOnTouchListener(otl);
    }

    @Override
    int getMode() {
        return 0;
    }

    @Override
    float getHideRatio() {
        return .65F;
    }

    @Override
    void onScroll() {}

    @Override
    boolean getHide() {
        return true;
    }

    @Override
    void implementFlavourPreferences() {}

    @Override
    float getHandleOffset() {
        return draggableFromAnywhere ? 0 : handleOffset;
    }

    @Override
    float getIndicatorOffset() {
        return draggableFromAnywhere ? 0 : indicatorOffset;
    }
}
