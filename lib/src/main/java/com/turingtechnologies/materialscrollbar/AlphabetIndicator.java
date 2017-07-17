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

/**
 * Indicator which should be used when only one character will be displayed at a time.
 */
@SuppressLint("ViewConstructor")
public class AlphabetIndicator extends Indicator<INameableAdapter, AlphabetIndicator>{

    public AlphabetIndicator (Context c){
        super(c, INameableAdapter.class);
    }

    @Override
    protected String getTextElement(Integer currentSection, INameableAdapter adapter) {
        Character provided = adapter.getCharacterForElement(currentSection);
        return String.valueOf(Character.toUpperCase(provided));
    }

    @Override
    protected int getIndicatorHeight() {
        return 75;
    }

    @Override
    protected int getIndicatorWidth() {
        return 75;
    }

    @Override
    protected int getTextSize() {
        return 40;
    }

}