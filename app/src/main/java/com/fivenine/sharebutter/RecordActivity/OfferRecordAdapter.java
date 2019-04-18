package com.fivenine.sharebutter.RecordActivity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.models.TradeOffer;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OfferRecordAdapter extends RecyclerView.Adapter<OfferRecordAdapter.ViewHolder> {

    public static final String CURRENT_TRADE = "current_trade";

    private Context context;
    private List<OfferRecordCard> offerRecordCardList;
    private boolean isHistoryPage;

    public OfferRecordAdapter(Context context, List<OfferRecordCard> offerRecordCardList, boolean isHistoryPage) {
        this.context = context;
        this.offerRecordCardList = offerRecordCardList;
        this.isHistoryPage = isHistoryPage;
    }

    @Override
    public int getItemCount() {
        return offerRecordCardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ivSenderOffer;
        public ImageView ivReceiverOffer;
        public TextView tvSenderName;
        public TextView tvReceiverName;

        public ImageView ivSenderRemoved;
        public ImageView ivSenderTraded;
        public ImageView ivReceiverRemoved;
        public ImageView ivReceiverTraded;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivSenderOffer = itemView.findViewById(R.id.iv_sender_offer_img);
            ivSenderOffer.setVisibility(View.INVISIBLE);

            ivReceiverOffer = itemView.findViewById(R.id.iv_receiver_offer_img);
            ivReceiverOffer.setVisibility(View.INVISIBLE);

            tvSenderName = itemView.findViewById(R.id.tv_sender_name);
            tvReceiverName = itemView.findViewById(R.id.tv_receiver_name);

            ivSenderRemoved = itemView.findViewById(R.id.iv_sender_remove);
            ivSenderRemoved.setVisibility(View.INVISIBLE);

            ivSenderTraded = itemView.findViewById(R.id.iv_sender_traded);
            ivSenderTraded.setVisibility(View.INVISIBLE);

            ivReceiverRemoved = itemView.findViewById(R.id.iv_receiver_remove);
            ivReceiverRemoved.setVisibility(View.INVISIBLE);

            ivReceiverTraded = itemView.findViewById(R.id.iv_receiver_traded);
            ivReceiverTraded.setVisibility(View.INVISIBLE);

            if (isHistoryPage)
                itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Gson gson = new Gson();
            Intent tradeHistory = new Intent(context, HistoryActivity.class);
            tradeHistory.putExtra(CURRENT_TRADE, gson.toJson(offerRecordCardList.get(getAdapterPosition())));
            context.startActivity(tradeHistory);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_offer_record_card, viewGroup, false);
        return new OfferRecordAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        OfferRecordCard currentOfferRecord = offerRecordCardList.get(i);
        viewHolder.tvSenderName.setText(currentOfferRecord.getTraderItem().getItemOwnerName());
        viewHolder.tvReceiverName.setText(currentOfferRecord.getOwnerItem().getItemOwnerName());

        Picasso.get()
                .load(currentOfferRecord.getOwnerItem().getImg1URL())
                .centerCrop()
                .fit()
                .into(viewHolder.ivReceiverOffer);

        viewHolder.ivReceiverOffer.setVisibility(View.VISIBLE);

        Picasso.get()
                .load(currentOfferRecord.getTraderItem().getImg1URL())
                .centerCrop()
                .fit()
                .into(viewHolder.ivSenderOffer);

        viewHolder.ivSenderOffer.setVisibility(View.VISIBLE);


        if(!isHistoryPage) {
            if (currentOfferRecord.getOwnerItem().getTraded()) {
                viewHolder.ivReceiverTraded.setVisibility(View.VISIBLE);
            }

            if (currentOfferRecord.getOwnerItem().getDeleted()) {
                viewHolder.ivReceiverRemoved.setVisibility(View.VISIBLE);
                viewHolder.ivReceiverOffer.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.ivReceiverOffer.setVisibility(View.VISIBLE);
            }

            if (currentOfferRecord.getTraderItem().getTraded()) {
                viewHolder.ivSenderTraded.setVisibility(View.VISIBLE);
            }

            if (currentOfferRecord.getTraderItem().getDeleted()) {
                viewHolder.ivSenderRemoved.setVisibility(View.VISIBLE);
                viewHolder.ivSenderOffer.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.ivSenderOffer.setVisibility(View.VISIBLE);
            }
        }
    }

    public void updateList(List<OfferRecordCard> offerRecordCardList) {
        this.offerRecordCardList = offerRecordCardList;
        notifyDataSetChanged();
    }
}
