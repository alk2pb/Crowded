package com.crowded.materialnavigation;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerPanel;
    static SearchView searchView;
    static AutoCompleteTextView autoComplete;
    public static Handler UIHandler;

    static
    {
        UIHandler = new Handler(Looper.getMainLooper());
    }
    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpNavigationDrawer();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TempActivity.class);
                startActivity(intent);
            }
        });

        // Initial tab count
        setTabs(4);
    }

    private void setUpNavigationDrawer() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setContentInsetsRelative(0, 0);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        try {
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
//            actionBar.setSubtitle(getString(R.string.subtitle));
            actionBar.setDisplayShowTitleEnabled(true);
        } catch (Exception ignored) {
        }


        ListView mDrawerListView = (ListView) findViewById(R.id.navDrawerList);

        mDrawerPanel = (LinearLayout) findViewById(R.id.navDrawerPanel);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenWidth = dm.widthPixels;
        int actionBarSize = getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);

        ViewGroup.LayoutParams layout_description = mDrawerPanel.getLayoutParams();
        layout_description.width = Math.min(screenWidth - actionBarSize, (int) (5.75 * actionBarSize));
        mDrawerPanel.setLayoutParams(layout_description);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menulist));
        mDrawerListView.setAdapter(mAdapter);

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setTabs(position + 1);
                mDrawerLayout.closeDrawer(mDrawerPanel);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void setTabs(int count) {
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        ContentFragmentAdapter adapterViewPager = new ContentFragmentAdapter(getSupportFragmentManager(), this, count);
        vpPager.setAdapter(adapterViewPager);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setTextColor(getResources().getColor(R.color.tab_text_color));
        slidingTabLayout.setTextColorSelected(getResources().getColor(R.color.tab_text_color_selected));
        slidingTabLayout.setDistributeEvenly();
        slidingTabLayout.setViewPager(vpPager);
        slidingTabLayout.setTabSelected(0);

        // Change indicator color
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tab_indicator);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mDrawerPanel)) {
            mDrawerLayout.closeDrawer(mDrawerPanel);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setBackgroundResource(R.drawable.drop_shadow);


//            final EditText searchViewText = (EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            autoComplete = (AutoCompleteTextView)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);


            autoComplete.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
                @Override
                public void onDismiss() {
                    searchView.setBackgroundResource(R.drawable.drop_shadow);
                }
            });

            autoComplete.setHint("Search...");
            autoComplete.setTextColor(0x8A000000);
            autoComplete.setHintTextColor(0x1F000000);

            searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

//                    if (autoComplete.isPopupShowing()) {
//                        searchView.setBackgroundResource(R.drawable.drop_shadow2);
//                    } else {
//                        searchView.setBackgroundResource(R.drawable.drop_shadow);
//                    }
                }
            });

            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
//                    if (autoComplete.isPopupShowing()) {
//                        searchView.setBackgroundResource(R.drawable.drop_shadow2);
//                    } else {
//                        searchView.setBackgroundResource(R.drawable.drop_shadow);
//                    }
                }
            });

            searchView.setSuggestionsAdapter(new SearchSuggestionsAdapter(this));
//            searchView.getSuggestionsAdapter().getDropDownView

            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

                @Override
                public boolean onSuggestionClick(int position) {
                    Toast.makeText(MainActivity.this, "Position: " + position, Toast.LENGTH_SHORT).show();
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onSuggestionSelect(int position) {
                    return false;
                }
            });

            // use this method for search process
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // use this method when query submitted
                    Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                    searchView.clearFocus();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // use this method for auto complete search process
//                    if (autoComplete.isPopupShowing()) {
//                        searchView.setBackgroundResource(R.drawable.drop_shadow2);
//                    }
//                    else {
//                        searchView.setBackgroundResource(R.drawable.drop_shadow);
//                    }
                    return false;
                }
            });

            final View dropDownAnchor = searchView.findViewById(autoComplete.getDropDownAnchor());


            if (dropDownAnchor != null) {
                dropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                               int oldLeft, int oldTop, int oldRight, int oldBottom) {

//                        int point2[] = new int[2];
                        // calculate width of DropdownView
//                        autoComplete.setDropDownHorizontalOffset(10);
//                        searchView.getLocationOnScreen(point2);


                        autoComplete.setDropDownWidth(searchView.getWidth());
                        autoComplete.setDropDownHorizontalOffset(0 - autoComplete.getPaddingLeft() - 4);
                        autoComplete.setDropDownVerticalOffset(14);

//                        autoComplete.setDropDownBackgroundResource(R.drawable.background);


//                        int point[] = new int[2];
//                        dropDownAnchor.getLocationOnScreen(point);
//                        // x coordinate of DropDownView
//                        int dropDownPadding = point[0] + autoComplete.getDropDownHorizontalOffset();
//
//                        Rect screenSize = new Rect();
//                        getWindowManager().getDefaultDisplay().getRectSize(screenSize);
//                        // screen width
//                        int screenWidth = screenSize.width();
//
//                        // set DropDownView width
//                        autoComplete.setDropDownWidth(screenWidth - dropDownPadding * 2);

//                        if (check) {
//                            searchView.setBackgroundResource(R.drawable.drop_shadow);
//                            check=false;
//                        }
//                        else {
//                            searchView.setBackgroundResource(R.drawable.drop_shadow2);
//                            check=true;
//                        }

//                        if (autoComplete.isPopupShowing()) {
////                searchView.setBackgroundResource(R.drawable.drop_shadow2);
//                            Log.d("TAG","POPUP SHOWING");
//                        }
//                        else {
////                searchView.setBackgroundResource(R.drawable.drop_shadow);
//                            Log.d("TAG","POPUP NOT SHOWING");
//                        }
                    }

                });
            }

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item != null && item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerPanel)) {
                mDrawerLayout.closeDrawer(mDrawerPanel);
            } else {
                mDrawerLayout.openDrawer(mDrawerPanel);
            }
            return true;
        }

        /*if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(MainActivity.this,TempActivity.class);
            startActivity(intent);
        }*/

        return /*item.getItemId() == R.id.action_settings ||*/ super.onOptionsItemSelected(item);
    }

    public static class SearchSuggestionsAdapter extends SimpleCursorAdapter
    {
        private static final String[] mFields  = { "_id", "result" };
        private static final String[] mVisible = { "result" };
        private static final int[]    mViewIds = { android.R.id.text1 };


        public SearchSuggestionsAdapter(Context context)
        {
            super(context, R.layout.suggestion_layout, null, mVisible, mViewIds, 0);
        }

//        @Override
//        public View getDropDownView(int position, View convertView, ViewGroup parent){
//            View view = super.getView(position, convertView, parent);
////            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
////                @Override
////                public void onViewAttachedToWindow(View v) {
////                    Log.d("TAG","TESTTESTTESTTESTTESTTESTTESTTESTTESTTEST");
////                }
////
////                @Override
////                public void onViewDetachedFromWindow(View v) {
////                    Log.d("TAG","TESTTESTTESTTESTTESTTESTTESTTESTTESTTEST");
////                }
////            });
//
////            view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
////                @Override
////                public void onSystemUiVisibilityChange(int visibility) {
////                    Log.d("TAG","TESTTESTTESTTESTTESTTESTTESTTESTTESTTEST");
////                }
////            });
//
////            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
////                @Override
////                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
////                    Log.d("TAG","TESTTESTTESTTESTTESTTESTTESTTESTTESTTEST");
////                }
////            });
////            Log.d("TAG","TESTTESTTESTTESTTESTTESTTESTTESTTESTTEST");
////            TextView tv=(TextView) view.findViewById(R.id.spinnertarget);
////            tv.setTextColor(Color.BLACK);
//            return view;
//        }

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
                final int count = 100;
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
//                    Log.d("TAG","TEST");
                    MainActivity.runOnUI(new Runnable() {
                        public void run() {
                            MainActivity.searchView.setBackgroundResource(R.drawable.drop_shadow2);
                        }
                    });

                }
                else {
//                    Log.d("TAG","TEST2");

                    MainActivity.runOnUI(new Runnable() {
                        public void run() {
                            MainActivity.searchView.setBackgroundResource(R.drawable.drop_shadow);
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
}
