package hk.ust.cse.comp4521.taiotourism;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import hk.ust.cse.comp4521.taiotourism.syncAdapter.POIModel;

/**
 * Created by amanda on 20/04/16.
 */
public class ItemListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ItemListFragment";

    private RecyclerView mRecyclerViewList;
    private ItemListAdapter mItemListAdapter;
    private LinearLayoutManager mLayoutManager;
    private ItemListAdapter.ItemClickListener mItemClickListener;

    private List<POIModel> itemList = new ArrayList<POIModel>();
    private String listType = null;
    private static final int ID_LOADER = 0;

    // Setters
    public void setItemClickListener(ItemListAdapter.ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    // ************************************************************************
    //                           LIFECYCLE CALLBACKS
    // ************************************************************************
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerViewList = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);

        // Initialize list of POIs to be displayed
        Bundle bundleArgs = this.getArguments();
        listType = bundleArgs.getString(Constants.LIST_CATEGORY);
        getLoaderManager().initLoader(ID_LOADER, null, this);

        // Set toolbar title
        String toolbarTitle;
        switch (listType) {
            case Constants.CATEGORY_TOUR_STOP:
                toolbarTitle = getString(R.string.toolbar_tour_stops);
                break;
            case Constants.CATEGORY_RESTAURANT:
                toolbarTitle = getString(R.string.toolbar_restaurants);
                break;
            case Constants.CATEGORY_FACILITY:
                toolbarTitle = getString(R.string.toolbar_facilities);
                break;
            default:
                toolbarTitle = "";
                break;
        }
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(toolbarTitle);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the screen width for picasso to display image correctly
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        // Set adapters, layout manager and recycler view for list
        mItemListAdapter = new ItemListAdapter(itemList, getActivity(), width);
        mItemListAdapter.setItemClickListener(mItemClickListener);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerViewList.setHasFixedSize(true);
        mRecyclerViewList.setAdapter(mItemListAdapter);
        mRecyclerViewList.setLayoutManager(mLayoutManager);
    }

    // ************************************************************************
    //                          LOADING DATA METHODS
    // ************************************************************************
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Takes action based on the ID of the Loader that's being created
        String[] projection = {
            TaiODataContract.POIEntry._ID, TaiODataContract.POIEntry.COLUMN_NAME,
            TaiODataContract.POIEntry.COLUMN_CATEGORY, TaiODataContract.POIEntry.COLUMN_TOUR_ORDER,
            TaiODataContract.POIEntry.COLUMN_DESCRIPTION, TaiODataContract.POIEntry.COLUMN_RATING,
            TaiODataContract.POIEntry.COLUMN_OPENING_HOURS,
            TaiODataContract.POIEntry.COLUMN_PICTURE_URL};

        String selection = TaiODataContract.POIEntry.COLUMN_CATEGORY + "=?";
        String[] selectionArgs = {listType};

        if (id == ID_LOADER) {
            return new CursorLoader(
                    getActivity(),                  // Parent activity context
                    TaiODataProvider.POIENTRY_URI,  // Table to query
                    projection,                     // Projection to return
                    selection,                      // No selection clause
                    selectionArgs,                  // No selection arguments
                    null                            // Default sort order
            );
        }
        // If wrong loader id.
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        setUpList(data);
        mItemListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemListAdapter.notifyDataSetChanged();
    }

    private void setUpList(Cursor itemCursor) {
        if (itemCursor.isClosed()) return;
        // Initialise the cursor to the first row and the list
        itemList.clear();
        itemCursor.moveToFirst();

        // Populate the list
        if (!itemCursor.isAfterLast()) {
            do {
                POIModel item = new POIModel();
                item.setName(itemCursor.getString(itemCursor.getColumnIndexOrThrow(
                        TaiODataContract.POIEntry.COLUMN_NAME)));
                item.setDescription(itemCursor.getString(itemCursor.getColumnIndexOrThrow(
                        TaiODataContract.POIEntry.COLUMN_DESCRIPTION)));
                item.setOpeningHours(itemCursor.getString(itemCursor.getColumnIndexOrThrow(
                        TaiODataContract.POIEntry.COLUMN_OPENING_HOURS)));
                item.setPictureUrl(itemCursor.getString(itemCursor.getColumnIndexOrThrow(
                        TaiODataContract.POIEntry.COLUMN_PICTURE_URL)));
                item.setRating(itemCursor.getDouble(itemCursor.getColumnIndexOrThrow(
                        TaiODataContract.POIEntry.COLUMN_RATING)));

                itemList.add(item);
            }
            while (itemCursor.moveToNext());
        }
        itemCursor.close();


        // TODO : delete, for testing mock-up data
        POIModel item1 = new POIModel();
        item1.setName("Tour stop 1");
        item1.setDescription("detailed description of the tour stop");
        item1.setOpeningHours("9:00-00:00");
        item1.setPictureUrl(
                "http://static.panoramio.com/photos/large/32997299.jpg");
        POIModel item2 = new POIModel();
        item2.setName("Tour stop 2");
        item2.setDescription("detailed description of the tour stop");
        item2.setOpeningHours("9:00-00:00");
        item2.setPictureUrl(
                "https://wendymctavish.files.wordpress.com/2011/04/69748013_7d5b129dcc.jpg?w=300&h=168");
        POIModel item3 = new POIModel();
        item3.setName("Tour stop 3");
        item3.setDescription("detailed description of the tour stop");
        item3.setOpeningHours("9:00-00:00");
        item3.setPictureUrl(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2b/Food_stalls_in_Tai_O_village%2C_Hong_Kong_%286847729712%29.jpg/1024px-Food_stalls_in_Tai_O_village%2C_Hong_Kong_%286847729712%29.jpg");
        itemList.clear();
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);
    }
}
