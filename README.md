# MaterialScrollBar

An Android library that brings the Material Design 5.1 scrollbar to pre-5.1 devices. Designed for recyclerViews.

<a href="https://play.google.com/store/apps/details?id=com.turingtechnologies.materialscrollbardemo">
  <img alt="Android app on Google Play" src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a>

![](https://img.shields.io/hexpm/l/plug.svg) ![](https://img.shields.io/github/release/krimin-killr21/MaterialScrollBar.svg?label=jCenter) ![](https://img.shields.io/badge/API-7%2B-blue.svg?style=flat) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MaterialScrollBar-blue.svg?style=flat)](https://android-arsenal.com/details/1/2441)

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
    compile 'com.turingtechnologies.materialscrollbar:lib:3.+'
}
```

How to use - ScrollBar
--------
For version 3.0.0 and later, all you need to add is this line of code:

```java
MaterialScrollBar materialScrollBar = new MaterialScrollBar(this, recyclerView);
```

where 'recyclerView' is the recyclerView to which you want to link the scrollBar.

If you're updating from an older version, remove any xml from this library and rewrite your code to implement the API as above.

Also note that the library does not currently support recyclerViews which do not boarder the screen's edge on the right side.

How to use - Section Indicator
--------
To add a section indicator, simply add the following line of code:

```java
materialScrollBar.addSectionIndicator(this);
```

To use a section indicator, you **MUST** make your recyclerView's adapter implement INameableAdapter. If you do not, the library will throw a runtime error informing you of your mistake.

Versioning Policy
-------

All versions shall have 3 nodes (X.X.X). The first shall increment every time that an application written for the previous version might be rendered incompatibile with the new version. This occurs whenever a feature's implementation must be changed on the developer's end to continue working. The second node shall change whenever a new feature is added or previous features are updated, without breaking any code which was written for the previous version. The last node shall be updated for bugfixes or dependancy updates.

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
