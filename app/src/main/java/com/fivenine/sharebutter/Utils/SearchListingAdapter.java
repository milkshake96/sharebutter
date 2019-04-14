package com.fivenine.sharebutter.Utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.models.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.security.auth.callback.Callback;

public class SearchListingAdapter extends RecyclerView.Adapter<SearchListingAdapter.SearchListingViewHolder> {
    private static final String TAG = "SearchListingAdapter";

    private ArrayList<Item> mItemList;
    private OnItemClickListener onItemClickListener;
    public boolean isChecked = false;
    public int clickedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;

    }

    public SearchListingAdapter(ArrayList<Item> itemList) {

        mItemList = itemList;

    }

    public class SearchListingViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImage;
        TextView tvItemName;
        TextView tvItemHashtag;

        SearchListingViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.iv_item_img);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemHashtag = itemView.findViewById(R.id.tv_item_hashtag);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        public View getView() {
            return itemView;
        }
    }

    @NonNull
    @Override
    public SearchListingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_resultlist, viewGroup, false);
        return new SearchListingViewHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchListingViewHolder holder, int position) {
        Item item = mItemList.get(position);
        Picasso.get()
                .load(item.getImg1URL())
                .fit()
                .centerCrop()
                .into(holder.ivItemImage);

        holder.tvItemHashtag.setText(item.getHashTag());
        holder.tvItemName.setText(item.getName());

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public void filteredList(ArrayList<Item> newList) {
        mItemList = newList;
        notifyDataSetChanged();
    }

}