package hk.ust.cse.comp4521.taiotourism;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import android.database.Cursor;
import android.net.Uri;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.common.collect.ImmutableMap;
import com.google.android.gms.maps.MapFragment;
import com.strongloop.android.loopback.callbacks.ListCallback;

import java.util.List;

import hk.ust.cse.comp4521.taiotourism.syncAdapter.POIModel;
import hk.ust.cse.comp4521.taiotourism.syncAdapter.SyncAdapter;

import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.remoting.adapters.Adapter;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import hk.ust.cse.comp4521.taiotourism.syncAdapter.POIModel;
import hk.ust.cse.comp4521.taiotourism.syncAdapter.SyncAdapter;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, TaiOMapFragment.OnFragmentInteractionListener {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "hk.ust.cse.comp4521.datasync";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    private static final String LIST_TYPE = "listType";
    private static final String TOUR_STOPS_LIST = "tourStops";
    private static final String RESTAURANTS_LIST = "restaurants";
    private static final String FACILITIES_LIST = "facilities";

    private static final String POI_NAME = "name";
    private static final String POI_DESCRIPTION = "description";
    private static final String POI_PICTURE_URL = "pictureUrl";
    private static final String POI_OPENING_HOURS = "openingHours";
    private static final String POI_RATING = "rating";


    public static FragmentManager fragmentManager;

    Account mAccount;
    RestAdapter adapter;

    private SharedPreferences mSharedPreferences;

    // Points to fragment that is currently displayed.
    private Class fragmentClass = HomeFragment.class;


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

        drawerToggle = setupDrawerToggle();

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // Set Home page fragment
        Fragment fragment = null;
        try {
            fragment = (Fragment) HomeFragment.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // load fragment
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        setTitle("大澳 Tai O Guide");


//        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
//        myFab.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(getBaseContext(), MapActivity.class);
//                startActivity(intent);
//            }
//        });

        // Create the dummy account
        mAccount = GetSyncAccount(this);

        if (mAccount != null) getContentResolver().setSyncAutomatically(mAccount, TaiODataContract.AUTHORITY, true);

//        adapter = getLoopBackAdapter();
//        SyncAdapter.POIRepository POIRepo = adapter.createRepository(SyncAdapter.POIRepository.class);

//        POIRepo.findAll(new ListCallback<POIModel>() {
//                @Override
//                public void onSuccess(List<POIModel> objects) {
//                    Log.i("Main Activity", "First result from server is " + objects.get(0).getName());
//                    Log.e("Main Activity", "Successfullly fetched data!");
//                }
//
//                @Override
//                public void onError(Throwable t) {
//                    Log.e("Main Activity", "Failed to fetch data!");
//                }
//            });

        //might not need this line
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,MODE_PRIVATE);

        //Test to see if the sync adapter works
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(mAccount, TaiODataContract.AUTHORITY, settingsBundle);
    }

    public RestAdapter getLoopBackAdapter() {
        if (adapter == null) {
            // Instantiate the shared RestAdapter. In most circumstances,
            // you'll do this only once; putting that reference in a singleton
            // is recommended for the sake of simplicity.
            // However, some applications will need to talk to more than one
            // server - create as many Adapters as you need.
            adapter = new RestAdapter(
                    getApplicationContext(), "http://10.0.2.2:3000/api");

        }
        Log.i("Main Activity", getApplicationContext().toString());
        return adapter;
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

    public void selectDrawerItem(MenuItem menuItem) {

        Bundle bundleArgs = null;
        Fragment fragment = null;

        // On Nav Drawer menu item selection
        // Switch fragment in content pane according to selected item

        // Toolbar title
        String title = "";

        switch(menuItem.getItemId()) {

            case R.id.nav_first_fragment:
                // home
                fragmentClass = HomeFragment.class;
                title = getResources().getString(R.string.toolbar_home);
                break;
            case R.id.nav_second_fragment:
                // map
                fragmentClass = TaiOMapFragment.class;
                title = getResources().getString(R.string.toolbar_map);
                break;
            case R.id.nav_third_fragment:
                // tour
                //TODO: add map filter to tour selection
                fragmentClass = TaiOMapFragment.class;
                title = getResources().getString(R.string.toolbar_home);
                break;
            case R.id.nav_ptg_poi:
                bundleArgs = new Bundle();
                bundleArgs.putString(LIST_TYPE, TOUR_STOPS_LIST);
                fragmentClass = ItemListFragment.class;
                break;
            case R.id.nav_ptg_restaurants:
                bundleArgs = new Bundle();
                bundleArgs.putString(LIST_TYPE, RESTAURANTS_LIST);
                fragmentClass = ItemListFragment.class;
                break;
            case R.id.nav_ptg_facilities:
                bundleArgs = new Bundle();
                bundleArgs.putString(LIST_TYPE, FACILITIES_LIST);
                fragmentClass = ItemListFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
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
                                Fragment poiFragment = (Fragment) POIFragment.newInstance(
                                        poi.getName(), poi.getDescription(), poi.getPictureUrl(),
                                        poi.getOpeningHours(), poi.getRating());
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.flContent, poiFragment).commit();
                            }
                        }
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Swap fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        menuItem.setChecked(true);

        // Set action bar title
        setTitle(title);

        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            /*
                Handle toolbar -> action settings event
             */

            case R.id.action_settings:

                    // only show popup on home
                    if (fragmentClass == HomeFragment.class) {
                        // inflate filter widget for google map
                        // TODO: fix position under tool bar
                        PopupMenu popup = new PopupMenu(this, findViewById(R.id.flContent), Gravity.RIGHT);
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.settings_home, popup.getMenu());
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

        Fragment fragment = null;

        nvDrawer.getMenu().getItem(0).setChecked(false);

        switch(view.getId()) {

            // Handle OnClick methods for HomeScreen buttons
            case R.id.home_screen_map_button:
                fragmentClass = TaiOMapFragment.class;
                nvDrawer.getMenu().getItem(1).setChecked(true);
                break;
            case R.id.home_screen_tour_button:
                // TODO: pass parameters to fragment to filter tour stops only.
                fragmentClass = TaiOMapFragment.class;
                nvDrawer.setCheckedItem(R.id.nav_map);
                nvDrawer.getMenu().getItem(1).setChecked(true);
                break;
            case R.id.home_screen_places_button:
                // TODO: Add fragment class for POI list view
                return;
            case R.id.home_screen_transport_button:
                // TODO: Add fragment class for Transport fragment
                return;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Swap fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

    }

    @Override
    public void OnMapFragmentInteraction() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }
}
