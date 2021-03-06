//# COMP 4521    #  AMANDA SAMBATH                  20363161          asambath@connect.ust.hk
//# COMP 4521    #  ETHAN RAYMOND       20363147          eoraymond@connect.ust.hk
//# COMP 4521    #  Nicholas JARZEMBOWSKI               2033 8324          njj@connect.ust.hk
//# COMP 4521    #  LOIC BRUNO STEPAHNE TURIN        20363264          lbsturin@connect.ust.hk

package hk.ust.cse.comp4521.taiotourism;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import hk.ust.cse.comp4521.taiotourism.syncAdapter.GeoPoint;
import hk.ust.cse.comp4521.taiotourism.syncAdapter.POIModel;

import com.strongloop.android.loopback.RestAdapter;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, TaiOMapFragment.OnFragmentInteractionListener {

    private static final String TAG = "Main Activity";

    private DrawerLayout mDrawer;
    public Toolbar toolbar;
    public NavigationView nvDrawer;
    public ActionBarDrawerToggle drawerToggle;

    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "hk.ust.cse.comp4521.datasync";
    // The account name
    public static final String ACCOUNT = "dummyaccount";

    public static FragmentManager fragmentManager;

    Account mAccount;
    RestAdapter adapter;

    // Points to fragment that is currently displayed.
    private Class fragmentClass = HomeFragment.class;

    public int lastSelected;

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Inflate the layout.
        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view.
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Set up drawer toggle (Hamburger and Back Arrow)
        drawerToggle = setupDrawerToggle();
        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        // Set up nav drawer menu selection listen
        nvDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        lastSelected = menuItem.getItemId();
                        return true;
                    }
                });

                // Setup drawer view
                setupDrawerContent(nvDrawer);

        // Set Home page fragment
        Fragment fragment = null;
        try {
            fragment = HomeFragment.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // load home fragment onCreate
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        setTitle("");

        // Create the dummy account
        mAccount = GetSyncAccount(this);

        if (mAccount != null) getContentResolver().setSyncAutomatically(mAccount, TaiODataContract.AUTHORITY, true);

        Log.i("Language", Locale.getDefault().getDisplayLanguage());

        //Test to see if the sync adapter works
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(mAccount, TaiODataContract.AUTHORITY, settingsBundle);

    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account GetSyncAccount(Context context) {

        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);

        Account [] account = accountManager.getAccountsByType(ACCOUNT_TYPE);

        if (account.length == 0) { // no account exists
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            // Create the account type and default account
            Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);

            if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
                return newAccount;
            } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
                return null;
            }
        }
        else
            return account[0];
    }

    /**
     * Sets up the drawer open and close handler
     * @return  ActionBarDrawerToggle
     */
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    /**
     * Handles nav drawer menu item selection
     * @param menuItem
     */
    public void selectDrawerItem(MenuItem menuItem) {

        Bundle bundleArgs = null;
        Fragment fragment = null;

        // On Nav Drawer menu item selection
        // Switch fragment in content pane according to selected item
        switch(menuItem.getItemId()) {

            case R.id.nav_first_fragment:
                // home
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_second_fragment:
                // map
                fragmentClass = TaiOMapFragment.class;
                break;
            case R.id.nav_third_fragment:
                // tour
                //TODO: add map filter to tour selection
                fragmentClass = TaiOMapFragment.class;
                //title = getResources().getString(R.string.toolbar_home);
                bundleArgs = new Bundle();
                bundleArgs.putString(Constants.ARG_MAP_FILTER_SETTING, Constants.CATEGORY_TOUR_STOP);
                bundleArgs.putDouble(Constants.ARG_POI_LATITUDE, Constants.INITIAL_LAT);
                bundleArgs.putDouble(Constants.ARG_POI_LONGITUDE, Constants.INITIAL_LNG);
                break;
            case R.id.nav_ptg_poi:
                bundleArgs = new Bundle();
                bundleArgs.putString(Constants.LIST_CATEGORY, Constants.CATEGORY_TOUR_STOP);
                fragmentClass = ItemListFragment.class;
                break;
            case R.id.nav_ptg_restaurants:
                bundleArgs = new Bundle();
                bundleArgs.putString(Constants.LIST_CATEGORY, Constants.CATEGORY_RESTAURANT);
                fragmentClass = ItemListFragment.class;
                break;
            case R.id.nav_ptg_facilities:
                bundleArgs = new Bundle();
                bundleArgs.putString(Constants.LIST_CATEGORY, Constants.CATEGORY_FACILITY);
                fragmentClass = ItemListFragment.class;
                break;
            case R.id.nav_directions:
                fragmentClass = TransportInfoFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
                break;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
            if (bundleArgs != null) {
                fragment.setArguments(bundleArgs);
            }

            // Setting listener on cardViews for ItemListFragment
            if (fragment instanceof ItemListFragment) {
                ((ItemListFragment) fragment).setItemClickListener(
                        new ItemListAdapter.ItemClickListener() {
                            @Override
                            public void onItemClickListener(POIModel poi) {
                                GeoPoint poiCoordinates = poi.getCoordinates();
                                POIFragment poiFragment = POIFragment.newInstance(
                                        poi.getName(), poi.getDescription(), poi.getPictureUrl(),
                                        poi.getOpeningHours(), poi.getRating(),
                                        poiCoordinates.getLat(), poiCoordinates.getLng());

                                // Set listener to locate a poi on the map from the detailed view
                                poiFragment.setToMapListener(
                                        new POIFragment.ToMapListener() {
                                            @Override
                                            public void onToMapClickListener(double lat, double lng) {
                                                TaiOMapFragment mapFragment =
                                                        TaiOMapFragment.newInstance(lat, lng);

                                                // Swap fragment from detailed view to map
                                                SwapFragment(mapFragment);

                                            }
                                        }
                                );

                                // Swap fragment list to detailed poi
                                SwapFragment(poiFragment);

                            }
                        }
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwapFragment(fragment);
        menuItem.setChecked(true);

        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    /**
     * Handles interaction with buttons on the toolbar.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            /*
                Handle toolbar -> action settings event
             */

            case R.id.action_settings:

                    // only show popup on home
                    if (fragmentClass != TaiOMapFragment.class) {
                        // inflate filter widget for google map
                        // TODO: fix position under tool bar
                        PopupMenu popup = new PopupMenu(this, findViewById(R.id.flContent), Gravity.RIGHT);

                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.settings_drop_down, popup.getMenu());

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.settings_menu_option: {
                                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                        startActivity(intent);
                                        return true;
                                    }
                                }
                                return true;
                            }
                        });
                        popup.show();
                    }

                break;
        }

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    // configuration change (i.e screen rotation)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void OnHomeFragmentInteraction(View view) {

        Bundle homeFragBundleArgs = null;
        Fragment fragment = null;
        nvDrawer.getMenu().getItem(0).setChecked(false);

        switch(view.getId()) {
            // Handle OnClick methods for HomeScreen buttons
            case R.id.home_map_button:
                fragmentClass = TaiOMapFragment.class;
                break;
            case R.id.home_tour_button:
                homeFragBundleArgs = new Bundle();
                homeFragBundleArgs.putString(Constants.ARG_MAP_FILTER_SETTING, Constants.CATEGORY_TOUR_STOP);
                homeFragBundleArgs.putDouble(Constants.ARG_POI_LATITUDE, Constants.INITIAL_LAT);
                homeFragBundleArgs.putDouble(Constants.ARG_POI_LONGITUDE, Constants.INITIAL_LNG);
                fragmentClass = TaiOMapFragment.class;
                break;
            case R.id.home_transport_button:
                fragmentClass = TransportInfoFragment.class;
                break;
            case R.id.home_about_button:
                fragmentClass = GeneralInfoFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
                break;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
            if (homeFragBundleArgs != null) {
                fragment.setArguments(homeFragBundleArgs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwapFragment(fragment);
    }

    /**
     * Called every time a fragment in the content panel is replaced.
     * @param fragment Fragment that will replace current fragment.
     */
    private void SwapFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

        // Execute slide in/out animation only for List view to POI
        if (fragment.getClass().getSimpleName().equals("POIFragment")) {
            ft.setCustomAnimations(R.anim.slide_left_0, R.anim.slide_left_1, R.anim.slide_right_1,R.anim.slide_right_0);
        }

        ft.replace(R.id.flContent, fragment);
        ft.addToBackStack(fragment.getClass().getSimpleName());
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void OnMapFragmentPOIPopupInteraction(String name, String description, String pictureUrl, String openingHours, double rating, double latitude, double longitude) {
        POIFragment poiFragment =
                POIFragment.newInstance(name, description, pictureUrl, openingHours, rating, latitude, longitude);
        poiFragment.setToMapListener(
                new POIFragment.ToMapListener() {
                    @Override
                    public void onToMapClickListener(double lat, double lng) {
                        TaiOMapFragment mapFragment =
                                TaiOMapFragment.newInstance(lat,lng);
                        SwapFragment(mapFragment);
                    }
                }
        );
        SwapFragment(poiFragment);
    }
}

