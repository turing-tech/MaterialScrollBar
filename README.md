# MaterialScrollBar
An Android library that brings the Material Design 5.1 sidebar to pre-5.1 devices.

![](http://i.imgur.com/BsJewvv.png)

How to add
--------

```gradle
maven {
    url "https://jitpack.io"
  }
```

```gradle
dependencies {
    compile 'com.github.krimin-killr21:MaterialScrollBar:+'
}
```

How to use
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
