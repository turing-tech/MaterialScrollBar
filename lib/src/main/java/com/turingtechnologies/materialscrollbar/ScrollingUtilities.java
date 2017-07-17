/*
 *  Copyright © 2016-2017, Turing Technologies, an unincorporated organisation of Wynne Plaga
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

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

/*
 * Lots of complicated maths taken mostly from Google. Abandon all hope, ye who enter here.
 */
class ScrollingUtilities {

    private MaterialScrollBar materialScrollBar;

    ScrollingUtilities(MaterialScrollBar msb){
        materialScrollBar = msb;
    }

    ICustomScroller customScroller;

    private ScrollPositionState scrollPosState = new ScrollPositionState();

    private int constant;

    private LinearLayoutManager layoutManager;

    private class ScrollPositionState {
        // The index of the first visible row
        private int rowIndex;
        // The offset of the first visible row
        private int rowTopOffset;
        // The height of a given row (they are currently all the same height)
        private int rowHeight;
    }

    void scrollHandleAndIndicator(){
        int scrollBarY;
        getCurScrollState();
        if(customScroller != null){
            constant = customScroller.getDepthForItem(materialScrollBar.recyclerView.getChildAdapterPosition(materialScrollBar.recyclerView.getChildAt(0)));
        } else {
            constant = scrollPosState.rowHeight * scrollPosState.rowIndex;
        }
        scrollBarY = (int) getScrollPosition();
        ViewCompat.setY(materialScrollBar.handleThumb, scrollBarY);
        materialScrollBar.handleThumb.invalidate();
        if(materialScrollBar.indicator != null){
            int element;
            if (materialScrollBar.recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                element = scrollPosState.rowIndex * ((GridLayoutManager)materialScrollBar.recyclerView.getLayoutManager()).getSpanCount();
            } else {
                element = scrollPosState.rowIndex;
            }
            materialScrollBar.indicator.setText(element);

            materialScrollBar.indicator.setScroll(scrollBarY + materialScrollBar.getTop());
        }
    }

    private float getScrollPosition(){
        getCurScrollState();
        int scrollY = materialScrollBar.getPaddingTop() + constant - scrollPosState.rowTopOffset;
        return ((float) scrollY / getAvailableScrollHeight()) * getAvailableScrollBarHeight();
    }

    private int getRowCount(){
        int rowCount = materialScrollBar.recyclerView.getLayoutManager().getItemCount();
        if (materialScrollBar.recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            int spanCount = ((GridLayoutManager) materialScrollBar.recyclerView.getLayoutManager()).getSpanCount();
            rowCount = (int) Math.ceil((double) rowCount / spanCount);
        }
        return rowCount;
    }

    /**
     * Returns the available scroll bar height:
     * AvailableScrollBarHeight = Total height of the visible view - thumb height
     */
    int getAvailableScrollBarHeight() {
        return materialScrollBar.getHeight() - materialScrollBar.handleThumb.getHeight();
    }

    void scrollToPositionAtProgress(float touchFraction) {
        if(customScroller == null) {
            int spanCount = 1;
            if (materialScrollBar.recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                spanCount = ((GridLayoutManager) materialScrollBar.recyclerView.getLayoutManager()).getSpanCount();
            }

            // Stop the scroller if it is scrolling
            materialScrollBar.recyclerView.stopScroll();

            getCurScrollState();

            //The exact position of our desired item
            int exactItemPos = (int) (getAvailableScrollHeight() * touchFraction);

            //Scroll to the desired item. The offset used here is kind of hard to explain.
            //If the position we wish to scroll to is, say, position 10.5, we scroll to position 10,
            //and then offset by 0.5 * rowHeight. This is how we achieve smooth scrolling.
            LinearLayoutManager layoutManager = ((LinearLayoutManager) materialScrollBar.recyclerView.getLayoutManager());
            layoutManager.scrollToPositionWithOffset(spanCount * exactItemPos / scrollPosState.rowHeight,
                    -(exactItemPos % scrollPosState.rowHeight));
        } else {
            if(layoutManager == null){
                layoutManager = ((LinearLayoutManager) materialScrollBar.recyclerView.getLayoutManager());
            }
            int position = customScroller.getItemIndexForScroll(touchFraction);
            int offset = (int) (customScroller.getDepthForItem(position) - touchFraction * getAvailableScrollHeight());
            layoutManager.scrollToPositionWithOffset(position, offset);
        }
    }

    int getAvailableScrollHeight() {
        int visibleHeight = materialScrollBar.getHeight();
        int scrollHeight;
        if(customScroller != null){
            scrollHeight = materialScrollBar.getPaddingTop() + customScroller.getTotalDepth() + materialScrollBar.getPaddingBottom();
        } else {
            scrollHeight = materialScrollBar.getPaddingTop() + getRowCount() * scrollPosState.rowHeight + materialScrollBar.getPaddingBottom();
        }
        return scrollHeight - visibleHeight;
    }

    void getCurScrollState() {
        scrollPosState.rowIndex = -1;
        scrollPosState.rowTopOffset = -1;
        scrollPosState.rowHeight = -1;

        if  (materialScrollBar.recyclerView.getAdapter() == null) {
            Log.e("MaterialScrollBarLib", "The adapter for your recyclerView has not been set; " +
                    "skipping layout.");
            return;
        }

        int itemCount = materialScrollBar.recyclerView.getAdapter().getItemCount();

        // Return early if there are no items
        if (itemCount == 0) {
            return;
        }
        View child = materialScrollBar.recyclerView.getChildAt(0);

        scrollPosState.rowIndex = materialScrollBar.recyclerView.getChildAdapterPosition(child);
        if (materialScrollBar.recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            scrollPosState.rowIndex = scrollPosState.rowIndex / ((GridLayoutManager) materialScrollBar.recyclerView.getLayoutManager()).getSpanCount();
        }
        if(child == null){
            scrollPosState.rowTopOffset = 0;
            scrollPosState.rowHeight = 0;
        } else {
            scrollPosState.rowTopOffset = materialScrollBar.recyclerView.getLayoutManager().getDecoratedTop(child);
            scrollPosState.rowHeight = child.getHeight();
        }
    }

}
