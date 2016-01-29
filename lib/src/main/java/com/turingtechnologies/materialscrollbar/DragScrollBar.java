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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

public class DragScrollBar extends MaterialScrollBar{

    public DragScrollBar(Context context, RecyclerView recyclerView, boolean lightOnTouch){
        super(context, recyclerView, lightOnTouch);
    }

    @Override
    void setTouchIntercept() {
        handle.setOnTouchListener(new OnTouchListener() {
            public int[] posWorkspace = new int[2];

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!totallyHidden) {
                    if (event.getAction() != MotionEvent.ACTION_UP) {
                        hold = true;

                        final MaterialScrollBar self = getMe();
                        final float handleHeight = handle.getHeight();
                        final float handleMiddle = handleHeight / 2;
                        final float handleOffset = (handleHeight * 3 / 2);
                        final float eventY = event.getRawY();
                        final float myY = self.getY();
                        final float myHeight = getHeight();
                        final float eventYOffset = eventY - handleOffset;

                        if (indicator != null && indicator.getVisibility() == INVISIBLE) {
                            indicator.setVisibility(VISIBLE);
                            indicator.setScroll(scrollListener.calculateScrollProgress(recyclerView) * (myHeight - handleHeight));
                        }

                        final RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                        if (manager instanceof GridLayoutManager && ((GridLayoutManager) manager).getSpanCount() != 1) {
                            final GridLayoutManager gridManager = (GridLayoutManager) manager;
                            if(eventY - handleOffset >= myY && eventY - handleMiddle <= getBottom() + myY) {
                                int itemsInWindow = recyclerView.getHeight() / recyclerView.getChildAt(0).getHeight() * gridManager.getSpanCount();

                                int numItemsInList = recyclerView.getAdapter().getItemCount();
                                int numScrollableSectionsInList = numItemsInList - itemsInWindow;
                                self.getLocationOnScreen(posWorkspace);
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
                        }
                        recyclerView.onScrolled(0, 0);

                        if (lightOnTouch) {
                            handle.setBackgroundColor(handleColour);
                        }

                        fadeIn();
                    } else {
                        hold = false;
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
        });
    }

    @Override
    int getMode() {
        return 0;
    }

    @Override
    float getHideRatio() {
        return .35F;
    }

    @Override
    void onScroll() {}

    @Override
    boolean getHide() {
        return true;
    }


}
