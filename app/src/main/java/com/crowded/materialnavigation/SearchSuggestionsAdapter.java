package com.crowded.materialnavigation;

import android.content.Context;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class SearchSuggestionsAdapter extends SimpleCursorAdapter
{
    private static final String[] mFields  = { "_id", "result" };
    private static final String[] mVisible = { "result" };
    private static final int[]    mViewIds = { android.R.id.text1 };


    public SearchSuggestionsAdapter(Context context)
    {
        super(context, R.layout.suggestion_layout, null, mVisible, mViewIds, 0);
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint)
    {
        return new SuggestionsCursor(constraint);
    }

    private static class SuggestionsCursor extends AbstractCursor
    {
        private ArrayList<String> mResults;

        public SuggestionsCursor(CharSequence constraint)
        {
            final int count = 4;
            mResults = new ArrayList<>(count);
            for(int i = 0; i < count; i++){
                mResults.add("Result " + (i + 1));
            }
            if(!TextUtils.isEmpty(constraint)){
                String constraintString = constraint.toString().toLowerCase(Locale.ROOT);
                Iterator<String> iter = mResults.iterator();
                while(iter.hasNext()){
                    if(!iter.next().toLowerCase(Locale.ROOT).startsWith(constraintString))
                    {
                        iter.remove();
                    }
                }
            }

            if (mResults.size() > 0 && constraint != null && constraint.length() > 1) {
                MainActivity.runOnUI(new Runnable() {
                    public void run() {
                        MainActivity.searchView.setBackgroundResource(R.drawable.search_shadow_expanded);
                    }
                });

            }
            else {
                MainActivity.runOnUI(new Runnable() {
                    public void run() {
                        MainActivity.searchView.setBackgroundResource(R.drawable.search_shadow_collapsed);
                    }
                });
            }
        }

        @Override
        public int getCount()
        {
            return mResults.size();
        }

        @Override
        public String[] getColumnNames()
        {
            return mFields;
        }

        @Override
        public long getLong(int column)
        {
            if(column == 0){
                return mPos;
            }
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public String getString(int column)
        {
            if(column == 1){
                return mResults.get(mPos);
            }
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public short getShort(int column)
        {
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public int getInt(int column)
        {
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public float getFloat(int column)
        {
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public double getDouble(int column)
        {
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public boolean isNull(int column)
        {
            return false;
        }
    }
}
