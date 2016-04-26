package hk.ust.cse.comp4521.taiotourism;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        //This was just a test so we can work out grabbing POIs from internal storage and placing them on the map
//        final ContentValues values = new ContentValues();
//        values.put(TaiODataContract.POIEntry.COLUMN_NAME, "Test");
//        values.put(TaiODataContract.POIEntry.COLUMN_LATITUDE, 22.253155);
//        values.put(TaiODataContract.POIEntry.COLUMN_LONGITUDE, 113.858185);
//        values.put(TaiODataContract.POIEntry.COLUMN_CATEGORY,"Tour Stop in Tai O");
//        values.put(TaiODataContract.POIEntry.COLUMN_TOUR_ORDER, 2);
//        values.put(TaiODataContract.POIEntry.COLUMN_DESCRIPTION,"This is a really test dude 2");
//        values.put(TaiODataContract.POIEntry.COLUMN_RATING, 4.5);
//        values.put(TaiODataContract.POIEntry.COLUMN_OPENING_HOURS, "5-9");
//        values.put(TaiODataContract.POIEntry.COLUMN_VISIT_COUNTER, 100);

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Uri contractUri = getContentResolver().insert(TaiODataProvider.POIENTRY_URI, values);
//                long rawContactId = ContentUris.parseId(contractUri);
                Intent intent = new Intent(getBaseContext(), MapActivity.class);
                startActivity(intent);
            }
        });

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
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
