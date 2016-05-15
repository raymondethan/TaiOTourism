package hk.ust.cse.comp4521.taiotourism;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemViewHolder> {

    private List<Item> itemList;
    private static ItemClickListener itemClickListener;

    // Constructor
    public ItemListAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    // Setters and Getters
    public void setItemList(List<Item> itemList) {
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
        Item item = itemList.get(position);

        holder.vTitle.setText(item.title);
        //holder.vPhoto.setImage(item.photo);  // Just for test
        holder.vOpeningHours.setText(item.openingHours);
    }

    // Return the size of your dataset (invoked by the layout manager)
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

        public ItemViewHolder(View view) {
            super(view);

            vTitle = (TextView) view.findViewById(R.id.item_title);
            vPhoto = (ImageView) view.findViewById(R.id.item_photo);
            vOpeningHours = (TextView) view.findViewById(R.id.item_hours);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    // Interface for custom click on item listener
    public interface ItemClickListener {
        public void onItemClick(int position, View view);
    }
}
