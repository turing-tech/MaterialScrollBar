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
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.nineoldandroids.view.ViewHelper;

public class MaterialScrollBar extends RelativeLayout {

    private View background;
    private View handle;
    protected int handleColour;
    private Activity a;
    private ScrollListener scrollListener = new ScrollListener(this);
    private boolean hidden = true;
    private int hideDuration = 2500;
    private boolean hide = true;
    private RecyclerView recyclerView;
    private SectionIndicator sectionIndicator;
    private int textColour = android.R.color.white;

    //Thread which checks every 1/10th of a second to decide if the scrollBar should slide away.
    class BarFade extends Thread {

        MaterialScrollBar materialScrollBar;

        BarFade(MaterialScrollBar msb){
            materialScrollBar = msb;
        }

        //Variable which is later set to indicate the time after which the scrollBar should disappear
        long time = 0;

        //Variable which is set to false when dragging is occurring and true when dragging is stopped.
        boolean run = false;

        @Override
        public void run() {
            try{
                while(true){
                    //Is it past the time where the bar should be animated away AND is no scrolling occurring?
                    if(run && time <= System.currentTimeMillis()){
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

    private BarFade fade;

    public MaterialScrollBar(Context context, RecyclerView recyclerView){
        super(context);

        if(!isInEditMode()){
            a = (Activity) context;
        }

        background = new View(context);
        LayoutParams lp = new RelativeLayout.LayoutParams(Utils.getDP(8, this), LayoutParams.MATCH_PARENT);
        lp.addRule(ALIGN_PARENT_RIGHT);
        background.setLayoutParams(lp);
        background.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        ViewHelper.setAlpha(background, 0.4F);

        handle = new View(context);
        lp = new RelativeLayout.LayoutParams(Utils.getDP(8, this),
                Utils.getDP(48, this));
        lp.addRule(ALIGN_PARENT_RIGHT);
        handle.setLayoutParams(lp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            handleColour = fetchAccentColour(context);
        } else {
            handleColour = Color.parseColor("#9c9c9c");
        }
        handle.setBackgroundColor(handleColour);

        addView(background);
        addView(handle);

        setId(R.id.reservedNamedId);
        LayoutParams layoutParams = new LayoutParams(Utils.getDP(20, this), ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(ALIGN_PARENT_RIGHT);
        ((ViewGroup) recyclerView.getParent()).addView(this, layoutParams);
        recyclerView.addOnScrollListener(scrollListener);
        this.recyclerView = recyclerView;

        setTouchIntercept();

        fade = new BarFade(this);
        fade.start();

        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        anim.setFillAfter(true);
        startAnimation(anim);
    }

    class recyclerViewNotSetException extends RuntimeException{
        public recyclerViewNotSetException() { super("You failed to run setRecyclerView()! You must do this."); }
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
                        throw new recyclerViewNotSetException();
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
     * Provides the ability to programmatically set the text colour of the indicator. Will do nothing if there is no section indicator.
     * @param colour to set the text of the indicator.
     */
    public MaterialScrollBar setTextColour(int colour){
        textColour = colour;
        if(sectionIndicator != null){
            sectionIndicator.setTextColour(colour);
        }
        return this;
    }

    /**
     * Provides the ability to programmatically set the text colour of the indicator. Will do nothing if there is no section indicator.
     * @param colour to set the text of the indicator.
     */
    public MaterialScrollBar setTextColour(String colour){
        textColour = Color.parseColor(colour);
        if(sectionIndicator != null){
            sectionIndicator.setTextColour(Color.parseColor(colour));
        }
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
        public adapterNotNameableException() { super("In order to add a sectionIndicator, the adapter for your recyclerView MUST implement INameableAdapter."); }
    }

    /**
     * Adds a section indicator which accompanies this scroll bar.
     */
    public MaterialScrollBar addSectionIndicator(Context c) {
        if(!(recyclerView.getAdapter() instanceof INameableAdapter)){
            throw new adapterNotNameableException();
        }
        sectionIndicator = new SectionIndicator(c, this);
        sectionIndicator.setTextColour(textColour);
        return this;
    }

    //Fetch accent colour on devices running Lollipop or newer.
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private int fetchAccentColour(Context context) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { android.R.attr.colorAccent });
        int colour = a.getColor(0, 0);

        a.recycle();

        return colour;
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {

        MaterialScrollBar materialScrollBar;

        ScrollListener(MaterialScrollBar msb){
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