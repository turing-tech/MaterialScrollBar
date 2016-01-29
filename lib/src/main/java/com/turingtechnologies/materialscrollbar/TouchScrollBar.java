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
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class TouchScrollBar extends MaterialScrollBar{

    boolean hide = true;
    private int hideDuration = 2500;
    private Handler mUIHandler = new Handler(Looper.getMainLooper());

    private Runnable mFadeBar = new Runnable() {

        @Override
        public void run() {
            fadeOut();
        }
    };

    public TouchScrollBar(Context context, RecyclerView recyclerView, boolean lightOnTouch){
        super(context, recyclerView, lightOnTouch);
    }

    public TouchScrollBar setHideDuration(int duration){
        hideDuration = duration;
        return this;
    }

    @Override
    void setTouchIntercept() {
        setOnTouchListener(new OnTouchListener() {
            public int[] posWorkspace = new int[2];

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!totallyHidden) {
                    if (event.getAction() != MotionEvent.ACTION_UP) {
                        hold = true;

                        final MaterialScrollBar self = getMe();
                        final float handleHeight = handle.getHeight();
                        final float handleMiddle = handleHeight / 2;
                        final float handleOffset = (handleHeight * 3 / 2);
                        final float eventY = event.getRawY();
                        final float myY = self.getY();
                        final float myHeight = self.getHeight();
                        final float eventYOffset = eventY - handleOffset;

                        final RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                        if (manager instanceof GridLayoutManager && ((GridLayoutManager) manager).getSpanCount() != 1) {
                            final GridLayoutManager gridManager = (GridLayoutManager) manager;
                            if(eventYOffset >= myY && eventY - handleHeight / 2 <= getBottom() + myY) {
                                int itemsInWindow = recyclerView.getHeight() / recyclerView.getChildAt(0).getHeight() * gridManager.getSpanCount();

                                int numItemsInList = recyclerView.getAdapter().getItemCount();
                                int numScrollableSectionsInList = numItemsInList - itemsInWindow;
                                getMe().getLocationOnScreen(posWorkspace);
                                gridManager.scrollToPositionWithOffset((int) (((eventY - posWorkspace[1]) / (myHeight - handleOffset)) * numScrollableSectionsInList), 0);
                                float finalY = Math.min(myHeight - handleHeight, eventYOffset - myY);
                                handle.setY(finalY);
                                scrollListener.calculateScrollProgress(recyclerView);
                                if (indicator != null && indicator.getVisibility() == VISIBLE) {
                                    indicator.setScroll(eventY - handleMiddle - Utils.getDP(40, self));
                                }
                            }
                        } else {
                            if(eventYOffset >= myY && eventY - handleMiddle <= getBottom() + myY){
                                recyclerView.scrollToPosition((int) (recyclerView.getAdapter().getItemCount() * ((eventYOffset - myY) / (myHeight - handleHeight))));
                                float finalY = Math.min(myHeight - handleHeight, eventYOffset - myY);
                                handle.setY(finalY);
                                scrollListener.calculateScrollProgress(recyclerView);
                                if(indicator != null && indicator.getVisibility() == VISIBLE){
                                    indicator.setScroll(eventY - handleMiddle - Utils.getDP(40, self));
                                }
                            }
                            recyclerView.onScrolled(0, 0);
                            if(indicator != null && indicator.getVisibility() == INVISIBLE){
                                indicator.setVisibility(VISIBLE);
                                getMe().getLocationOnScreen(posWorkspace);
                                indicator.setScroll(scrollListener.calculateScrollProgress(recyclerView) * (myHeight - handleHeight) + posWorkspace[1]);
                            }
                        }

                        if(lightOnTouch){
                            handle.setBackgroundColor(handleColour);
                        }

                        mUIHandler.removeCallbacks(mFadeBar);
                        fadeIn();
                    } else {
                        hold = false;
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
                            mUIHandler.removeCallbacks(mFadeBar);
                            mUIHandler.postDelayed(mFadeBar, hideDuration);
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
        mUIHandler.removeCallbacks(mFadeBar);
        mUIHandler.postDelayed(mFadeBar, hideDuration);
        fadeIn();
    }

    @Override
    boolean getHide() {
        return hide;
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
