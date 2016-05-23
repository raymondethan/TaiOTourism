//# COMP 4521    #  AMANDA SAMBATH                            20363161          asambath@connect.ust.hk
//# COMP 4521    #  ETHAN RAYMOND                              20363147          eoraymond@connect.ust.hk
//# COMP 4521    #  NICKOLAS JARZEMBOWSKI               2033 8324          njj@connect.ust.hk
//# COMP 4521    #  LOIC BRUNO STEPAHNE TURIN        20363264          lbsturin@connect.ust.hk

package hk.ust.cse.comp4521.taiotourism;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import hk.ust.cse.comp4521.taiotourism.syncAdapter.POIModel;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemViewHolder> {
    private static final String TAG = "Item List Adapter";

    private List<POIModel> itemList;
    private int screenWidth = 0;
    private static ItemClickListener itemClickListener;
    private static Context parentContext;

    // Constructor
    public ItemListAdapter(List<POIModel> itemList, Context parentContext, int screenWidth) {
        this.itemList = itemList;
        this.parentContext = parentContext;
        if(screenWidth > 0) {
            this.screenWidth = screenWidth;
        }
    }

    // Setters and Getters
    public void setItemList(List<POIModel> itemList) {
        if (itemList == null) {
            itemList = new ArrayList<POIModel>();
        }
        this.itemList = itemList;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.fragment_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        POIModel item = itemList.get(position);

        holder.setItem(item);
        holder.vTitle.setText(item.getName());
        Picasso.with(parentContext)
                .load(item.getPictureUrl())
                .resize(screenWidth, 0)
                .into(holder.vPhoto);
        holder.vOpeningHours.setText(
                parentContext.getString(R.string.opening_hours, item.getOpeningHours()));
    }

    // Return the size of the data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ************************************************************************
    //                       CUSTOM CLASS AND INTERFACE
    // ************************************************************************
    // Provide a reference to the views for each data item
    public static class ItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        protected TextView vTitle;
        protected ImageView vPhoto;
        protected TextView vOpeningHours;

        protected POIModel poi;

        public ItemViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            vTitle = (TextView) view.findViewById(R.id.item_title);
            vPhoto = (ImageView) view.findViewById(R.id.item_photo);
            vOpeningHours = (TextView) view.findViewById(R.id.item_hours);
        }

        public void setItem(POIModel poi) {
            this.poi = poi;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClickListener(poi);
        }
    }

    public interface ItemClickListener {
        void onItemClickListener(POIModel item);
    }

}
