# MaterialScrollBar

An Android library that brings the Material Design 5.1 scrollbar to pre-5.1 devices. Designed for Android's 'recyclerView'.

Go see the wiki!

[![Apache 2.0](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0) [![BinTray](https://img.shields.io/github/release/krimin-killr21/MaterialScrollBar.svg?label=jCenter)](https://bintray.com/krimin-killr21/maven/material-scroll-bar/view) [![Version](https://img.shields.io/badge/API-7%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=7) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MaterialScrollBar-blue.svg?style=flat)](https://android-arsenal.com/details/1/2441)

<a href="https://play.google.com/store/apps/details?id=com.turingtechnologies.materialscrollbardemo">
  <img alt="Android app on Google Play" src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a> 
[![PayPal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=UH23JHQ8K4U2C)

Preview
======

[Video](https://youtu.be/F5glJeAFnA4)

[Image Preview](http://imgur.com/a/TErZR)

How to add
======

```gradle
maven {
    jcenter()
}
```

```gradle
dependencies {
    compile 'com.turingtechnologies.materialscrollbar:lib:8.+'
}
```
Documentation
======

Note: All customisation methods (setAutoHide, setBarColour, etc) return the materialScrollBar, so they can be chained together if wanted. Alternatively, you can just operate on a variable.

##How to use - ScrollBar

The 'scrollBar' can be implemented programatically or through XML.

###XML

The Google Now Launcher Option

```xml
<com.turingtechnologies.materialscrollbar.DragScrollBar
    android:id="@+id/dragScrollBar"
    android:layout_width="wrap_content"
    app:recyclerView="@id/recyclerView"
    app:lightOnTouch="[[boolean]]"
    android:layout_height="match_parent" />
```

or

The Google Messenger Option

```xml
<com.turingtechnologies.materialscrollbar.TouchScrollBar
    android:id="@+id/touchScrollBar"
    android:layout_width="wrap_content"
    app:recyclerView="@id/recyclerView"
    app:lightOnTouch="[[boolean]]"
    android:layout_height="match_parent" />
```

Additonal optional attributes:

* handleColour - Colour
* barColour - Colour
* handleOffColour - Colour
* textColour - Colour
* barThickness - Integer

Please note that for both of these configurations, both recyclerView and lightOnTouch* must have a valid value. The recyclerView attribute should point to the id of the 'recyclerView' to which you want to link the scrollbar.

\* lightOnTouch behaves as follows. A value of true will cause the handle to be grey until pressed, when it will become the normal accent colour (as set). A value of false will cause the handle to always have the accent colour, even when not being pressed.

###Programmatically

The Google Now Launcher Option

```java
DragScrollBar materialScrollBar = new DragScrollBar(this, recyclerView, {{lightOnTouch}});
```

or

The Google Messenger Option

```java
TouchScrollBar materialScrollBar = new TouchScrollBar(this, recyclerView, {{lightOnTouch}});
```

where 'recyclerView' is the object of the 'recyclerView' to which you want to link the 'scrollBar'. The difference between the two options is that the touch option hides after a cooldown period and touches anywhere on the track, whether on the button or not, scroll the view. The drag option on the other hand hides using the animation seen in the video and will only respond to touches on the handle. "lightOnTouch" can either be true or false. A value of true will cause the handle to be grey until pressed, when it will become the normal accent colour (as set). A value of false will cause the handle to always have the accent colour, even when not being pressed.

It is also strongly recommended that you provide the accent colour if your app supports devices below Lollipop. You can do this by invoking:

```java
materialScrollBar.setHandleColour([[Accent Colour]]);
```

For devices running Lollipop and above, the accent colour will be read automatically. If you fail to provide an accent colour, devices running version of Android below Lollipop will default to a usable but bland grey colour.

##How to use - Indicator

To add an indicator, simply add the following line of code:

```java
materialScrollBar.addIndicator({{Indicator}}, {{addSpace}});
```

If you implemented the bar programmatically, simply get the object using 'findViewById()' and then the scrollbar's id.

The indicator should be either 'AlphatbetIndicator', 'DateAndTimeIndicator', or 'CustomIndicator'. See below for specific instructions per indicator.

'{{addSpace}}' is a boolean which indicates whether there should be space in between the indicator and the bar. True adds space, as in the latest version of the Google Launcher, while false adds no space, as in the Android 5.1 system scrollbars.

To use an indicator, you **MUST** make your 'recyclerView''s adapter implement the relevant interface. If you do not, the library will throw a runtime error informing you of your mistake. See documentation for the relevant interface.

##Indicators
###AlphabetIndicator

**Required Interface:** 'INameableAdapter'

To implement an 'AlphabetIndicator', which displays one character usually corresponding to the first letter of each item, add the following to the end of your 'materialScrollBar' instantiation, or add it as a seperate line.
```java
...addSectionIndicator(new AlphabetIndicator(this));
```

###DateAndTimeIndicator

**Required Interface:** 'IDateableAdapter'

To implement a 'DateAndTimeIndicator', which displays any combination of time, day of the month, month, and year, add the following to the end of your 'materialScrollBar' instantiation, or add it as a seperate line.
```java
...addSectionIndicator(new DateAndTimeIndicator(this, {{includeYear}}, {{includeMonth}}, {{includeDay}}, {{includeTime}}));
```

All of the arguments are booleans (except for this first one obviously). The indicator will dynamically size, add punctuation, and localise for you. All you need to do is provide a 'Date' object for each element in your adapter. You should almost always use miliseconds since the epoch unless you have a good reason not to. Otherwise, the library might crash.

###CustomIndicator

**Required Interface:** 'ICustomAdapter'

To implement a 'CustomIndicator', which displays any text you want, add the following to the end of your 'materialScrollBar' instantiation, or add it as a seperate line.
```java
...addSectionIndicator(new CustomIndicator(this));
```

##Customisation Options

For info on other methods, see the detailed documentation from the wiki: https://github.com/krimin-killr21/MaterialScrollBar/wiki/Documentation

License
======

Material Scroll Bar:

    Copyright 2016 Turing Technologies, an unincorporated orginisation of Wynne Plaga.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
    This licensing is applicable to all code offered as part of this
    repository, which can be identified by the lisence notice preceding
    the content AND/OR by its inclusion in a package starting with "com.
    turingtechnologies.materialscrollbar".

RecyclerView-FastScroll:

     Copyright (C) 2016 Tim Malseed
   
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

Launcher 3:
 
     Copyright (C) 2010 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
