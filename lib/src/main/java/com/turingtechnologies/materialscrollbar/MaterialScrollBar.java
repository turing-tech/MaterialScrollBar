/*
 * Copyright © 2015, Turing Technologies, an unincorporated organisation of Wynne Plaga
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.turingtechnologies.materialscrollbar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.nineoldandroids.view.ViewHelper;

public class MaterialScrollBar extends RelativeLayout {

    private View background;
    private View handle;
    protected int handleColour;
    protected Activity a;
    private ScrollListener scrollListener = new ScrollListener(this);
    private boolean hidden = true;
    private int hideDuration = 2500;
    private boolean hide = true;
    protected RecyclerView recyclerView;
    private SectionIndicator sectionIndicator;

    class BarFade extends Thread {

        MaterialScrollBar materialScrollBar;

        BarFade(MaterialScrollBar msb){
            materialScrollBar = msb;
        }

        long time = 0;
        boolean run = false;

        @Override
        public void run() {
            try{
                while(true){
                    if(time <= System.currentTimeMillis() && run){
                        run = false;
                        materialScrollBar.a.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
                                anim.setDuration(500);
                                anim.setFillAfter(true);
                                hidden = true;
                                materialScrollBar.startAnimation(anim);
                            }
                        });
                    }
                    Thread.sleep(100);
                }
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    BarFade fade;

    public MaterialScrollBar(Context context, AttributeSet attributeSet){
        super(context, attributeSet);

        if(!isInEditMode()){
            a = (Activity) context;
        }
        background = new View(context);
        LayoutParams lp = new RelativeLayout.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()), LayoutParams.MATCH_PARENT);
        lp.addRule(ALIGN_PARENT_RIGHT);
        background.setLayoutParams(lp);
        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attributeSet, R.styleable.MaterialScrollBar, 0, 0);
        background.setBackgroundColor(attributes.getColor(R.styleable.MaterialScrollBar_barColour, getResources().getColor(android.R.color.darker_gray)));
        com.nineoldandroids.view.ViewHelper.setAlpha(background, 0.4F);

        handle = new View(context);
        lp = new RelativeLayout.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics()));
        lp.addRule(ALIGN_PARENT_RIGHT);
        handle.setLayoutParams(lp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            handleColour = attributes.getColor(R.styleable.MaterialScrollBar_handleColour, fetchAccentColor(context));
            handle.setBackgroundColor(handleColour);
        } else {
            handleColour = attributes.getColor(R.styleable.MaterialScrollBar_handleColour, Color.parseColor("#9c9c9c"));
            handle.setBackgroundColor(handleColour);
        }

        addView(background);
        addView(handle);

        setTouchIntercept();

        fade = new BarFade(this);
        fade.start();

        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        anim.setFillAfter(true);
        startAnimation(anim);
    }

    /**
     * @param rv to assign as the ScrollBar's recycler view.
     */
    public MaterialScrollBar setRecyclerView(RecyclerView rv){
        recyclerView = rv;
        rv.addOnScrollListener(this.scrollListener);
        return this;
    }

    class recyclerViewNotSetException extends RuntimeException{
        public recyclerViewNotSetException(String message) { super(message); }
    }

    private void setTouchIntercept(){
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    try {
                        recyclerView.scrollToPosition((int) (recyclerView.getAdapter().getItemCount() * (event.getY() / (getHeight() - handle.getHeight()))));
                        if(sectionIndicator != null && sectionIndicator.getVisibility() == INVISIBLE){
                            sectionIndicator.setVisibility(VISIBLE);
                        }
                    } catch (NullPointerException e) {
                        throw new recyclerViewNotSetException("You failed to run setRecyclerView()! You must do this.");
                    }

                    if (hide) {
                        if (hidden) {
                            hidden = false;
                            TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
                            anim.setDuration(500);
                            anim.setFillAfter(true);
                            startAnimation(anim);
                        }
                    }
                } else {
                    if(sectionIndicator != null && sectionIndicator.getVisibility() == VISIBLE){
                        sectionIndicator.setVisibility(INVISIBLE);
                    }

                    if (hide) {
                        fade.run = true;
                        fade.time = System.currentTimeMillis() + hideDuration;
                    }
                }

                return true;
            }
        });
    }

    /**
     * Provides the ability to programmatically set the hide duration of the scrollbar.
     * @param duration for the bar to remain visible after inactivity before hiding.
     */
    public MaterialScrollBar setHideDuration(int duration){
        this.hideDuration = duration;
        return this;
    }

    /**
     * Provides the ability to programmatically set the colour of the scrollbar handle.
     * @param colour to set the handle.
     */
    public MaterialScrollBar setHandleColour(String colour){
        handle.setBackgroundColor(Color.parseColor(colour));
        return this;
    }

    /**
     * Provides the ability to programmatically set the colour of the scrollbar handle.
     * @param colour to set the handle.
     */
    public MaterialScrollBar setHandleColour(int colour){
        handle.setBackgroundColor(colour);
        return this;
    }

    /**
     * Provides the ability to programmatically set the colour of the scrollbar.
     * @param colour to set the bar.
     */
    public MaterialScrollBar setBarColour(String colour){
        background.setBackgroundColor(Color.parseColor(colour));
        return this;
    }

    /**
     * Provides the ability to programmatically set the colour of the scrollbar.
     * @param colour to set the bar.
     */
    public MaterialScrollBar setBarColour(int colour){
        background.setBackgroundColor(colour);
        return this;
    }

    /**
     * Provides the ability to programmatically alter whether the scrollbar
     * should hide after a period of inactivity or not.
     * @param hide sets whether the bar should hide or not.
     */
    public MaterialScrollBar setAutoHide(Boolean hide){
        if(!hide){
            fade.interrupt();
            TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
            anim.setFillAfter(true);
            startAnimation(anim);
        } else if (!this.hide){
            fade.start();
        }
        this.hide = hide;
        return this;
    }

    class adapterNotNameableException extends RuntimeException{
        public adapterNotNameableException(String message) { super(message); }
    }

    /**
     * Sets the section indicator which accompanies this scroll bar.
     * @param sectionIndicator which should be paired to this scroll bar.
     */
    public MaterialScrollBar setSectionIndicator(SectionIndicator sectionIndicator) {
        if(!(recyclerView.getAdapter() instanceof INameableAdapter)){
            throw new adapterNotNameableException("In order to add a sectionIndicator, the adapter for your recyclerView MUST implement INameableAdapter.");
        }
        this.sectionIndicator = sectionIndicator;
        sectionIndicator.pairScrollBar(this);
        return this;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected int fetchAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { android.R.attr.colorAccent });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {

        MaterialScrollBar materialScrollBar;

        protected ScrollListener(MaterialScrollBar msb){
            materialScrollBar = msb;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            ViewHelper.setY(handle, calculateScrollProgress(recyclerView) * (materialScrollBar.getHeight() - handle.getHeight()));
            if(sectionIndicator != null && sectionIndicator.getVisibility() == VISIBLE){
                sectionIndicator.setScroll(calculateScrollProgress(recyclerView) * (materialScrollBar.getHeight() - handle.getHeight()));
            }
        }

        public float calculateScrollProgress(RecyclerView recyclerView) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int lastFullyVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();

            View visibleChild = recyclerView.getChildAt(0);
            if (visibleChild == null) {
                return 0;
            }
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(visibleChild);
            int itemHeight = holder.itemView.getHeight();
            int recyclerHeight = recyclerView.getHeight();
            int itemsInWindow = recyclerHeight / itemHeight;

            int numItemsInList = recyclerView.getAdapter().getItemCount();
            int numScrollableSectionsInList = numItemsInList - itemsInWindow;
            int indexOfLastFullyVisibleItemInFirstSection = numItemsInList - numScrollableSectionsInList - 1;

            int currentSection = lastFullyVisiblePosition - indexOfLastFullyVisibleItemInFirstSection;
            if(sectionIndicator != null && sectionIndicator.getVisibility() == VISIBLE){
                if(currentSection == 0){
                    sectionIndicator.setCharacter(((INameableAdapter)recyclerView.getAdapter()).getCharacterForElement(currentSection));
                } else {
                    sectionIndicator.setCharacter(((INameableAdapter)recyclerView.getAdapter()).getCharacterForElement(currentSection - 1));
                }
            }

            return (float) currentSection / numScrollableSectionsInList;
        }

        @Override
        public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if(hide){
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    fade.time = System.currentTimeMillis() + hideDuration;
                    fade.run = true;
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    fade.run = false;
                    if(hidden){
                        hidden = false;
                        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
                        anim.setDuration(500);
                        anim.setFillAfter(true);
                        materialScrollBar.startAnimation(anim);
                    }
                }
            }
        }
    }
}