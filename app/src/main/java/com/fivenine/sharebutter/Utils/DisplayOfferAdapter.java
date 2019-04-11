package com.fivenine.sharebutter.Utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.SquareImageView;
import com.fivenine.sharebutter.models.Item;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DisplayOfferAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<Item> itemArrayList;

    public DisplayOfferAdapter(Context mContext, ArrayList<Item> itemArrayList){
        this.mContext = mContext;
        this.itemArrayList = itemArrayList;
    }

    @Override
    public int getCount() {
        return itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SquareImageView squareImageView = (SquareImageView) convertView;
        if(squareImageView == null){
            squareImageView = new SquareImageView(mContext);
            squareImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        String url = itemArrayList.get(position).getImg1URL();

        Picasso.get()
                .load(url)
                .fit()
                .into(squareImageView);

        return squareImageView;
    }
}
