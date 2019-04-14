package com.fivenine.sharebutter.Message;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fivenine.sharebutter.Exchange.ConfirmationActivity;
import com.fivenine.sharebutter.Exchange.TraderExistingOffers;
import com.fivenine.sharebutter.Home.HomeFragment;
import com.fivenine.sharebutter.Home.ItemInfoActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.models.Chat;
import com.fivenine.sharebutter.models.Item;
import com.fivenine.sharebutter.models.MessageChannel;
import com.fivenine.sharebutter.models.TradeOffer;
import com.fivenine.sharebutter.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.View.GONE;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MessageActivity";
    public static final String CURRENT_TRADER = "current_trader";
    public static final String CURRENT_TRADE_OFFER = "current_trade_offer";

    //Materials view all offers
    LinearLayout llViewAllOffers;
    TextView tvViewAllOffers;
    Button btnViewAllOffers;

    //Materials (When item to offer does not exist)
    LinearLayout llNoOfferPage;
    ImageView ivUploadPhoto;

    //Materials (When item to offer exist)
    LinearLayout llOfferExistPage;
    ImageView ivOfferPhoto;
    ImageView ivOfferPhotoTraded;
    TextView tvItemName;
    TextView tvItemDescription;
    TextView tvItemExpDate;
    Button btnChangeTrade;

    //Chat Box
    RecyclerView rvChatList;
    ArrayList<Chat> chatArrayList;
    ChatAdapter chatAdapter;

    RelativeLayout rlChatBox;
    EditText etChatInput;
    ImageView ivSendChat;

    //Toolbar
    ImageView ivSupportAction;
    TextView tvTitle;
    TextView tvAction;

    //Firebase
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ValueEventListener chatListener;
    ValueEventListener existingTradeOfferListener;
    ValueEventListener messageChannelListener;
    ValueEventListener targetItemListener;
    ValueEventListener targetItemUserListener;
    ValueEventListener traderItemListener;
    ValueEventListener traderItemUserListener;

    List<TradeOffer> existingTradeList;
    List<MessageChannel> existingMessageChannelList;

    User targetItemUser;
    Item targetItem;
    User traderItemUser;
    Item traderItem;
    MessageChannel currentSelfMessageChannel;
    MessageChannel currentTargetMessageChannel;
    TradeOffer currentTradeOffer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        init();
    }

    private void init() {
        initializeData();

        //View All Offers
        llViewAllOffers = findViewById(R.id.ll_view_all_offer);
        tvViewAllOffers = findViewById(R.id.tv_view_all_offer);
        btnViewAllOffers = findViewById(R.id.btn_view_all_offer);
        btnViewAllOffers.setOnClickListener(this);

        if(firebaseUser.getUid().equals(targetItemUser.getUser_id())){
            llViewAllOffers.setVisibility(View.VISIBLE);
        } else {
            getTraderByUserID(firebaseUser.getUid());
            llViewAllOffers.setVisibility(View.GONE);
        }

        //When no item offered for trade
        llNoOfferPage = findViewById(R.id.ll_no_offer_exist_page);
        ivUploadPhoto = findViewById(R.id.iv_upload_photo);
        ivUploadPhoto.setOnClickListener(this);

        //When selected an item for trade
        llOfferExistPage = findViewById(R.id.ll_offer_exist_page);
        ivOfferPhoto = findViewById(R.id.iv_uploaded_photo);
        ivOfferPhotoTraded = findViewById(R.id.iv_uploaded_photo_traded);
        tvItemName = findViewById(R.id.tv_item_name);
        tvItemDescription = findViewById(R.id.tv_item_description);
        tvItemExpDate = findViewById(R.id.tv_item_exp_date);
        btnChangeTrade = findViewById(R.id.btn_chage_trade);

        //Chat box
        rlChatBox = findViewById(R.id.rl_chat_box);
        rvChatList = findViewById(R.id.rv_chat_list);
        rvChatList.setHasFixedSize(true);
        chatArrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((getApplicationContext()));
        linearLayoutManager.setStackFromEnd(true);
        rvChatList.setLayoutManager(linearLayoutManager);

        etChatInput = findViewById(R.id.et_chat_input);

        ivSendChat = findViewById(R.id.iv_send_chat);
        ivSendChat.setOnClickListener(this);

        //Toolbar
        ivSupportAction = findViewById(R.id.tb_iv_support_action);
        ivSupportAction.setOnClickListener(this);
        tvTitle = findViewById(R.id.tb_tv_title);
        tvTitle.setText("");
        tvAction = findViewById(R.id.tb_tv_action);
        tvAction.setText("");
    }

    private void initializeData(){
        //Firebase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentTradeOffer = null;
        currentSelfMessageChannel = null;
        traderItemUser = null;

        //Initialize Target Item
        Gson gson = new Gson();
        targetItem = gson.fromJson(getIntent().getStringExtra(HomeFragment.SELECTED_ITEM), Item.class);
        targetItemUser = gson.fromJson(getIntent().getStringExtra(ItemInfoActivity.ITEM_OWNER), User.class);
        currentSelfMessageChannel = gson.fromJson(getIntent().getStringExtra(MessageFragment.MESSAGE_CHANNEL_ITEM_OWNER), MessageChannel.class);
        if(currentTargetMessageChannel != null) {
            getTargetMessageChannel();
        }

        //Initialize Firebase
        //Existing Trades
        existingTradeList = new ArrayList<>();
        existingTradeOfferListener = getExistingTradeOfferListener();

        //Trade Item Listeners
        traderItemListener = getTraderItemListener();
        traderItemUserListener = getTraderItemUserListener();
        targetItemListener = getTargetItemListener();
        targetItemUserListener = getTargetItemUserListener();
        databaseReference.child(getString(R.string.dbname_trade_offers))
                .child(firebaseUser.getUid()).addListenerForSingleValueEvent(existingTradeOfferListener);

        //MessageChannel Channel
        existingMessageChannelList = new ArrayList<>();
        messageChannelListener = getMessageChannelListener();

        chatListener = getChatListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_upload_photo:
                selectItemToTrade();
                break;
            case R.id.iv_uploaded_photo:
                break;
            case R.id.btn_view_all_offer:
                viewAllTraderOffers();
            case R.id.iv_send_chat:
                sendChat();
                break;
            case R.id.tb_iv_support_action:
                backButtonClicked();
                break;
            case R.id.btn_chage_trade:
                btnChangeTradeOnClicked();
                break;
            default:
                break;
        }
    }

    private void selectItemToTrade(){
        Intent intent = new Intent(MessageActivity.this, TraderExistingOffers.class);

        Gson gson = new Gson();
        intent.putExtra(TraderExistingOffers.ITEM_TARGETED, gson.toJson(targetItem));
        intent.putExtra(CURRENT_TRADER, gson.toJson(traderItemUser));
        intent.putExtra(CURRENT_TRADE_OFFER, gson.toJson(currentTradeOffer));
        startActivity(intent);
    }

    private void viewAllTraderOffers(){
        Intent intent = new Intent(MessageActivity.this, TraderExistingOffers.class);

        Gson gson = new Gson();
        intent.putExtra(CURRENT_TRADER, gson.toJson(traderItemUser));
        intent.putExtra(CURRENT_TRADE_OFFER, gson.toJson(currentTradeOffer));
        startActivity(intent);
    }

    private void sendChat(){
        String message = etChatInput.getText().toString();

        if(message.isEmpty()){
//            Toast.makeText(this, "You can't send empty message", Toast.LENGTH_SHORT).show();
        } else {
            if(currentTradeOffer != null && currentSelfMessageChannel != null && currentTargetMessageChannel != null){
                currentSelfMessageChannel.setUnSeenMessages(0);
                currentTargetMessageChannel.setUnSeenMessages(currentTargetMessageChannel.getUnSeenMessages() + 1);

                //Store into self id
                //Message Channel
                databaseReference.child(getString(R.string.dbname_message_channels))
                        .child(currentTradeOffer.getOwnerId()).child(String.valueOf(currentSelfMessageChannel.getId()))
                        .setValue(currentSelfMessageChannel);

                //Store into target user id
                //Message Channel
                databaseReference.child(getString(R.string.dbname_message_channels))
                        .child(currentTradeOffer.getRequesterId()).child(String.valueOf(currentSelfMessageChannel.getId()))
                        .setValue(currentTargetMessageChannel);
            } else {
                currentTradeOffer = new TradeOffer(new Date().getTime(),
                        targetItem.getItemOwnerId(), firebaseUser.getUid(),
                        String.valueOf(targetItem.getId()), "No Item",
                        TradeOffer.TRADE_PENDING);

                currentSelfMessageChannel = new MessageChannel(currentTradeOffer.getId(),
                        firebaseUser.getUid(), currentTradeOffer.getOwnerId(),
                        etChatInput.getText().toString(),
                        String.valueOf(new Date().getTime())  ,0);

                currentTargetMessageChannel = new MessageChannel(currentTradeOffer.getId(),
                        firebaseUser.getUid(), currentTradeOffer.getOwnerId(),
                        etChatInput.getText().toString(),
                        String.valueOf(new Date().getTime())  ,1);

                existingTradeList.add(currentTradeOffer);
                existingMessageChannelList.add(currentSelfMessageChannel);

                //Store into self id
                //Trade Offer
                databaseReference.child(getString(R.string.dbname_trade_offers))
                        .child(currentTradeOffer.getOwnerId()).child(String.valueOf(currentTradeOffer.getId()))
                        .setValue(currentTradeOffer);

                //Message Channel
                databaseReference.child(getString(R.string.dbname_message_channels))
                        .child(currentTradeOffer.getOwnerId()).child(String.valueOf(currentSelfMessageChannel.getId()))
                        .setValue(currentSelfMessageChannel);

                //Store into target user id
                //Trade Offer
                databaseReference.child(getString(R.string.dbname_trade_offers))
                        .child(currentTradeOffer.getRequesterId()).child(String.valueOf(currentTradeOffer.getId()))
                        .setValue(currentTradeOffer);

                //Message Channel
                databaseReference.child(getString(R.string.dbname_message_channels))
                        .child(currentTradeOffer.getRequesterId()).child(String.valueOf(currentSelfMessageChannel.getId()))
                        .setValue(currentTargetMessageChannel);

                databaseReference.child(getString(R.string.dbname_chats))
                        .child(firebaseUser.getUid()).child(String.valueOf(currentSelfMessageChannel.getId()))
                        .addValueEventListener(chatListener);
            }

            if(firebaseUser.getUid().equals(currentSelfMessageChannel.getSenderId())){
                Chat newChat = new Chat(message, currentSelfMessageChannel.getSenderId(), currentSelfMessageChannel.getReceiverId(),
                        Long.parseLong(currentSelfMessageChannel.getLatestMessageTime()), currentSelfMessageChannel.getId());

                //Chats
                databaseReference.child(MessageActivity.this.getString(R.string.dbname_chats)).child(newChat.getSender())
                        .child(String.valueOf(currentSelfMessageChannel.getId())).push().setValue(newChat);

                databaseReference.child(MessageActivity.this.getString(R.string.dbname_chats)).child(newChat.getReceiver())
                        .child(String.valueOf(currentSelfMessageChannel.getId())).push().setValue(newChat);
            } else {
                Chat newChat = new Chat(message, currentSelfMessageChannel.getReceiverId(), currentSelfMessageChannel.getSenderId(),
                        Long.parseLong(currentSelfMessageChannel.getLatestMessageTime()), currentSelfMessageChannel.getId());

                //Chats
                databaseReference.child(MessageActivity.this.getString(R.string.dbname_chats)).child(newChat.getSender())
                        .child(String.valueOf(currentSelfMessageChannel.getId())).push().setValue(newChat);

                databaseReference.child(MessageActivity.this.getString(R.string.dbname_chats)).child(newChat.getReceiver())
                        .child(String.valueOf(currentSelfMessageChannel.getId())).push().setValue(newChat);
            }


            etChatInput.setText("");
        }
    }

    private void backButtonClicked(){
        finish();
    }

    private ValueEventListener getChatListener(){
        chatArrayList = new ArrayList<>();

        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatArrayList.clear();
                for(DataSnapshot chat : dataSnapshot.getChildren()){
                    Chat chats = chat.getValue(Chat.class);
                    if(chats.getSender().equals(currentTradeOffer.getOwnerId()) && chats.getReceiver().equals(currentTradeOffer.getRequesterId()) ||
                            chats.getSender().equals(currentTradeOffer.getRequesterId()) && chats.getReceiver().equals(currentTradeOffer.getOwnerId())) {
                        chatArrayList.add(chats);
                    }
                }

                chatAdapter = new ChatAdapter(MessageActivity.this, chatArrayList);
                rvChatList.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener getExistingTradeOfferListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot existingTrades : dataSnapshot.getChildren()){
                    TradeOffer tradeOffer = existingTrades.getValue(TradeOffer.class);
                    existingTradeList.add(tradeOffer);
                }

                if(targetItem == null){
                    for(int i = 0; i < existingTradeList.size(); i++){
                        if(existingTradeList.get(i).getId() == currentSelfMessageChannel.getId()){
                            currentTradeOffer = existingTradeList.get(i);

                            databaseReference.child(getString(R.string.dbname_items))
                                    .child(currentTradeOffer.getOwnerId()).child(currentTradeOffer.getOwnerItemId())
                                    .addListenerForSingleValueEvent(targetItemListener);

                            databaseReference.child(getString(R.string.dbname_users))
                                    .child(currentTradeOffer.getOwnerId())
                                    .addListenerForSingleValueEvent(targetItemUserListener);

                            monitorTradeOffer();
                        }
                    }
                }

                databaseReference.child(getString(R.string.dbname_message_channels))
                        .child(firebaseUser.getUid()).addListenerForSingleValueEvent(messageChannelListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener getTargetItemListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                targetItem = dataSnapshot.getValue(Item.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener getTargetItemUserListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                targetItemUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener getTraderItemListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                traderItem = dataSnapshot.getValue(Item.class);
                //Update UI
                llNoOfferPage.setVisibility(GONE);
                //Update item image
                Picasso.get()
                        .load(traderItem.getImg1URL())
                        .fit()
                        .into(ivOfferPhoto);

                tvItemName.setText(traderItem.getName());
                tvItemDescription.setText(traderItem.getDescription());
                tvItemExpDate.setText(traderItem.getExpiredDate());

                if(firebaseUser.getUid().equals(currentTradeOffer.getRequesterId()))
                    btnChangeTrade.setText("Change Offer");
                else
                    btnChangeTrade.setText("Trade");

                btnChangeTrade.setOnClickListener(MessageActivity.this);

                if(currentTradeOffer.getStatus().equals(TradeOffer.TRADE_SUCCESSFUL)){
                    btnChangeTrade.setVisibility(GONE);
                }

                if(traderItem.getTraded()){
                    ivOfferPhotoTraded.setVisibility(View.VISIBLE);
                    btnChangeTrade.setEnabled(false);
                }
                else{
                    ivOfferPhotoTraded.setVisibility(GONE);
                    btnChangeTrade.setEnabled(true);
                }

                llOfferExistPage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener getTraderItemUserListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                traderItemUser = dataSnapshot.getValue(User.class);

                if(targetItemUser.getUser_id().equals(firebaseUser.getUid())){
                    llNoOfferPage.setVisibility(GONE);
                    String message = "View all " + traderItemUser.getUsername() +" Offer to pick your favourite choice.";
                    tvViewAllOffers.setText(message);
                } else {
                    llViewAllOffers.setVisibility(GONE);
                }

                if(firebaseUser.equals(targetItemUser.getUser_id())){
                    tvTitle.setText(traderItemUser.getUsername());
                } else {
                    tvTitle.setText(targetItemUser.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener getMessageChannelListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot existingMessageChannel : dataSnapshot.getChildren()){
                    MessageChannel messageChannel = existingMessageChannel.getValue(MessageChannel.class);
                    existingMessageChannelList.add(messageChannel);
                }

                if(currentSelfMessageChannel == null || currentTradeOffer == null) {
                    for (int i = 0; i < existingTradeList.size(); i++) {
                        if (currentTradeOffer == null) {
                            if (existingTradeList.get(i).getOwnerItemId()
                                    .equals(String.valueOf(targetItem.getId()))) {
                                currentTradeOffer = existingTradeList.get(i);
                            }
                        }

                        for (int j = 0; j < existingMessageChannelList.size(); j++) {
                            if (existingMessageChannelList.get(j).getId() == existingTradeList.get(i).getId()) {
                                currentSelfMessageChannel = existingMessageChannelList.get(j);
                            }
                        }
                    }
                }

                if(currentSelfMessageChannel != null && currentTradeOffer != null) {
                    databaseReference.child(getString(R.string.dbname_users))
                            .child(currentTradeOffer.getRequesterId()).addListenerForSingleValueEvent(traderItemUserListener);

                    databaseReference.child(getString(R.string.dbname_chats))
                            .child(firebaseUser.getUid()).child(String.valueOf(currentSelfMessageChannel.getId()))
                            .addValueEventListener(chatListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void monitorTradeOffer(){
        databaseReference.child(getString(R.string.dbname_trade_offers))
                .child(firebaseUser.getUid()).child(String.valueOf(currentTradeOffer.getId())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentTradeOffer = dataSnapshot.getValue(TradeOffer.class);
                if(!currentTradeOffer.getRequesterItemId().equals("No Item")){
                    databaseReference.child(getString(R.string.dbname_items))
                            .child(currentTradeOffer.getRequesterId()).child(currentTradeOffer.getRequesterItemId())
                            .addListenerForSingleValueEvent(traderItemListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void btnChangeTradeOnClicked(){
        Gson gson = new Gson();

        if(traderItem.getTraded()){
            Toast.makeText(this, "Item already traded.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(firebaseUser.getUid().equals(currentTradeOffer.getOwnerId())){
            Intent intent = new Intent(MessageActivity.this, ConfirmationActivity.class);
            intent.putExtra(CURRENT_TRADER, gson.toJson(traderItemUser));
            intent.putExtra(TraderExistingOffers.ITEM_TARGETED, gson.toJson(targetItem));
            intent.putExtra(TraderExistingOffers.ITEM_SELECTED, gson.toJson(traderItem));
            intent.putExtra(CURRENT_TRADE_OFFER, gson.toJson(currentTradeOffer));

            startActivity(intent);
        } else if(firebaseUser.getUid().equals(currentTradeOffer.getRequesterId())){
            Intent intent = new Intent(MessageActivity.this, TraderExistingOffers.class);
            intent.putExtra(CURRENT_TRADER, gson.toJson(traderItemUser));
            intent.putExtra(TraderExistingOffers.ITEM_TARGETED, gson.toJson(targetItem));
            intent.putExtra(TraderExistingOffers.ITEM_SELECTED, gson.toJson(traderItem));
            intent.putExtra(CURRENT_TRADE_OFFER, gson.toJson(currentTradeOffer));

            startActivity(intent);
        }
    }

    private void getTargetMessageChannel(){
        if(firebaseUser.getUid().equals(currentSelfMessageChannel.getSenderId())){
            databaseReference.child(getString(R.string.dbname_message_channels))
                    .child(currentSelfMessageChannel.getReceiverId()).child(String.valueOf(currentSelfMessageChannel.getId()))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            currentTargetMessageChannel = dataSnapshot.getValue(MessageChannel.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            databaseReference.child(getString(R.string.dbname_message_channels))
                    .child(currentSelfMessageChannel.getSenderId()).child(String.valueOf(currentSelfMessageChannel.getId()))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            currentTargetMessageChannel = dataSnapshot.getValue(MessageChannel.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void getTraderByUserID(String userId){
        databaseReference.child(getString(R.string.dbname_users))
                .child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                traderItemUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseReference.child(MessageActivity.this.getString(R.string.dbname_chats))
                .child(firebaseUser.getUid()).removeEventListener(chatListener);
    }
}
