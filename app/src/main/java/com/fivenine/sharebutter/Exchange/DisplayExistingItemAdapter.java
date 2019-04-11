package com.fivenine.sharebutter.Exchange;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.SquareImageView;
import com.fivenine.sharebutter.models.Item;
import com.fivenine.sharebutter.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DisplayExistingItemAdapter extends BaseAdapter {

    private final Context mContext;
    private List<Item> itemArrayList;
    private User trader;
    private FirebaseUser firebaseUser;

    public DisplayExistingItemAdapter(Context mContext, List<Item> itemArrayList, User trader){
        this.mContext = mContext;
        this.itemArrayList = itemArrayList;
        this.trader = trader;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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

        if (squareImageView == null) {
            squareImageView = new SquareImageView(mContext);
            squareImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        String url = itemArrayList.get(position).getImg1URL();

        if(firebaseUser.getUid().equals(trader.getUser_id()) && position == 0){
            squareImageView.setImageDrawable(mContext.getResources().getDrawable(Integer.parseInt(url)));

            return squareImageView;
        }

        Picasso.get()
                .load(url)
                .fit()
                .into(squareImageView);

        return squareImageView;
    }
}

