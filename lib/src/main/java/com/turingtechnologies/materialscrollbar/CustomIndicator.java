package com.turingtechnologies.materialscrollbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;

/**
 * Indicator which should be used in all other cases.
 */
@SuppressLint("ViewConstructor")
public class CustomIndicator extends Indicator {

    private int textSize = 25;
    private Context context;

    public CustomIndicator(Context context){
        super(context);
        this.context = context;
    }

    @Override
    String getTextElement(Integer currentSection, RecyclerView.Adapter adapter) {
        String text = ((ICustomAdapter)adapter).getCustomStringForElement(currentSection);
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        int width = Utils.getDP((int) paint.measureText(text), context) + Utils.getDP(30, context);
        if(width < Utils.getDP(75, context)){
            width = Utils.getDP(75, context);
        }
        layoutParams.width = width;
        setLayoutParams(layoutParams);
        return text;
    }

    @Override
    int getIndicatorHeight() {
        return 75;
    }

    @Override
    int getIndicatorWidth() {
        return 0;
    }

    @Override
    void testAdapter(RecyclerView.Adapter adapter) {
        if(!(adapter instanceof ICustomAdapter)){
            throw new adapterNotSetupForIndicatorException("ICustomAdapter");
        }
    }

    @Override
    int getTextSize() {
        return textSize;
    }

    public CustomIndicator setTextSize(int textSize){
        this.textSize = textSize;
        return this;
    }

}
