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
    int handleColour;
    int handleOffColour = Color.parseColor("#9c9c9c");
    protected boolean hidden = true;
    private int textColour = ContextCompat.getColor(getContext(), android.R.color.white);
    boolean lightOnTouch;
    private TypedArray a; //XML attributes
    private Boolean rtl = false;
    boolean hiddenByUser = false;
    private float fastScrollSnapPercent = 0;

    //Associated Objects
    RecyclerView recyclerView;
    private int seekId = 0; //ID of the associated RecyclerView
    ScrollingUtilities scrollUtils = new ScrollingUtilities(this);
    SwipeRefreshLayout swipeRefreshLayout;

    //Misc
    private OnLayoutChangeListener indicatorLayoutListener;
    private float previousScrollPercent = 0;
    Boolean draggableFromAnywhere = false;
    ArrayList<Runnable> onAttach = new ArrayList<>();
    private boolean attached = false;

    //CHAPTER I - INITIAL SETUP

    //Programmatic constructor
    MaterialScrollBar(Context context, RecyclerView recyclerView, boolean lightOnTouch){
        super(context);

        this.recyclerView = recyclerView;

        addView(setUpHandleTrack(context)); //Adds the handle track
        addView(setUpHandle(context, lightOnTouch)); //Adds the handle

        setRightToLeft(Utils.isRightToLeft(context)); //Detects and applies the Right-To-Left status of the app

        generalSetup();
    }

    //Style-less XML Constructor
    MaterialScrollBar(Context context, AttributeSet attributeSet){
        this(context, attributeSet, 0);
    }

    //Styled XML Constructor
    MaterialScrollBar(Context context, AttributeSet attributeSet, int defStyle){
        super(context, attributeSet, defStyle);

        setUpProps(context, attributeSet); //Discovers and applies some XML attributes

        addView(setUpHandleTrack(context)); //Adds the handle track
        addView(setUpHandle(context, a.getBoolean(R.styleable.MaterialScrollBar_msb_lightOnTouch, true))); //Adds the handle

        setRightToLeft(Utils.isRightToLeft(context)); //Detects and applies the Right-To-Left status of the app
    }

    //Unpacks XML attributes and ensures that no mandatory attributes are missing, then applies them.
    void setUpProps(Context context, AttributeSet attributes){
        a = context.getTheme().obtainStyledAttributes(
                attributes,
                R.styleable.MaterialScrollBar,
                0, 0);

        if(!a.hasValue(R.styleable.MaterialScrollBar_msb_lightOnTouch)){
            throw new IllegalStateException(
                    "You are missing the following required attributes from a scroll bar in your XML: lightOnTouch");
        }

        if(!isInEditMode()){
            seekId = a.getResourceId(R.styleable.MaterialScrollBar_msb_recyclerView, 0); //Discovers and saves the ID of the recyclerView
        }
    }

    //Sets up bar.
    View setUpHandleTrack(Context context){
        handleTrack = new View(context);
        LayoutParams lp = new RelativeLayout.LayoutParams(Utils.getDP(12, this), LayoutParams.MATCH_PARENT);
        lp.addRule(ALIGN_PARENT_RIGHT);
        handleTrack.setLayoutParams(lp);
        handleTrack.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        ViewCompat.setAlpha(handleTrack, 0.4F);
        return(handleTrack);
    }

    //Sets up handleThumb.
    Handle setUpHandle(Context context, Boolean lightOnTouch){
        handleThumb = new Handle(context, getMode());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Utils.getDP(12, this),
                Utils.getDP(72, this));
        lp.addRule(ALIGN_PARENT_RIGHT);
        handleThumb.setLayoutParams(lp);

        this.lightOnTouch = lightOnTouch;
        int colorToSet;
        handleColour = fetchAccentColour(context);
        if(lightOnTouch){
            colorToSet = Color.parseColor("#9c9c9c");
        } else {
            colorToSet = handleColour;
        }
        handleThumb.setBackgroundColor(colorToSet);
        return handleThumb;
    }

    //Implements optional attributes.
    void implementPreferences(){
        if(a.hasValue(R.styleable.MaterialScrollBar_msb_barColor)){
            setBarColour(a.getColor(R.styleable.MaterialScrollBar_msb_barColor, 0));
        }
        if(a.hasValue(R.styleable.MaterialScrollBar_msb_handleColor)){
            setHandleColour(a.getColor(R.styleable.MaterialScrollBar_msb_handleColor, 0));
        }
        if(a.hasValue(R.styleable.MaterialScrollBar_msb_handleOffColor)){
            setHandleOffColour(a.getColor(R.styleable.MaterialScrollBar_msb_handleOffColor, 0));
        }
        if(a.hasValue(R.styleable.MaterialScrollBar_msb_textColor)){
            setTextColour(a.getColor(R.styleable.MaterialScrollBar_msb_textColor, 0));
        }
        if(a.hasValue(R.styleable.MaterialScrollBar_msb_barThickness)){
            setBarThickness(a.getDimensionPixelSize(R.styleable.MaterialScrollBar_msb_barThickness, 0));
        }
        if(a.hasValue(R.styleable.MaterialScrollBar_msb_rightToLeft)){
            setRightToLeft(a.getBoolean(R.styleable.MaterialScrollBar_msb_rightToLeft, false));
        }
    }

    public T setRecyclerView(RecyclerView rv){
        if(seekId != 0){
            throw new IllegalStateException("There is already a recyclerView set by XML.");
        } else if (recyclerView != null){
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

        if(seekId != 0){
            recyclerView = getRootView().findViewById(seekId);
            generalSetup();
        }
    }

    //General setup.
    private void generalSetup(){
        recyclerView.setVerticalScrollBarEnabled(false); // disable any existing scrollbars
        recyclerView.addOnScrollListener(new scrollListener()); // lets us read when the recyclerView scrolls

        implementPreferences();

        implementFlavourPreferences(a);

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
    void identifySwipeRefreshParents(){
        boolean cycle = true;
        ViewParent parent = getParent();
        if(parent != null){
            while(cycle){
                if(parent instanceof SwipeRefreshLayout){
                    swipeRefreshLayout = (SwipeRefreshLayout)parent;
                    cycle = false;
                } else {
                    if(parent.getParent() == null){
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

    boolean sizeUnchecked = true;

    //Checks each time the bar is laid out. If there are few enough view that
    //they all fit on the screen then the bar is hidden. If a view is added which doesn't fit on
    //the screen then the bar is unhidden.
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if(recyclerView == null && !isInEditMode()){
            throw new RuntimeException("You need to set a recyclerView for the scroll bar, either in the XML or using setRecyclerView().");
        }

        if(sizeUnchecked && !isInEditMode()){
            scrollUtils.getCurScrollState();
            if(scrollUtils.getAvailableScrollHeight() <= 0){
                handleTrack.setVisibility(GONE);
                handleThumb.setVisibility(GONE);
            } else {
                handleTrack.setVisibility(VISIBLE);
                handleThumb.setVisibility(VISIBLE);
                sizeUnchecked = false;
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
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
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

    abstract void implementFlavourPreferences(TypedArray a);

    abstract float getHandleOffset();

    abstract float getIndicatorOffset();

    //CHAPTER III - CUSTOMISATION METHODS

    private void checkCustomScrollingInterface(){
        if((recyclerView.getAdapter() instanceof  ICustomScroller)){
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
    private void checkCustomScrolling(){
        if (ViewCompat.isAttachedToWindow(this))
            checkCustomScrollingInterface();
        else
            addOnLayoutChangeListener(new OnLayoutChangeListener()
            {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
                {
                    MaterialScrollBar.this.removeOnLayoutChangeListener(this);
                    checkCustomScrollingInterface();
                }
            });
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar handleThumb.
     * @param color to set the handleThumb.
     */
    public T setHandleColour(final String color){
        handleColour = Color.parseColor(color);
        setHandleColour();
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar handleThumb.
     * @param color to set the handleThumb.
     */
    public T setHandleColour(@ColorInt final int color){
        handleColour = color;
        setHandleColour();
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar handleThumb.
     * @param colorResId to set the handleThumb.
     */
    public T setHandleColourRes(@ColorRes final int colorResId){
        handleColour = ContextCompat.getColor(getContext(), colorResId);
        setHandleColour();
        return (T)this;
    }

    private void setHandleColour(){
        if(indicator != null){
            ((GradientDrawable)indicator.getBackground()).setColor(handleColour);
        }
        if(!lightOnTouch){
            handleThumb.setBackgroundColor(handleColour);
        }
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar handleThumb when unpressed. Only applies if lightOnTouch is true.
     * @param color to set the handleThumb when unpressed.
     */
    public T setHandleOffColour(final String color){
        handleOffColour = Color.parseColor(color);
        if(lightOnTouch){
            handleThumb.setBackgroundColor(handleOffColour);
        }
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar handleThumb when unpressed. Only applies if lightOnTouch is true.
     * @param color to set the handleThumb when unpressed.
     */
    public T setHandleOffColour(@ColorInt final int color){
        handleOffColour = color;
        if(lightOnTouch){
            handleThumb.setBackgroundColor(handleOffColour);
        }
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar handleThumb when unpressed. Only applies if lightOnTouch is true.
     * @param colorResId to set the handleThumb when unpressed.
     */
    public T setHandleOffColourRes(@ColorRes final int colorResId){
        handleOffColour = ContextCompat.getColor(getContext(), colorResId);
        if(lightOnTouch){
            handleThumb.setBackgroundColor(handleOffColour);
        }
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar.
     * @param color to set the bar.
     */
    public T setBarColour(final String color){
        handleTrack.setBackgroundColor(Color.parseColor(color));
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar.
     * @param color to set the bar.
     */
    public T setBarColour(@ColorInt final int color){
        handleTrack.setBackgroundColor(color);
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the color of the scrollbar.
     * @param colorResId to set the bar.
     */
    public T setBarColourRes(@ColorRes final int colorResId){
        handleTrack.setBackgroundColor(ContextCompat.getColor(getContext(), colorResId));
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the text color of the indicator. Will do nothing if there is no section indicator.
     * @param color to set the text of the indicator.
     */
    public T setTextColour(@ColorInt final int color){
        textColour = color;
        if(indicator != null){
            indicator.setTextColour(textColour);
        }
        return(T)this;
    }


    /**
     * Provides the ability to programmatically set the text color of the indicator. Will do nothing if there is no section indicator.
     * @param colorResId to set the text of the indicator.
     */
    public T setTextColourRes(@ColorRes final int colorResId){
        textColour = ContextCompat.getColor(getContext(), colorResId);
        if(indicator != null){
            indicator.setTextColour(textColour);
        }
        return (T)this;
    }

    /**
     * Provides the ability to programmatically set the text color of the indicator. Will do nothing if there is no section indicator.
     * @param color to set the text of the indicator.
     */
    public T setTextColour(final String color){
        textColour = Color.parseColor(color);
        if(indicator != null){
            indicator.setTextColour(textColour);
        }
        return (T)this;
    }

    /**
     * Removes any indicator.
     */
    public T removeIndicator(){
        if(this.indicator != null){
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
        if(ViewCompat.isAttachedToWindow(this)){
            setupIndicator(indicator, addSpaceSide);
        } else {
            removeOnLayoutChangeListener(indicatorLayoutListener);
            indicatorLayoutListener = new OnLayoutChangeListener()
            {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom){
                    setupIndicator(indicator, addSpaceSide);
                    MaterialScrollBar.this.removeOnLayoutChangeListener(this);
                }
            };
            addOnLayoutChangeListener(indicatorLayoutListener);
        }
        return (T)this;
    }

    /**
     * Shared code for the above method.
     */
    private void setupIndicator(Indicator indicator, boolean addSpaceSide){
        MaterialScrollBar.this.indicator = indicator;
        indicator.testAdapter(recyclerView.getAdapter());
        indicator.setRTL(rtl);
        indicator.linkToScrollBar(MaterialScrollBar.this, addSpaceSide);
        indicator.setTextColour(textColour);
    }

    /**
     * Allows the developer to set a custom bar thickness.
     * @param thickness The desired bar thickness.
     */
    public T setBarThickness(final int thickness){
        if(!attached) {
            onAttach.add(new Runnable() {
                @Override
                public void run() {
                    setBarThickness(thickness);
                }
            });
        }
        LayoutParams layoutParams = (LayoutParams) handleThumb.getLayoutParams();
        layoutParams.width = thickness;
        handleThumb.setLayoutParams(layoutParams);

        layoutParams = (LayoutParams) handleTrack.getLayoutParams();
        layoutParams.width = thickness;
        handleTrack.setLayoutParams(layoutParams);

        if(indicator != null){
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
    public void setScrollBarHidden(boolean hidden){
        hiddenByUser = hidden;
        if(hiddenByUser){
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

    /**
     * Overrides the right-to-left settings for the scroll bar.
     */
    public void setRightToLeft(boolean rtl){
        this.rtl = rtl;
        handleThumb.setRightToLeft(rtl);
        if(indicator != null){
            indicator.setRTL(rtl);
            indicator.setLayoutParams(indicator.refreshMargins((LayoutParams) indicator.getLayoutParams()));
        }
    }

    /**
     * define if the scrollbar is draggable from anywhere or only from the handle itself
     */
    public void setDraggableFromAnywhere(boolean draggableFromAnywhere){
        this.draggableFromAnywhere = draggableFromAnywhere;
    }

    //CHAPTER IV - MISC METHODS

    //Fetch accent color.
    static int fetchAccentColour(Context context) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    /**
     * Animates the bar out of view
     */
    void fadeOut(){
        if(!hidden){
            TranslateAnimation anim = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_SELF, rtl ? -getHideRatio() : getHideRatio(),
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            anim.setDuration(150);
            anim.setFillAfter(true);
            hidden = true;
            startAnimation(anim);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    handleThumb.expandHandle();
                }
            }, anim.getDuration() / 3);
        }
    }

    /**
     * Animates the bar into view
     */
    void fadeIn(){
        if(hidden && getHide() && !hiddenByUser){
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

    protected void onDown(MotionEvent event){
        if (indicator != null && indicator.getVisibility() == INVISIBLE && recyclerView.getAdapter() != null) {
            indicator.setVisibility(VISIBLE);
            if(Build.VERSION.SDK_INT >= 12){
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
        }

        int top = handleThumb.getHeight() / 2;
        int bottom = recyclerView.getHeight() - Utils.getDP(72, recyclerView.getContext());
        float boundedY = Math.max(top, Math.min(bottom, event.getY() - getHandleOffset()));

        float currentScrollPercent = (boundedY - top) / (bottom - top);
        if (isScrollChangeLargeEnoughForFastScroll(currentScrollPercent) ||
                currentScrollPercent == 0 || currentScrollPercent == 1) {
            previousScrollPercent = currentScrollPercent;
            scrollUtils.scrollToPositionAtProgress(currentScrollPercent);
            scrollUtils.scrollHandleAndIndicator();
            recyclerView.onScrolled(0, 0);
        }

        if (lightOnTouch) {
            handleThumb.setBackgroundColor(handleColour);
        }
    }

    protected void onUp(){
        if (indicator != null && indicator.getVisibility() == VISIBLE) {
            if (Build.VERSION.SDK_INT <= 12) {
                indicator.clearAnimation();
            }
            if(Build.VERSION.SDK_INT >= 12){
                indicator.animate().alpha(0F).setDuration(150).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        indicator.setVisibility(INVISIBLE);
                    }
                });
            } else {
                indicator.setVisibility(INVISIBLE);
            }
        }

        if (lightOnTouch) {
            handleThumb.setBackgroundColor(handleOffColour);
        }
    }

    //Tests to ensure that the touch is on the handleThumb depending on the user preference
    protected boolean validTouch(MotionEvent event){
        return draggableFromAnywhere || (event.getY() >= ViewCompat.getY(handleThumb) - Utils.getDP(20, recyclerView.getContext()) && event.getY() <= ViewCompat.getY(handleThumb) + handleThumb.getHeight());
    }

    class scrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            scrollUtils.scrollHandleAndIndicator();
            if(dy != 0){
                onScroll();
            }

            //Disables any swipeRefreshLayout parent if the recyclerview is not at the top and enables it if it is.
            if(swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()){
                if(((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0){
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        }
    }

}