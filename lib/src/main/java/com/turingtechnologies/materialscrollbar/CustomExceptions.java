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

import java.util.ArrayList;

class CustomExceptions {

    static class AdapterNotSetupForIndicatorException extends RuntimeException {

        AdapterNotSetupForIndicatorException(Class aClass, String shouldExtend){
            super("In order to add this indicator, the adapter for your recyclerView, " + aClass.getName() + ", MUST implement " + shouldExtend + ".");
        }

    }

    static class MissingAttributesException extends RuntimeException {

         MissingAttributesException(ArrayList<String> missing){
             super("You are missing the following required attributes from a scroll bar in your XML: " + missing);
         }

    }

    static class UnsupportedParentException extends RuntimeException {

        UnsupportedParentException(){
            super("The recyclerView which is associated with a programmatically added scroll bar must be the child of a relative layout.");
        }

    }

    static class AdapterNotSetupForCustomScrollingException extends RuntimeException {

        AdapterNotSetupForCustomScrollingException(Class aClass){
            super("In order to use custom scrolling, the adapter for your recyclerView, " + aClass.getName() + ", MUST implement ICustomScroller.");
        }

    }

}
