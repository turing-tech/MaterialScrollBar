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

import android.content.Context;
import android.os.Build;
import androidx.annotation.IdRes;
import android.util.LayoutDirection;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.ParameterizedType;

class Utils {

    /**
     * @param dp Desired size in dp (density-independent pixels)
     * @param v View
     * @return Number of corresponding density-dependent pixels for the given device
     */
    static int getDP(int dp, View v) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, v.getResources().getDisplayMetrics());
    }

    /**
     * @param dp Desired size in dp (density-independent pixels)
     * @param c Context
     * @return Number of corresponding density-dependent pixels for the given device
     */
    static int getDP(int dp, Context c) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    /**
     *
     * @param c Context
     * @return True if the current layout is RTL.
     */
    static boolean isRightToLeft(Context c) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                c.getResources().getConfiguration().getLayoutDirection() == LayoutDirection.RTL;
    }

    static <T> String getGenericName(T object) {
        return ((Class<T>) ((ParameterizedType) object.getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getSimpleName();
    }

    /**
     * Like findViewById(), but traverses upwards from the view given instead of downwards,
     * ignores the view itself and its direct ascendants, and prefers siblings of the initial
     * view over the siblings of one of its ascendants.
     *
     * @param id the id to search for.
     * @param viewToStartFrom the view whose siblings (and whose parents' siblings) should be searched.
     * @return the view found, or null if none could be located.
     */
    static View findNearestNeighborWithID(@IdRes int id, View viewToStartFrom) {
        if (viewToStartFrom == null) return null;

        ViewGroup parent;
        try {
            parent = (ViewGroup) viewToStartFrom.getParent();
        } catch (ClassCastException e) {
            return null;
        }
        for (int i = 0; i < parent.getChildCount(); i++) { // Checks the children of the given view's parent
            if (viewToStartFrom == parent.getChildAt(i)) continue; // Excluding the given view itself
            View result = parent.getChildAt(i).findViewById(id);
            if (result != null) {
                return result;
            }
        }
        return findNearestNeighborWithID(id, parent); // If the view could not be found, check the next higher generation
    }

}
