package com.turingtechnologies.materialscrollbar;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by flisar on 11.04.2016.
 */
public class FastScrollerUtil
{
    private static final String TAG = FastScrollerUtil.class.getSimpleName();
    private static boolean DEBUG = false;

    public static void enableDebugging(boolean debug)
    {
        DEBUG = debug;
    }

    public interface IHeaderAdapter
    {
        /**
         * @param index Index or position of the view
         * @return Is the view a the given index a header
         */
        boolean isHeader(int index);
        int getItemCount();
        /**
         * Should define the manager variable and call {@link HeaderScrollManager#calcData(RecyclerView.Adapter)}
         */
        void initScrollManager(int span);
        /**
         * @return The height of a header view
         */
        int getHeaderHeight();
        /**
         * @return The height of a row
         */
        int getRowHeight();
    }

    // ---------------------
    // IHeaderAdapter - public functions
    // --------------------

    public static void initHeaderScroller(RecyclerView rv)
    {
        RecyclerView.Adapter adapter = rv.getAdapter();
        if (adapter instanceof IHeaderAdapter)
        {
            ((IHeaderAdapter) adapter).initScrollManager(getSpanSize(rv));
            initSpanSizeLookup(rv, (IHeaderAdapter)adapter);
        }
    }

    // ---------------------
    // IHeaderAdapter - helper functions
    // --------------------

    private static Integer getSpanSize(RecyclerView rv)
    {
        final RecyclerView.LayoutManager lm = rv.getLayoutManager();
        if (lm != null && lm instanceof GridLayoutManager)
            return ((GridLayoutManager)lm).getSpanCount();
        return 1;
    }

    public static boolean initSpanSizeLookup(final RecyclerView rv, final IHeaderAdapter adapter)
    {
        final RecyclerView.LayoutManager lm = rv.getLayoutManager();
        if (lm != null && lm instanceof GridLayoutManager)
        {
            ((GridLayoutManager)lm).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (rv == null)
                        return 1;
                    return adapter.isHeader(position) ? ((GridLayoutManager) lm).getSpanCount() : 1;
                }
            });
            return true;
        }
        return false;
    }

    // ---------------------
    // IHeaderAdapter - helper classes
    // --------------------

    public static class HeaderData
    {
        private int headerIndex; // index of header in the list of ALL items (not only of headers!)
        private int items; // items underneath the header

        public HeaderData(int headerIndex, int items)
        {
            this.items = items;
            this.headerIndex = headerIndex;
        }

        public boolean containsIndex(int index)
        {
            return index >= headerIndex && index <= headerIndex + items;
        }

        public boolean containsRow(int rowsBeforeThisHeader, int span, int row)
        {
            return row >= rowsBeforeThisHeader + 1 && row <= rowsBeforeThisHeader + getRows(span);
        }

        public int getFirstItemIndex(int rowsBeforeThisHeader, int span, int row)
        {
            int relativeRowIndex = row - rowsBeforeThisHeader - 1;
            if (relativeRowIndex == 0)
                return headerIndex;
            return headerIndex + relativeRowIndex * span - (span - 1);
        }

        public int getRows(int span)
        {
            return 1 + (int)Math.ceil((float)items / (float)span);
        }

        public int getRelativeRowIndex(int span, int index)
        {
            if (index - headerIndex == 0)
                return 0;
            return (int)Math.ceil((float)(index - headerIndex) / (float)span);
        }
    }

    public static class HeaderScrollManager
    {
        private int mSpan;
        private int mCount;
        private int mRows;
        private ArrayList<HeaderData> mHeaderData;

        private int mHeaderHeight;
        private int mRowHeight;
        private int mTotalHeight;

        public HeaderScrollManager(int span)
        {
            mSpan = span;
        }

        public int getSpan()
        {
            return mSpan;
        }

        public <T extends RecyclerView.Adapter & IHeaderAdapter> void calcData(T adapter)
        {
            mHeaderData = new ArrayList<>();

            // 1) How many items do we have (inkl. headers) + init local variables
            mCount = adapter.getItemCount();
            mHeaderHeight = adapter.getHeaderHeight();
            mRowHeight = adapter.getRowHeight();
            // 2) How many rows do we have? + fill map with index of header and items under this header + calc rows
            mRows = 0;
            int itemsAddedToHeaders = 0;
            for (int i = 1; i < mCount; i++)
            {
                if (adapter.isHeader(i) || i == mCount - 1)
                {
                    // current header group end found => save the data
                    int itemsUnderneathHeader = i - itemsAddedToHeaders;
                    if (i != mCount - 1)
                        itemsUnderneathHeader -= 1; // remove next header item from count
                    HeaderData headerData = new HeaderData(itemsAddedToHeaders, itemsUnderneathHeader);
                    mHeaderData.add(headerData);
                    // increase itemsHandled, so that the next header knows how many items are before it
                    itemsAddedToHeaders += 1; // add header item
                    itemsAddedToHeaders += itemsUnderneathHeader; // add items of this header
                    // adjust row count
                    mRows += headerData.getRows(mSpan);
                }
            }

            // 3) calc total height
            mTotalHeight = mHeaderData.size() * mHeaderHeight;
            for (int i = 0; i < mHeaderData.size(); i++)
                mTotalHeight += (mHeaderData.get(i).getRows(mSpan) - 1) * mRowHeight;

            if (DEBUG)
            {
                for (int i = 0; i < mHeaderData.size(); i++)
                    Log.d(TAG, "Header data " + i + ": headerIndex=" + mHeaderData.get(i).headerIndex + " | items=" + mHeaderData.get(i).items + " | rows=" + mHeaderData.get(i).getRows(mSpan));
                for (int i = 0; i <= mCount; i++)
                {
                    float progress = (float) i / (float) mCount;
                    getItemIndexForScroll(progress);
                }
                for (int i = 0; i < mCount; i++)
                    getDepthForItem(i);
            }
        }

        public int getTotalDepth()
        {
            return mTotalHeight;
        }

        public int getDepthForItem(int index)
        {
            // 1) calculate row of this index
            int row = 0;
            int headersAbove = 0;
            for (int i = 0; i < mHeaderData.size(); i++)
            {
                HeaderData headerData = mHeaderData.get(i);
                headersAbove++;
                if (headerData.containsIndex(index))
                {
                    int relRow = headerData.getRelativeRowIndex(mSpan, index);
                    row += relRow + 1;
                    // remove header itself from headersAbove count if row is a header
                    if (relRow == 0)
                        headersAbove--;
//                    L.d(this, "index => Header Data Index: " + index + " => " + i);
                    break;
                }
                else
                    row += headerData.getRows(mSpan);
            }

            int totalOffset = headersAbove * mHeaderHeight + (row - headersAbove - 1) * mRowHeight;

            if (DEBUG)
                Log.d(TAG, "index => row=" + row + " of " + mRows + ", headersAbove=" + headersAbove + " (totalOffset=" + totalOffset + ")");

            return totalOffset;
        }

        public int getItemIndexForScroll(float scrollBarPos)
        {
            if (scrollBarPos < 0)
                scrollBarPos = 0;
            else if (scrollBarPos > 1)
                scrollBarPos = 1;

            // 1) calculate row that corresponds to scrollBarPos
            int rowIndex = Math.round((float)(mRows - 1) * scrollBarPos);
            int row = rowIndex + 1;

            // 2) find header that contains this row
            int rows = 0;
            for (int i = 0; i < mHeaderData.size(); i++)
            {
                HeaderData headerData = mHeaderData.get(i);
                if (headerData.containsRow(rows, mSpan, row))
                {
                    int index = headerData.getFirstItemIndex(rows, mSpan, row);

                    if (DEBUG)
                        Log.d(TAG, "scrollBarPos=" + scrollBarPos + " => row " + row + " of " + mRows + " (item " + (index + 1) + " of " + mCount + ")");

                    return index;
                }
                rows += headerData.getRows(mSpan);
            }

            throw new RuntimeException("Could not find index for scroll position!");
        }
    }
}