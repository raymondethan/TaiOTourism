package hk.ust.cse.comp4521.taiotourism;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.common.collect.ImmutableMap;
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

public class MainActivity extends AppCompatActivity {

    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "hk.ust.cse.comp4521.datasync";
    // The account name
    public static final String ACCOUNT = "dummyaccount";

    Account mAccount;

    RestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MapActivity.class);
                startActivity(intent);
            }
        });

        // Create the dummy account
        mAccount = GetSyncAccount(this);

        if (mAccount != null)
            getContentResolver().setSyncAutomatically(mAccount, TaiODataContract.AUTHORITY, true);

        adapter = getLoopBackAdapter();
        SyncAdapter.POIRepository POIRepo = adapter.createRepository(SyncAdapter.POIRepository.class);

        POIRepo.findAll(new ListCallback<POIModel>() {
                @Override
                public void onSuccess(List<POIModel> objects) {
                    Log.i("Main Activity", "First result from server is " + objects.get(0).getName());
                    Log.e("Main Activity", "Successfullly fetched data!");
                }

                @Override
                public void onError(Throwable t) {
                    Log.e("Main Activity", "Failed to fetch data!");
                }
            });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                return true;

            case R.id.action_gotomap:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Bundle settingsBundle = new Bundle();
                settingsBundle.putBoolean(
                        ContentResolver.SYNC_EXTRAS_MANUAL, true);
                settingsBundle.putBoolean(
                        ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
                ContentResolver.requestSync(mAccount, TaiODataContract.AUTHORITY, settingsBundle);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
