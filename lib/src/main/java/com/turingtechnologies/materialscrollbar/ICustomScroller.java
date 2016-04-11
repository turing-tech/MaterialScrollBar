package com.turingtechnologies.materialscrollbar;

public interface ICustomScroller {

    /**
     * @param index The index of the relevant element.
     * @return An integer in pixels representing the depth of the item within the recyclerView.
     * Usually just the sum of the height of all elements which appear above it in the recyclerView.
     */
    int getDepthForItem(int index);

    /**
     * @return An integer representing the index of the item which should be scrolled to when the
     * user clicks at the specified length down the bar. For example, if "progress" is 0.5F then you
     * should return the index of the item which is half-way down the recyclerView.
     */
    int getItemIndexForScroll(float progress);

    /**
     * @return The sum of the heights of all the views in the recyclerView.
     */
    int getTotalDepth();

}
