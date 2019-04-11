package com.fivenine.sharebutter.Message;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.fivenine.sharebutter.Exchange.TraderExistingOffers.ITEM_TRADER;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MessageActivity";
    public static final String CURRENT_TRADER = "current_trader";

    //Materials view all offers
    LinearLayout llViewAllOffers;
    TextView tvViewAllOffers;
    ImageView ivViewAllOffers;

    //Materials (When item to offer does not exist)
    LinearLayout llNoOfferPage;
    ImageView ivUploadPhoto;

    //Materials (When item to offer exist)
    LinearLayout llOfferExistPage;
    ImageView ivOfferPhoto;
    TextView tvItemName;
    TextView tvItemDescription;
    TextView tvItemExpDate;

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
    MessageChannel currentMessageChannel;
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
        ivViewAllOffers = findViewById(R.id.iv_view_all_offer);
        ivViewAllOffers.setOnClickListener(this);

        //When no item offered for trade
        llNoOfferPage = findViewById(R.id.ll_no_offer_exist_page);
        ivUploadPhoto = findViewById(R.id.iv_upload_photo);
        ivUploadPhoto.setOnClickListener(this);

        //When selected an item for trade
        llOfferExistPage = findViewById(R.id.ll_offer_exist_page);
        ivOfferPhoto = findViewById(R.id.iv_uploaded_photo);
        tvItemName = findViewById(R.id.tv_item_name);
        tvItemDescription = findViewById(R.id.tv_item_description);
        tvItemExpDate = findViewById(R.id.tv_item_exp_date);

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
        currentMessageChannel = null;
        traderItemUser = null;

        //Initialize Target Item
        Gson gson = new Gson();
        targetItem = gson.fromJson(getIntent().getStringExtra(HomeFragment.SELECTED_ITEM), Item.class);
        targetItemUser = gson.fromJson(getIntent().getStringExtra(ItemInfoActivity.ITEM_OWNER), User.class);
        currentMessageChannel = gson.fromJson(getIntent().getStringExtra(MessageFragment.MESSAGE_CHANNEL), MessageChannel.class);

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
            case R.id.iv_view_all_offer:
                viewAllTraderOffers();
            case R.id.iv_send_chat:
                sendChat();
                break;
            case R.id.tb_iv_support_action:
                backButtonClicked();
                break;
            case R.id.tb_tv_action:
                break;
            default:
                break;
        }
    }

    private void selectItemToTrade(){
        Intent intent = new Intent(MessageActivity.this, TraderExistingOffers.class);

        Gson gson = new Gson();
        String trader = gson.toJson(traderItemUser);
        intent.putExtra(CURRENT_TRADER, trader);
        startActivity(intent);
    }

    private void viewAllTraderOffers(){
        Intent intent = new Intent(MessageActivity.this, TraderExistingOffers.class);

        Gson gson = new Gson();
        String trader = gson.toJson(traderItemUser);
        intent.putExtra(CURRENT_TRADER, trader);
        startActivity(intent);
    }

    private void sendChat(){
        String message = etChatInput.getText().toString();

        if(message.isEmpty()){
//            Toast.makeText(this, "You can't send empty message", Toast.LENGTH_SHORT).show();
        } else {
            if(currentTradeOffer != null && currentMessageChannel != null){
                currentMessageChannel.setUnSeenMessages(currentMessageChannel.getUnSeenMessages() + 1);

                //Store into self id
                //Message Channel
                databaseReference.child(getString(R.string.dbname_message_channels))
                        .child(firebaseUser.getUid()).child(String.valueOf(currentMessageChannel.getId()))
                        .setValue(currentMessageChannel);

                //Store into target user id
                //Message Channel
                databaseReference.child(getString(R.string.dbname_message_channels))
                        .child(targetItem.getItemOwnerId()).child(String.valueOf(currentMessageChannel.getId()))
                        .setValue(currentMessageChannel);
            } else {
                currentTradeOffer = new TradeOffer(new Date().getTime(),
                        targetItem.getItemOwnerId(), firebaseUser.getUid(),
                        String.valueOf(targetItem.getId()), "No Item",
                        TradeOffer.TRADE_PENDING);

                currentMessageChannel = new MessageChannel(currentTradeOffer.getId(),
                        firebaseUser.getUid(), currentTradeOffer.getOwnerId(),
                        etChatInput.getText().toString(),
                        String.valueOf(new Date().getTime())  ,1);

                existingTradeList.add(currentTradeOffer);
                existingMessageChannelList.add(currentMessageChannel);

                //Store into self id
                //Trade Offer
                databaseReference.child(getString(R.string.dbname_trade_offers))
                        .child(firebaseUser.getUid()).child(String.valueOf(currentTradeOffer.getId()))
                        .setValue(currentTradeOffer);

                //Message Channel
                databaseReference.child(getString(R.string.dbname_message_channels))
                        .child(firebaseUser.getUid()).child(String.valueOf(currentMessageChannel.getId()))
                        .setValue(currentMessageChannel);

                //Store into target user id
                //Trade Offer
                databaseReference.child(getString(R.string.dbname_trade_offers))
                        .child(targetItem.getItemOwnerId()).child(String.valueOf(currentTradeOffer.getId()))
                        .setValue(currentTradeOffer);

                //Message Channel
                databaseReference.child(getString(R.string.dbname_message_channels))
                        .child(targetItem.getItemOwnerId()).child(String.valueOf(currentMessageChannel.getId()))
                        .setValue(currentMessageChannel);

                databaseReference.child(getString(R.string.dbname_chats))
                        .child(firebaseUser.getUid()).child(String.valueOf(currentMessageChannel.getId()))
                        .addValueEventListener(chatListener);
            }

            Chat newChat = new Chat(message, firebaseUser.getUid(), targetItem.getItemOwnerId(),
                    Long.parseLong(currentMessageChannel.getLatestMessageTime()), currentMessageChannel.getId());

            //Chats
            databaseReference.child(MessageActivity.this.getString(R.string.dbname_chats)).child(newChat.getSender())
                    .child(String.valueOf(currentMessageChannel.getId())).push().setValue(newChat);

            databaseReference.child(MessageActivity.this.getString(R.string.dbname_chats)).child(newChat.getReceiver())
                    .child(String.valueOf(currentMessageChannel.getId())).push().setValue(newChat);

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
                    if(chats.getSender().equals(targetItemUser.getUser_id()) && chats.getReceiver().equals(traderItemUser.getUser_id()) ||
                            chats.getSender().equals(traderItemUser.getUser_id()) && chats.getReceiver().equals(targetItemUser.getUser_id())) {
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
                    if(tradeOffer.getStatus().equals(TradeOffer.TRADE_PENDING)){
                        existingTradeList.add(tradeOffer);
                    }
                }

                if(targetItem == null){
                    for(int i = 0; i < existingTradeList.size(); i++){
                        if(existingTradeList.get(i).getId() == currentMessageChannel.getId()){
                            currentTradeOffer = existingTradeList.get(i);

                            databaseReference.child(getString(R.string.dbname_items))
                                    .child(currentTradeOffer.getOwnerId()).child(currentTradeOffer.getOwnerItemId())
                                    .addListenerForSingleValueEvent(targetItemListener);

                            databaseReference.child(getString(R.string.dbname_users))
                                    .child(currentTradeOffer.getOwnerId())
                                    .addListenerForSingleValueEvent(targetItemUserListener);
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
                    llNoOfferPage.setVisibility(View.GONE);
                    String message = "View all " + traderItemUser.getUsername() +" Offer to pick your favourite choice.";
                    tvViewAllOffers.setText(message);
                } else {
                    llViewAllOffers.setVisibility(View.GONE);
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

                if(currentMessageChannel == null || currentTradeOffer == null) {
                    for (int i = 0; i < existingTradeList.size(); i++) {
                        if (currentTradeOffer == null) {
                            if (existingTradeList.get(i).getOwnerItemId()
                                    .equals(String.valueOf(targetItem.getId()))) {
                                currentTradeOffer = existingTradeList.get(i);
                            }
                        }

                        for (int j = 0; j < existingMessageChannelList.size(); j++) {
                            if (existingMessageChannelList.get(j).getId() == existingTradeList.get(i).getId()) {
                                currentMessageChannel = existingMessageChannelList.get(j);
                            }
                        }
                    }
                }

                if(currentMessageChannel != null && currentTradeOffer != null) {
                    if(!currentTradeOffer.getRequesterItemId().equals("No Item")){
                        databaseReference.child(getString(R.string.dbname_items))
                                .child(currentTradeOffer.getRequesterId()).child(currentTradeOffer.getRequesterItemId())
                                .addListenerForSingleValueEvent(traderItemListener);
                    }

                    databaseReference.child(getString(R.string.dbname_users))
                            .child(currentTradeOffer.getRequesterId()).addListenerForSingleValueEvent(traderItemUserListener);

                    databaseReference.child(getString(R.string.dbname_chats))
                            .child(firebaseUser.getUid()).child(String.valueOf(currentMessageChannel.getId()))
                            .addValueEventListener(chatListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseReference.child(MessageActivity.this.getString(R.string.dbname_chats))
                .child(firebaseUser.getUid()).removeEventListener(chatListener);
    }
}
