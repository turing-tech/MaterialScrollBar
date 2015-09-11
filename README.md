# MaterialScrollBar

An Android library that brings the Material Design 5.1 scrollbar to pre-5.1 devices. Designed for recyclerViews.

Go see the wiki!

[![Apache 2.0](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0) [![BinTray](https://img.shields.io/github/release/krimin-killr21/MaterialScrollBar.svg?label=jCenter)](https://bintray.com/krimin-killr21/maven/material-scroll-bar/view) [![Version](https://img.shields.io/badge/API-7%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=7) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MaterialScrollBar-blue.svg?style=flat)](https://android-arsenal.com/details/1/2441)

<a href="https://play.google.com/store/apps/details?id=com.turingtechnologies.materialscrollbardemo">
  <img alt="Android app on Google Play" src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a> 
[![PayPal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=UH23JHQ8K4U2C)

Preview
======

[Video](https://youtu.be/CmcPsJYuzME)

![](http://i.imgur.com/9rY0e8h.png)
![](http://i.imgur.com/8DNLqkn.png)

How to add
======

```gradle
maven {
    jcenter()
}
```

```gradle
dependencies {
    compile 'com.turingtechnologies.materialscrollbar:lib:34+'
}
```
Documentation
======

Note: All customisation methods (setAutoHide, setBarColour, etc) return the materialScrollBar, so they can be chained together if wanted. Alternatively, you can just operate on a variable.

##How to use - ScrollBar

```java
MaterialScrollBar materialScrollBar = new MaterialScrollBar(this, recyclerView, {{lightOnTouch}});
```

where 'recyclerView' is the recyclerView to which you want to link the scrollBar. "lightOnTouch" can either be true or false. A value of true will cause the handle to be grey until pressed, when it will become the normal accent colour (as set). A value of false will cause the handle to always have the accent colour, even when not being pressed.

It is also strongly recommended that you provide the accent colour if your app supports devices below Lollipop. You can do this by invoking:

```java
materialScrollBar.setHandleColour([[Accent Colour]]);
```

For devices running Lollipop and above, the accent colour will be read automatically. If you fail to provide an accent colour, devices running version of Android below Lollipop will default to a usable but bland grey colour.

Also note that the library does not currently support recyclerViews which do not boarder the screen's edge on the right side.

##How to use - Section Indicator

To add a section indicator, simply add the following line of code:

```java
materialScrollBar.addSectionIndicator({{Section Indicator}});
```

The section indicator should be either AlphatbetIndicator, DateAndTimeIndicator, or CustomIndicator. See below for specific instructions per indicator.

To use an indicator, you **MUST** make your recyclerView's adapter implement the relevant interface. If you do not, the library will throw a runtime error informing you of your mistake. See documentation for the relevant interface.

##Indicators
###AlphabetIndicator

**Required Interface:** INameableAdapter

To implement an AlphabetIndicator, which displays one character usually corresponding to the first letter of each item, add the following to the end of your materialScrollBar instantiation, or add it as a seperate line.
```java
...addSectionIndicator(new AlphabetIndicator(this));
```

###DateAndTimeIndicator

**Required Interface:** IDateableAdapter

To implement a DateAndTimeIndicator, which displays any combination of time, day of the month, month, and year, add the following to the end of your materialScrollBar instantiation, or add it as a seperate line.
```java
...addSectionIndicator(new DateAndTimeIndicator(this, {{includeYear}}, {{includeMonth}}, {{includeDay}}, {{includeTime}}));
```

All of the arguments are booleans (except for this first one obviously). The indicator will dynamically size, add punctuation, and localise for you. All you need to do is provide a Date object for each element in your adapter. You should almost always use miliseconds since the epoch unless you have a good reason not to. Otherwise, the library might crash.

###CustomIndicator

**Required Interface:** ICustomAdapter

To implement a CustomIndicator, which displays any text you want, add the following to the end of your materialScrollBar instantiation, or add it as a seperate line.
```java
...addSectionIndicator(new CustomIndicator(this));
```

##Customisation Options

For info on other methods, see the detailed documentation from the wiki: https://github.com/krimin-killr21/MaterialScrollBar/wiki/Documentation

License
======

    Copyright 2015 Turing Technologies, an unincorporated orginisation of Wynne Plaga.

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
    the content AND/OR by it's inclusion in a package starting with "com.
    turingtechnologies.materialscrollbar".
