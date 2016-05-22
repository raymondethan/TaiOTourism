package hk.ust.cse.comp4521.taiotourism;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
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
import hk.ust.cse.comp4521.taiotourism.syncAdapter.SyncAdapter;

import com.strongloop.android.loopback.RestAdapter;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, TaiOMapFragment.OnFragmentInteractionListener {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "hk.ust.cse.comp4521.datasync";
    // The account name
    public static final String ACCOUNT = "dummyaccount";

    public static FragmentManager fragmentManager;

    Account mAccount;
    RestAdapter adapter;

    private SharedPreferences mSharedPreferences;

    // Points to fragment that is currently displayed.
    private Class fragmentClass = HomeFragment.class;

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

        // load home fragment onCreate
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        setTitle("");



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

                                // Hide hamburger in toolbar and replace with back button
                                drawerToggle.setDrawerIndicatorEnabled(false);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                                drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onBackPressed();
                                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                                        drawerToggle.setDrawerIndicatorEnabled(true);
                                    }
                                });

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
            case R.id.home_map_button:
                fragmentClass = TaiOMapFragment.class;
                nvDrawer.getMenu().getItem(1).setChecked(true);
                break;
            case R.id.home_tour_button:
                // TODO: pass parameters to fragment to filter tour stops only.
                fragmentClass = TaiOMapFragment.class;
                nvDrawer.setCheckedItem(R.id.nav_map);
                nvDrawer.getMenu().getItem(1).setChecked(true);
                break;
            case R.id.home_transport_button:
                // TODO: Add fragment class for Transport fragment
                return;
            case R.id.home_about_button:
                // TODO: Add fragment class for general info
                return;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwapFragment(fragment);

    }

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
    public void OnMapFragmentInteraction() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    private Fragment getCurrentFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentByTag(fragmentTag);
        return currentFragment;
    }

    @Override
    /**
     *  Remark: TaiOFragment and ItemListFragment both go back to the main screen when the back button is pushed.
     */
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            int count = getSupportFragmentManager().getBackStackEntryCount();
            String lastBackStack = getSupportFragmentManager().getBackStackEntryAt(count - 1).getName();

            if ( lastBackStack.equals("TaiOMapFragment") || lastBackStack.equals("ItemListFragment") ) {
                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                return;
            }

            getSupportFragmentManager().popBackStack();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            drawerToggle.setDrawerIndicatorEnabled(true);

        } else {
            super.onBackPressed();
        }
    }
}
