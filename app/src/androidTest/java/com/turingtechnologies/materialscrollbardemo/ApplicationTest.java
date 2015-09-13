package com.turingtechnologies.materialscrollbardemo;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;

import com.robotium.solo.Solo;
import com.turingtechnologies.materialscrollbar.MaterialScrollBar;

import junit.framework.Assert;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ActivityInstrumentationTestCase2<SplashActivity> {

    private Solo solo;
    Point size;


    @TargetApi(Build.VERSION_CODES.FROYO)
    public ApplicationTest() {
        super(SplashActivity.class);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void testOfColours() throws Throwable {
        solo.waitForView(R.id.reservedNamedId);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).setBarColour("#FF0000");
                ((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).setBarColour(0xFF0000);
                ((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).setBarColour(android.R.color.holo_red_dark);
                ((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).setHandleColour("#FF0000");
                ((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).setHandleColour(0xFF0000);
                ((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).setHandleColour(android.R.color.holo_red_dark);
                ((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).setHandleOffColour("#FF0000");
                ((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).setHandleOffColour(0xFF0000);
                ((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).setHandleOffColour(android.R.color.holo_red_dark);
                ((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).setTextColour("#FF0000");
                ((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).setTextColour(0xFF0000);
                ((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).setTextColour(android.R.color.holo_red_dark);
            }
        });
    }

    public void testTouch() throws Throwable {
        solo.waitForView(R.id.reservedNamedId);
        solo.clickLongOnScreen(size.x - 5, size.y - 5);
        Assert.assertFalse(((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).getHidden());
    }

    public void testScroll() throws Throwable {
        solo.waitForView(R.id.reservedNamedId);
        solo.drag(0, 0, 500, 0, 10);
        Assert.assertFalse(((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).getHidden());
    }

    public void testHiding() throws Throwable{
        solo.waitForView(R.id.reservedNamedId);
        ((MaterialScrollBar)solo.getView(R.id.reservedNamedId)).setScrollBarHidden(true);
        solo.drag(0, 0, 500, 0, 10);
        Assert.assertTrue(((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).getHidden());
    }

    public void testTitling(){
        solo.waitForView(R.id.reservedNamedId);
        solo.clickOnView(solo.getView(R.id.action_toDate));
        solo.waitForView(R.id.reservedNamedId);
        solo.clickOnView(solo.getView(R.id.action_toName));
        solo.waitForView(R.id.reservedNamedId);
        solo.drag(0, 0, 500, 0, 10);
        solo.drag(size.x - 5, size.x - 5, 500, -100, 25);
        Assert.assertEquals(((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).getIndicatorText(), SplashActivity.pkgLabelList.get(0));
    }

    public void testAlphabeting(){
        solo.drag(0, 0, 500, 0, 10);
        solo.drag(size.x - 5, size.x - 5, 500, -100, 25);
        Assert.assertEquals(((MaterialScrollBar) solo.getView(R.id.reservedNamedId)).getIndicatorText(), SplashActivity.pkgLabelList.get(0).substring(0, 1));
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}