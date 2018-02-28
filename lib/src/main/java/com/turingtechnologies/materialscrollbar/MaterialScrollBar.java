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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/*
 * Table Of Contents:
 *
 * I - Initial Setup
 * II - Abstraction for flavour differentiation
 * III - Customisation methods
 * IV - Misc Methods
 *
 * Outline for developers.
 *
 * The two flavours of the MaterialScrollBar are the DragScrollBar and the TouchScrollBar. They both
 * extend this class. Implementations which are unique to each flavour are implemented through
 * abstraction. The use of the T generic is used to maintain the identity of the subclass when
 * chaining settings (ie. So that DragScrollBar(...).setIndicator(...) will return dragScrollBar and
 * not MaterialScrollBar).
 *
 * The class can be instantiated only through XML.
 *
 * Scrolling logic is computed separably in ScrollingUtilities. A unique instance is made for each
 * instance of the bar.
 */
@SuppressWarnings({"unchecked", "unused"})
public abstract class MaterialScrollBar<T> extends RelativeLayout {

    //Component Views
    private View handleTrack;
    Handle handleThumb;
    Indicator indicator;

    //Characteristics
    int handleColor;
    int handleOffColor = Color.parseColor("#9c9c9c");
    protected boolean hidden = true;
    private int textColor = ContextCompat.getColor(getContext(), android.R.color.white);
    private boolean lightOnTouch;
    private TypedArray a; //XML attributes
    private Boolean rtl = false;
    boolean hiddenByUser = false;
    private boolean hiddenByNotEnoughElements = false;
    private float fastScrollSnapPercent = 0;

    //Associated Objects
    RecyclerView recyclerView;
    private int seekId = 0; //ID of the associated RecyclerView
    ScrollingUtilities scrollUtils = new ScrollingUtilities(this);
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<RecyclerView.OnScrollListener> listeners = new ArrayList<>();

    //Misc
    private OnLayoutChangeListener indicatorLayoutListener;
    private float previousScrollPercent = 0;
    Boolean draggableFromAnywhere = false;
    ArrayList<Runnable> onAttach = new ArrayList<>();
    private boolean attached = false;

    //CHAPTER I - INITIAL SETUP

    //Programmatic constructor
    MaterialScrollBar(Context context, RecyclerView recyclerView, boolean lightOnTouch) {
        super(context);

        this.recyclerView = recyclerView;

        addView(setUpHandleTrack(context)); //Adds the handle track
        addView(setUpHandle(context, lightOnTouch)); //Adds the handle

        setRightToLeft(Utils.isRightToLeft(context)); //Detects and applies the Right-To-Left status of the app

        generalSetup();
    }

    //Style-less XML Constructor
    MaterialScrollBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    //Styled XML Constructor
    MaterialScrollBar(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);

        setUpProps(context, attributeSet); //Discovers and applies some XML attributes

        addView(setUpHandleTrack(context)); //Adds the handle track
        addView(setUpHandle(context, a.getBoolean(R.styleable.MaterialScrollBar_msb_lightOnTouch, true))); //Adds the handle

        setRightToLeft(Utils.isRightToLeft(context)); //Detects and applies the Right-To-Left status of the app
    }

    //Unpacks XML attributes and ensures that no mandatory attributes are missing, then applies them.
    void setUpProps(Context context, AttributeSet attributes) {
        a = context.getTheme().obtainStyledAttributes(
                attributes,
                R.styleable.MaterialScrollBar,
                0, 0);

        if(!a.hasValue(R.styleable.MaterialScrollBar_msb_lightOnTouch)) {
            throw new IllegalStateException(
                    "You are missing the following required attributes from a scroll bar in your XML: lightOnTouch");
        }

        if(!isInEditMode()) {
            seekId = a.getResourceId(R.styleable.MaterialScrollBar_msb_recyclerView, 0); //Discovers and saves the ID of the recyclerView
        }
    }

    //Sets up bar.
    View setUpHandleTrack(Context context) {
        handleTrack = new View(context);
        LayoutParams lp = new RelativeLayout.LayoutParams(Utils.getDP(12, this), LayoutParams.MATCH_PARENT);
        lp.addRule(ALIGN_PARENT_RIGHT);
        handleTrack.setLayoutParams(lp);
        handleTrack.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        handleTrack.setAlpha(0.4F);
        return(handleTrack);
    }

    //Sets up handleThumb.
    Handle setUpHandle(Context context, Boolean lightOnTouch) {
        handleThumb = new Handle(context, getMode());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Utils.getDP(12, this),
                Utils.getDP(72, this));
        lp.addRule(ALIGN_PARENT_RIGHT);
        handleThumb.setLayoutParams(lp);

        this.lightOnTouch = lightOnTouch;
        int colorToSet;
        handleColor = fetchAccentColor(context);
        if(lightOnTouch) {
            colorToSet = Color.parseColor("#9c9c9c");
        } else {
            colorToSet = handleColor;
        }
        handleThumb.setBackgroundColor(colorToSet);
        return handleThumb;
    }

    //Implements optional attributes.
    void implementPreferences() {
        if(a.hasValue(R.styleable.MaterialScrollBar_msb_barColor)) {
            setBarColor(a.getColor(R.styleable.MaterialScrollBar_msb_barColor, 0));
        }
        if(a.hasValue(R.styleable.MaterialScrollBar_msb_handleColor)) {
            setHandleColor(a.getColor(R.styleable.MaterialScrollBar_msb_handleColor, 0));
        }
        if(a.hasValue(R.styleable.MaterialScrollBar_msb_handleOffColor)) {
            setHandleOffColor(a.getColor(R.styleable.MaterialScrollBar_msb_handleOffColor, 0));
        }
        if(a.hasValue(R.styleable.MaterialScrollBar_msb_textColor)) {
            setTextColor(a.getColor(R.styleable.MaterialScrollBar_msb_textColor, 0));
        }
        if(a.hasValue(R.styleable.MaterialScrollBar_msb_barThickness)) {
            setBarThickness(a.getDimensionPixelSize(R.styleable.MaterialScrollBar_msb_barThickness, 0));
        }
        if(a.hasValue(R.styleable.MaterialScrollBar_msb_rightToLeft)) {
            setRightToLeft(a.getBoolean(R.styleable.MaterialScrollBar_msb_rightToLeft, false));
        }
    }

    public T setRecyclerView(RecyclerView rv) {
        if(seekId != 0) {
            throw new IllegalStateException("There is already a recyclerView set by XML.");
        } else if(recyclerView != null) {
            throw new IllegalStateException("There is already a recyclerView set.");
        }
        recyclerView = rv;
        generalSetup();
        return (T)this;
    }

    //Waits for all of the views to be attached to the window and then implements general setup.
    //Waiting must occur so that the relevant recyclerview can be found.
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        attached = true;

        if(seekId != 0) {
            recyclerView = getRootView().findViewById(seekId);
            generalSetup();
        }
    }

    //General setup.
    private void generalSetup() {
        recyclerView.setVerticalScrollBarEnabled(false); // disable any existing scrollbars
        recyclerView.addOnScrollListener(new ScrollListener()); // lets us read when the recyclerView scrolls

        implementPreferences();

        implementFlavourPreferences();

        a.recycle();

        setTouchIntercept(); // catches touches on the bar

        identifySwipeRefreshParents();

        checkCustomScrolling();

        for(int i = 0; i < onAttach.size(); i++) {
            onAttach.get(i).run();
        }

        //Hides the view
        TranslateAnimation anim = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_SELF, rtl ? -getHideRatio() : getHideRatio(),
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        anim.setDuration(0);
        anim.setFillAfter(true);
        hidden = true;
        startAnimation(anim);
    }

    //Identifies any SwipeRefreshLayout parent so that it can be disabled and enabled during scrolling.
    void identifySwipeRefreshParents() {
        boolean cycle = true;
        ViewParent parent = getParent();
        if(parent != null) {
            while(cycle) {
                if(parent instanceof SwipeRefreshLayout) {
                    swipeRefreshLayout = (SwipeRefreshLayout)parent;
                    cycle = false;
                } else {
                    if(parent.getParent() == null) {
                        cycle = false;
                    } else {
                        parent = parent.getParent();
                    }
                }
            }
        }
    }

    boolean isScrollChangeLargeEnoughForFastScroll(float currentScrollPercent) {
        return Math.abs(currentScrollPercent - previousScrollPercent) > fastScrollSnapPercent;
    }

    //Checks each time the bar is laid out. If there are few enough view that
    //they all fit on the screen then the bar is hidden. If a view is added which doesn't fit on
    //the screen then the bar is unhidden.
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if(recyclerView == null && !isInEditMode()) {
            throw new RuntimeException("You need to set a recyclerView for the scroll bar, either in the XML or using setRecyclerView().");
        }

        if(!isInEditMode()) {
            scrollUtils.scrollHandleAndIndicator();
            if(hiddenByNotEnoughElements = (scrollUtils.getAvailableScrollHeight() <= 0)) {
                handleTrack.setVisibility(GONE);
                handleThumb.setVisibility(GONE);
            } else {
                handleTrack.setVisibility(VISIBLE);
                handleThumb.setVisibility(VISIBLE);
            }
        }
    }

    // Makes the bar render correctly for XML
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = Utils.getDP(12, this);
        int desiredHeight = 100;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if(widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if(widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if(heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if(heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    //CHAPTER II - ABSTRACTION FOR FLAVOUR DIFFERENTIATION

    abstract void setTouchIntercept();

    abstract int getMode();

    abstract float getHideRatio();

    abstract void onScroll();

    abstract boolean getHide();

    abstract void implementFlavourPreferences();

    abstract float getHandleOffset();

    abstract float getIndicatorOffset();

    //CHAPTER III - CUSTOMISATION METHODS

    private void checkCustomScrollingInterface() {
        if((recyclerView.getAdapter() instanceof  ICustomScroller)) {
            scrollUtils.customScroller = (ICustomScroller) recyclerView.getAdapter();
        }
    }

    /**
     * With very long lists, it may be advantageous to put a buffer on the drag bar to give the
     * user some time to actually see the scroll handle and the content. This will make the
     * bar less "smooth scrolling" and instead, snap to specific scroll percents. This could
     * be useful for the {@link DateAndTimeIndicator} style scrollbars, where you don't need to see
     * every single date available.
     *
     * @param snapPercent percentage that the fast scroll bar should snap to.
     */
    public T setFastScrollSnapPercent(float snapPercent) {
        fastScrollSnapPercent = snapPercent;
        return (T)this;
    }

    /**
     * The scrollBar should attempt to use dev provided scrolling logic and not default logic.
     *
     * The adapter must implement {@link ICustomScroller}.
     */
    private void checkCustomScrolling() {
        if(ViewCompat.isAttachedToWindow(this)) {
            checkCustomScrollingInterface();
        } else {
            addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    MaterialScrollBar.this.removeOnLayoutChangeListener(this);
                    checkCustomScrollingInterface();
                }
            });
        }
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar handleThumb.
     * @param color to set the handleThumb.
     */
    public T setHandleColor(final String color) {
        handleColor = Color.parseColor(color);
        setHandleColor();
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar handleThumb.
     * @param color to set the handleThumb.
     */
    public T setHandleColor(@ColorInt final int color) {
        handleColor = color;
        setHandleColor();
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar handleThumb.
     * @param colorResId to set the handleThumb.
     */
    public T setHandleColorRes(@ColorRes final int colorResId) {
        handleColor = ContextCompat.getColor(getContext(), colorResId);
        setHandleColor();
        return (T)this;
    }

    private void setHandleColor() {
        if(indicator != null) {
            ((GradientDrawable)indicator.getBackground()).setColor(handleColor);
        }
        if(!lightOnTouch) {
            handleThumb.setBackgroundColor(handleColor);
        }
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar handleThumb when unpressed. Only applies if lightOnTouch is true.
     * @param color to set the handleThumb when unpressed.
     */
    public T setHandleOffColor(final String color) {
        handleOffColor = Color.parseColor(color);
        if(lightOnTouch) {
            handleThumb.setBackgroundColor(handleOffColor);
        }
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar handleThumb when unpressed. Only applies if lightOnTouch is true.
     * @param color to set the handleThumb when unpressed.
     */
    public T setHandleOffColor(@ColorInt final int color) {
        handleOffColor = color;
        if(lightOnTouch) {
            handleThumb.setBackgroundColor(handleOffColor);
        }
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar handleThumb when unpressed. Only applies if lightOnTouch is true.
     * @param colorResId to set the handleThumb when unpressed.
     */
    public T setHandleOffColorRes(@ColorRes final int colorResId) {
        handleOffColor = ContextCompat.getColor(getContext(), colorResId);
        if(lightOnTouch) {
            handleThumb.setBackgroundColor(handleOffColor);
        }
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar.
     * @param color to set the bar.
     */
    public T setBarColor(final String color) {
        handleTrack.setBackgroundColor(Color.parseColor(color));
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar.
     * @param color to set the bar.
     */
    public T setBarColor(@ColorInt final int color) {
        handleTrack.setBackgroundColor(color);
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar.
     * @param colorResId to set the bar.
     */
    public T setBarColorRes(@ColorRes final int colorResId) {
        handleTrack.setBackgroundColor(ContextCompat.getColor(getContext(), colorResId));
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the text color of the indicator. Will do nothing if there is no section indicator.
     * @param color to set the text of the indicator.
     */
    public T setTextColor(@ColorInt final int color) {
        textColor = color;
        if(indicator != null) {
            indicator.setTextColor(textColor);
        }
        return(T)this;
    }


    /**
     * Provides the ability to programmatically set the text color of the indicator. Will do nothing if there is no section indicator.
     * @param colorResId to set the text of the indicator.
     */
    public T setTextColorRes(@ColorRes final int colorResId) {
        textColor = ContextCompat.getColor(getContext(), colorResId);
        if(indicator != null) {
            indicator.setTextColor(textColor);
        }
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the text color of the indicator. Will do nothing if there is no section indicator.
     * @param color to set the text of the indicator.
     */
    public T setTextColor(final String color) {
        textColor = Color.parseColor(color);
        if(indicator != null) {
            indicator.setTextColor(textColor);
        }
        return (T)this;
    }

    /**
     * Removes any indicator.
     */
    public T removeIndicator() {
        if(this.indicator != null) {
            this.indicator.removeAllViews();
        }
        this.indicator = null;
        return (T)this;
    }

    /**
     * Adds an indicator which accompanies this scroll bar.
     *
     * @param addSpaceSide Should space be put between the indicator and the bar or should they touch?
     */
    public T setIndicator(final Indicator indicator, final boolean addSpaceSide) {
        if(ViewCompat.isAttachedToWindow(this)) {
            setupIndicator(indicator, addSpaceSide);
        } else {
            removeOnLayoutChangeListener(indicatorLayoutListener);
            indicatorLayoutListener = (a,b,c,d,e,f,g,h,i) -> {
                setupIndicator(indicator, addSpaceSide);
                MaterialScrollBar.this.removeOnLayoutChangeListener(indicatorLayoutListener);
            };
            addOnLayoutChangeListener(indicatorLayoutListener);
        }
        return (T)this;
    }

    /**
     * Shared code for the above method.
     */
    private void setupIndicator(Indicator indicator, boolean addSpaceSide) {
        MaterialScrollBar.this.indicator = indicator;
        indicator.testAdapter(recyclerView.getAdapter());
        indicator.setRTL(rtl);
        indicator.linkToScrollBar(MaterialScrollBar.this, addSpaceSide);
        indicator.setTextColor(textColor);
    }

    /**
     * Allows the developer to set a custom bar thickness.
     * @param thickness The desired bar thickness.
     */
    public T setBarThickness(final int thickness) {
        if(!attached) {
            onAttach.add(() -> setBarThickness(thickness));
            return (T) this;
        }
        LayoutParams layoutParams = (LayoutParams) handleThumb.getLayoutParams();
        layoutParams.width = thickness;
        handleThumb.setLayoutParams(layoutParams);

        layoutParams = (LayoutParams) handleTrack.getLayoutParams();
        layoutParams.width = thickness;
        handleTrack.setLayoutParams(layoutParams);

        if(indicator != null) {
            indicator.setSizeCustom(thickness);
        }

        layoutParams = (LayoutParams) getLayoutParams();
        layoutParams.width = thickness;
        setLayoutParams(layoutParams);

        return (T)this;
    }

    /**
     * Hide or unhide the scrollBar.
     */
    public void setScrollBarHidden(boolean hidden) {
        hiddenByUser = hidden;
        if(hiddenByUser) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

    /**
     * Overrides the right-to-left settings for the scroll bar.
     */
    public void setRightToLeft(boolean rtl) {
        this.rtl = rtl;
        handleThumb.setRightToLeft(rtl);
        if(indicator != null) {
            indicator.setRTL(rtl);
            indicator.setLayoutParams(indicator.refreshMargins((LayoutParams) indicator.getLayoutParams()));
        }
    }

    /**
     * define if the scrollbar is draggable from anywhere or only from the handle itself
     */
    public void setDraggableFromAnywhere(boolean draggableFromAnywhere) {
        this.draggableFromAnywhere = draggableFromAnywhere;
    }

    /**
     * Add a listener for scroll events triggered by the scroll bar.
     */
    public void addScrollListener(RecyclerView.OnScrollListener scrollListener) {
        listeners.add(scrollListener);
    }

    /**
     * Remove a listener for scroll events triggered by the scroll bar.
     */
    public void removeScrollListener(RecyclerView.OnScrollListener scrollListener) {
        listeners.remove(scrollListener);
    }

    /**
     * Clear listeners for scroll events triggered by the scroll bar.
     */
    public void clearScrollListeners() {
        listeners.clear();
    }

    //CHAPTER IV - MISC METHODS

    //Fetch accent color.
    static int fetchAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    /**
     * Animates the bar out of view
     */
    void fadeOut() {
        if(!hidden) {
            TranslateAnimation anim = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_SELF, rtl ? -getHideRatio() : getHideRatio(),
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            anim.setDuration(150);
            anim.setFillAfter(true);
            hidden = true;
            startAnimation(anim);
            postDelayed(() -> handleThumb.expandHandle(), anim.getDuration() / 3);
        }
    }

    /**
     * Animates the bar into view
     */
    void fadeIn() {
        if(hidden && getHide() && !hiddenByUser) {
            hidden = false;
            TranslateAnimation anim = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, rtl ? -getHideRatio() : getHideRatio(),
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            anim.setDuration(150);
            anim.setFillAfter(true);
            startAnimation(anim);
            handleThumb.collapseHandle();
        }
    }

    protected void onDown(MotionEvent event) {
        if(indicator != null && indicator.getVisibility() == INVISIBLE && recyclerView.getAdapter() != null && !hiddenByNotEnoughElements) {
            indicator.setVisibility(VISIBLE);
            indicator.setAlpha(0F);
            indicator.animate().alpha(1F).setDuration(150).setListener(new AnimatorListenerAdapter() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    indicator.setAlpha(1F);
                }
            });
        }

        int top = handleThumb.getHeight() / 2;
        int bottom = recyclerView.getHeight() - Utils.getDP(72, recyclerView.getContext());
        float boundedY = Math.max(top, Math.min(bottom, event.getY() - getHandleOffset()));

        float currentScrollPercent = (boundedY - top) / (bottom - top);
        if(isScrollChangeLargeEnoughForFastScroll(currentScrollPercent) ||
                currentScrollPercent == 0 || currentScrollPercent == 1) {
            previousScrollPercent = currentScrollPercent;
            int dy = scrollUtils.scrollToPositionAtProgress(currentScrollPercent);
            scrollUtils.scrollHandleAndIndicator();
            if(dy != 0) {
                for(RecyclerView.OnScrollListener listener : listeners) {
                    listener.onScrolled(recyclerView, 0, dy);
                }
            }
        }

        if(lightOnTouch) {
            handleThumb.setBackgroundColor(handleColor);
        }
    }

    protected void onUp() {
        if(indicator != null && indicator.getVisibility() == VISIBLE) {
            indicator.animate().alpha(0F).setDuration(150).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    indicator.setVisibility(INVISIBLE);
                }
            });
        }

        if(lightOnTouch) {
            handleThumb.setBackgroundColor(handleOffColor);
        }
    }

    //Tests to ensure that the touch is on the handleThumb depending on the user preference
    protected boolean validTouch(MotionEvent event) {
        return draggableFromAnywhere || (event.getY() >= handleThumb.getY() - Utils.getDP(20, recyclerView.getContext()) && event.getY() <= handleThumb.getY() + handleThumb.getHeight());
    }

    class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            scrollUtils.scrollHandleAndIndicator();
            if(dy != 0) {
                onScroll();
            }

            //Disables any swipeRefreshLayout parent if the recyclerview is not at the top and enables it if it is.
            if(swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
                if(((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        }
    }

}