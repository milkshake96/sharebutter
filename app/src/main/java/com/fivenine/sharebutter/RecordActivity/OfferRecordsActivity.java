package com.fivenine.sharebutter.RecordActivity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivenine.sharebutter.Home.CategoriesActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.models.Item;
import com.fivenine.sharebutter.models.TradeOffer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OfferRecordsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SELECTION_TAG_ON = "ON";
    public static final String SELECTION_TAG_OFF = "OFF";

    //Toolbar
    ImageView tbIvSupportAction;
    TextView tbTvTitle;
    TextView tbTvAction;

    //Widgets
    TextView tvOfferSent;
    TextView tvOfferReceived;
    RecyclerView rvTradeList;

    //Firebase
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    //Var
    List<OfferRecordCard> offerRecordSentList;
    List<OfferRecordCard> offerRecordReceivedList;
    RecyclerView.Adapter offerRecordSentAdapter;
    RecyclerView.Adapter offerRecordReceivedAdapter;
    private boolean isHistoryPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_records);

        init();
    }

    private void init() {
        tbIvSupportAction = findViewById(R.id.tb_iv_support_action);
        tbIvSupportAction.setOnClickListener(this);
        tbTvTitle = findViewById(R.id.tb_tv_title);

        tbTvAction = findViewById(R.id.tb_tv_action);
        tbTvAction.setText("");

        tvOfferSent = findViewById(R.id.tv_offer_sent);
        tvOfferSent.setTag(SELECTION_TAG_ON);
        tvOfferSent.setOnClickListener(this);

        tvOfferReceived = findViewById(R.id.tv_offer_received);
        tvOfferReceived.setTag(SELECTION_TAG_OFF);
        tvOfferReceived.setOnClickListener(this);

        rvTradeList = findViewById(R.id.rv_trade_list);
        rvTradeList.setHasFixedSize(true);
        rvTradeList.setLayoutManager(new LinearLayoutManager(OfferRecordsActivity.this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        offerRecordSentList = new ArrayList<>();
        offerRecordReceivedList = new ArrayList<>();

        int currentPage = getIntent().getIntExtra(CategoriesActivity.TAG, CategoriesActivity.RC_OFFER_RECORD);
        if (currentPage == CategoriesActivity.RC_OFFER_HISTORY)
            isHistoryPage = true;

        if(isHistoryPage){
            tbTvTitle.setText("History");
        } else {
            tbTvTitle.setText("Offer Records");
        }

        getExistingTrades();
    }

    private void getExistingTrades() {
        databaseReference.child(getString(R.string.dbname_trade_offers)).child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot curTrade : dataSnapshot.getChildren()) {
                            TradeOffer currentTrade = curTrade.getValue(TradeOffer.class);
                            OfferRecordCard offerRecordCard = new OfferRecordCard();
                            offerRecordCard.setTradeOffer(currentTrade);

                            if (isHistoryPage) {
                                if (currentTrade.getStatus().equals(TradeOffer.TRADE_SUCCESSFUL)) {
                                    if(currentTrade.getOwnerId().equals(firebaseUser.getUid())){
                                        offerRecordReceivedList.add(offerRecordCard);
                                    } else {
                                        offerRecordSentList.add(offerRecordCard);
                                    }
                                }
                            } else {
                                if (!currentTrade.getStatus().equals(TradeOffer.TRADE_SUCCESSFUL)) {
                                    if (currentTrade.getStatus().equals(TradeOffer.TRADE_PENDING) &&
                                            currentTrade.getOwnerId().equals(firebaseUser.getUid())) {
                                        offerRecordReceivedList.add(offerRecordCard);
                                    } else {
                                        offerRecordSentList.add(offerRecordCard);
                                    }
                                }
                            }


                            for (int i = 0; i < offerRecordReceivedList.size(); i++) {
                                getReceivedOfferRecordItems(i);
                            }

                            for (int i = 0; i < offerRecordSentList.size(); i++) {
                                getSentOfferRecordItems(i);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getSentOfferRecordItems(final int position) {
        //Get Owner Item
        databaseReference.child(getString(R.string.dbname_items))
                .child(offerRecordSentList.get(position).getTradeOffer().getOwnerId())
                .child(offerRecordSentList.get(position).getTradeOffer().getOwnerItemId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Item ownerItem = dataSnapshot.getValue(Item.class);
                        offerRecordSentList.get(position).setOwnerItem(ownerItem);

                        //Get Requester Item
                        databaseReference.child(getString(R.string.dbname_items))
                                .child(offerRecordSentList.get(position).getTradeOffer().getRequesterId())
                                .child(offerRecordSentList.get(position).getTradeOffer().getRequesterItemId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Item requesterItem = dataSnapshot.getValue(Item.class);
                                        offerRecordSentList.get(position).setTraderItem(requesterItem);
                                        offerRecordSentAdapter = new OfferRecordAdapter(OfferRecordsActivity.this, offerRecordSentList);
                                        rvTradeList.setAdapter(offerRecordSentAdapter);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void getReceivedOfferRecordItems(final int position) {
        //Get Owner Item
        databaseReference.child(getString(R.string.dbname_items))
                .child(offerRecordReceivedList.get(position).getTradeOffer().getOwnerId())
                .child(offerRecordReceivedList.get(position).getTradeOffer().getOwnerItemId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Item ownerItem = dataSnapshot.getValue(Item.class);
                        offerRecordReceivedList.get(position).setOwnerItem(ownerItem);

                        //Get Requester Item
                        databaseReference.child(getString(R.string.dbname_items))
                                .child(offerRecordReceivedList.get(position).getTradeOffer().getRequesterId())
                                .child(offerRecordReceivedList.get(position).getTradeOffer().getRequesterItemId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Item requesterItem = dataSnapshot.getValue(Item.class);
                                        offerRecordReceivedList.get(position).setTraderItem(requesterItem);
                                        offerRecordReceivedAdapter = new OfferRecordAdapter(OfferRecordsActivity.this, offerRecordReceivedList);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tb_iv_support_action:
                finish();
                break;
            case R.id.tv_offer_sent:
                onOfferSentClicked();
                break;
            case R.id.tv_offer_received:
                onOfferReceivedClicked();
                break;
            default:
                break;
        }
    }

    private void onOfferSentClicked() {
        if (tvOfferSent.getTag().toString().equals(SELECTION_TAG_ON)) {
            return;
        } else {
            tvOfferSent.setTag(SELECTION_TAG_ON);
            tvOfferReceived.setTag(SELECTION_TAG_OFF);
            rvTradeList.setAdapter(offerRecordSentAdapter);
        }
    }

    private void onOfferReceivedClicked() {
        if (tvOfferReceived.getTag().toString().equals(SELECTION_TAG_ON)) {
            return;
        } else {
            tvOfferReceived.setTag(SELECTION_TAG_ON);
            tvOfferSent.setTag(SELECTION_TAG_OFF);
            rvTradeList.setAdapter(offerRecordReceivedAdapter);
        }
    }
}
