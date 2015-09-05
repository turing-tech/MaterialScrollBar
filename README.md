# MaterialScrollBar

An Android library that brings the Material Design 5.1 scrollbar to pre-5.1 devices. Designed for recyclerViews.

<a href="https://play.google.com/store/apps/details?id=com.turingtechnologies.materialscrollbardemo">
  <img alt="Android app on Google Play" src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a>

![](https://img.shields.io/hexpm/l/plug.svg) ![](https://img.shields.io/github/release/krimin-killr21/MaterialScrollBar.svg?label=jCenter) ![](https://img.shields.io/badge/API-7%2B-blue.svg?style=flat)

Preview
-------

[Video](https://youtu.be/CmcPsJYuzME)

![](http://i.imgur.com/9rY0e8h.png)
![](http://i.imgur.com/8DNLqkn.png)

How to add
--------

```gradle
maven {
    jcenter()
}
```

```gradle
dependencies {
    compile 'com.turingtechnologies.materialscrollbar:lib:2.+'
}
```

How to use - ScrollBar
--------
It is very important that the scroller be aligned to the right side of the screen in order to present correctly. You can manipulate the handle and bar colours through the xml or programatically. The recomended width for the view is 20dp to allow for a reasonable area for dragging on the side of the screen.

```xml
<com.turingtechnologies.materialscrollbar.MaterialScrollBar
    android:id="@+id/material_scroller"
    android:layout_width="20dp"
    android:layout_height="match_parent"
    app:handleColour="{{colour here}}" (optional)
    app:barColour="{{colour here}}" (optional)
    android:layout_alignParentRight="true"
    android:layout_alignTop="@+id/recycler_view" />
```

Lastly, it also imperative that you give the materialScrollBar whatever recyclerView to which you wish to attach it. If you fail to do this, the library will spam error messages to the log until you fix it.

```java
materialScrollBar.setRecyclerView(recyclerView);
```

How to use - Section Indicator
--------
Every step here is important, so make sure you've done them all.

1- Add it to the xml.

```xml
<com.turingtechnologies.materialscrollbar.SectionIndicator
  android:layout_width="100dp"
  android:id="@+id/sectionIndicator"
  android:layout_alignRight="[Id of scrollBar]"
  android:layout_alignTop="[Id of scrollBar]"
  android:layout_height="match_parent"/>
```
2- Link the scrollBar and the section indicator programatically.

```java
  scrollBar.setSectionIndicator((SectionIndicator) findViewById(R.id.sectionIndicator));
```

3- Make the adapter for your recyclerView implement INameableAdapter and fill in the getCharacterForElement method for your adapter.

License
--------

    Copyright 2015 Wynne Plaga.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
