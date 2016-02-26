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

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

/*
 * Lots of complicated maths taken mostly from Google. Abandon all hope, ye who enter here.
 */
class ScrollingUtilities {

    MaterialScrollBar materialScrollBar;

    ScrollingUtilities(MaterialScrollBar msb){
        materialScrollBar = msb;
    }

    private ScrollPositionState scrollPosState = new ScrollPositionState();

    public class ScrollPositionState {
        // The index of the first visible row
        public int rowIndex;
        // The offset of the first visible row
        public int rowTopOffset;
        // The height of a given row (they are currently all the same height)
        public int rowHeight;
    }

    protected void scrollHandleAndIndicator(){
        getCurScrollState();
        int scrollY = materialScrollBar.getPaddingTop() + (scrollPosState.rowIndex * scrollPosState.rowHeight) - scrollPosState.rowTopOffset;
        int scrollBarY = (int) (((float) scrollY / getAvailableScrollHeight()) * getAvailableScrollBarHeight());
        ViewHelper.setY(materialScrollBar.handle, scrollBarY);
        if(materialScrollBar.indicator != null){
            materialScrollBar.indicator.setScroll(scrollBarY, materialScrollBar.programmatic);
            materialScrollBar.indicator.textView.setText(materialScrollBar.indicator.getTextElement(scrollPosState.rowIndex, materialScrollBar.recyclerView.getAdapter()));
        }
    }

    int getRowCount(){
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
    protected int getAvailableScrollBarHeight() {
        int visibleHeight = materialScrollBar.getHeight();
        return visibleHeight - materialScrollBar.handle.getHeight();
    }

    public void scrollToPositionAtProgress(float touchFraction) {
        int spanCount = 1;
        if(materialScrollBar.recyclerView.getLayoutManager() instanceof GridLayoutManager){
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
    }

    protected int getAvailableScrollHeight() {
        int visibleHeight = materialScrollBar.getHeight();
        int scrollHeight = materialScrollBar.getPaddingTop() + getRowCount() * scrollPosState.rowHeight + materialScrollBar.getPaddingBottom();
        return scrollHeight - visibleHeight;
    }

    public void getCurScrollState() {
        scrollPosState.rowIndex = -1;
        scrollPosState.rowTopOffset = -1;
        scrollPosState.rowHeight = -1;

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
        scrollPosState.rowTopOffset = materialScrollBar.recyclerView.getLayoutManager().getDecoratedTop(child);
        scrollPosState.rowHeight = child.getHeight();
    }

}
