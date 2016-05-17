package hk.ust.cse.comp4521.taiotourism;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import hk.ust.cse.comp4521.taiotourism.syncAdapter.POIModel;

/**
 * Created by amanda on 20/04/16.
 */
public class ItemListFragment extends Fragment {

    private RecyclerView mRecyclerViewList;
    private ItemListAdapter mItemListAdapter;
    private LinearLayoutManager mLayoutManager;
    private ItemListAdapter.ItemClickListener mItemClickListener;

    private List<POIModel> itemList;
    private static final String LIST_TYPE = "listType";
    private static final String TOUR_STOPS_LIST = "tourStops";
    private static final String RESTAURANTS_LIST = "restaurants";
    private static final String FACILITIES_LIST = "facilities";

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
        String listType = bundleArgs.getString(LIST_TYPE);

        switch (listType) {
            case TOUR_STOPS_LIST:
                break;
            case RESTAURANTS_LIST:
                break;
            case FACILITIES_LIST:
                break;
            default:
                break;
        }
        // TODO : for testing, mock-up data
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
        itemList = new ArrayList<POIModel>();
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mItemListAdapter = new ItemListAdapter(itemList, getContext());
        mItemListAdapter.setItemClickListener(mItemClickListener);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerViewList.setHasFixedSize(true);
        mRecyclerViewList.setAdapter(mItemListAdapter);
        mRecyclerViewList.setLayoutManager(mLayoutManager);
    }
}
