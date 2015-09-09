package com.crowded.materialnavigation;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

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
        toolbar.setContentInsetsRelative(0, 0);
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
            searchView.setBackgroundResource(R.drawable.search_shadow_collapsed);

            autoComplete = (AutoCompleteTextView)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            autoComplete.setHint("Search...");
            autoComplete.setTextColor(0x8A000000);
            autoComplete.setHintTextColor(0x1F000000);
//            Log.d("LOG",Float.toString(pixelsToSp(MainActivity.this, autoComplete.getTextSize())));


            autoComplete.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
                @Override
                public void onDismiss() {
                    searchView.setBackgroundResource(R.drawable.search_shadow_collapsed);
//                    autoComplete.showDropDown();
                }
            });

            searchView.setSuggestionsAdapter(new SearchSuggestionsAdapter(this));

//            autoComplete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    autoComplete.dismissDropDown();
////                    Log.d("LOG","TEST");
//                }
//            });

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
                    return false;
                }
            });

            final View dropDownAnchor = searchView.findViewById(autoComplete.getDropDownAnchor());

            if (dropDownAnchor != null) {
                dropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        autoComplete.setDropDownWidth(searchView.getWidth());
                        autoComplete.setDropDownHorizontalOffset(0 - autoComplete.getPaddingLeft() - 4);
                        autoComplete.setDropDownVerticalOffset(14);
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

    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }
}
