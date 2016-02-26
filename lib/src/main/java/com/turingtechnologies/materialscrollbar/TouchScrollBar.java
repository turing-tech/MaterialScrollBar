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
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class TouchScrollBar extends MaterialScrollBar<TouchScrollBar>{

    private boolean hide = true;
    private int hideDuration = 2500;
    private Handler uiHandler = new Handler(Looper.getMainLooper());

    private Runnable fadeBar = new Runnable() {

        @Override
        public void run() {
            fadeOut();
        }
    };

    public TouchScrollBar(Context context, RecyclerView recyclerView, boolean lightOnTouch){
        super(context, recyclerView, lightOnTouch);
    }

    public TouchScrollBar(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    public TouchScrollBar(Context context, AttributeSet attributeSet, int defStyle){
        super(context, attributeSet, defStyle);
    }

    public TouchScrollBar setHideDuration(int duration){
        hideDuration = duration;
        return this;
    }

    @Override
    void setTouchIntercept() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!hiddenByUser) {
                    if (event.getAction() != MotionEvent.ACTION_UP) {
                        if (indicator != null && indicator.getVisibility() == INVISIBLE) {
                            indicator.setVisibility(VISIBLE);
                        }
                        int top = 0;
                        int bottom = recyclerView.getHeight() - Utils.getDP(72, recyclerView.getContext());
                        float boundedY = Math.max(top, Math.min(bottom, event.getY()));
                        scrollUtils.scrollToPositionAtProgress((boundedY - top) / (bottom - top));
                        scrollUtils.scrollHandleAndIndicator();
                        recyclerView.onScrolled(0, 0);

                        if(lightOnTouch){
                            handle.setBackgroundColor(handleColour);
                        }

                        if(hide){
                            uiHandler.removeCallbacks(fadeBar);
                            fadeIn();
                        }
                    } else {
                        if(indicator != null && indicator.getVisibility() == VISIBLE){
                            if(Build.VERSION.SDK_INT <= 12){
                                indicator.clearAnimation();
                            }
                            indicator.setVisibility(INVISIBLE);
                        }

                        if(lightOnTouch){
                            handle.setBackgroundColor(handleOffColour);
                        }

                        if (hide) {
                            uiHandler.removeCallbacks(fadeBar);
                            uiHandler.postDelayed(fadeBar, hideDuration);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    int getMode() {
        return 1;
    }

    @Override
    float getHideRatio() {
        return 1.0F;
    }

    @Override
    void onScroll() {
        System.out.println(hide);
        if(hide){
            uiHandler.removeCallbacks(fadeBar);
            uiHandler.postDelayed(fadeBar, hideDuration);
            fadeIn();
        }
    }

    @Override
    boolean getHide() {
        return hide;
    }

    @Override
    void implementFlavourPreferences(TypedArray a) {
        if(a.hasValue(R.styleable.TouchScrollBar_autoHide)){
            setAutoHide(a.getBoolean(R.styleable.TouchScrollBar_autoHide, true));
            System.out.println(hide);
        }
        if(a.hasValue(R.styleable.TouchScrollBar_hideDelayInMilliseconds)){
            hideDuration = (a.getInteger(R.styleable.TouchScrollBar_hideDelayInMilliseconds, 2500));
        }
    }

    /**
     * Provides the ability to programmatically alter whether the scrollbar
     * should hide after a period of inactivity or not.
     * @param hide sets whether the bar should hide or not.
     */
    public MaterialScrollBar setAutoHide(Boolean hide){
        if(!hide){
            TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
            anim.setFillAfter(true);
            startAnimation(anim);
        }
        this.hide = hide;
        return this;
    }
}
