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

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

/**
 * Indicator which should be used to display dates and/or times. Automatically sizes and localises.
 */
@SuppressLint("ViewConstructor")
public class DateAndTimeIndicator extends Indicator<IDateableAdapter, DateAndTimeIndicator> {

    private String[] months = new DateFormatSymbols().getMonths();

    private Boolean includeYear;
    private Boolean includeMonth;
    private Boolean includeDay;
    private Boolean includeTime;
    private Context context;

    public DateAndTimeIndicator(Context c, boolean includeYear, boolean includeMonth, boolean includeDay, boolean includeTime){
        super(c, IDateableAdapter.class);
        context = c;
        this.includeYear = includeYear;
        this.includeMonth = includeMonth;
        this.includeDay = includeDay;
        this.includeTime = includeTime;
    }

    @Override
    protected String getTextElement(Integer currentSection, IDateableAdapter adapter) {
        Date date = adapter.getDateForElement(currentSection);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String text = "";
        if(includeTime){
            text += DateFormat.getTimeFormat(context).format(date);
        }
        if(includeMonth){
            text += " " + months[calendar.get(Calendar.MONTH)].substring(0, 3);
        }
        if(includeDay){
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if(String.valueOf(day).length() == 1){
                text += " 0" + day;
            } else {
                text += " " + day;
            }
        }
        if(includeYear){
            if(includeDay){
                text += ",";
            }
            text += " " + calendar.get(Calendar.YEAR);
        }
        return text.trim();
    }

    @Override
    protected int getIndicatorHeight() {
        return 75;
    }

    @Override
    protected int getIndicatorWidth() {
        int width = 62;
        if(includeYear){
            if(includeDay){
                width += 14;
            }
            width += 56;
        }
        if(includeMonth){
            width += 43;
        }
        if(includeDay){
            width += 28;
        }
        if(includeTime){
            if(DateFormat.is24HourFormat(context)){
                width += 70;
            } else {
                width += 115;
            }
        }
        return width;
    }

    @Override
    protected int getTextSize() {
        return 28;
    }

}