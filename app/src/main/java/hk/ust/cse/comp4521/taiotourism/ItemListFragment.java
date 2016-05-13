package hk.ust.cse.comp4521.taiotourism;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

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
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
