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
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class TouchScrollBar extends MaterialScrollBar<TouchScrollBar> {

    private boolean hide = true;
    private int hideDuration = 2500;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private boolean respondToTouch = true;
    private TypedArray flavourAttributes;

    private Runnable fadeBar = this::fadeOut;

    public TouchScrollBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TouchScrollBar(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public TouchScrollBar(Context context, RecyclerView recyclerView, boolean lightOnTouch) {
        super(context, recyclerView, lightOnTouch);
    }

    public TouchScrollBar setHideDuration(int duration) {
        hideDuration = duration;
        return this;
    }

    @Override
    void setUpProps(Context context, AttributeSet attributes) {
        super.setUpProps(context, attributes);
        flavourAttributes = context.getTheme().obtainStyledAttributes(
                attributes, R.styleable.TouchScrollBar, 0, 0);
    }

    @Override
    void setTouchIntercept() {
        setOnTouchListener((v, event) -> {
            if(!hiddenByUser) {

                boolean valid = validTouch(event);

                // check valid touch region only on action down => otherwise the check will fail if users scrolls very fast
                if(event.getAction() == MotionEvent.ACTION_DOWN && !valid) {
                    return false;
                }

                //On Down
                if(event.getAction() != MotionEvent.ACTION_UP) {

                    if(!hidden || respondToTouch) {
                        onDown(event);

                        if(hide) {
                            uiHandler.removeCallbacks(fadeBar);
                            fadeIn();
                        }
                    }

                    //On Up
                } else {

                    onUp();

                    if(hide) {
                        uiHandler.removeCallbacks(fadeBar);
                        uiHandler.postDelayed(fadeBar, hideDuration);
                    }
                }
                performClick();
                return true;
            }
            return false;
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
        if(hide) {
            uiHandler.removeCallbacks(fadeBar);
            uiHandler.postDelayed(fadeBar, hideDuration);
            fadeIn();
        }
    }

    @Override
    boolean getHide() {
//        return hide;
        return true;
    }

    @Override
    void implementFlavourPreferences() {
        if(flavourAttributes.hasValue(R.styleable.TouchScrollBar_msb_autoHide)) {
            setAutoHide(flavourAttributes.getBoolean(R.styleable.TouchScrollBar_msb_autoHide, true));
        }
        if(flavourAttributes.hasValue(R.styleable.TouchScrollBar_msb_hideDelayInMilliseconds)) {
            hideDuration = (flavourAttributes.getInteger(R.styleable.TouchScrollBar_msb_hideDelayInMilliseconds, 2500));
        }
    }

    @Override
    float getHandleOffset() {
        return 0;
    }

    @Override
    float getIndicatorOffset() {
        return 0;
    }

    /**
     * Provides the ability to programmatically alter whether the scrollbar
     * should hide after a period of inactivity or not.
     * @param hide sets whether the bar should hide or not.
     *
     *             This method is experimental
     */
    public TouchScrollBar setAutoHide(Boolean hide) {
        if(!hide) {
            TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
            anim.setFillAfter(true);
            startAnimation(anim);
        }
        //This is not obeyed. If you print `hide` outside of this method it disagrees with what is
        //set here. I have no idea wtf is going on so if anyone could figure that out that'd be
        //great.
        this.hide = hide;
        return this;
    }

    /**
     * @param respondToTouchIfHidden Should the bar pop out and scroll if it is hidden?
     */
    public TouchScrollBar setRespondToTouchIfHidden(boolean respondToTouchIfHidden) {
        respondToTouch = respondToTouchIfHidden;
        return this;
    }
}
