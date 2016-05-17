package hk.ust.cse.comp4521.taiotourism;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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

    private List<POIModel> itemList;
    private static ItemClickListener itemClickListener;
    private static Context parentContext;

    // Constructor
    public ItemListAdapter(List<POIModel> itemList, Context parentContext) {
        this.itemList = itemList;
        this.parentContext = parentContext;
    }

    // Setters and Getters
    public void setItemList(List<POIModel> itemList) {
        if (itemList == null) {
            itemList = new ArrayList<POIModel>();
        }
        if (itemList == null) {
            itemList.clear();
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
                .into(holder.vPhoto);
        holder.vOpeningHours.setText(item.getOpeningHours());
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
//            Intent poiIntent = new Intent(parentContext, POIActivity.class);
//            poiIntent.putExtra(POI_NAME, poi.getName());
//            poiIntent.putExtra(POI_DESCRIPTION, poi.getDescription());
//            poiIntent.putExtra(POI_OPENING_HOURS, poi.getOpeningHours());
//            poiIntent.putExtra(POI_PICTURE_URL, poi.getPictureUrl());
//            poiIntent.putExtra(POI_RATING, poi.getRating());
//
//            parentContext.startActivity(poiIntent);
            itemClickListener.onItemClickListener(poi);
        }
    }

    public interface ItemClickListener {
        void onItemClickListener(POIModel item);
    }

}
