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

/**
 * Created by amanda on 20/04/16.
 */
public class ItemListFragment extends Fragment {

    private RecyclerView mRecyclerViewList;
    private ItemListAdapter mItemListAdapter;
    private LinearLayoutManager mLayoutManager;

    private List<Item> itemList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerViewList = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);

        // TODO : for testing, mock-up data
        Item item1 = new Item("Tour stop 1",
                "https://wendymctavish.files.wordpress.com/2011/04/69748013_7d5b129dcc.jpg",
                "detailed description of the tour stop", "9:00-00:00");
        Item item2 = new Item("Tour stop 2",
                "https://wendymctavish.files.wordpress.com/2011/04/69748013_7d5b129dcc.jpg",
                "detailed description of the tour stop", "9:00-00:00");
        Item item3 = new Item("Tour stop 3",
                "https://wendymctavish.files.wordpress.com/2011/04/69748013_7d5b129dcc.jpg",
                "detailed description of the tour stop", "9:00-00:00");
        itemList = new ArrayList<Item>();
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

        mItemListAdapter = new ItemListAdapter(itemList);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerViewList.setHasFixedSize(true);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewList.setAdapter(mItemListAdapter);
        mRecyclerViewList.setLayoutManager(mLayoutManager);

        // Set listener for clicks on item
        mItemListAdapter.setItemClickListener(
                new ItemListAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(int position, View view) {
                        // TODO : start POIActivity intent
                        return;
                    }
                }
        );
    }
}
