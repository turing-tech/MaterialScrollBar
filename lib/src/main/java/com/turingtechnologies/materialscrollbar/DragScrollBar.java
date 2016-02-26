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
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

public class DragScrollBar extends MaterialScrollBar<DragScrollBar>{

    public DragScrollBar(Context context, RecyclerView recyclerView, boolean lightOnTouch){
        super(context, recyclerView, lightOnTouch);
    }

    public DragScrollBar(Context context, AttributeSet attributeSet, int defStyle){
        super(context, attributeSet, defStyle);
    }

    public DragScrollBar(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    @Override
    void setTouchIntercept() {
        OnTouchListener otl = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!hiddenByUser) {
                    if (event.getAction() != MotionEvent.ACTION_UP && event.getY() >= ViewHelper.getY(handle) - Utils.getDP(20, recyclerView.getContext()) && event.getY() <= ViewHelper.getY(handle) + handle.getHeight()) {
                        if (indicator != null && indicator.getVisibility() == INVISIBLE) {
                            indicator.setVisibility(VISIBLE);
                        }
                            int top = 0;
                            int bottom = recyclerView.getHeight() - Utils.getDP(72, recyclerView.getContext());
                            float boundedY = Math.max(top, Math.min(bottom, event.getY()));
                            scrollUtils.scrollToPositionAtProgress((boundedY - top) / (bottom - top));
                            scrollUtils.scrollHandleAndIndicator();
//                        }
                        recyclerView.onScrolled(0, 0);

                        if (lightOnTouch) {
                            handle.setBackgroundColor(handleColour);
                        }

                        fadeIn();
                    } else {
                        if (indicator != null && indicator.getVisibility() == VISIBLE) {
                            if (Build.VERSION.SDK_INT <= 12) {
                                indicator.clearAnimation();
                            }
                            indicator.setVisibility(INVISIBLE);
                        }

                        if (lightOnTouch) {
                            handle.setBackgroundColor(handleOffColour);
                        }

                        fadeOut();
                    }
                    return true;
                }
                return false;
            }
        };
        setOnTouchListener(otl);
    }

    @Override
    int getMode() {
        return 0;
    }

    @Override
    float getHideRatio() {
        if(super.programmatic){
            return .35F;
        } else {
            return .65F;
        }
    }

    @Override
    void onScroll() {}

    @Override
    boolean getHide() {
        return true;
    }

    @Override
    void implementFlavourPreferences(TypedArray a) {}
}
