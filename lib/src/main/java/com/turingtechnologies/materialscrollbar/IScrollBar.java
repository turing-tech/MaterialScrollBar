package com.turingtechnologies.materialscrollbar;

import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;

/**
 * Created by flisar on 10.02.2017.
 */

public interface IScrollBar<T>
{
    T setRecyclerView(RecyclerView rv);

    void setEnabled(boolean enabled);

    T setHandleColour(@ColorInt int colour);

    T setHandleOffColour(String colour);
    T setHandleOffColour(@ColorInt int colour);
    T setHandleOffColourRes(@ColorRes int colourResId);
    T setBarColour(String colour);
    T setBarColour(@ColorInt int colour);
    T setBarColourRes(@ColorRes int colourResId);
    T setTextColour(@ColorInt int colour);
    T setTextColourRes(@ColorRes int colourResId);
    T setTextColour(String colour);
    T removeIndicator();
    T setIndicator(final Indicator indicator, final boolean addSpaceSide);
}
